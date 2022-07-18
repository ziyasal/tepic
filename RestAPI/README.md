# JOURNEY TO BECOME EPIC

## HIGH LEVEL DESIGN

### SCALING
Below design shows high level design with involved components on how to scale system millions of users.
A few key points on how to scale:

#### SERVICE LAYER
- Load Balancer with multiple Web Servers
- Horizontal scaling
  * Designing web servers to be stateless (except persistent connections servers)
  * Storing user sessions on de-centralized data store or persistent cache solutions
  * Scaling out using commodity machines is more cost efficient and results in higher availability than scaling up a single server on more expensive hardware, called Vertical Scaling.

#### PERSISTENCE LAYER
- Primary — Replica, replicated database design, with federation (or functional partitioning) splits up databases by function) and sharding (data could be sharded by platform, platform-region)
  * Federation helps in less read and write traffic to each database and therefore less replication lag. However we'll need to design our application logic to determine which database to read.
  * Sharding distributes data across different databases such that each database can only manage a subset of the data. Similar to Federation sharding results in less read and write traffic, less replication, and more cache hits. Index size is also reduced, which generally improves performance with faster queries. Also Like federation, there is no single central master serializing writes, allowing to write in parallel with increased throughput.

ℹ️ When we create sharded database solution how to access data is important. We could follow the "Data-dependent routing" approach here, which is a fundamental pattern when working with sharded databases. It enables to use of the data in a query to route the request to an appropriate database. If the sharding key is not part of the query then the request context may also be used to route the request. 

##### MY APPROACH
For example, data could be sharded by platform, platform + region, or region.  In the below design, I will go with including the "platform" and "region" part of the URL, since it easily allows "Data-dependent routing" with a few routing policies (by computing shard key) by using NGINX Lua scripts or an Envoy filter, similar one. This could also be done in application code as well.

#### CACHING 
Databases often benefit from a uniform distribution of reads and writes across its partitions. Popular items can skew the distribution, causing bottlenecks. Putting a cache in front of a database can help absorb uneven loads and spikes in traffic. Here are few options to consider:
   * Caching at the database query level
   * Caching at the object level like (player sessions, player activities etc)

Cache-aside model:
1) Look for entry in cache, resulting in a cache miss
2) Load entry from the database
3) Add entry to cache
4) Return entry

Write-through model:
1) Application adds/updates entry in cache
2) Cache synchronously writes entry to data store
3) Return

##### MY APPROACH
ℹ️  I would prefer to use cache aside and write-through in conjunction, and also set a time-to-live (TTL) to mitigate stale data cases. This conjunction would help mitigating the case  "when a new node is created due to failure or scaling, having the new node does not cache entries until the entry is updated in the database".

#### DATABASE
- Denormalization
  Once data becomes distributed with techniques such as federation and sharding, managing joins across data centers further increases complexity. Denormalization might circumvent the need for such complex joins.
- SQL tuning
 * Tightening up schemas
 * Using good indices
 * Avoidig expensive joins (denormalization could help here as I explained above)
 * Partitioning tables
 * Tuning the query cache

#### FURTHER DETAILS (not in the scope of the work)
- The Analytics Database (Redshift, BgQuery, Snowflake  or self hosted Clickhouse) could be used for a data warehousing solution for game analytics.

### ARCHITECTURAL DIAGRAM

![](/RestAPI/high-level-design.png)


## REST API SPEC

It follows JSON:API spec — https://jsonapi.org/

### Discussion points:

-  Title distinction if multiple titles are developed on the complete backend platform for live games with managed game services, real-time analytics, and LiveOps. This could change the design to consider "Game Titles" by including them in the API domain to target them.

ℹ️ I will go with having title distinction from the beginning for extensibility purposes.

### AUTHORIZATION
In order for the API to accept a request, users will need to send an API key via the Authorization header.

Example authorization string: `Authorization: Bearer $API_KEY`

### CONTENT NEGOTIATION
Users need to specify in the headers that we accept content in the format that the API returns. Clients using the API should specify that they accept responses using the `application/vnd.api+json` format. For convenience, API will also accept `application/json` since it is the default for many popular client libraries.

Example content type string:  `Accept: application/vnd.api+json`

