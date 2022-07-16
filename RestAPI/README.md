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

### PAGINATION OPTIONS
 -  Offset pagination
 -  Cursor pagination
    There are 4 types of links in the paged response: `prev`, `next`, `first`, and `last`. `prev` and 

 ℹ️ I would go with cursor based since its enables to get accurate pages and avoid cases like "if an item from a prior page is deleted while the client is paginating, all subsequent results will be shifted forward by one".

### ACCOUNT INFORMATION

__Option 1)__
```
# It enables fetching multiple accounts if accountIds passed as comma seperated
# Page size parameter is optional, it uses default one if not specified.

POST  {titleId}/id/v1/accounts?filter[accountIds]={accountId}&page[size]=20
HOST api.journeytoepic.com
```

__Option 2)__

```
POST  {titleId}/id/v1/accounts/{accountId}
HOST api.journeytoepic.com
```

#### REQUEST HEADERS
| Name            | Required | Type   | Description                                           |
|-----------------|----------|--------|-------------------------------------------------------|
| Authorization   | true     | string | Bearer [JSON Web Token]                               |
| Accept-Encoding | optional | string | Specifies how server will compress responses, ie gzip |
| Accept          | true     | string | application/vnd.api+json                              |                                                 

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
| country           | string  | two-letter country codes (ISO 3166)                                                                |
| continent         | string  | two-letter continent code                                                                          |


__Response for "Option 1":__
```json
{
    "meta": {
        "page": {
            "total": 50
        }
    },
    "relationships": {},
    "links": {
        "prev": "{titleId}/game/v1/gamemodes?sort=popularity&page[before]={before_cursor_here}&page[size]=10",
        "next": "{titleId}/game/v1/gamemodes?sort=popularity&page[after]={before_cursor_here}&page[size]=10"
    },
    "data": [
        {
            "type": "gameMode",
            "id": "ID_HERE",
            "attributes": {
                "id": "ID_HERE",
                "created": "",
                "displayName": "bug the system",
                "preferredLanguage": "en",
                "country": "de",
                "continent": "eu"
            }
        }
    ]
}
```

__Response for "Option 2":__
```json
{
    "data": {
        "type": "gameMode",
        "id": "ID_HERE",
        "attributes": {
            "id": "ID_HERE",
            "acountId": "ACCOUNT_ID_HERE",
            "displayName": "bugthesystem",
            "preferredLanguage": "en",
            "country": "de",
            "continent": "eu"
        }
    }
}
```

- `400 Bad Request`

> TODO


### GAME MODES
> Game mode should depend on something else?

```
GET {titleId}/game/v1/gamemodes?sort=rank&platform={platform}&region={region}&page[size]=20
Host: api.journeytoepic.com
Accept: application/vnd.api+json                              |   
```

#### REQUEST HEADERS
| Name            | Required | Type   | Description                                           |
|-----------------|----------|--------|-------------------------------------------------------|
| Authorization   | true     | string | Bearer [JSON Web Token]                               |
| Accept          | true     | string | application/vnd.api+json                              |
| Accept-Encoding | optional | string | Specifies how server will compress responses, ie gzip |  

#### REQUEST
__GameModeRequest__
| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| BuildVersion   | string  | previously uploaded build version for which game modes are being requested                         |
| Platform       | string  | previously uploaded build version for which game modes are being requested                         |
| Country        | string  | previously uploaded build version for which game modes are being requested                         |
| ServerRegion   | string  | previously uploaded build version for which game modes are being requested                         |


#### RESPONSES
- `200 OK`
__GameModeInfo__

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| Gamemode       | string  | specific game mode type                                                                            |
| MaxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                     |
| MinPlayerCount | number  | minimum user count required for this Game Server Instance to continue (usually 1)                  |
| StartOpen      | boolean | whether to start as an open session, meaning that players can matchmake into it (defaults to true) |

Response contains links to `prev` and the `next` pages.

```json
{
    "meta": {
            "page": { "total": 50 }
        },
    "relationships":{}
    "links" : {
            "prev" : "{titleId}/game/v1/gamemodes?sort=rank&platform={platform}&region={region}&page[before]={before_cursor_here}&page[size]=20",
            "next" : "{titleId}/game/v1/gamemodes?sort=rank&platform={platform}&region={region}&page[before]={after_cursor_here}&page[size]=20"
    },
     "data": [{
             "type": "gameMode",
             "id": "ID_HERE",
             "attributes":{
                 "id": "ID_HERE",
                 "createdAt":"",
                 "name": "solo",
                 "maxPlayerCount": 50,
                 "minPlayerCount": 1,
                 "startOpen": true
             }

        }],
    
}
```

#### ERROR CASES

- `400 Bad Request`

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

## Resources
- https://jsonapi.org/format/
- https://jsonapi.org/profiles/ethanresnick/cursor-pagination/#:~:text=Cursor%2Dbased%20pagination%20(aka%20keyset,be%20shifted%20forward%20by%20one.
- https://dev.epicgames.com/docs/services/en-US/WebAPIRef/AuthWebAPI/index.html#accountinformation
- https://github.com/mautini/pubgjava/blob/b875556acc/pubg-java/src/main/java/com/github/mautini/pubgjava/model/player/Player.java
- https://github.com/weeco/fortnite-client/blob/develop/src/fortnite-client.ts
- https://na.battlegrounds.pubg.com/2022/01/12/cross-platform-play-explained/
- https://github.com/SkYNewZ/fortnite-api/blob/master/lib/config.js