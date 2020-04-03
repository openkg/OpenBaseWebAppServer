package com.openkg.openbase.service;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.openkg.openbase.common.Cache;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.model.DataPropertyNode;
import com.openkg.openbase.model.EnNode;
import com.openkg.openbase.model.Page;
import com.openkg.openbase.model.Neo4jModule.Node;
import com.openkg.openbase.model.Neo4jModule.Relation;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import java.util.*;


public class ViewService {
    private static final String ENTITYCOLLECTION = "checked_entity";
//    private static final String ENTITYCOLLECTION = "entity";
    private static final MongoCollection<Document> entityCollection = Singleton.mongoDBUtil.getdb().getCollection(ENTITYCOLLECTION);
    private static final String EntitySearchRedisTableName = "EntitySearch";
    private final static Logger LOGGER = LoggerFactory.getLogger(ViewService.class);

    public static Map getEntityByName(String entName, Page page, String domain) {
        List<Map> retrievedEntities = new ArrayList<>();

        boolean isEmptySearch = (entName == null || entName.isEmpty());
        long kg4aiEntityCount, buddismEntityCount, peopleEntityCount,agricultureEntityCount;
        long sevenLoreEntityCount, acgnEntityCount, kg4openkgEntityCount, xLoreEnityCount, legalEntityCount, beltAndRoadEntityCount;
        Bson matchBson = null;
        if (!isEmptySearch){
            //String pattern = ".*" + entName + ".*";
            //matchBson =  Filters.regex("@name", pattern, "i");
            matchBson = Filters.eq("@name", entName);
        }


        long start = System.currentTimeMillis();
        long total_doc_count =0;
        if(isEmptySearch){
            //total_doc_count = entityCollection.count(matchDocument);
            total_doc_count = 11550159;
        }else{
            //total_doc_count = entityCollection.count(matchBson);
            total_doc_count = 0;
            String total_doc_count_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":totalCount");
            if (null == total_doc_count_str || total_doc_count_str.isEmpty()){
                MongoCursor<Document> aggregate_cursor = entityCollection.aggregate(Arrays.asList(Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    total_doc_count = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":totalCount", Long.toString(total_doc_count), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                total_doc_count = Long.parseLong(total_doc_count_str);
            }

        }
        long end = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        LOGGER.info("Total documents count query time is : " + formatter.format((end - start) / 1000d) + " seconds");


        start = System.currentTimeMillis();
        if (isEmptySearch){
            //kg4aiEntityCount = entityCollection.count(kg4aiMatchDocument);
            kg4aiEntityCount = 2034;
            //buddismEntityCount = entityCollection.count(buddismMatchDocument);
            buddismEntityCount = 8661;
            //peopleEntityCount = entityCollection.count(peopleMatchDocument);
            peopleEntityCount = 915145;
            //agricultureEntityCount = entityCollection.count(agricultureMatchDocument);
            agricultureEntityCount = 32967;
            sevenLoreEntityCount = 8668638;
            acgnEntityCount = 299302;
            kg4openkgEntityCount=658;
            xLoreEnityCount=1597798;
            legalEntityCount=26990;
            beltAndRoadEntityCount=14308;
        }else{
            //kg4aiEntityCount = entityCollection.count(Filters.and(matchBson, kg4aiMatchDocument));
            kg4aiEntityCount = 0;
            buddismEntityCount = 0;
            peopleEntityCount = 0;
            agricultureEntityCount = 0;
            sevenLoreEntityCount = 0;
            acgnEntityCount = 0;
            kg4openkgEntityCount=0;
            xLoreEnityCount=0;
            legalEntityCount=0;
            beltAndRoadEntityCount=0;
            MongoCursor<Document> aggregate_cursor;
            String kg4aiEntityCount_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":kg4aiCount");
            if (null == kg4aiEntityCount_str || kg4aiEntityCount_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","kg4ai")), Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    kg4aiEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":kg4aiCount", Long.toString(kg4aiEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":kg4aiCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                kg4aiEntityCount = Long.parseLong(kg4aiEntityCount_str);
            }
            String buddismEntityCount_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":buddismCount");
            if (null == buddismEntityCount_str || buddismEntityCount_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","buddism")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    buddismEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":buddismCount", Long.toString(buddismEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":buddismCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                buddismEntityCount = Long.parseLong(buddismEntityCount_str);
            }
            String peopleEntityCount_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":peopleCount");
            if (null == peopleEntityCount_str || peopleEntityCount_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","people")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    peopleEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":peopleCount", Long.toString(peopleEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":peopleCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                peopleEntityCount = Long.parseLong(peopleEntityCount_str);
            }
            String agricultureEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":agricultureCount");
            if (null == agricultureEntity_str || agricultureEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","agriculture")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    agricultureEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":agricultureCount", Long.toString(agricultureEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":agricultureCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                agricultureEntityCount = Long.parseLong(agricultureEntity_str);
            }
            String sevenLoreEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":sevenLoreCount");
            if (null == sevenLoreEntity_str || sevenLoreEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","7Lore")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    sevenLoreEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":sevenLoreCount", Long.toString(sevenLoreEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":sevenLoreCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                sevenLoreEntityCount = Long.parseLong(sevenLoreEntity_str);
            }
            String acgnEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":acgnCount");
            if (null == acgnEntity_str || acgnEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","acgn")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    acgnEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":acgnCount", Long.toString(acgnEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":acgnCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                acgnEntityCount = Long.parseLong(acgnEntity_str);
            }
            String kg4openkgEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":kg4openkgCount");
            if (null == kg4openkgEntity_str || kg4openkgEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","kg4openkg")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    kg4openkgEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":kg4openkgCount", Long.toString(kg4openkgEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":kg4openkgCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                kg4openkgEntityCount = Long.parseLong(kg4openkgEntity_str);
            }
            String xLoreEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":xloreCount");
            if (null == xLoreEntity_str || xLoreEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","xlore")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    xLoreEnityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":xloreCount", Long.toString(xLoreEnityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":xloreCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                xLoreEnityCount = Long.parseLong(xLoreEntity_str);
            }
            String legalEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":legalCount");
            if (null == legalEntity_str || legalEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","legal")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    legalEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":legalCount", Long.toString(legalEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":legalCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                legalEntityCount = Long.parseLong(legalEntity_str);
            }
            String beltAndRoadEntity_str = Cache.getInstance().get(EntitySearchRedisTableName, entName+":beltAndRoadCount");
            if (null == beltAndRoadEntity_str || beltAndRoadEntity_str.isEmpty()){
                aggregate_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","beltAndRoad")),Aggregates.match(matchBson), new Document("$count", "count"))).iterator();
                if(aggregate_cursor.hasNext()){
                    beltAndRoadEntityCount = aggregate_cursor.next().getInteger("count");
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":beltAndRoadCount", Long.toString(beltAndRoadEntityCount), Cache.TimeUnit.SECOND, 1800);
                }else{
                    Cache.getInstance().set(EntitySearchRedisTableName,entName+":beltAndRoadCount", Long.toString(0), Cache.TimeUnit.SECOND, 1800);
                }
            }else{
                beltAndRoadEntityCount = Long.parseLong(beltAndRoadEntity_str);
            }
        }
        end = System.currentTimeMillis();
        formatter = new DecimalFormat("#0.00000");
        LOGGER.info("Seperate documents count query time is : " + formatter.format((end - start) / 1000d) + " seconds");

        if (domain.equals("kg4ai")){
            page.setCountTotal((int)kg4aiEntityCount);
        }else if(domain.equals("buddism")){
            page.setCountTotal((int)buddismEntityCount);
        }else if(domain.equals("people")){
            page.setCountTotal((int)peopleEntityCount);
        }else if(domain.equals("agriculture")){
            page.setCountTotal((int)agricultureEntityCount);
        }else if(domain.equals("7Lore")){
            page.setCountTotal((int)sevenLoreEntityCount);
        } else if(domain.equals("acgn")){
            page.setCountTotal((int)acgnEntityCount);
        }else if(domain.equals("kg4openkg")){
            page.setCountTotal((int)kg4openkgEntityCount);
        }else if(domain.equals("xlore")){
            page.setCountTotal((int)xLoreEnityCount);
        }else if(domain.equals("legal")){
            page.setCountTotal((int)legalEntityCount);
        }else if(domain.equals("beltAndRoad")){
            page.setCountTotal((int)beltAndRoadEntityCount);
        }else{
            page.setCountTotal((int)total_doc_count);
        }

        start = System.currentTimeMillis();
        MongoCursor<Document> result_cursor = null;
        if(isEmptySearch) {
            if (domain.equals("kg4ai")) {
                result_cursor = entityCollection.find(new Document("@domain","kg4ai")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            } else if (domain.equals("buddism")) {
                result_cursor = entityCollection.find(new Document("@domain", "buddism")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            } else if (domain.equals("people")) {
                result_cursor = entityCollection.find(new Document("@domain", "people")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            } else if (domain.equals("agriculture")) {
                result_cursor = entityCollection.find(new Document("@domain", "agriculture")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if (domain.equals("7Lore")) {
                result_cursor = entityCollection.find(new Document("@domain", "7Lore")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            } else if (domain.equals("acgn")) {
                result_cursor = entityCollection.find(new Document("@domain", "acgn")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if (domain.equals("kg4openkg")) {
                result_cursor = entityCollection.find(new Document("@domain", "kg4openkg")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if (domain.equals("xlore")) {
                result_cursor = entityCollection.find(new Document("@domain", "xlore")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if (domain.equals("legal")) {
                result_cursor = entityCollection.find(new Document("@domain", "legal")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if (domain.equals("beltAndRoad")) {
                result_cursor = entityCollection.find(new Document("@domain", "beltAndRoad")).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else {
                result_cursor = entityCollection.find(new Document()).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }
        }
        else{
            if(domain.equals("kg4ai")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "kg4ai"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("buddism")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "buddism"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("people")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "people"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("agriculture")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "agriculture"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("7Lore")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "7Lore"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("acgn")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "acgn"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("kg4openkg")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "kg4openkg"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("xlore")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "xlore"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("legal")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "legal"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else if(domain.equals("beltAndRoad")){
                result_cursor = entityCollection.find(Filters.and(new Document("@domain", "beltAndRoad"), matchBson)).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }else{
                result_cursor = entityCollection.find(matchBson).skip(page.getOffset()).limit(page.getPageSize()).iterator();
            }
        }
        while (result_cursor.hasNext()) {
            Document record = result_cursor.next();
            record.remove("_id");
            Map one = new HashMap();
            for (String key : record.keySet()) {
                String property_value = null;
                if(!key.equals("_id") && !key.startsWith("@") && !key.equals("appID")){
                    List<Document> property_values = (List<Document>)record.get(key);
                    assert property_values.size() > 0;
                    for(Document onevalue :property_values){
                        String tmp = onevalue.getString("@value");
                        if (null != tmp && !tmp.isEmpty()){
                            property_value = tmp;
                            break;
                        }
                    }
                }else if(key.equals("appID")){
                    property_value = record.getString("appID");
                }else if (key.equals("@name")){
                    property_value = record.getString("@name");
                }else if (key.equals("@id")){
                    property_value = record.getString("@id");
                }
                if(null != property_value && !property_value.isEmpty()){
                    one.put(key,property_value);
                }
            }
            retrievedEntities.add(one);

        }
        end = System.currentTimeMillis();
        formatter = new DecimalFormat("#0.00000");
        LOGGER.info("documents data query time is : " + formatter.format((end - start) / 1000d) + " seconds");

        Map res = new HashMap();
        res.put("RetrievedEntities", retrievedEntities);
        res.put("kg4ai_count", kg4aiEntityCount);
        res.put("buddism_count", buddismEntityCount);
        res.put("agriculture_count", agricultureEntityCount);
        res.put("people_count", peopleEntityCount);
        res.put("7Lore_count", sevenLoreEntityCount);
        res.put("acgn_count", acgnEntityCount);
        res.put("kg4openkg_count", kg4openkgEntityCount);
        res.put("xlore_count", xLoreEnityCount);
        res.put("legal_count", legalEntityCount);
        res.put("beltAndRoad_count", beltAndRoadEntityCount);
        res.put("total_count", total_doc_count);

        return res;
    }

    public static List<Map> getEntityById(String entId) {
        List<Map> res = new ArrayList<>();
        Document matchDocument = new Document();
        matchDocument = new Document();
        Document resultDocument = null;
        matchDocument.put("@id", entId);
        MongoCursor<Document> result_cursor = entityCollection.find(matchDocument).iterator();
        if(result_cursor.hasNext()){
            resultDocument = result_cursor.next();
        }
        /*matchDocument.put("appID", entId);
        MongoCursor<Document> result_cursor = entityCollection.find(matchDocument).iterator();
        Document resultDocument = null;
        if(result_cursor.hasNext()){
            resultDocument = result_cursor.next();
        }
        if (null == resultDocument){
            matchDocument = new Document();
            matchDocument.put("@id", entId);
            result_cursor = entityCollection.find(matchDocument).iterator();
            if(result_cursor.hasNext()){
                resultDocument = result_cursor.next();
            }
        }*/
        if (null != resultDocument){
            Map one = new HashMap();
            for (String key : resultDocument.keySet()) {
                String property_value = null;
                if(!key.equals("_id") && !key.startsWith("@") && !key.equals("appID")){
                    List<Document> property_values = (List<Document>)resultDocument.get(key);
                    assert property_values.size() > 0;
                    for(Document onevalue :property_values){
                        String tmp = onevalue.getString("@value");
                        if (null != tmp && !tmp.isEmpty()){
                            property_value = tmp;
                            break;
                        }
                    }
                }else if(key.equals("appID")){
                    property_value = resultDocument.getString("appID");
                }else if (key.equals("@name")){
                    property_value = resultDocument.getString("@name");
                }else if(key.equals("@id")){
                    property_value = resultDocument.getString("@id");
                }
                if(null != property_value && !property_value.isEmpty()){
                    one.put(key, property_value);
                }
            }
            res.add(one);
        }

        return res;
    }

    public static Map getMongoKG(String entityAtID, String propertyID, String domain) {
        List<Node> nodes = new ArrayList<>();
        List<Relation> relations = new ArrayList<>();
        String centNode = null;
        String masterScholar = null;
//        System.out.println(1);

        if (entityAtID == null && propertyID == null) {
            EnNode curEntity = getEntityRandom(domain);
            masterScholar = curEntity.getId();
            List<DataPropertyNode> fis = getFieldsByAtID(curEntity.getId());
            for (DataPropertyNode f : fis) {
                nodes.add(f);
                relations.add(new Relation(curEntity.getId(), f.getId(), "property"));
            }
            DataPropertyNode curField = getOnePropertyByAtID(curEntity.getId());
            centNode = curField.getId();
            List<EnNode> scs = getScholarsByField(curField);
            nodes.add(curEntity);
            for (EnNode s : scs) {
                nodes.add(s);
                if (!s.getId().equals(curEntity.getId())) {
                    relations.add(new Relation(s.getId(), curField.getId(), "property"));
                }
            }
        }
        if (entityAtID != null && propertyID == null) {
            masterScholar = entityAtID;
            EnNode scurScholar = getOneNodeByAtID(entityAtID);
            List<DataPropertyNode> fields = getFieldsByAtID(scurScholar.getId());
            nodes.addAll(fields);
            nodes.add(scurScholar);
            for (DataPropertyNode f : fields) {
                relations.add(new Relation(scurScholar.getId(), f.getId(), "property"));
            }
        }
        if (entityAtID != null && propertyID != null) {
            centNode = propertyID;
            masterScholar = entityAtID;
            EnNode curScholar = getOneNodeByAtID(entityAtID);
            List<DataPropertyNode> fs = getFieldsByAtID(curScholar.getId());
            nodes.addAll(fs);
            nodes.add(curScholar);
            for (DataPropertyNode f : fs) {
                relations.add(new Relation(curScholar.getId(), f.getId(), "property"));
            }
            DataPropertyNode curField = getOnePropertyByPropertyId(propertyID);
            List<EnNode> ss = getScholarsByField(curField);
            for (EnNode s : ss) {
                if (s.getId().equals(curScholar.getId())) {
                    continue;
                }
                nodes.add(s);
                relations.add(new Relation(s.getId(), curField.getId(), "property"));
            }
        }
        Map res = new HashMap();

        Set<String> haveName = new HashSet<>();
        Iterator<Node> iter = nodes.iterator();
        while (iter.hasNext()){
            Node node = iter.next();
            if(!haveName.contains(node.getName())){
                haveName.add(node.getName());
                continue;
            }
            else {
                iter.remove();
            }
        }

        haveName.clear();
        Iterator<Relation> iterR = relations.iterator();
        while (iterR.hasNext()){
            Relation relation = iterR.next();
            String key = String.format("%s%s",relation.getStart(),relation.getEnd());
            if(!haveName.contains(key)){
                haveName.add(key);
                continue;
            }
            else {
                iterR.remove();
            }
        }

        res.put("nodes", nodes);
        res.put("relations", relations);
        res.put("masterField", centNode);
        res.put("masterScholar",masterScholar);
        return res;
    }


    public static DataPropertyNode getOnePropertyByAtID(String atID) {
        MongoCursor<Document> entity_cursor = entityCollection.find(new Document("@id", atID)).limit(1).iterator();
        if(entity_cursor.hasNext()){
            Document entity_document =  entity_cursor.next();
            for(String k :entity_document.keySet()){
                if(!k.equals("summary") && !k.equals("_id") && !k.startsWith("@") && !k.equals("appID")){
                    List<Document> property_values = (List<Document>)entity_document.get(k);
                    assert property_values.size() > 0;
                    for(Document onevalue :property_values){
                        String tmp = onevalue.getString("@value");
                        if (null != tmp && !tmp.isEmpty()){
                            DataPropertyNode onePropertyNode = new DataPropertyNode(atID+"_"+k, k,tmp);
                            return onePropertyNode;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static EnNode getEntityRandom(String domain) {
//        MongoCursor<Document> result_cursor =  entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain",domain)), new Document("$sample", new Document("size",1)))).iterator();
        MongoCursor<Document> result_cursor =  entityCollection.aggregate(Arrays.asList(new Document("$sample", new Document("size",1)))).iterator();
        if(result_cursor.hasNext()){
            Document oneDocument = result_cursor.next();
            String atID = oneDocument.getString("@id");
            String name = oneDocument.getString("@name");
            List<String> type = (List<String>)oneDocument.get("@type");
            return new EnNode(atID, name,type.get(0));
        }
        return null;
    }

    public static EnNode getOneNodeByAtID(String atID) {
        Document matchDocument = new Document();
        matchDocument.put("@id", atID);
        MongoCursor<Document> result_cursor =  entityCollection.find(matchDocument).limit(1).iterator();
        Document resultDocument = null;
        if (result_cursor.hasNext()){
            resultDocument = result_cursor.next();
        }
        if (null != resultDocument){
            return new EnNode(atID, resultDocument.getString("@name"), "people");
        }

        return null;
    }

    public static DataPropertyNode getOnePropertyByPropertyId(String propertyID) {
        String[] i_list = propertyID.split("_");
        assert i_list.length == 2;
        MongoCursor<Document> result_cursor =  entityCollection.find(new Document("appID",i_list[0])).limit(1).iterator();
        if(result_cursor.hasNext()){
            Document resultDocument = result_cursor.next();
            List<Document> property_values = (List<Document>)resultDocument.get(i_list[1]);
            assert property_values.size() > 0;
            for(Document onevalue :property_values){
                String tmp = onevalue.getString("@value");
                if (null != tmp && !tmp.isEmpty()){
                    DataPropertyNode onePropertyNode = new DataPropertyNode(propertyID, i_list[1],tmp);
                    return onePropertyNode;
                }
            }
        }
        return null;
    }

    public static List<EnNode> getScholarsByField(DataPropertyNode pNode) {
        List<EnNode> res = new ArrayList<>();
        MongoCursor<Document> result_cursor =  entityCollection.find(new Document(pNode.getName(),pNode.getValue())).iterator();
        while (result_cursor.hasNext()){
            Document oneEntity = result_cursor.next();
            List<String> type = (List<String>)oneEntity.get("@type");
            res.add(new EnNode(oneEntity.getString("appID"), oneEntity.getString("@name"), type.get(0)));
        }
        return res;
    }

    public static List<DataPropertyNode> getFieldsByAtID(String atID) {
        List<DataPropertyNode> res = new ArrayList<>();
        Document matchDocument = new Document();
        matchDocument.put("@id", atID);
        MongoCursor<Document> result_cursor =  entityCollection.find(matchDocument).limit(1).iterator();
        Document resultDocument = null;
        if (result_cursor.hasNext()){
            resultDocument = result_cursor.next();
        }

        if (null != resultDocument){
            Set<String> keys =  resultDocument.keySet();
            for(String k :keys){
                if (!k.equals("_id") && !k.startsWith("@") && !k.equals("appID")){
                    List<Document> property_values = (List<Document>)resultDocument.get(k);
                    assert property_values.size() > 0;
                    for(Document onevalue :property_values){
                        String tmp = onevalue.getString("@value");
                        if (null != tmp && !tmp.isEmpty()){
                            DataPropertyNode onePropertyNode = new DataPropertyNode(atID+"_"+k, k,tmp);
                            res.add(onePropertyNode);
                            break;
                        }
                    }
                }
            }
        }

        return res;
    }

}