### RESPONSES
All server responses will be in [JSON-API](https://jsonapi.org/format/) format and contain a root JSON object.

Each response will contain at least one of the following top-level members:

- `data` : the response’s “primary data”
- `errors` : an array of error objects

A response may contain any of these top-level members:

- `links`: a links object related to the primary data.
- `included`: an array of resource objects that are related to the primary data and/or each other (“included resources”).
- `meta`: only `page` information included like `per-page`, `from` cursor, `to` cursor and `has-more`.

If a document does not contain a top-level data key, the included array will not be present either.

### PAGINATION OPTIONS

 ℹ️ I prefer to go with cursor based since its enables to get accurate pages and avoid cases like "if an item from a prior page is deleted while the client is paginating, all subsequent results will be shifted forward by one".

There may be 4 types of links in the paged response depends on response payload: `prev`, `next`, `first`, and `last`.

### REQUEST HEADERS
| Name            | Required | Type   | Description                                           |
|-----------------|----------|--------|-------------------------------------------------------|
| Authorization   | true     | string | Bearer [JSON Web Token]                               |
| Accept          | true     | string | application/vnd.api+json                              |
| Accept-Encoding | false    | string | Specifies how server will compress responses, ie gzip |  

### ACCOUNT INFORMATION

In the design below, users have one account and multiple `Title Player Profile`s, which enables them to have specific profiles with various settings for each title. 

Since we define the above relationship, while fetching an account, we could request to include title player profiles in the response payload as a compound document. In the `Title Player Profile`,  some profile information like `current game mode` for specific title would be returned (please see an example response payload below).

This way users' combined account details together with profiles when needed could be fetched.

__Option 1) Filtering accounts by Account IDs:__
```
# NOTES:
# It enables fetching multiple accounts if accountIds passed as comma seperated
# Page size parameter "page[size]=20" is optional, it default page size set on the server if not specified

POST  /id/v1/accounts?filter[accountIds]={accountId} HTTP/1.1
HOST api.journeytoepic.com
Accept: application/vnd.api+json
Authorization: Bearer $API_KEY_HERE
```

__Option 2) Getting account by Account ID:__

```
POST  /id/v1/accounts/{accountId} HTTP/1.1
HOST api.journeytoepic.com
Accept: application/vnd.api+json
Authorization: Bearer $API_KEY_HERE

```

#### REQUEST PARAMETERS

 TODO===

#### RESPONSES

- `200 OK`
__AccountInfo__

> Only relevant fields included the rest is omitted. 

| Name              | Type     | Description                                                                                        |
|-------------------|----------|----------------------------------------------------------------------------------------------------|
| id                | string   | Universally Unique Identifier (UUID)                                                               |
| displayName       | string   | User's name shown on the UI                                                                        |
| preferredLanguage | string   | Usee's preferred language                                                                          |
| countryCode       | string   | two-letter country codes (ISO 3166)                                                                |
| continentCode     | string   | two-letter continent code                                                                          |
| linkedAccounts    | []       | "omitted"                                                                                          |

> More fields are omitted for brevity



Since we would like to get game mode with user account as well, then response payload will be a "compound object" that includes titlePlayerProfiles that associated with the uiser account.

__TitlePlayerProfile__

> Only relevant fields included the rest is omitted. 

| Name              | Type    | Description                                                                                        |
|-------------------|---------|----------------------------------------------------------------------------------------------------|
| id                | string  | Universally Unique Identifier (UUID)                                                               |
| titleId           | string  | The title that the profile is associated                                                           |
| currentGameMode   | string  | The current game mode for the title that the profile is associated                                 |

__Response for "Option 1 — Filtering accounts by Account IDs":__

Response is a “compound document” which includes titlePlayerProfile in the response as well, the response payload document for a single account looks like below:

```json
{
    "meta": {
        "page": {
            "per-page": 10,
            "from": "before_cursor",
            "to": "after_cursor",
            "has-more": false
        }
    },
    "links": {
        "first": "/id/v1/accounts?filter[accountIds]={accountId}",
        "prev": "/id/v1/accounts?filter[accountIds]={accountId}&page[before]={before_cursor}",
        "next": "",
    },
    "data": [
        {
            "type": "gameMode",
            "id": "ID_HERE",
            "attributes": {
                "id": "ID_HERE",
                "displayName": "bug the system",
                "preferredLanguage": "en",
                "countryCode": "de",
                "continentCode": "eu"
            },
            "relationships": {
                "titlePlayerProfiles": {
                    "links": {
                        "self": "/{titleId}/id/v1/accounts/{accountId}/relationships/titlePlayerProfiles",
                        "related": "/{titleId}/id/v1/accounts/{accountId}/titlePlayerProfiles"
                    },
                    "data": [
                        {
                            "type": "titlePlayerProfile",
                            "id": "1"
                        }
                    ]
                }
            }
        }
    ],
    "included": [
        {
            "type": "titlePlayerProfile",
            "id": "1",
            "attributes": {
                "name": "name here",
                "titleId": "{titleId}",
                "buildVersion": "holds build version of the game that the profile is associated",
                "currentGameMode": "squad",
            },
            "links": {
                "self": "/titlePlayerProfiles/1"
            }
        }
    ]
}
```

__Response for "Option 2" — Getting account by Account ID:__

Response is a “compound document” which includes titlePlayerProfile in the response as well,  the response payload document for a single account looks like below:

```json
{
    "links": {
        "self": "/{titleId}/id/v1/accounts/{accountId}?include=titlePlayerProfiles"
    },
    "data": {
        "type": "gameMode",
        "id": "{accountId}",
        "attributes": {
            "displayName": "Bug the System",
            "preferredLanguage": "en",
            "countryCode": "de",
            "continentCode": "eu",
            "linkedAccounts": ["omitted"]
        },
        "relationships": {
            "titlePlayerProfiles": {
                "links": {
                    "self": "/{titleId}/id/v1/accounts/{accountId}/relationships/titlePlayerProfiles",
                    "related": "/{titleId}/id/v1/accounts/{accountId}/titlePlayerProfiles"
                },
                "data": [
                    {
                        "type": "titlePlayerProfile",
                        "id": "1"
                    }
                ]
            }
        }
    },
    "included": [
        {
            "type": "titlePlayerProfile",
            "id": "1",
            "attributes": {
                "titleId": "{titleId}",
                "currentGameMode": "squad",
            },
            "links": {
                "self": "/titlePlayerProfiles/1"
            }
        }
    ]
}
```

