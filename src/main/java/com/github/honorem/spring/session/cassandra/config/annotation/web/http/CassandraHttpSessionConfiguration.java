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
package com.github.honorem.spring.session.cassandra.config.annotation.web.http;

import com.github.honorem.spring.session.cassandra.CassandraSession;
import com.github.honorem.spring.session.cassandra.CassandraSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;

/**
 *
 * @author Marc Honoré <marc@shareif.com>
 */
@Configuration
public class CassandraHttpSessionConfiguration extends SpringHttpSessionConfiguration implements ImportAware {

    private final CassandraSessionRepository cassandraSessionRepository;
    
    @Autowired
    public CassandraHttpSessionConfiguration(CassandraTemplate cassandraTemplate) {
        cassandraSessionRepository = new CassandraSessionRepository(cassandraTemplate);
    }
    
    @Bean
    public CassandraSessionRepository cassandraSessionRepository() {
        return cassandraSessionRepository;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableCassandraHttpSession.class.getName()));
        CassandraSession.setSessionValidity(attributes.getNumber("maxInactiveIntervalInSeconds"));
        System.out.println(attributes.getString("cassandraTableName"));
        cassandraSessionRepository.setTableName(attributes.getString("cassandraTableName"));
    }

}
