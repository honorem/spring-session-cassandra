/*
 * The MIT License
 *
 * Copyright 2016 honorem.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.honorem.spring.session.cassandra;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 *
 * @author Marc Honoré <marc@shareif.com>
 */
@Repository
public class CassandraSessionRepository implements SessionRepository<CassandraSession> {

    private final CassandraTemplate cassandra;

    private String tableName;

    public CassandraSessionRepository(CassandraTemplate cassandra) {
        Assert.notNull(cassandra);
        this.cassandra = cassandra;
    }

    public void setTableName(String _tableName) {
        this.tableName = _tableName;
    }

    @Override
    public CassandraSession createSession() {
        return new CassandraSession();
    }

    @Override
    public void save(CassandraSession session) {
        cassandra.execute(CassandraTemplate.createInsertQuery(tableName, session, null, cassandra.getConverter()));
    }

    @Override
    public CassandraSession getSession(String id) {
        Select select = QueryBuilder.select().all().from(tableName);
        select.where(QueryBuilder.eq("id", id));
        return cassandra.selectOne(select, CassandraSession.class);
    }

    @Override
    public void delete(String id) {
        cassandra.execute(CassandraTemplate.createDeleteQuery(tableName, new CassandraSession(id), null, cassandra.getConverter()));
    }

}
