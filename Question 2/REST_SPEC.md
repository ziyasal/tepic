# REST API SPEC

The API is designed to implement the required features of the JSON-API specification. Data returned from each of the API’s endpoints will be in JSON-API format. Additional information about the specification could be found [here]( https://jsonapi.org/).

<!-- TOC -->

- [REST API SPEC](#rest-api-spec)
    - [AUTHORIZATION](#authorization)
    - [CONTENT NEGOTIATION](#content-negotiation)
    - [RESPONSE COMPRESSION](#response-compression)
    - [RATE-LIMITING](#rate-limiting)
    - [RESPONSE FORMAT](#response-format)
    - [PAGINATION OPTIONS](#pagination-options)
    - [REQUEST HEADERS](#request-headers)
    - [ACCOUNT API](#account-api)
        - [RESPONSES](#responses)
        - [OPTIONS TO FETCH AN ACCOUNT](#options-to-fetch-an-account)
            - [OPTION 1 — Filtering accounts by Account IDs](#option-1--filtering-accounts-by-account-ids)
                - [REQUEST PARAMETERS](#request-parameters)
                - [EXAMPLE JSON RESPONSE](#example-json-response)
            - [OPTION 2 — Getting account by an Account ID](#option-2--getting-account-by-an-account-id)
                - [REQUEST PARAMETERS](#request-parameters)
                - [EXAMPLE JSON RESPONSE](#example-json-response)
    - [GAME MODES API](#game-modes-api)
        - [REQUEST PATH AND QUERY PARAMETERS](#request-path-and-query-parameters)
        - [RESPONSES](#responses)
    - [ERROR CASES](#error-cases)

<!-- /TOC -->

__Discussion points:__

-  Title distinction if multiple titles are developed on the complete backend platform for live games with managed game services, real-time analytics, and LiveOps. This could change the design to consider "Game Titles" by including them in the API to target them.

ℹ️ I will go with having title separation from the beginning for extensibility purposes.

## AUTHORIZATION
In order for the API to accept a request, users will need to send an API key via the Authorization header.

Example authorization string: `Authorization: Bearer $API_KEY`

## CONTENT NEGOTIATION
Users need to specify in the headers that we accept content in the format that the API returns. Clients using the API should specify that they accept responses using the `application/vnd.api+json` format. For convenience, API will also accept `application/json` since it is the default for many popular client libraries.

Example content type string:  `Accept: application/vnd.api+json`

## RESPONSE COMPRESSION
Clients can specify the header `Accept-Encoding: gzip`, and the server will compress responses. Responses will be returned with `Content-Encoding: gzip`.

Given the size of response payload, this can have significant performance benefits.

## RATE-LIMITING
Rate limiting is a critical component of an API product’s scalability. So we might need to consider the following when setting up API rate limits. 
So when clients receive an `HTTP 429` response (rate limit reached), this would help clients to understand how to manage the situation when it occurs, by taking a look at the following headers:

| HTTP Header                 | Description                                                                                 |
|-----------------------------|---------------------------------------------------------------------------------------------|
| X-RateLimit-Limit           | Request limit per hour                                                                      |
| X-RateLimit-Remaining       | The number of requests left for the time window                                             |
| X-Rate-Limit-Reset          | The remaining window before the rate limit resets in UTC epoch seconds                      |
| Retry-After                 | he number of seconds before the next time-frame where you'll be able to retry your API call |

## RESPONSE FORMAT
All server responses will be in [JSON-API](https://jsonapi.org/format/) format and contain a root JSON object.

Each response will contain at least one of the following top-level members:

- `data` : the response’s “primary data”
- `errors` : an array of error objects

A response may contain any of these top-level members:

- `links`: a links object related to the primary data.
- `included`: an array of resource objects that are related to the primary data and/or each other (“included resources”).
- `meta`: only `page` information included like `per-page`, `from` cursor, `to` cursor and `has-more`.

If a document does not contain a top-level data key, the included array will not be present either.

## PAGINATION OPTIONS

 ℹ️ I prefer to go with cursor based paging approach since its enables to get accurate pages and avoid cases like "if an item from a prior page is deleted while the client is paginating, all subsequent results will be shifted forward by one".

There may be 4 types of links in the paged response depends on response payload: `prev`, `next`, `first`, and `last`.

## REQUEST HEADERS
| Name            | Required | Type   | Description                                           |
|-----------------|----------|--------|-------------------------------------------------------|
| Authorization   | true     | string | Bearer [JSON Web Token]                               |
| Accept          | true     | string | application/vnd.api+json                              |
| Accept-Encoding | false    | string | Specifies how server will compress responses, ie gzip |  


## ACCOUNT API
An API that reports the user’s details like region and title player profiles that contains `game mode`. In the design below, users have one account and multiple `Title Player Profile`s, which enables them to have specific profiles with various settings for each title. 

Since we define the above relationship, while fetching an account, we could request to include `title player profiles` in the response payload as part of a compound document. In the `Title Player Profile`,  some profile information like `current game mode` for specific title would be returned (please see an example response payload below).

This way users' combined account details together with profiles when needed could be fetched.

### RESPONSES

- `200 OK`
__AccountInfo__

> More fields are omitted for brevity

| Name              | Type     | Description                                                                                        |
|-------------------|----------|----------------------------------------------------------------------------------------------------|
| id                | string   | Universally Unique Identifier (UUID)                                                               |
| displayName       | string   | User's name shown on the UI                                                                        |
| preferredLanguage | string   | Usee's preferred language                                                                          |
| countryCode       | string   | two-letter country codes (ISO 3166)                                                                |
| continentCode     | string   | two-letter continent code                                                                          |
| linkedAccounts    | []       | "omitted"                                                                                          |


Since we would like to get game mode with user account as well, then response payload will be a "compound object" that includes titlePlayerProfiles that associated with the user account.

__TitlePlayerProfile__
 
> More fields are omitted for brevity

| Name              | Type    | Description                                                                                        |
|-------------------|---------|----------------------------------------------------------------------------------------------------|
| id                | string  | Universally Unique Identifier (UUID)                                                               |
| titleId           | string  | The title that the profile is associated                                                           |
| currentGameMode   | string  | The current game mode for the title that the profile is associated                                 |


### OPTIONS TO FETCH AN ACCOUNT

User accounts could be fetched either by `filtering accounts by Account IDs` or `getting account directly by an Account ID`. 

Please see below for details of both options:

####  OPTION 1 — Filtering accounts by Account IDs
```
# NOTES:
# It enables fetching multiple accounts if accountIds passed as comma separated
# Page size parameter "page[size]=20" is optional, it uses default page size set on the server if not specified

POST  /id/v1/accounts?filter[accountIds]={accountId}&include=titlePlayerProfiles HTTP/1.1
HOST api.epic-journey.com
Accept: application/vnd.api+json
Authorization: Bearer $API_KEY_HERE
```

##### REQUEST PARAMETERS

| Name                | Type    | Description                                              |
|---------------------|---------|----------------------------------------------------------|
| filter[accountIIds] | string  | comma separated accountIds to fetch                      |
| include             | string  | comma separated relationships of the resource to include |

##### EXAMPLE JSON RESPONSE

Response is a “compound document” which includes `titlePlayerProfile` in the response as well, the response payload document for a single account looks like below:

```json
{
    "meta": {
        "page": {
            "per-page": 10,
            "from": "before_cursor",
            "to": "after_cursor",
            "has-more": false,
            "total": 1
        }
    },
    "links": {
        "first": "/id/v1/accounts?filter[accountIds]={accountId}?include=titlePlayerProfiles",
        "prev": "/id/v1/accounts?filter[accountIds]={accountId}?include=titlePlayerProfiles&page[before]={before_cursor}",
        "next": "",
    },
    "data": [
        {
            "type": "account",
            "id": "ID_HERE",
            "attributes": {
                "id": "ID_HERE",
                "displayName": "bug the system",
                "preferredLanguage": "en",
                "countryCode": "de",
                "continentCode": "eu",
                "linkedAccounts": ["omitted"]
            },
            "relationships": {
                "titlePlayerProfiles": {
                    "links": {
                        "self": "/id/v1/accounts/{accountId}/relationships/titlePlayerProfiles",
                        "related": "/id/v1/accounts/{accountId}/titlePlayerProfiles"
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
                "name": "Epic Journey Profile",
                "titleId": "{titleId}",
                "buildVersion": "holds build version of the game that the profile is associated",
                "currentGameMode": "squad"
            },
            "links": {
                "self": "/id/v1/accounts/{accountId}/titlePlayerProfiles/1"
            }
        }
    ]
}
```

#### OPTION 2 — Getting account by an Account ID

```
POST  /id/v1/accounts/{accountId}?include=titlePlayerProfiles HTTP/1.1
HOST api.epic-journey.com
Accept: application/vnd.api+json
Authorization: Bearer $API_KEY_HERE

```

##### REQUEST PARAMETERS

| Name             | Type    | Description                                              |
|------------------|---------|----------------------------------------------------------|
| accountId]       | string  | ID of account to fetch                                   |
| include          | string  | comma separated relationships of the resource to include |


##### EXAMPLE JSON RESPONSE

Response is a “compound document” which includes `titlePlayerProfile` in the response as well,  the response payload document for a single account looks like below:

```json
{
    "links": {
        "self": "/id/v1/accounts/{accountId}?include=titlePlayerProfiles"
    },
    "data": {
        "type": "account",
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
                    "self": "/id/v1/accounts/{accountId}/relationships/titlePlayerProfiles",
                    "related": "/id/v1/accounts/{accountId}/titlePlayerProfiles"
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
                "buildVersion": "holds build version of the game that the profile is associated",
                "currentGameMode": "squad"
            },
            "links": {
                "self": "/id/v1/accounts/{accountId}/titlePlayerProfiles/1"
            }
        }
    ]
}
```


## GAME MODES API

An API to query the game modes for the region in which the player is located, like `current most popular game modes`

```
# NOTES:
# To sort in descending order "sort=-rank" could be passed, without minus (U+002D HYPHEN-MINUS "-") sort happens in ascending order
# Page size parameter "page[size]=20" is optional, it uses default page size set on the server if not specified

GET /game/v1/{titleId}/{platform or platform-region}/gameModes?sort=rank&buildVersion={buildVersion}
Host: api.epic-journey.com
Accept: application/vnd.api+json
Authorization: Bearer $API_KEY_HERE
```

### REQUEST PATH AND QUERY PARAMETERS

| Name           | Type    | Description                                                                                        |
|----------------|---------|----------------------------------------------------------------------------------------------------|
| platform       | string  | platform where player plays on                                                                     |
| region         | string  | players region represented as two-letter country codes (ISO 3166)                                  |
| titleId        | string  | title to fetch associated resources (this could have been a header as well like `X-TITLE-ID`)      |
| sort           | string  | to sort resource collections according to one or more criteria ("sort fields")                     |
| buildVersion   | string  | previously uploaded build version for which game modes are being requested                         |



### RESPONSES
- `200 OK`

__GameModeInfo__

> More fields are omitted for brevity

| Name           | Type    | Description                                                                                         |
|----------------|---------|-----------------------------------------------------------------------------------------------------|
| name           | string  | name of the game mode                                                                               |
| maxPlayerCount | number  | maximum user count a specific Game Server Instance can support                                      |
| minPlayerCount | number  | minimum user count required for this Game Server Instance to continue (usually 1)                   |
| startOpen      | boolean | whether to start as an open session, meaning that players can match-make into it (defaults to true) |
| description    | string  | description of the game mode                                                                        |
| titleId        | string  | UUID that identifies the studio and game                                                            |
| buildVersion   | string  | holds the build version of the game that the mode is associated                                     |



Response contains links to `prev` and the `next` pages. 
`prev` will be empty for the initial response and `self` contains the link that generated the current response document.

```json
{
    "meta": {
        "page": {
            "per-page": 10,
            "from": "before_cursor",
            "to": "after_cursor",
            "has-more": true,
            "total": 30
        }
    },
    "links": {
        "self": "/game/v1/{titleId}/{platform or platform-region}/gameModes?sort=popularity&buildVersion={buildVersion}",
        "first": "link to the first page",
        "last": "link to the last page",
        "prev": "/game/v1/{titleId}/{platform or platform-region}/gameModes?sort=popularity&buildVersion={buildVersion}",
        "next": "/game/v1/{titleId}/{platform or platform-region}/gameModes?sort=popularity&page[after]={after_cursor}&buildVersion={buildVersion}"
    },
    "data": [
        {
            "type": "gameMode",
            "id": "ID_HERE",
            "attributes": {
                "name": "solo",
                "description": "Single player discovery mode",
                "maxPlayerCount": 16,
                "minPlayerCount": 1,
                "startOpen": true,
                "titleId": "{titleId}",
                "buildVersion" : "{buildVersion}"
        }
    ]
}
```

## ERROR CASES

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


__TitleID:__
```json
{
  "errors": [{
    "title": "Invalid Parameter.",
    "detail": "Title ID must be valid; got A-TITLE",
    "source": { "path": "titleId" },
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
    "source": { "path": "platform" },
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
    "source": { "path": "region" },
    "status": "400"
  }]
}
```

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


__BuildVersion:__
```json
{
  "errors": [{
    "title": "Invalid Parameter.",
    "detail": "Build version must be valid; got x=build",
    "source": { "parameter": "buildVersion" },
    "status": "400"
  }]
}
```