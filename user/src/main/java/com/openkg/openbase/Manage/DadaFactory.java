package com.openkg.openbase.Manage;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.model.DataStream.OriginTriple;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mi on 18-10-19.
 */
public class DadaFactory {
    private static final String TRIPLEDATACOLLECTION = "OpenbaseTripleData";
    private static final MongoCollection<Document> tripleCollection = Singleton.mongoDBUtil.getdb().getCollection(TRIPLEDATACOLLECTION);


    public static List<OriginTriple> getTriplesByJobId(Integer jobId){
        List<OriginTriple> res =  new ArrayList<>();

        Document match = new Document("jobId",jobId);
        Document sort = new Document("subjectId",1);

        FindIterable findIterable = tripleCollection.find(match);
        findIterable = findIterable.sort(sort);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()){
            Document one = mongoCursor.next();
            OriginTriple triple = Singleton.GSON.fromJson(one.toJson(),OriginTriple.class);
            res.add(triple);
        }
        return res;
    }
    
    public static long getJobLength(Integer jobId) {
    		//List<OriginTriple> res =  new ArrayList<>();

        Document match = new Document("jobId",jobId);
        //Document sort = new Document("subjectId",1);
        return tripleCollection.count(match);
    }
}
