package com.openkg.openbase.service;


import com.github.jsonldjava.utils.Obj;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.openkg.openbase.common.Singleton;
import org.bson.Document;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EditService {

    private static final String ENTITYCOLLECTION = "entity";
    private static final String EditHistoryCOLLECTION = "entity_edit_history";
    private static final MongoCollection<Document> entityCollection = Singleton.mongoDBUtil.getdb().getCollection(ENTITYCOLLECTION);
    private static final MongoCollection<Document> editHistoryCollection = Singleton.mongoDBUtil.getdb().getCollection(EditHistoryCOLLECTION);

    private Map<String, String> cnSchemaPropertyNameMap;

    private Document getEntityByID(String atID){
        Document matchDocument = new Document();
        Document resultDocument = null;
        matchDocument.put("@id", atID);
        MongoCursor<Document> result_cursor = entityCollection.find(matchDocument).iterator();
        if(result_cursor.hasNext()){
            resultDocument = result_cursor.next();
            resultDocument.remove("_id");
        }
        return  resultDocument;
    }

    private Map<String, String> getPropertyNameHashMap(){
        if (null == cnSchemaPropertyNameMap){
            //InputStream inputStream = ClassLoaderUtil.getResourceAsStream("MimeType2FileExt_config_daizhen", VerticalMain.class);
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("cnShcema_properties_name_list");
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            //File file = new File(getClass().getClassLoader().getResource("MimeType2FileExt_config_daizhen").getFile());
            //String mimeType2Ext_Filepath = "conf/MimeType2FileExt_config_daizhen";
            //System.out.println(mimeType2Ext_Filepath);
            Map<String, String> cnSchemaPropertyName_hashmap = new HashMap<String, String>();
            try{
                //BufferedReader reader = new BufferedReader(new FileReader(file));
                BufferedReader reader = new BufferedReader(streamReader);
                String line = reader.readLine();
                while (line != null) {
                    //System.out.println(line);
                    String[] i_list =  line.trim().split("\\t");
                    //System.out.println(i_list[0]);
                    cnSchemaPropertyName_hashmap.put(i_list[0], i_list[1]);
                    // read next line
                    line = reader.readLine();
                }
                reader.close();
            }catch (IOException ex){
                ex.printStackTrace();
                System.err.println("[ERROR] [RESOURCES NOT FOUND]  can't read cnShcema_properties_name_list file content");
                //throw new IOException("Error reading MimeType2FileExt_config_daizhen file !!");
            }
            cnSchemaPropertyNameMap = cnSchemaPropertyName_hashmap;
        }

        return cnSchemaPropertyNameMap;
    }

    public boolean updateEntity(String user_id, HashMap<String, String> entity_dict, String timeStamp){
        if(null != entity_dict.get("@id") && !entity_dict.get("@id").isEmpty() && null != entity_dict.get("@name") && !entity_dict.get("@name").isEmpty()){

            Document newDocument = getEntityByID(entity_dict.get("@id"));

            for (Map.Entry<String, String> entry : entity_dict.entrySet()){
                if (entry.getValue() != null && !entry.getValue().isEmpty() && !entry.getKey().equals("@name") && !entry.getKey().equals("@id")){
                    String key = entry.getKey();
                    String value = entry.getValue();
                    // single string property
                    if (key.equals("appID")){
                        newDocument.put(entry.getKey(), value);
                    }else{
                        // @value property
                        if(newDocument.containsKey(key)) {
                            List<Document> property_values = (List<Document>) newDocument.get(key);
                            assert property_values.size() > 0;
                            property_values.get(0).put("@value", value);
                            newDocument.put(key, property_values);
                        }else{
                            Document my_doc = new Document();
                            my_doc.put("@value", value);
                            List<Document> property_values = new ArrayList<Document>();
                            property_values.add(my_doc);
                            newDocument.put(key, property_values);
                        }
                    }
                    //newDocument.put(entry.getKey(), entry.getValue());
                }
            }

            List<String> to_delete_key_list = new ArrayList<>();
            for(String key : newDocument.keySet()){
                if(!key.equals("_id") && !key.startsWith("@") && !key.equals("appID")){
                    if (!entity_dict.containsKey(key)){
                        //newDocument.remove(key);
                        to_delete_key_list.add(key);
                    }
                }
            }
            for(String key : to_delete_key_list){
                newDocument.remove(key);
            }

            // add new history entry for this edit by daizhen. first
            Document newEditHistoryDocument = new Document();
            newEditHistoryDocument.put("editorID",user_id);
//            String timeStamp = new SimpleDateFormat("yyyy年MM月dd日HH小时mm分ss秒").format(new Date());
            newEditHistoryDocument.put("editTimeStamp", timeStamp);
            newEditHistoryDocument.put("updatedVersion", newDocument);
            newEditHistoryDocument.put("originalVersion", getEntityByID(entity_dict.get("@id")));
            newEditHistoryDocument.put("editType","update");
            editHistoryCollection.insertOne(newEditHistoryDocument);

            // update new entry for this edit by daizhen. second
            Document conditionDocument = new Document();
            conditionDocument.put("@id", entity_dict.get("@id"));
            entityCollection.replaceOne(conditionDocument, newDocument);
            return true;
        }

        return false;
    }

    public boolean deleteEntity(String user_id, HashMap<String, String> entity_dict){
        if(null != entity_dict.get("@id") && !entity_dict.get("@id").isEmpty()){
            // add new history entry for this edit by daizhen. first
            Document newEditHistoryDocument = new Document();
            newEditHistoryDocument.put("editorID",user_id);
            String timeStamp = new SimpleDateFormat("yyyy年MM月dd日HH小时mm分ss秒").format(new Date());
            newEditHistoryDocument.put("editTimeStamp", timeStamp);
            newEditHistoryDocument.put("originalVersion", getEntityByID(entity_dict.get("@id")));
            newEditHistoryDocument.put("editType","delete");
            editHistoryCollection.insertOne(newEditHistoryDocument);

            // update new entry for this edit by daizhen. second
            Document conditionDocument = new Document();
            conditionDocument.put("@id", entity_dict.get("@id"));
            entityCollection.deleteOne(conditionDocument);
            return true;
        }

        return false;
    }

    public boolean createEntity(String user_id, HashMap<String, String> entity_dict){

        if(null != entity_dict.get("@name") && !entity_dict.get("@name").isEmpty()){

            Document newDocument = new Document();
            for (Map.Entry<String, String> entry : entity_dict.entrySet()){

                if (entry.getValue() != null && !entry.getValue().isEmpty()){
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if(!key.equals("_id") && !key.startsWith("@") && !key.equals("appID")){
                        HashMap<String, String> tmp_map = new HashMap<>();
                        tmp_map.put("@value", value);
                        List<HashMap<String, String>> tmp_list = new ArrayList<>();
                        tmp_list.add(tmp_map);
                        newDocument.put(entry.getKey(), tmp_list);
                    }else if(key.equals("appID") || key.equals("@name") ){
                        newDocument.put(key, value);
                    }else if (key.equals("@domain") || key.equals("@type")){
                        List<String> tmp_list = new ArrayList<>();
                        tmp_list.add(value);
                        newDocument.put(key,tmp_list);
                    }

                    //newDocument.put(entry.getKey(), entry.getValue());
                }
            }
            String generatedUUID = UUID.randomUUID().toString().replace("-", "");
            entity_dict.put("@id", generatedUUID);
            newDocument.put("@id", generatedUUID);

            // add new history entry for this edit by daizhen. first
            Document newEditHistoryDocument = new Document();
            newEditHistoryDocument.put("editorID",user_id);
            String timeStamp = new SimpleDateFormat("yyyy年MM月dd日HH小时mm分ss秒").format(new Date());
            newEditHistoryDocument.put("editTimeStamp", timeStamp);
            newEditHistoryDocument.put("updatedVersion", newDocument);
            newEditHistoryDocument.put("editType","create");
            editHistoryCollection.insertOne(newEditHistoryDocument);

            // insert new entry for this edit by daizhen. second
            entityCollection.insertOne(newDocument);
            return true;
        }

        return false;
    }

    public HashMap searchPropertyNameList(String key){
        if (null == key || key.isEmpty()){
            return  null;
        }
        Map<String, String> cnShemaPropertyNameListMap = getPropertyNameHashMap();
        HashMap<String, String> resultPropertyNameHashMap = new HashMap<>();
        for(String cnShcema_key : cnShemaPropertyNameListMap.keySet()){
            if(cnShcema_key.startsWith(key)){
                resultPropertyNameHashMap.put(cnShcema_key, cnShemaPropertyNameListMap.get(cnShcema_key));
            }
        }
        return resultPropertyNameHashMap;
    }
}
