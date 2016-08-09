# spring-session-cassandra

Spring session implementation with Couchbase as backend

inspired by [cambierr/spring-session-couchbase](https://github.com/cambierr/spring-session-couchbase)

# Spring Session Project Site

Everything you need about Spring (documentation, issue management, support, samples) is at [http://spring.io/spring-session/](http://projects.spring.io/spring-session/)

# Configuration

In order to work properly, you must create a table named "cassandra_session" in your working keyspace as defined in you `AbstractCassandraConfiguration` implementation.
Here is the table definition : 
```cql
CREATE TABLE cassandra_session (
    id text PRIMARY KEY,
    accessed bigint,
    created bigint,
    data map<text, text>,
    interval int
) 
```
