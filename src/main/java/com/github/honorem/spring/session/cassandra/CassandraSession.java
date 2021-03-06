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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.mapping.Table;
import org.springframework.session.ExpiringSession;

/**
 *
 * @author Marc Honoré <marc@shareif.com>
 */
@Table
public class CassandraSession implements ExpiringSession {

    public static final String DEFAULT_TABLE_NAME = "cassandra_sessions";
    
    //The default validity of a session (in s)
    public static final int SESSION_DEFAULT_VALIDITY = 1800;

    //The validity of this session
    protected static int SESSION_VALIDITY = SESSION_DEFAULT_VALIDITY;

    //the id of the session used as a primary key
    @PrimaryKeyColumn(name = "id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String id;
    //the interval beetwen which the session is valid
    private int interval;
    //the time of creation of the session
    private long created;
    //the last time the session has been accessed
    private long accessed;
    //the data in the session
    private Map<String, String> data;
    
    //update default session validity
    public static void setSessionValidity(int validity) {
        SESSION_VALIDITY = validity;
    }
    
    //constructor used by cassandra session repository
    public CassandraSession() {
        this(SESSION_DEFAULT_VALIDITY);
    }

    //constructor used to delete a session
    public CassandraSession(String id) {
        this.id = id;
    }

    //constructor used by cassandra session repository
    public CassandraSession(int interval) {
        this(UUID.randomUUID().toString(), interval);
    }
    //constructor used by cassandra session repository
    public CassandraSession(String _id, int interval) {
        this.id = _id;
        this.interval = interval;
        this.accessed = System.currentTimeMillis();
        this.data = new HashMap<>();
    }

    @Override
    public long getCreationTime() {
        return this.created;
    }

    @Override
    public void setLastAccessedTime(long lastAccessedTime) {
        this.accessed = lastAccessedTime;
    }

    @Override
    public long getLastAccessedTime() {
        return this.accessed;
    }

    @Override
    public void setMaxInactiveIntervalInSeconds(int interval) {
        this.interval = interval;
    }

    @Override
    public int getMaxInactiveIntervalInSeconds() {
        return this.interval;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > (this.accessed + 1000 * this.interval);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public <T> T getAttribute(String attributeName) {
        if (this.data == null || !this.data.containsKey(attributeName)) {
            return null;
        }
        String o = this.data.get(attributeName);
        return (T) deserialize(o);

    }

    @Override
    public Set<String> getAttributeNames() {
        return this.data.keySet();
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        if(this.data == null){
            this.data = new HashMap<>();
        }
        if(this.data.getClass().getSimpleName().equals("UnmodifiableMap")){
            Map<String, String> map = new HashMap<>();
            this.data.keySet().stream().forEach((key) -> {
                map.put(key, this.data.get(key));
            });
            this.data = map;
        }
        this.data.put(attributeName, serialize(attributeValue));
    }

    @Override
    public void removeAttribute(String attributeName) {
        if(this.data == null){
            this.data = new HashMap<>();
            return;
        }
        if(this.data.getClass().getSimpleName().equals("UnmodifiableMap")){
            Map<String, String> map = new HashMap<>();
            this.data.keySet().stream().forEach((key) -> {
                map.put(key, this.data.get(key));
            });
            this.data = map;
        }
        this.data.remove(attributeName);
    }

    /**
     * Serialize object as base64 encoded String
     * 
     * @param _object The object to serialize
     * @return The serialized object
     */
    private String serialize(Object _object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(_object);
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Deserialize base64 encoded object 
     * 
     * @param _serialized The object serialized
     * @return The deserialized object
     */
    private Object deserialize(String _serialized) {
        if (_serialized == null) {
            return null;
        }
        try {
            return new ObjectInputStream(
                    new ByteArrayInputStream(
                            Base64.getDecoder().decode(_serialized)
                    )
            ).readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public Map<String, String> getMetadata(){
        return data;
    }
}
