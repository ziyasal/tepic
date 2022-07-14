
# REST API
## DATA MODELING

__GameModeInfo__

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| Gamemode       | string  | specific game mode type                                                                            |
| MaxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                     |
| MinPlayerCount | number  | minimum user count required for this Game Server Instance to continue (usually 1)                  |
| StartOpen      | boolean | whether to start as an open session, meaning that players can matchmake into it (defaults to true) |

## SHARDING OPTIONS

### Platform
Samples:
`api.jorneytoepic.com/v1/shards/steam/....`
`api.jorneytoepic.com/v1/shards/xbox/....`
`api.jorneytoepic.com/v1/shards/psn/....`

### Platform-Region
Samples:
`api.jorneytoepic.com/v1/shards/steam-eu/....`
`api.jorneytoepic.com/v1/shards/xbox-as/....`
`api.jorneytoepic.com/v1/shards/psn-de/....`


### ACCESSSING TO SHARDED DATA
#### Routing by extracting shard information from the URL paramseter

- NGINX module or ENVOY filter
This would enables to locate to the relevent shards where user data is located.

- SHARD Locator in the code
This would enable to the locate shards to get data from user where data is located.


## API

### PLAYER PROFILE
`https://api.jorneytoepic.com/v1/shards/{shard}/players/{id}`

### GAME MODES

Game mode should depend of something else?
`https://api.jorneytoepic.com/v1/gamemodes`