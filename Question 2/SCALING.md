
# SCALING DESIGN
Below topics discusses design with involved components on how to scale system millions of users.
A few key points on how to scale:

## PERSISTENCE LAYER
- De-normalization
  Once data becomes distributed with techniques such as federation and sharding, managing joins across data centers further increases complexity. Denormalization might circumvent the need for such complex joins.
- SQL tuning
 * Tightening up schemas
 * Using good indices
 * Avoiding expensive joins (de-normalization could help here as I explained above)
 * Partitioning tables
 * Tuning the query cache

### MY APPROACH
ℹ️  I would prefer to use cache aside and write-through in conjunction, and also set a time-to-live (TTL) to mitigate stale data cases. This conjunction would help mitigating the case  "when a new node is created due to failure or scaling, having the new node does not cache entries until the entry is updated in the database".


## CACHING 
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

Write=behind Model:

### MY APPROACH
ℹ️  I would prefer to use cache aside and write-through in conjunction, and also set a time-to-live (TTL) to mitigate stale data cases. This conjunction would help mitigating the case  "when a new node is created due to failure or scaling, having the new node does not cache entries until the entry is updated in the database".

## FURTHER DETAILS (not in the scope of the work)
- The Analytics Database (Redshift, BgQuery, Snowflake  or self hosted "Clickhouse") could be used for a data warehousing solution for game analytics.
