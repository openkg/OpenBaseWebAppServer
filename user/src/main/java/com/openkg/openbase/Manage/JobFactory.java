package com.openkg.openbase.Manage;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.model.JobBase;
import com.openkg.openbase.model.JobBase.ParticipantInfo;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Math.min;

/**
 * Created by mi on 18-10-15.
 */
public class JobFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(JobFactory.class);

    private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String JOBCOLLECTION = "OpenbaseJobManage";
    private static final String ENTITYCOLLECTION = "entity";
    private static final MongoCollection<Document> jobCollection = Singleton.mongoDBUtil.getdb().getCollection(JOBCOLLECTION);
    private static final MongoCollection<Document> entityCollection = Singleton.mongoDBUtil.getdb().getCollection(ENTITYCOLLECTION);
    private static Map<String, Document> cachedEnityDocumentMap = new HashMap<String, Document>();
    private enum Coin{Heads, Tails};

    public static Coin flip(){
        Coin coinFlip;
        Random randomNum = new Random();
        int result = randomNum.nextInt(2);
        if(result == 0){
            coinFlip = Coin.Heads;
        }else{
            coinFlip = Coin.Tails;
        }
        return coinFlip;
    }

    //返回审核者某个用户的job信息
    public static JobBase getJobByJobId(String jobId){

        List<Document> pipList = new ArrayList<>();
        //匹配uid的document
        Document match = new Document();
        match.put("jobUUID",jobId);
        long count  = jobCollection.count(match);
        if(count <= 0){
            return null;
        }
        MongoCursor<Document> cursor =  jobCollection.find(match).iterator();
        JobBase jobBase = null;
        if(cursor.hasNext()){
            jobBase = Singleton.GSON.fromJson(cursor.next().toJson(),JobBase.class);
        }
        cursor.close();
        return jobBase;
    }

    //返回审核用户的所有job
    public static List<JobBase> getReviewJobsByUser(String uId, String jobDomain){
        List<JobBase> res = new ArrayList<>();

        List<Document> pipList = new ArrayList<>();
        //匹配uid的document
        Document match = new Document();
        match.put("joinedReviewers.uid",uId);
        // 去掉domain
//        match.put("jobDomain",jobDomain);

        MongoCursor<Document> mongoCursor = jobCollection.find(match).iterator();
        while (mongoCursor.hasNext()){
            Document oneDocument = mongoCursor.next();
            res.add(Singleton.GSON.fromJson(oneDocument.toJson(), JobBase.class));
        }
        return res;
    }

    //返回验收用户的所有job
    public static List<JobBase> getCheckJobsByUser(String uId,String jobDomain){
        List<JobBase> res = new ArrayList<>();

        List<Document> pipList = new ArrayList<>();
        //匹配uid的document
        Document match = new Document();
        match.put("joinedCheckers.uid",uId);
        match.put("jobDomain",jobDomain);

        MongoCursor<Document> mongoCursor = jobCollection.find(match).iterator();
        while (mongoCursor.hasNext()){
            Document oneDocument = mongoCursor.next();
            res.add(Singleton.GSON.fromJson(oneDocument.toJson(), JobBase.class));
        }
        return res;
    }

    //领取审核任务
    public static JobBase getReviewJob(String uid,String source){
        System.out.println("\t\t getReviewJob");
        JobBase tmp = new JobBase();
        ParticipantInfo participantInfo = tmp.new ParticipantInfo(uid);
        Document participantDocument = Document.parse(participantInfo.toString());
        participantDocument.put("startTime",sdf.format(new Date()));

        Document match = new Document();
        // leftReviewerCount大于0，可以进行审核的
        match.put("leftReviewerCount",new Document("$gt",0));
        // 该uid之前没有审核过这个任务
        match.put("joinedReviewers.uid",new Document("$ne",uid));
        // 去掉jobDomain，不局限在kg4ai
//        match.put("jobDomain",source);

        Document update = new Document();
        update.put("$inc",new Document("leftReviewerCount",-1));
        update.put("$push",new Document("joinedReviewers",participantDocument));

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        Document sort = new Document("leftReviewerCount",1);
        options.sort(sort);
        options.returnDocument(ReturnDocument.AFTER);

        Document res = jobCollection.findOneAndUpdate(match,update,options);
        if (res == null||res.isEmpty()){
            System.out.println("\t\t 找不到之前的任务，生成新的审核任务");
            JobBase jobBase = generateANewReviewjob(uid, source);
            return jobBase;
        }
        System.out.println("\t\t 存在审核任务");
        JobBase jobBase = Singleton.GSON.fromJson(res.toJson(),JobBase.class);
        return jobBase;
    }

    public static List<JobBase.TaskRecord> assignNewTaskRecordsForUserId(JobBase initialJob, String user_id){
//        MongoCursor<Document> triple_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","kg4ai")),new Document("$sample", new Document("size",300)))).iterator();
//        MongoCursor<Document> entity_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match", new Document("@domain","kg4ai")),new Document("$sample", new Document("size",200)))).iterator();
        System.out.println("\t\t assignNewTaskRecordsForUserId");
        // 先随机sample 2个
        MongoCursor<Document> triple_cursor = entityCollection.aggregate(Arrays.asList(new Document("$sample", new Document("size",2)))).iterator();
//        MongoCursor<Document> entity_cursor = entityCollection.aggregate(Arrays.asList(new Document("$sample", new Document("size",200)))).iterator();


        List<JobBase.TaskRecord> initialTripleRecords = new ArrayList<JobBase.TaskRecord>();
        List<JobBase.TaskRecord> initialEntityRecords = new ArrayList<JobBase.TaskRecord>();
        //initialJob.joinNewReviewer(user_id);
        try{
            while (triple_cursor.hasNext()){
                Document oneDocument = triple_cursor.next();
                initialTripleRecords.addAll(initialJob.parseToTripleTaskRecord(oneDocument, user_id));
                if (null != (List<Document>)oneDocument.get("summary")) {
                    initialEntityRecords.add(initialJob.parseToEntityTaskRecord(oneDocument, user_id));
                }
            }
        }finally {
            triple_cursor.close();
        }
        // 都是最少取200个，修改成5个
        List<JobBase.TaskRecord> subInitialTripleRecords = initialTripleRecords.subList(0, min(initialTripleRecords.size(), 5));
        List<JobBase.TaskRecord> subInitialEntityRecords = initialEntityRecords.subList(0, min(initialEntityRecords.size(), 5));
        if (initialJob.getType() == JobBase.JobType.TRIPLE){
            initialJob.appendJobHistory(subInitialTripleRecords);
        }else{
            initialJob.appendJobHistory(subInitialEntityRecords);
        }
        Document match = new Document();
        match.put("jobUUID", initialJob.getJobUUID());

        Document update = Document.parse(initialJob.toString());
        //FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        jobCollection.replaceOne(match, update);
        //jobCollection.insertOne(Document.parse(initialJob.toString()));
        return initialJob.getType() == JobBase.JobType.TRIPLE?subInitialTripleRecords:subInitialEntityRecords;
    }

    public static boolean saveJobBase(JobBase jobToSave){
        System.out.println("\t\t saveJobBase");

        Document match = new Document();
        match.put("jobUUID", jobToSave.getJobUUID());

        Document update = Document.parse(jobToSave.toString());
        //FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        com.mongodb.client.result.UpdateResult res =  jobCollection.replaceOne(match, update);
        if (res.getModifiedCount() >0){
            return true;
        }else{
            return false;
        }
    }

    public static JobBase generateANewReviewjob(String user_id, String jobDomain){
        System.out.println("\t\t generateANewReviewjob");
//        MongoCursor<Document> triple_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match",
//                new Document("@domain",jobDomain)),new Document("$sample", new Document("size",300)))).iterator();
//        MongoCursor<Document> entity_cursor = entityCollection.aggregate(Arrays.asList(new Document("$match",
//                new Document("@domain",jobDomain)),new Document("$sample", new Document("size",200)))).iterator();

        // 先随机sample 2个
        MongoCursor<Document> triple_cursor = entityCollection.aggregate(Arrays.asList(new Document("$sample",
                new Document("size",2)))).iterator();
//        MongoCursor<Document> entity_cursor = entityCollection.aggregate(Arrays.asList(new Document("$sample",
//                new Document("size",200)))).iterator();

        JobBase.JobType chosenTye;
        // 不要扔硬币了，新冠的数据没有"summary"这个key，所以不存在entity的审核。
//        if(flip() == Coin.Heads){
//           chosenTye =  JobBase.JobType.TRIPLE;
//        }else{
//            chosenTye = JobBase.JobType.ENTITY;
//        }
        chosenTye =  JobBase.JobType.TRIPLE;

        // 便于测试，reviewerCount设置为1
        JobBase resultJobBase  = new JobBase(chosenTye, 2, 1, jobDomain);
        resultJobBase.generateUUID();
        List<JobBase.TaskRecord> initialTripleRecords = new ArrayList<JobBase.TaskRecord>();
        List<JobBase.TaskRecord> initialEntityRecords = new ArrayList<JobBase.TaskRecord>();
        resultJobBase.joinNewReviewer(user_id);
        try{
            while (triple_cursor.hasNext()){
                Document oneDocument = triple_cursor.next();
                // 生成三元组的数据
                initialTripleRecords.addAll(resultJobBase.parseToTripleTaskRecord(oneDocument, user_id));
                if (null != (List<Document>)oneDocument.get("summary")) {
                    // 生成实体的数据
                    initialEntityRecords.add(resultJobBase.parseToEntityTaskRecord(oneDocument, user_id));
                }
            }
        }finally {
            triple_cursor.close();
        }
        // 都是最少取200个，修改成5个
        List<JobBase.TaskRecord> subInitialTripleRecords = initialTripleRecords.subList(0, min(initialTripleRecords.size(), 5));
        List<JobBase.TaskRecord> subInitialEntityRecords = initialEntityRecords.subList(0, min(initialEntityRecords.size(), 5));
        if (resultJobBase.getType() == JobBase.JobType.TRIPLE){
            resultJobBase.setJobHistory(subInitialTripleRecords);
        }else{
            resultJobBase.setJobHistory(subInitialEntityRecords);
        }
        // 存入到OpenbaseJobManage里面
        jobCollection.insertOne(Document.parse(resultJobBase.toString()));
        return resultJobBase;
    }



    public static String retrievePropertyValue(String entity_id, String property_name){
        String one_return_value = null;
        Document oneDocument;
        if(cachedEnityDocumentMap.containsKey(entity_id)){
            oneDocument =  cachedEnityDocumentMap.get(entity_id);
            List<Document> property_values = (List<Document>)oneDocument.get(property_name);
            assert property_values.size() > 0;
            for(Document onevalue :property_values){
                String tmp = onevalue.getString("@value");
                if (null != tmp && !tmp.isEmpty()){
                    one_return_value = tmp;
                    break;
                }
            }
        }else{
            // 当前对于多值的property只取其中的第一个 !!
            Document match = new Document();
            match.put("@id",entity_id);

            long count  = entityCollection.count(match);
            assert count == 1;

            MongoCursor<Document> cursor = entityCollection.find(match).iterator();

            if (cursor.hasNext()){
                oneDocument =  cursor.next();
                oneDocument.remove("_id");
                List<Document> property_values = (List<Document>)oneDocument.get(property_name);
                assert property_values.size() > 0;
                for(Document onevalue :property_values){
                    String tmp = onevalue.getString("@value");
                    if (null != tmp && !tmp.isEmpty()){
                        one_return_value = tmp;
                        break;
                    }
                }
                if(cachedEnityDocumentMap.size() > 100){
                    cachedEnityDocumentMap.clear();
                }
                cachedEnityDocumentMap.put(entity_id, oneDocument);
            }
            cursor.close();

        }

        return one_return_value;
    }
    public static String retrieveAtNameValue(String entity_id){
        // 当前对于多值的property只取其中的第一个 !!
        Document match = new Document();
        match.put("@id",entity_id);

        long count  = entityCollection.count(match);
        assert count == 1;
        Document oneDocument;
        MongoCursor<Document> cursor = entityCollection.find(match).iterator();
        String one_return_value = null;
        if (cursor.hasNext()){
            oneDocument =  cursor.next();
            one_return_value = oneDocument.getString("@name");
        }
        cursor.close();
        return one_return_value;
    }
    public static String retrieveSummaryValue(String entity_id){
        // 当前对于多值的property只取其中的第一个 !!
        Document match = new Document();
        match.put("@id",entity_id);

        long count  = entityCollection.count(match);
        assert count == 1;
        Document oneDocument;
        MongoCursor<Document> cursor = entityCollection.find(match).iterator();
        String one_return_value = null;
        if (cursor.hasNext()){
            oneDocument =  cursor.next();
            List<Document> property_values = (List<Document>)oneDocument.get("summary");
            assert property_values.size() > 0;
            for(Document onevalue :property_values){
                String tmp = onevalue.getString("@value");
                if (null != tmp && !tmp.isEmpty()){
                    one_return_value = tmp;
                    break;
                }
            }
        }
        cursor.close();
        return one_return_value;
    }

    //保存审核任务
    public static Boolean saveAJobByJobID(String jobUUID, JobBase job){
        System.out.println("\t\t saveAJobByJobID");

        try {
            Document match = new Document();
            match.put("jobUUID", jobUUID);

            Document update = Document.parse(job.toString());
            //FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
            jobCollection.replaceOne(match, update);
            //jobCollection.findOneAndUpdate(match,update,options);
            return true;
        }
        catch (Exception e){
            LOGGER.error("review save error",e);
            return false;
        }
    }


    //领取验收任务
    public static JobBase getCheckJob(String uid,String source){
        System.out.println("\t\t getCheckJob");
        JobBase tmp = new JobBase();
        ParticipantInfo participantInfo = tmp.new ParticipantInfo(uid);
        Document participantDocument = Document.parse(participantInfo.toString());
        participantDocument.put("startTime",sdf.format(new Date()));

        Document match = new Document();
        match.put("leftCheckerCount",new Document("$gt",0));
        match.put("reviewPhaseFinished",true);
        match.put("joinedCheckers.uid",new Document("$ne",uid));
        // 不需要jobDomain的匹配
//        match.put("jobDomain",source);

        Document update = new Document();
        update.put("$inc",new Document("leftCheckerCount",-1));
        update.put("$push",new Document("joinedCheckers",participantDocument));

        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        Document sort = new Document("leftCheckerCount",-1);
        options.sort(sort);
        options.returnDocument(ReturnDocument.AFTER);

        Document res = jobCollection.findOneAndUpdate(match,update,options);
        if (res == null||res.isEmpty()){
            return null;
        }

        JobBase jobBase = Singleton.GSON.fromJson(res.toJson(),JobBase.class);

        return jobBase;
    }
}
