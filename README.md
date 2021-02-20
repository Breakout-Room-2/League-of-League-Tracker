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

## 4. Wireframes
<img src='https://github.com/Breakout-Room-2/lol-tracker/blob/master/wireframes.jpg' title='Wireframes' width='' alt='Wireframes' />

## 5. Schema

### Models

#### Summoner
    | accountID | String    | Encrypted account ID
    | name      | Sring     | Summoner name
    | id        | String    | Encrypted summoner ID

#### Champion
    | championID    | int   | Champion ID
    | championLevel | int   | Mastery Level for a champion
    | championPoints| int   | Mastery Points for a champion

#### Matchlist
    | matches   | List [MatchReferenceDTO]  | List of match references

#### MatchReferenceDTO
    | gameID    | long      | game ID unique to match
    | role      | String    | role of user in match 
    | champion  | int       | (same as championID)
    | queue     | int       | type of queue (e.g. ranked, draft)
    | timestamp | long      | Date of match (in UnixEpochMilliseconds)

#### Match
    | queueId       | int                   | type of queue and map
    | gameCreation  | long                  | (same as timestamp)
    | gameDuration  | long                  | match duration in seconds
    | participants  | list[particpantDTO]   | list of match participants

#### participantDTO
    | particpantID  | int                   | participant ID
    | championID    | int                   | (same as championID)
    | teamId        | int                   | 100 for blue, 200 for red side
    | spell1Id      | int                   | first summoner spell ID
    | spell2Id      | int                   | second summoner spell ID
    | stats         | participantStatsDTO   | 

#### participantStatsDTO
A bit of long model, contains stats relavant to each summoner in game such as
KDA, cs, damage dealt, vision score, runes, etc.

### Networking

### Existing API Endpoints
