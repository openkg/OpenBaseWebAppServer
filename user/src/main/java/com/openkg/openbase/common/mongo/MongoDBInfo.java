package com.openkg.openbase.common.mongo;

import com.google.gson.Gson;
import com.openkg.openbase.common.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by mi on 18-10-11.
 */
public class MongoDBInfo {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBInfo.class);

    private List<Address> hosts;
    private String user;
    private String password;
    private String rs;
    private String authDB;
    private String db;

    public static class Address {
        private String name;
        private int port;

        public Address(String name, int port) {
            this.name = name;
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    private static final Map<Env, MongoDBInfo> MONGODB_INFO_MAP = new EnumMap<>(Env.class);

    public static MongoDBInfo getInstance() {
        return getInstance(Env.DEFAULT);
    }

    public static MongoDBInfo getInstance(Env env) {
        if (!MONGODB_INFO_MAP.containsKey(env)) {
            synchronized (MONGODB_INFO_MAP) {
                if (!MONGODB_INFO_MAP.containsKey(env)) {
                    MONGODB_INFO_MAP.put(env, new MongoDBInfo(env));
                }
            }
        }

        return MONGODB_INFO_MAP.get(env);
    }

    private MongoDBInfo(Env env) {
        String propertyFilename;
        switch (env) {

            case DEV:
            case PROD:
                propertyFilename = String.format("mongo/mongo.%s.properties", env.getFullName());
                break;
            case DEFAULT:
                propertyFilename = "mongo/mongo.properties";
                break;
            default:
                propertyFilename = "mongo/mongo.properties";
                break;
        }

        // 加载配置文件
        Properties properties = new Properties();
        try {
            ClassLoader classLoader = MongoDBInfo.class.getClassLoader();
            InputStream in = classLoader.getResourceAsStream(propertyFilename);
            properties.load(in);
        }
        catch (IOException e) {
            LOGGER.info("Load config file fail.", e);
        }

        String msg = properties.getProperty("msg");
        //String msg ="{\"db\": \"openbase\", \"password\": \"admin\", \"hosts\": [{\"name\": \"10.136.12.55\", \"port\": 20000}, {\"name\": \"10.136.12.21\", \"port\": 20000}, {\"name\": \"10.118.43.13\", \"port\": 20000}], \"user\": \"admin\"}";
        LOGGER.info(String.format("mongo msg is %s",msg));

        if (msg != null) {
            MongoDBInfo mongoDBInfo = new Gson().fromJson(msg, MongoDBInfo.class);
            this.hosts = mongoDBInfo.hosts;
            this.user = mongoDBInfo.user;
            this.password = mongoDBInfo.password;
            this.rs = mongoDBInfo.rs;
            this.db = mongoDBInfo.db;
            this.authDB = mongoDBInfo.authDB;
        }
    }

    public List<Address> getAddress() {
        return hosts;
    }

    public void setAddress(List<Address> hosts) {
        this.hosts = hosts;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getAuthDB() {
        return authDB;
    }

    public static void main(String[] args) {
        MongoDBInfo mongoDBInfo = getInstance();
    }
}
