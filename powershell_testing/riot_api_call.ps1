# Get summoner name either as commandline arg or user input
$summoner=$args[0]

if(!$summoner){
    $summoner=Read-Host("Summoner");
}

# Set API key from file, as well as API_host
$api_key=Get-Content dev_key;
$api_host="https://na1.api.riotgames.com";

# Make a request to get summoners by name and grab summoner account ID
$api_reqest="/lol/summoner/v4/summoners/by-name/";
$request="$api_host$api_reqest$summoner`?api_key=$api_key";
$acount_id=(curl -s $request | ConvertFrom-Json).accountId;

# Make a request to get matchlist by summoner account ID using a python tool to
# 'pretty-fy' the response and paste it into a file
$api_request="/lol/match/v4/matchlists/by-account/";
$request="$api_host$api_request$acount_id`?api_key=$api_key";
curl -s $request | python -m json.tool > matchlist.json;

# Make a directly called matchlist if non-exist
if(!(Test-Path matchlist)){
    mkdir matchlist;
}

# Make a request to get match details by match ID, using the matchlist file 
# generated in previous request
$api_request="/lol/match/v4/matches/";
$matchlist=(Get-Content .\matchlist.json | ConvertFrom-Json).matches

# Iterating through first 10 matches and extracting the match's gameId to get 
# match details, using the python tool again to 'pretty-fy' the response and 
# paste it into a respective file under matchlist directory
for ($index=0; $index -lt 10; $index++){
    $request="$api_host$api_request$(($matchlist[$index]).gameId)`?api_key=$api_key";
    curl -s $request | python -m json.tool > matchlist/match_details_$index.json;
}
