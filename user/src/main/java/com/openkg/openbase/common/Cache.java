package com.openkg.openbase.common;

/**
 * Created by mi on 18-9-27.
 */
import org.apache.jena.base.Sys;
import redis.clients.jedis.*;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cache {
    public enum TimeUnit {
        MILLISECOND("PX"), SECOND("EX");

        private String sign;

        TimeUnit(String sign) {
            this.sign = sign;
        }

        private String get() {
            return sign;
        }
    }

    private static final String INTERNAL_SEPARATOR = ":";

    private static final Map<Env, Cache> CACHE_MAP = new EnumMap<>(Env.class);

    public static Cache getInstance() {
        return getInstance(Env.DEFAULT);
    }

    public static Cache getInstance(Env env) {
        if (!CACHE_MAP.containsKey(env)) {
            synchronized (CACHE_MAP) {
                if (!CACHE_MAP.containsKey(env)) {
                    CACHE_MAP.put(env, new Cache(env));
                }
            }
        }
        return CACHE_MAP.get(env);
    }

    private final JedisCluster jc;


    private Cache(Env env) {
        String filename;
        switch (env) {
            case DEV:
            case PROD:
                filename = String.format("cache/redis.%s.conf", env.getFullName());
                break;
            default:
                filename = "cache/redis.conf";
                break;
        }
        Config config = ConfigFactory.load(filename);
        Set<HostAndPort> redisNodes = new HashSet<>();
        for (Config c : config.getConfigList("com.openkg.openbase.common.cache.redis.urls")) {
            System.out.println(c.getString("ip"));
            redisNodes.add(new HostAndPort(c.getString("ip"), c.getInt("port")));
        }

        jc = new JedisCluster(redisNodes);
    }


    /**
     * Saving string in redis with time limit
     *
     * @param tableName String  key prefix
     * @param key       String  key postfix
     * @param value     String
     * @param timeUnit  TimeUnit
     * @param time      long saving time
     * @return boolean
     **/
    public boolean set(String tableName, String key, String value, TimeUnit timeUnit, long time) {
        switch (timeUnit) {
            case SECOND:
                return translateReply(
                        jc.setex(getInternalKey(tableName, key), time > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) time, value));
            case MILLISECOND:
                return translateReply(jc.psetex(getInternalKey(tableName, key), time, value));
            default:
                return false;
        }
    }

    /**
     * Set key value time
     *
     * @param tableName String  key prefix
     * @param key       String  key postfix
     * @param timeUnit  TimeUnit
     * @param time      long saving time
     * @return boolean
     **/
    public boolean setValueTime(String tableName, String key, TimeUnit timeUnit, long time) {
        int timeValue = time > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) time;
        switch (timeUnit) {
            case SECOND:
                return translateReply(
                        jc.expire(getInternalKey(tableName, key),timeValue));
            case MILLISECOND:
                return translateReply(
                        jc.expire(getInternalKey(tableName, key), timeValue*60));
            default:
                return false;
        }
    }

    /**
     * Saving string in redis without time limit
     *
     * @param tableName String  key prefix
     * @param key       String  key postfix
     * @param value     String
     * @return boolean
     **/
    public boolean set(String tableName, String key, String value) {
        if (translateReply(jc.set(getInternalKey(tableName, key), value))) {
            jc.sadd(tableName, key);
            return true;
        }
        return false;
    }

    /**
     * delete key and value
     *
     * @param tableName String  key prefix
     * @param keys      String  key postfix
     * @return boolean
     **/
    public Long del(String tableName, String... keys) {
        Long reply = jc.del(getInternalKeys(tableName, keys));
        jc.srem(tableName, keys);
        return reply;
    }

    /**
     * get value
     *
     * @param tableName String  key prefix
     * @param key      String  key postfix
     * @return String
     **/
    public String get(String tableName, String key) {
        return jc.get(getInternalKey(tableName, key));
    }


    private String[] getInternalKeys(String tableName, String... keys) {
        String[] internalKeys = new String[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            internalKeys[i] = getInternalKey(tableName, keys[i]);
        }
        return internalKeys;
    }

    private String getInternalKey(String tableName, String key) {
        return String.format("%s%s%s", tableName, INTERNAL_SEPARATOR, key);
    }

    private boolean translateReply(String reply) {
        return "OK".equals(reply);
    }

    private boolean translateReply(Long reply) {
        return 1==reply;
    }
}
