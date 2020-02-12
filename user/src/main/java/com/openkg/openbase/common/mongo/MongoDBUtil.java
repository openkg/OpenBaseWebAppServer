package com.openkg.openbase.common.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.openkg.openbase.common.Env;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi on 18-10-11.
 */
public class MongoDBUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBUtil.class);
    private MongoClient mongoClient = null;
    private MongoDatabase database;

    public MongoDBUtil(Env env) {
        MongoDBInfo mongoDBInfo = env == null?MongoDBInfo.getInstance():MongoDBInfo.getInstance(env);
        String dbName = mongoDBInfo.getDb();

        //建立链接
        try {
            String url = getStringConnection(mongoDBInfo);
            mongoClient = new MongoClient(new MongoClientURI(url));
        } catch (Throwable e) {
            LOGGER.error("mongo connection error", e);
        }
        try {
            database = mongoClient.getDatabase(dbName);
        } catch (Throwable e) {
            LOGGER.error(String.format("get db error:%s",dbName), e);
        }
    }

    public MongoDBUtil(){
        this(null);
    }

    public MongoDatabase getdb(){
        return database;
    }

    private static String getStringConnection(MongoDBInfo instance) {
        String mongo_db_url = "";
        try {
            mongo_db_url += "mongodb://";
            if (!StringUtils.isEmpty(instance.getUser())) {
                mongo_db_url += instance.getUser() + ":";
                mongo_db_url += instance.getPassword();
                mongo_db_url += "@";
            }
            for (MongoDBInfo.Address address : instance.getAddress()) {
                mongo_db_url += address.getName() + ":" + address.getPort() + ",";
            }
            mongo_db_url = mongo_db_url.substring(0, mongo_db_url.length() - 1);
            mongo_db_url += "/" + instance.getDb();
            mongo_db_url += "?";
            mongo_db_url += "maxPoolSize=40";

            if (!StringUtils.isEmpty(instance.getRs())) {
                mongo_db_url += "&replicaSet=" + instance.getRs();
            }
            if (!StringUtils.isEmpty(instance.getAuthDB())) {
                mongo_db_url += "&authSource=" + instance.getAuthDB();
                mongo_db_url += "&authMechanism=SCRAM-SHA-1";
            } else {
                mongo_db_url += "&authSource=" + "admin";
                mongo_db_url += "&authMechanism=SCRAM-SHA-1";
            }
            LOGGER.info(mongo_db_url);
        } catch (Exception e) {
            LOGGER.error("mongodb连接串构建失败:", e);
        }
        return mongo_db_url;
    }

    public static List<Document> find(MongoCollection<Document> collection, Document findDocument, Document sortDocument, Integer limit) {
        if (collection == null) {
            LOGGER.debug("collection null is not allow");
            return null;
        }
        if(findDocument.isEmpty()){
            LOGGER.warn("empty document is not allow");
            return null;
        }
        LOGGER.debug(findDocument.toJson());
        List<Document> res = new ArrayList<>();
        FindIterable<Document> findIterable;
        if (findDocument.isEmpty()) {
            findIterable = collection.find();
        } else {
            findIterable = collection.find(findDocument);
        }
        if (sortDocument!=null && !sortDocument.isEmpty()) {
            findIterable = findIterable.sort(sortDocument);
        }
        if (limit != null && limit > 0) {
            findIterable = findIterable.limit(limit);
        }
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document one = mongoCursor.next();
            res.add(one);
        }
        return res;
    }

    public static void insert(MongoCollection<Document> collection,List<Document> documents){
        if (collection == null) {
            LOGGER.debug("collection or document is null");
        }

        List<Document> puts = new ArrayList<>();
        int count = 0;
        for(int i=0;i<documents.size();i++){
            puts.add(documents.get(i));
            if(++count % 1000 == 0){
                collection.insertMany(puts);
                puts.clear();
            }
        }
        if(!puts.isEmpty()){
            collection.insertMany(puts);
        }
    }

    public static void deleteAll(MongoCollection<Document> collection){
        DeleteResult deleteResult = collection.deleteMany(new Document());
        LOGGER.info("delete numbers is ：" + deleteResult.getDeletedCount());
    }

    public static List<Document> findInPip(MongoCollection<Document> collection,List<Document> pipline) {
        List<Document> res = new ArrayList<>();
        AggregateIterable<Document> dataRes = collection.aggregate(pipline);
        MongoCursor<Document> mongoCursor = dataRes.iterator();
        while (mongoCursor.hasNext()) {
            Document one = mongoCursor.next();
            res.add(one);
        }
        return res;
    }
}
