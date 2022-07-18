# PERSISTENCE LAYER

- Primary — Replica, replicated database design, with federation (or functional partitioning) splits up databases by function) and sharding (data could be sharded by platform, platform-region)
  * Federation helps in less read and write traffic to each database and therefore less replication lag. However we'll need to design our application logic to determine which database to read.
  * Sharding distributes data across different databases such that each database can only manage a subset of the data. Similar to Federation, sharding results in less read and write traffic, less replication, and more cache hits. Index size is also reduced, which generally improves performance with faster queries. Also like federation, there is no single central master serializing writes, allowing to write in parallel with increased throughput.

ℹ️ When we create sharded database solution how to access data is important. We could follow the "Data-dependent routing" approach here, which is a fundamental pattern when working with sharded databases. It enables to use of the data in a query to route the request to an appropriate database. If the sharding key is not part of the query then the request context may also be used to route the request.

##### MY APPROACH
For example, data could be sharded by platform, platform + region, or region.  In the current design, I will go with including the "platform" and "region" part of the URL, since it easily allows "Data-dependent routing" with a few routing policies (by computing shard key) by using NGINX Lua scripts or an Envoy filter, or similar one. This could also be done in application code as well.
