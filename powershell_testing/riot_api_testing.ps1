# Script-wide definitions for paths, messages, and urls
$api_host = "https://na1.api.riotgames.com";
$datadragon = "https://ddragon.leagueoflegends.com";
$champ_page = "/cdn/11.3.1/data/en_US/champion.json";
$champions = ".\ddragon\champion.json";
$no_key_msg = "No API key found, running get_api_key script...";
$time_left_msg = "API key expired, please generate a new key and rerun the script when you're done`n`tDeleting expired key and Exiting now...";
$summoner_msg = "Summoner not specified as cmdline arg, please enter who you're searching up stats for:";

#                   Beginning of Helper functions
# -----------------------------------------------------------------------------
#
# Define a generic API call method using Invoke-RestMethod
#   - api_request: defines the particular API call to make
#   - request_inp: API call param (e.g. summoner name, match ID, etc.)
# 
# Uses Invoke-RestMethod to make request to riot API and returns response
function Make_API_Request($api_request, $request_inp){
# Info on request and input - used primarily for debugging
# Input needs url encoding in case of special characters
# url without key is written to terminal for more verbose output
    Write-Host "`nRequest: $api_request`nInput: $request_inp";
    $encoded_input = [System.Web.HttpUtility]::UrlEncode($request_inp);
    $url = "$api_host$api_request$encoded_input";
    Write-Host "Making a request to: $url";

# Read-Host can be placed here to debug (disrupt before sending request)
    Read-Host;

    return Invoke-RestMethod "$url`?api_key=$api_key";
}

# Define a generic API call method using curl
#
# Pretty much a copy of the above but using curl instead of Invoke-RestMethod
# when we want JSON output rather then powershell object output
function Make_curl_Request($api_request, $request_inp){
    Write-Host "Request: $api_request`nInput: $request_inp";
    $encoded_input = [System.Web.HttpUtility]::UrlEncode($request_inp);
    $url = "$api_host$api_request$encoded_input";
    Write-Host "Making a request to: $url";

    return curl -s "$url`?api_key=$api_key";
}

# API call for summoner info from summoner name and return response
function Get_Summoner_Info($summoner){
    $api_request = "/lol/summoner/v4/summoners/by-name/";
    return Make_API_Request $api_request $summoner;
}

# API call for matchlist from summoner account ID and return response
function Get_MatchList($account_id){
    $api_request = "/lol/match/v4/matchlists/by-account/";
    return Make_API_Request $api_request $account_id;
}

# API call for matchdetail from match ID and return response
function Get_MatchDetail($match_id){
    $api_request = "/lol/match/v4/matches/";
    return Make_curl_Request $api_request $match_id;
}

# API call for champion masteries from encrypted summoner ID and return reponse
function Get_Masteries($summoner_id){
    $api_request = "/lol/champion-mastery/v4/champion-masteries/by-summoner/";
    return Make_curl_Request $api_request $summoner_id | ConvertFrom-Json -AsHashtable;
}

# Resolves ID found in API responses to champions
function Get_ChampByID($champID){
    $path = "./ddragon";
    if(!(Test-Path $path)){
        New-Item -ItemType Directory $path | Out-Null;
        curl -s "$datadragon$champ_page" > $champions;
    }
    $champions = (Get-Content $champions | ConvertFrom-Json -AsHashtable).data;

    $champions.Values | foreach{
        if ($_.key -eq $champID){
            return $_;
        }
    }
}
# -----------------------------------------------------------------------------
#                        End of helper functions

#                        Beginning of 'main' code
# -----------------------------------------------------------------------------

# Define alias for edge - I could remove-alias afterwards but eh, keep it it's useful
Set-Alias edge "\Program Files (x86)\Microsoft\Edge\Application\msedge.exe";

# Check if key exists, running script to get key if it doesn't
if(!(Test-Path dev_key)){
    Write-Host $no_key_msg;
    ./get_dev_key.ps1;
}

# Get data from dev_key and extract exp date and api key 
$data = Get-Content dev_key | ConvertFrom-Json;
$api_key = $data.api_key | ConvertTo-SecureString | ConvertFrom-SecureString -AsPlainText;
$exp_date= Get-Date $data.exp_date;

# Check if key expired, opening up the dev page for user to generate key
# and removing expired key
$time_left = ($exp_date) - (Get-Date);
if($time_left -le 0){
    Write-Host $time_left_msg;
    Remove-Item dev_key;
    edge "https://developer.riotgames.com/";
    return;
}

# Notify user of time left on key, then continue
Write-Host -NoNewLine "Key will expire in ";
Write-Host -NoNewLine -ForegroundColor Yellow "$($time_left.Hours) hours";
Write-Host -NoNewLine " and ";
Write-Host -NoNewLine -ForegroundColor Yellow "$($time_left.Minutes) minutes`n";

# Try to summoner name from commandline args, otherwise prompt user
$summoner = $args[0];
if(!$summoner){
    $summoner = Read-Host -Prompt "Summoner";
}

# Summoner info is needed for pretty much any additional API calls concerning
# the summoner in question
# Account_ID, and encrtyped summoner ID are needed for matchlist and masteries
$account = Get_Summoner_Info($summoner);
$account_id = $account.accountId;
$summoner_id = $account.id;

# Get matchlist as a powershell object
$matchlist = (Get_MatchList($account_id)).matches;

# Make a directory to store match details called 'matchlist' if nonexist
if(!(Test-Path matchlist)){
    New-Item -ItemType Directory matchlist | Out-Null;
}

# Iterate through the first 10 matches and extract gameID
# Use gameID to get more details on each match as a powershell object
# Convert to JSON and run through a python tool to pretty-fy before 
# redirecting into respective file under the matchlist directory!
for ($index = 0; $index -lt 10; $index++){
# Extract gameID from matches for the first 10 matches and set the
# destination path for json file with match details unique to each ID
    $match_id = ($matchlist[$index]).gameID;
    $path = "matchlist/match_details_$match_id.json";

# If file for that match ID already exists, no need to create a new one!
#   This works for multiple summoners playing the same game = less API calls
#   overall!!
    if(!(Test-Path $path)){
        Get_MatchDetail($match_id) | python -m json.tool > $path;
    }
}

$masteries = @{};

(Get_Masteries($summoner_id))[0..2] | foreach{
    $masteries.add((Get_ChampByID($_.championId)).id, $_.championPoints);
}

$masteries;
# -----------------------------------------------------------------------------
#                        End of 'main' code
