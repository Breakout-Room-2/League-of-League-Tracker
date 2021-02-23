# Script-wide definitions for datafile location, messages, and urls
$newfile        = 1;
$datafile       = "powershell_testing\login.json";
$found_msg      = "`nLogin info found, just sit back and let the script do it's thing";
$notfound_msg   = "`nNo login file, you're gonna have to input some values yourself:";
$saving_msg     = "`nLogin successful, writing login info to 'login.json' file...";
$failure_msg    = "`nLogin failed, rerun script to enter credentials again...";
$writing_msg    = "writing API key to 'apikey.properties' file...";
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

# Helper function to parse the dev page for apikey and expiration date,
# and write to apikey.properties file
function writeApiKey($dev_page){
# Key can be found under inputFields w/ id "apikey"
    $key = $dev_page.InputFields.findById("apikey").value;

# Sadly could not find a way to convert htm source --> html object in pwsh 7
# so resorted to regex to parse the response content for expiration date
# A series of regex replacements removes extra characters and ordinalls for 
# a smoother conversion to DateTime
    $date = $dev_page.content | sed -n '/Expire[sd]/ {n;n;p}';
    @('\s\(.*', '@', 'th', 'rd', 'nd', 'st') | foreach {
        $date = $date -replace $_;
    }

# Get rid of extra space after converting to DateTime and 
    $date = Get-Date $date | sed -n '2p';

    Write-Output "API_KEY = `"$key`"`nEXP_DATE = `"$date`"" > apikey.properties;
}
# ----------------------------------------------------------------------------
#                   End of Helper Functions

#                    Starting of main code
# ----------------------------------------------------------------------------

# Check if apikey.properties file already exists, and making sure key hasn't
# expired - skipping the main body of code if the key is fine
if (Test-Path apikey.properties){
    $date = Get-Date ((sed -n "/EXP_DATE/p" apikey.properties) -replace '"' -replace "EXP_DATE = ");
    $time_left = ($date) - (Get-Date);

    if ($time_left -gt 0) {
        return;
    }

    Write-Host "Key expired";
    edge "https://developer.riotgames.com/";
    Read-Host "Press enter once you've regenrated the API key";
}

do{
# Attempt to retrieve user data w/ helper function and 
# Generate the payload for the upcoming PUT request as a json object
    $data = getUserCred;
    $payload = (@{type='auth';
            language='en_US';
            remember='false'; username=$($data.username);
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

# Finally write the api key and expiration date to apikey.properties file
writeApiKey($dev_page);      

Write-Host -ForegroundColor Green $writing_msg
# ----------------------------------------------------------------------------
