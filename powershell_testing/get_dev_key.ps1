# File setup to avoid having to login each time
# Login creds stored as securestring and converted when needed
$datafile = ".\login.json"
$success_msg = "Sucessfully logged in, writing API_key to 'dev_key' file now...";
$found_msg = "`nlogin info file found, just sit back and let the script do its thing...";
$notfound_msg = "`nNo login info file found, you're gonna have to login yourself:";

if(Test-Path $datafile){
    Write-Host $found_msg;

# Convert from json to PSCustomObject
    $data = Get-Content $datafile | ConvertFrom-Json;

# Extract login data from PSCustomObject and decrypt;
    $username = $data.Username | ConvertTo-SecureString 
        | ConvertFrom-SecureString -AsPlainText;
    $password = $data.Password | ConvertTo-SecureString 
        | ConvertFrom-SecureString -AsPlainText;
}

# Prompt user for usernamd and password if not defined
#   store into file to avoid re-prompting next time
else{
    Write-Host $notfound_msg;

    $username = Read-Host -Prompt "Username" 
        | ConvertTo-SecureString -AsPlainText;
    $password = Read-Host -Prompt "Password" -AsSecureString;

# While the data is still in securestring format, store 
# as a json object into a file
    (@{Username=$($username | ConvertFrom-SecureString);
     Password=$($password | ConvertFrom-SecureString);}) 
        | ConvertTo-Json > $datafile;

# Now convert the data back to plainstrings to use in script
    $username = $username | ConvertFrom-SecureString -AsPlainText;
    $password = $password | ConvertFrom-SecureString -AsPlainText;
}

# Define the url for login and authentication pages
$login_url = "developer.riotgames.com/login";
$auth_url = "auth.riotgames.com/api/v1/authorization";

# Generate the payload for the upcoming PUT request as a json object
$payload = (@{type='auth'; 
    language='en_US'; remember='false';
    username=$username;
    password=$password; }) | ConvertTo-Json;

# Get the login page taking all redirections and create a session variable
$login_page = Invoke-WebRequest -Uri $login_url -SessionVariable session;


# Inspect element reveals the login process involves submitting a PUT request
# with the payload (specifying content as application/json form) which is odd
$oath_page = Invoke-WebRequest -Uri $auth_url -WebSession $session `
    -Method PUT -Body $payload -ContentType 'application/json'

# Dev url can be parsed from the contents of the auth response if successful
$dev_url = ($oath_page.Content 
        | ConvertFrom-Json).response.parameters.uri;

# You can finally get the developer page using the now loaded
# session variable
$dev_page = Invoke-WebRequest -Uri $dev_url -WebSession $session; 

Write-Host "`n$success_msg";

# Recover the api key from the dev page by finding w/ id
$dev_page.InputFields.findById("apikey").value | ConvertTo-SecureString -AsPlainText | ConvertFrom-SecureString > dev_key;
