# Script-wide definitions for paths, messages, and urls
$api_host   = "https://na1.api.riotgames.com";
$datadragon = "https://ddragon.leagueoflegends.com";
$champ_page = "/cdn/11.3.1/data/en_US/champion.json";
$champ_cache = "champions.json";
$no_key_msg     = "No API key found, running get_api_key script...";
$time_left_msg  = "API key expired, please generate a new key and rerun the script when you're done`n`tDeleting expired key and Exiting now...";
$summoner_msg   = "Summoner not specified as cmdline arg, please enter who you're searching up stats for:";

#                   Beginning of Helper functions
# -----------------------------------------------------------------------------

# Helper function found online that will transform securestring --> plaintext
function decryptSecureString($SecureString){
    return [System.Runtime.InteropServices.Marshal]::PtrToStringUni([System.Runtime.InteropServices.Marshal]::SecureStringToCoTaskMemUnicode($SecureString));
}

# Define a generic API call method using Invoke-RestMethod
#   - api_request: defines the particular API call to make
#   - request_inp: API call param (e.g. summoner name, match ID, etc.)
# 
# Uses Invoke-RestMethod to make request to riot API and 
# returns response as a powershell object (auto converts json resp)
function Make_API_Request($api_request, $request_inp){

# Info on request and input - used primarily for debugging
# Input needs url encoding in case of special characters
# url without key is written to terminal for more verbose output
    Write-Host "`nRequest: $api_request`nInput: $request_inp";
    $encoded_input = [System.Web.HttpUtility]::UrlEncode($request_inp);
    $url = "$api_host$api_request$encoded_input";
    Write-Host "Making a request to: $url";

# Read-Host can be placed here to debug (disrupt before sending request)
    Write-Host "`t`t[User can interrupt script here or Press Enter to Continue]";
    Read-Host;

    return Invoke-RestMethod "$url`?api_key=$api_key";
}

# Define a generic API call method using Invoke-WebRequest
#
# Pretty much a copy of the above but using Invoke-WebRequest instead used
# when response needs to be in json format or 
# when returning a jsonList since the Invoke-RestMethod variant bugs out w/ lists
function Make_Web_Request($api_request, $request_inp){

    Write-Host "`nRequest: $api_request`nInput: $request_inp";
    $encoded_input = [System.Web.HttpUtility]::UrlEncode($request_inp);
    $url = "$api_host$api_request$encoded_input";
    Write-Host "Making a request to: $url";

    return (Invoke-WebRequest -Uri "$url`?api_key=$api_key" -UseBasicParsing).Content;
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
    return Make_Web_Request $api_request $match_id;
}

# API call for champion masteries from encrypted summoner ID and return reponse
function Get_Masteries($summoner_id){
    $api_request = "/lol/champion-mastery/v4/champion-masteries/by-summoner/";
    return Make_Web_Request $api_request $summoner_id | ConvertFrom-Json;
}

# Helper function to create/retreive a hastable to resolve champ ID to data
function Get_ChampData(){

# If already hastable already cached to a file, just read from that file
    if(Test-Path $champ_cache){
        $champions = Get-Content $champ_cache | ConvertFrom-Json;
    } 

# Otherwise will have to process the raw data from datadragon and reduce down
# to a more concise hashtable with only needed details and more favourable
# organization
    else {
        $raw_data = (Invoke-RestMethod "$datadragon$champ_page").data;

# Empty hashtable to add to when processing raw data 
        $champions = @{};
        
# raw data accesses champ data by name (NoteProperties)
        Get-Member -InputObject $raw_data -MemberType NoteProperty | foreach{
# More favourable organization will use champID (key) as key instead and
# only store champ name and image data 
            $champ = $_.Name;
            $champions.add($raw_data.$champ.key, @{
                    Name=$champ;
                    Image=@{full    = $raw_data.$champ.image.full;
                            sprite  = $raw_data.$champ.image.sprite;
                            group   = $raw_data.$champ.image.group}
                    });
        };

# Remember to write to file to avoid doing this work next time
        $champions | ConvertTo-Json > $champ_cache;
    }

# Also remember to return the hashtable 
    return $champions;
}

# Helper function to resolve ID found in API responses to champion data
function Get_ChampByID($champID){
    return (Get_ChampData)."$champID";
}

# One of the main functions
# Gets summoner name from cmd args or user prompt and returns account data
function Get_Account(){
# Try to read summoner name from cmd arguments 1st time, otherwise prompt user
# Retry if erroneous input 
    while (!($account)) {
        if (!$summoner -and $args[0]) {
            $summoner = $args[0];
        } else {
            $summoner = Read-Host -Prompt "Summoner";
        }

        $account = Get_Summoner_Info($summoner);
    }
    return $account;
}

# One of the main functions
# Gets matchlist from accountID and gets further match details for the top 10
# most recent matches, writing details to files in a directory 
function Get_MatchHistory($account_id){
# Get matchlist as a powershell object
    $matchlist = (Get_MatchList($account_id)).matches;

# Make a directory to store match details called 'matchlist' if nonexist
    if(!(Test-Path matchlist)){
        New-Item -ItemType Directory matchlist | Out-Null;
    }

# Iterate through the first 10 matches and extract gameID, which used to get
# more details on each match as a powershell object
    for ($index = 0; $index -lt 10; $index++){

# Extract gameID from matches for the first 10 matches and save to json file
# named after the unique matchid
        $match_id = ($matchlist[$index]).gameID;
        $path = "matchlist/match_details_$match_id.json";

# If file for that match ID already exists, no need to create a new one!
# Match ID is unique to the match, regardless of the summoner 
# meaning less API calls overall!
        if(!(Test-Path $path)){
            Get_MatchDetail($match_id) | python -m json.tool > $path;
        }
    }
}

# One of the main functions
# Gets mastery details from summonerID and creates an array from the top 3 
function Get_TopMasteries($summoner_id){
# Empty array to store the top 3 masteries the player has for each champ
    $masteries = @();

    (Get_Masteries($summoner_id))[0..2] | foreach{
        $masteries += [PSCustomObject]@{
            Name    = (Get_ChampByID($_.championId)).Name;
            Level   = $_.championLevel;
            Points  = $_.championPoints};
    }
    return $masteries;
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
$api_key = decryptSecureString($data.api_key | ConvertTo-SecureString);
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


# Summoner info is needed for pretty much any additional API calls concerning
# the summoner in question
# Account_ID, and encrtyped summoner ID are needed for matchlist and masteries
$account = Get_Account $args[0];
$account_id = $account.accountId;
$summoner_id = $account.id;

Get_MatchHistory($account_id);
Get_TopMasteries($summoner_id);

# -----------------------------------------------------------------------------
#                        End of 'main' code
