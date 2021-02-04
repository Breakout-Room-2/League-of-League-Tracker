# League-of-League-Tracker

## 1. User Stories (Required and Optional)

**Required Must-have Stories**

 * User can search stats by summoner name
 * User can view recent match history 
 * User can see current ranking,graph of win/loss ratio, and average KDA
 * User can click/tap on a match for more info

**Optional Nice-to-have Stories**

 * Infinite pagination for match history - recent 10 can be continually expanded to more matches
 * Favorites/Recent searches listed on screen for convenience

## 2. Screen Archetypes

 * Summoner Search screen (Login)
   * Input bar for user to specify the summoner to get stats for
   * Favourites/Recent searches bar
 * Summoner Stats screen (Stream/Feed)
   * List of recent matches with small amount of important stats on view
   * Ranking (Tier and LP) details, win/loss pie chart, and average KDA for displayed matches listed near top of screen
   * Should be able to click on match to get more detail (implementation tbd) 
 * Match detail screen / Dropdown? (TBD) 

## 3. Navigation

**Tab Navigation** (Tab to Screen)

 * Summoner search screen
 * ...

**Flow Navigation** (Screen to Screen)

 * Login/Summoner Search screen
   * User inputs summoner name in search bar (or as much as they remember)
   * User clicks on summoner they want to view details/stats for from results
   * Stretch - user can use a box of 'favorite/starred' summoners for quick access
 * Feed/Summoner Stats screen
   * User can scroll up and down a list of recent match summaries
   * User can click on a match for more details (deciding whether to expand details on same screen or navigate to Detail/Match Detail screen tbd)
