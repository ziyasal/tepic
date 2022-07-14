# JOURNEY TO BECOME EPIC

## REST API SPEC

### Discussion points:

> This points will be included in the high-level graphical design below.

-  Title distinction if multiple titles are developed on the complete backend platform for live games with managed game services, real-time analytics, and LiveOps. This could change the design to consider "Game Titles" by including them in the API domain to target them.

Since we are focsing on the sinle Title at the moment, I will go with having one title.

- Sharding 
Data could be sharded by platform, platform-region, or just region this would help to scale better and access to relevant data faster.  

I will go with having shard information on the URL since it easily allows routing on L7 with a few routing policies by using NGINX module or Envoy filter.

__Shard by Platform__

Samples:
`api.jorneytoepic.com/shards/steam/....`
`api.jorneytoepic.com/shards/xbox/....`
`api.jorneytoepic.com/shards/psn/....`
`api.jorneytoepic.com/shards/stadia/....`

__Shard by Platform-Region__

Samples:
`api.jorneytoepic.com/shards/steam-eu/....`
`api.jorneytoepic.com/shards/xbox-as/....`
`api.jorneytoepic.com/shards/psn-de/....`
`api.jorneytoepic.com/shards/stadia-de/....`


### ACCOUNT INFORMATION
```
GET id/v1/accounts/{id}
HOST api.journeytoepic.com
```

#### REQUEST HEADERS
| Name            | Required | Type   | Description                                  |
|-----------------|----------|--------|----------------------------------------------|
| Authorization   | true     | string | Bearer [JSON Web Token]                      |
| Content-Type:   | true     | string | application/json                             |
| Accept-Encoding | optional | string | Specifies how server will compress responses |                                                                        

#### REQUEST
N/A


#### RESPONSES

- `200 OK`
__AccountInfo__

| Name              | Type    | Description                                                                                        |
|-------------------|---------|----------------------------------------------------------------------------------------------------|
| accountId         | string  | Universally Unique Identifier (UUID)                                                               |
| displayName       | string  | User's name shown on the UI                                                                        |
| preferredLanguage | string  | Usee's preferred language                                                                          |
| region            | string  | two-letter country codes (ISO 3166)                                                                |

- `400 Bad Request`

### GAME MODES
> Game mode should depend on something else?

```
GET shards/{platform-region}/gamemodes/v1/popular
Host: api.{titleID}.journeytoepic.com
Content-Type: application/json
```

#### REQUEST HEADERS
| Name           | Required | Type   | Description                                   |
|----------------|----------|--------|-----------------------------------------------|
| Authorization  | true     | string | Bearer [JSON Web Token]                       |
| Content-Type:   | true     | string | application/json                             |
| Accept-Encoding | optional | string | Specifies how server will compress responses |  

#### REQUEST
__GameModeRequest__
| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| BuildVersion   | string  | previously uploaded build version for which game modes are being requested                         |


#### RESPONSES
- `200 OK`
__GameModeInfo__

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| Gamemode       | string  | specific game mode type                                                                            |
| MaxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                     |
| MinPlayerCount | number  | minimum user count required for this Game Server Instance to continue (usually 1)                  |
| StartOpen      | boolean | whether to start as an open session, meaning that players can matchmake into it (defaults to true) |
- `400 Bad Request`

<!-- ### PLAYER INFO
```
# HTTP
GET https://api.journeytoepic.com/v1/shards/{shard}/players/{id}`
```

#### REQUEST HEADERS

| Name           | Required | Type   | Description             |
|----------------|----------|--------|-------------------------|
| Authorization  | true     | string | Bearer [JSON Web Token] |  

#### REQUEST

__GameModeRequest__
| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| BuildVersion   | string  | previously uploaded build version for which game modes are being requested                         |
| MaxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                     |


#### RESPONSES

- `200 OK`
__GameModeInfo__

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| Gamemode       | string  | specific game mode type                                                                            |
| MaxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                     |
| MinPlayerCount | number  | minimum user count required for this Game Server Instance to continue (usually 1)                  |
| StartOpen      | boolean | whether to start as an open session, meaning that players can matchmake into it (defaults to true) |

- `400 Bad Request` -->

### PAGINATION OPTIONS

 -  Offset based

 -  Cursor based

 I would go with cursor based since its enables to get accurate pages.


## PERSITENCE LAYER

### SHARDING



### Accessing to sharded data

When accessing the sharded data below options would be considered, each has its own pros and cons.

!TODO: pros cons table and preferred approach

- __NGINX module,  ENVOY filter or Custom Routing Layer__
This would be an option to locate relevent shards where servers and user data is located.

- __SHARD Locator in the code__
This would be an option to locate to the relevant shards to get data from user where data is located.
This option allows for accessing sharded data from right in the code.

### Caching
TODO


## HIGH LEVEL DESIGN

![](/RestAPI/high-level-design.png)