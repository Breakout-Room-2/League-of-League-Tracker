#                   Beginning of Helper functions
# -----------------------------------------------------------------------------
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
# -----------------------------------------------------------------------------
#                        End of helper functions

#                        Beginning of 'main' code
# -----------------------------------------------------------------------------

# Define API host to the north american platform host
$api_host = "https://na1.api.riotgames.com";
$no_key_msg = "No API key found, running get_api_key script...";
$expired_msg = "Sorry, looks like the API key expired, please generate a new key and rerun the script when you're done`n Deleting expired key and Exiting now...";
$welcome_msg = "`n Returning back to the main script...";
$summoner_msg = "Summoner not specified as cmdline arg, please enter who you're searching up stats for:";

# If dev_key does not exist or has expired (24 hours since last write)
# prompt user for dev_key and write to file
if(!(Test-Path dev_key)){
    Write-Host $no_key_msg;
    ./get_dev_key.ps1;
    Write-Host $welcome_msg;
}
else{
    $expired = ($(Get-Date) - $((Get-Item .\dev_key).LastWriteTime)).Days;
    if($expired){
        Write-Host $expired_msg;
        Remove-Item dev_key;
        return;
    }
}
$api_key = Get-Content dev_key | ConvertTo-SecureString | ConvertFrom-SecureString -AsPlainText;

# Try to summoner name from commandline args, otherwise prompt user
$summoner = $args[0];
if(!$summoner){
    $summoner = Read-Host -Prompt "Summoner";
}

# Extract account_id from summoner info
$account_id = (Get_Summoner_Info($summoner)).accountId;

# Get matchlist as a powershell object
$matchlist = (Get_MatchList($account_id)).matches;

# Make a directory to store match details called 'matchlist'
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
# -----------------------------------------------------------------------------
#                        End of 'main' code