- `400 Bad Request`

> TODO


### GAME MODES
> Game mode should depend on something else?

```
# NOTES:
# To sort in descending order "sort=-rank" could be passed, without minus (U+002D HYPEN-MINUS "-") sort happens in ascending order
# # Page size parameter "page[size]=20" is optional, it default page size set on the server if not specified

GET /game/v1/gamemodes?sort=rank&platform={platform}&region={region}&titleId={titleId}&buildVersion={buildVersion}
Host: api.journeytoepic.com
Accept: application/vnd.api+json
Authorization: Bearer $API_KEY_HERE
```

#### REQUEST
PARAMETETERS

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| buildVersion   | string  | previously uploaded build version for which game modes are being requested                         |
| platform       | string  | platform where player plays on                                                                     |
| region         | string  | players region represented as two-letter country codes (ISO 3166)                                  |
| sort           | string  | to sort resorce collections according to one or more criteria ("sort fields")                      |
| titleId        | string  | title to fetch associated resources (this could have been a header as well like `X-TITLE-ID`)      |


#### RESPONSES

__GameModeInfo__

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| name           | string  | name of the game mode                                                                              |
| maxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                     |
| minPlayerCount | number  | minimum user count required for this Game Server Instance to continue (usually 1)                  |
| startOpen      | boolean | whether to start as an open session, meaning that players can matchmake into it (defaults to true) |
| description    | string  | description of the game mode                                                                       |
| titleId        | string  | UUID that identifies the studio and game                                                           |
| buildVersion   | string  | holds the build version of the game that the mode is associated                                    |

> More fields are omitted for brevity

Response contains links to `prev` and the `next` pages. 
`prev` will be empty for the initial response and `self` contains the link that generated the current response document.

```json
{
    "meta": {
        "page": {
            "total": 100
        }
    },
    //
    "relationships": {},
    "links": {
        "self": "/game/v1/gamemodes?sort=rank&platform={platform}&region={region}&titleId={titleId}&buildVersion={buildVersion}",
        "first": "link to the first document",
        "last": "link to the last document",
        "prev": "/game/v1/gamemodes?sort=rank&platform={platform}&region={region}&page[before]={before_cursor}&titleId={titleId}&buildVersion={buildVersion}",
        "next": "/game/v1/gamemodes?sort=rank&platform={platform}&region={region}&page[after]={after_cursor}&titleId={titleId}&buildVersion={buildVersion}"
    },
    "data": [
        {
            "type": "gameMode",
            "id": "ID_HERE",
            "attributes": {
                "id": "ID_HERE",
                "name": "solo",
                "description": "Single player discovery mode",
                "maxPlayerCount": 50,
                "minPlayerCount": 1,
                "startOpen": true,
                "titleId": "{titleId}"
            }
        }
    ]
}
```

#### ERROR CASES

- Invalid Parameter

__Page Size:__
```json
{
  "errors": [{
    "title": "Invalid Parameter.",
    "detail": "page[size] must be a positive integer; got 0",
    "source": { "parameter": "page[size]" },
    "status": "400"
  }]
}
```

__Region:__
```json
{
  "errors": [{
    "title": "Invalid Parameter.",
    "detail": "Region must be two-letter country code (ISO 3166); got USA",
    "source": { "parameter": "region" },
    "status": "400"
  }]
}
```

__Platform:__
```json
{
  "errors": [{
    "title": "Invalid Parameter.",
    "detail": "Platform must be a valid one (ie: psn, xbox, stadia, ); got USA",
    "source": { "parameter": "platform" },
    "status": "400"
  }]
}

__Sort:__
```json
{
  "errors": [{
    "title": "Invalid Parameter.",
    "detail": "Sort value must be a valid field; got QWERTY",
    "source": { "parameter": "sort" },
    "status": "400"
  }]
}
```

## Resources
- https://jsonapi.org/format/
- https://jsonapi.org/profiles/ethanresnick/cursor-pagination/#:~:text=Cursor%2Dbased%20pagination%20(aka%20keyset,be%20shifted%20forward%20by%20one.
- https://dev.epicgames.com/docs/services/en-US/WebAPIRef/AuthWebAPI/index.html#accountinformation
- https://github.com/mautini/pubgjava/blob/b875556acc/pubg-java/src/main/java/com/github/mautini/pubgjava/model/player/Player.java
- https://github.com/weeco/fortnite-client/blob/develop/src/fortnite-client.ts
- https://na.battlegrounds.pubg.com/2022/01/12/cross-platform-play-explained/
- https://github.com/SkYNewZ/fortnite-api/blob/master/lib/config.js