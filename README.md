# spring-session-cassandra

Spring session implementation with Cassandra as backend

inspired by [cambierr/spring-session-couchbase](https://github.com/cambierr/spring-session-couchbase)

# Spring Session Project Site

Everything you need about Spring (documentation, issue management, support, samples) is at [http://spring.io/spring-session/](http://projects.spring.io/spring-session/)

# Configuration

In order to work properly, you must create a table named "cassandra_sessions" (see below to change this name) in your working keyspace as defined in you `AbstractCassandraConfiguration` implementation.
Here is the table definition : 
```cql
CREATE TABLE cassandra_sessions (
    id text PRIMARY KEY,
    accessed bigint,
    created bigint,
    data map<text, text>,
    interval int
);
```

To enable the cassandra session use the annotation `@EnableCassandraHttpSession` in your main Spring Application class. By default, spring-cassandra-session use the table `casssandra_sessions` but you can edit this name by using `@EnableCassandraHttpSession(cassandraTableName = "your_table_name")`. Don't forget to update your cassandra table accordingly.

# Maven
If your are using maven, use the following dependency :
```xml
<dependency>
    <groupId>com.github.honorem</groupId>
    <artifactId>spring-session-cassandra</artifactId>
    <version>0.2</version>
</dependency>
```
