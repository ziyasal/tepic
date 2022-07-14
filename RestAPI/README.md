# JOURNEY TO BECOME EPIC

## REST API SPEC

### ACCOUNT INFORMATION
```
# HTTP
GET id/v1/accounts/{id}
HOST api.journeytoepic.com
```

#### REQUEST HEADERS
| Name           | Required | Type   | Description             |
|----------------|----------|--------|-------------------------|
| Authorization  | true     | string | Bearer [JSON Web Token] |
| Content-Type:  | true     | string | application/json        |                                                                       

#### REQUEST
__GameModeRequest__
| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| BuildVersion   | string  | previously uploaded build version for which game modes are being requested                         |

#### RESPONSES

- `200 OK`
__AccountInfo__

| Name              | Type    | Description                                                                                        |
|-------------------|---------|----------------------------------------------------------------------------------------------------|
| accountId         | string  | Universally Unique Identifier (UUID)                                                               |
| displayName       | string  |                                                                                                    |
| preferredLanguage | number  |                                                                                                    |
| region            | string  | two-letter country codes (ISO 3166)                                                                |

- `400 Bad Request`

### GAME MODES
> Game mode should depend on something else?

```
GET shards/{platform-region}/gamemodes/v1/popular
Host: api.journeytoepic.com
Content-Type: application/json
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

### SHARDING OPTIONS

__Shard by Platform__

Samples:
`api.jorneytoepic.com/v1/shards/steam/....`
`api.jorneytoepic.com/v1/shards/xbox/....`
`api.jorneytoepic.com/v1/shards/psn/....`

__Shard by Platform-Region__

Samples:
`api.jorneytoepic.com/v1/shards/steam-eu/....`
`api.jorneytoepic.com/v1/shards/xbox-as/....`
`api.jorneytoepic.com/v1/shards/psn-de/....`

### Accessing to sharded data

#### Routing by extracting shard information from the URL

- __NGINX module,  ENVOY filter or Custom Routing Layer__
This would be an option to locate relevent shards where servers and user data is located.

- __SHARD Locator in the code__
This would be an option to locate to the relevant shards to get data from user where data is located.
This option allows for accessing sharded data from right in the code.

### Caching
TODO