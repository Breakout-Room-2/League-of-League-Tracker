# Script-wide definitions for datafile location, messages, and urls
$newfile        = 1;
$datafile       = ".\login.json";
$found_msg      = "`nLogin info found, just sit back and let the script do it's thing";
$notfound_msg   = "`nNo login file, you're gonna have to input some values yourself:";
$saving_msg     = "`nLogin successful, writing login info to 'login.json' file...";
$failure_msg    = "`nLogin failed, rerun script to enter credentials again...";
$writing_msg    = "writing API key to 'dev_key' file...";
$login_url      = "developer.riotgames.com/login";
$auth_url       = "auth.riotgames.com/api/v1/authorization";

#                       Start of Helper Functions
# ----------------------------------------------------------------------------
#
# Helper function found online that will transform securestring --> plaintext
function decryptSecureString($SecureString){
    return [System.Runtime.InteropServices.Marshal]::PtrToStringUni([System.Runtime.InteropServices.Marshal]::SecureStringToCoTaskMemUnicode($SecureString));
}

# Helper function to gather user credentials. Will try to use and store
# login credentials as secure strings in a file to avoid having to retype
# them each time. 
function getUserCred(){
# If no datafile found
# Notify user
# Prompt user for username and password
# Store as securestrings in file
# Then continue with the rest of the code
    if (!(Test-Path $datafile)) {
        Write-Host -ForegroundColor Magenta $notfound_msg;

        $username = Read-Host -Prompt "Username" | ConvertTo-SecureString -AsPlainText -Force;
        $password = Read-Host -Prompt "Password" -AsSecureString;

        (@{Username=$($username | ConvertFrom-SecureString);
            Password=$($password | ConvertFrom-SecureString);
            }) | ConvertTo-Json > $datafile;
    }

# If datafile was found, all above code can get skipped
# Notify user and mark that newfile was not created
    else {
        Set-Variable -Name newfile -Value 0 -Scope Script;
        Write-Host -ForegroundColor Cyan $found_msg;
    }

# Extract data from the json file 
# Then return decrypted data
    $data = Get-Content $datafile | ConvertFrom-Json;

    $data.username = decryptSecureString($data.username | ConvertTo-SecureString);
    $data.password = decryptSecureString($data.password | ConvertTo-SecureString);

    return $data;
}

# Helper function to parse dev page for apikey and expiration date
# - Update: figured out how to create an HTML element
# Encode both date and key as secure strings and write to json file
# - works for both versions of PS
function parseForKey($dev_page) {
    $html = New-Object -ComObject "HTMLFile";

# Apparently HTMLFile object created will have different properties based off
# whether Office is installed or not... 
# link to the stack overflow: https://stackoverflow.com/a/48859819/13316141
    try {
        $html.IHTMLDocument2_write($dev_page.content);
    } catch {
        $src = [System.Text.Encoding]::Unicode.GetBytes($dev_page.content);
        $html.write($src);
    }

# Find element holding expiration date and extract it's innerHTML
# Run result through a series of regex replaces for cleaner conversion to DateTime
    $date = ($html.getElementsByTagName('b') | Where-Object {$_.InnerText -like 'Expir*'}).InnerText;

    @('Expire[sd]: ', '\(.*', '@', 'th', 'rd', 'nd', 'st') | ForEach-Object {
        $date = $date -replace $_;
    }

    $date = Get-Date $date;
    $key = $html.getElementById('apikey').value | ConvertTo-SecureString -AsPlainText -Force| ConvertFrom-SecureString;
    
    @{api_key=$key; exp_date=$date.DateTime} | ConvertTo-Json > dev_key
}
# ----------------------------------------------------------------------------
#                   End of Helper Functions

#                   Old Helper Functions that have been replaced
# ----------------------------------------------------------------------------
# Helper function to parse the dev page for apikey and expiration date,
# encode as securestrings and write as json to a file
# using Powershell V5
function parseForKeyV5($dev_page){
# Both key and date can be found from element w/ id "apikey"
    $element = $dev_page.parsedHTML.getElementById("apikey");
    $date = $element.parentElement.parentElement.textContent;

# For a clean conversion to DateTime, run through a series of regex replaces
# to remove text before/after date, the @, and any ordinal number suffixes
    @('.*Expire[sd]: ', '\s\(.*', '@', 'th', 'rd', 'nd', 'st') | foreach {
        $date = $date -replace $_;
    }

# Get rid of extra space after converting to DateTime and 
# encode api key as a secure string before writing to file
    $date = Get-Date $date;
    $key  = $element.value | ConvertTo-SecureString -AsPlainText -Force | ConvertFrom-SecureString;

    @{api_key=$key; exp_date=$date} | ConvertTo-Json > dev_key;
}

# Helper function to parse the dev page for apikey and expiration date,
# encode as securestrings and write as json to a file
# using Powershell V7
function parseForKeyV7($dev_page){
# Key can be found under inputFields w/ id "apikey"
    $key = $dev_page.InputFields.findById("apikey").value;

# Sadly could not find a way to convert htm source --> html object in pwsh 7
# so resorted to regex to parse the response content for expiration date
# Similar to V5, run through series of regex replacements to clean up for
# DateTime conversion
    $date = $dev_page.content | sed -n '/Expire[sd]/ {n;n;p}';
    @('\s\(.*', '@', 'th', 'rd', 'nd', 'st') | foreach {
        $date = $date -replace $_;
    }

# Get rid of extra space after converting to DateTime and 
# encode api key as a secure string before writing to file
    $date = Get-Date $date | sed -n '2p';
    $key = ConvertTo-SecureString -AsPlainText -Force $key | ConvertFrom-SecureString;

    @{api_key=$key; exp_date=$date} | ConvertTo-Json > dev_key;
}
# ----------------------------------------------------------------------------
#                   End of old helper functions

#                    Starting of main code
# ----------------------------------------------------------------------------
do{
# Attempt to retrieve user data w/ helper function and 
# Generate the payload for the upcoming PUT request as a json object
    $data = getUserCred;
    $payload = (@{type='auth';
            language='en_US';
            remember='false';
            username=$($data.username);
            password=$data.password; }) | ConvertTo-Json;

# Get the login page taking all redirections and create a session variable
    $login_page = Invoke-WebRequest -Uri $login_url -SessionVariable session -UseBasicParsing;

# Inspect element reveals the login process involves submitting a PUT request
# with the payload (specifying content as application/json form) which is odd
    $oath_page = Invoke-WebRequest -Uri $auth_url -WebSession $session `
        -Method PUT -Body $payload -ContentType 'application/json' -UseBasicParsing;
    $oath_resp = $oath_page.Content | ConvertFrom-Json;

# Repeat process until user succesfully logs in or interrupts script
# additionally notify user if saving a new file
if (!($oath_resp.error)){
    if ($newfile){
        Write-Host -ForegroundColor Green $saving_msg;
    }
    break;
}

Write-Host -ForegroundColor Yellow $failure_msg;
Remove-Item $datafile;
} while(1);

# Dev url can be parsed from the contents of the auth response if successful
# You can then get to the dev page w/ the cookies set in session variable
$dev_url = $oath_resp.response.parameters.uri;
$dev_page = Invoke-WebRequest -Uri $dev_url -WebSession $session -UseBasicParsing; 

parseForKey($dev_page);      

Write-Host -ForegroundColor Green $writing_msg
# ----------------------------------------------------------------------------
