package com.openkg.openbase.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.openkg.openbase.common.Singleton;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.sql.Timestamp;

@Service
public class MongoService {
    private static final String ENTITYCOLLECTION = "entity";
    private static final String EditHistoryCOLLECTION = "entity_edit_history";
    private static final String DatasetInfoCOLLECTION = "dataset_info";
    private static final MongoCollection<Document> entityCollection = Singleton.mongoDBUtil.getdb().getCollection(ENTITYCOLLECTION);
    private static final MongoCollection<Document> editHistoryCollection = Singleton.mongoDBUtil.getdb().getCollection(EditHistoryCOLLECTION);
    private static final MongoCollection<Document> datasetNameCollection = Singleton.mongoDBUtil.getdb().getCollection(DatasetInfoCOLLECTION);

    public String getEntityHistoryByID(String atID, String edit_type){
        Document matchDocument = new Document();
        // edit_type, 比如说"update"
        matchDocument.put("editType", edit_type);

        MongoCursor<Document> mongo_cursor = editHistoryCollection.find(matchDocument).iterator();

        List<Document> resultDocument = new ArrayList<Document>();
        Document iter_doc;
        Document updatedVersion_doc;

        if (mongo_cursor.hasNext()) {
//            System.out.println("\t 1111111111111111111111111111");
//            int counter = 0;
            while (mongo_cursor.hasNext()) {
                iter_doc = mongo_cursor.next();
//                if(counter==0){
//                    System.out.println(iter_doc.keySet());
//                }
                // 找到子字段
                updatedVersion_doc = (Document)(iter_doc.get("updatedVersion"));
//                if(counter==0){
//                    System.out.println("\t" + updatedVersion_doc.keySet());
//                    System.out.println("\t" + updatedVersion_doc.get("@id"));
//                    System.out.println("\t" + atID);
//                }
                String found_id = (String) updatedVersion_doc.get("@id");
                if(found_id.equals(atID)) {
//                    System.out.println("\t 22222222222222222222222222222222222");
                    resultDocument.add(iter_doc);
//                    System.out.println(iter_doc.get("editTimeStamp"));
                }
//                counter++;
            }

            if(resultDocument.isEmpty()) return "";

//            String time1 = "2020-03-02 23:50:51";
//            String time2 = "2020-03-03 11:36:59";
//            Timestamp a = Timestamp.valueOf(time1);
//            Timestamp b = Timestamp.valueOf(time2);
//             if(a.after(b)){
//                System.out.println("\t 111111111111");
//            }
//            if(a.before(b)){
//                System.out.println("\t 2222222222222");
//            }

//            System.out.println("\t -------" + resultDocument.size());
            // List自定义排序
            Collections.sort(resultDocument,new Comparator<Document>() {
                @Override
                public int compare(Document o1, Document o2){
                    String time1 = (String)(o1.get("editTimeStamp"));
                    String time2 = (String)(o2.get("editTimeStamp"));
                    Timestamp a = Timestamp.valueOf(time1);
                    Timestamp b = Timestamp.valueOf(time2);
                    if(a.after(b)){
                        return -1;
                    }
                    else if(a.before(b)){
                        return 1;
                    }
                    else {
                        return 0;
                    }
                }
            });
//            for (int i = 0; i < resultDocument.size(); i++){
//                System.out.println("\t " + resultDocument.get(i).get("editTimeStamp"));
//            }
//            int size =  resultDocument.get(0).size();
            String latest_timestamp= (String) resultDocument.get(0).get("editTimeStamp");
            return latest_timestamp;
        }
        return "";
    }

    public String getDatasetIdByName(String name){
        Document matchDocument = new Document();
        matchDocument.put("name", name);
        MongoCursor<Document> mongo_cursor = datasetNameCollection.find(matchDocument).iterator();
        Document resultDocument;
        if (mongo_cursor.hasNext()){
            resultDocument = mongo_cursor.next();
            String datasetId = (String)resultDocument.get("id");
            return datasetId;
        }
        return "";
    }
}
