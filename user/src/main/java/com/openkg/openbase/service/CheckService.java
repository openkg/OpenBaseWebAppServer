package com.openkg.openbase.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.openkg.openbase.Manage.JobFactory;
import com.openkg.openbase.Manage.ReviewFactory;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.model.DataStream.Element;
import com.openkg.openbase.model.JobBase;
import com.openkg.openbase.model.Res;
import com.openkg.openbase.model.Subject;
import com.openkg.openbase.model.Triple;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class CheckService {

    private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String CheckedEntityCOLLECTION = "checked_entity";
    private static final String ENTITYCOLLECTION = "entity";
    private static final MongoCollection<Document> checkedEntityCOLLECTION = Singleton.mongoDBUtil.getdb().getCollection(CheckedEntityCOLLECTION);
    private static final MongoCollection<Document> entityCollection = Singleton.mongoDBUtil.getdb().getCollection(ENTITYCOLLECTION);

    //验收任务领取
    public Map getJobData(String user_id,String jobDomain) {
        System.out.println("\t getJobData");
        //获取任务id
        JobBase jobBase = JobFactory.getCheckJob(user_id,jobDomain);
        if(jobBase == null){
            return null;
        }
        List<JobBase.TaskRecord> whole_task_list = new ArrayList<JobBase.TaskRecord>();
        whole_task_list.addAll(jobBase.getRecordHistory());
        List<JobBase.TaskRecord> filtered_task_lilst = new ArrayList<JobBase.TaskRecord>();
        for (JobBase.TaskRecord oneTask :whole_task_list){
            if (user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.CHECK){
                filtered_task_lilst.add(oneTask);
            }
        }
        if(filtered_task_lilst.isEmpty()){
            //filtered_task_lilst = JobFactory.assignNewTaskRecordsForUserId(jobBase, user_id);
            String someone_user_id = null;
            for(JobBase.TaskRecord oneTask:whole_task_list){
                if (oneTask.getTaskType() == JobBase.TaskType.REVIEW){
                    someone_user_id = oneTask.getExecutorID();
                    break;
                }
            }
            assert someone_user_id != null;
            for (JobBase.TaskRecord oneTask : whole_task_list){

                if (someone_user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.REVIEW){
                    JobBase.TaskRecord newOneTask = oneTask.CopyToNewRecord();
                    newOneTask.setTaskType(JobBase.TaskType.CHECK);
                    newOneTask.setExecutorID(user_id);
                    jobBase.appendJobHistory(Arrays.asList(newOneTask));
                    filtered_task_lilst.add(newOneTask);
                }
            }
            JobFactory.saveJobBase(jobBase);
        }
        List<Element> elements = ReviewFactory.getElementListFromRecordHistory(filtered_task_lilst, jobBase.getType());

        Map result = new HashMap<String, String>();
        result.put("jobId", jobBase.getJobUUID());
        result.put("type",jobBase.getType().getName());
        if(jobBase.isJobCompleted()){
            result.put("jobStatus", 1);
        }else{
            result.put("jobStatus", 0);
        }
        result.put("currentPage",0);
        result.put("acceptanceSpan",0);
        result.put("elements", elements);
        return result;
    }

    //验收任务保存
    public boolean saveTask(String user_id, String job_id, int currentPage, int reviewSpan, List<HashMap> data) {
        System.out.println("\t saveTask");
        JobBase job = JobFactory.getJobByJobId(job_id);
        String type = job.getType().getName();
        List<JobBase.TaskRecord> whole_task_list = job.getRecordHistory();
        Boolean res = false;
        switch (type){
            case "triple":
                for(HashMap map:data){
                    String jsonStr = Singleton.GSON.toJson(map);
                    Subject subject = Singleton.GSON.fromJson(jsonStr,Subject.class);
                    for(Triple triple:subject.getTriples()){
                        //ReviewFactory.saveRelationHistory(new TripleHistory(user_id,job_id,triple.getTripleId(),triple.getReviewedRes()));
                        for(JobBase.TaskRecord oneRecord :whole_task_list){
                            if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && user_id.equals(oneRecord.getExecutorID())){
                                if(triple.getProperty().equals(oneRecord.getPropertyName())){
                                    if(null == triple.getAcceptanceRes()){
                                        oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                    }else{
                                        switch (triple.getAcceptanceRes()){
                                            case 0:
                                                oneRecord.setOpType(JobBase.OpType.APPROVE);
                                                break;
                                            case 1:
                                                oneRecord.setOpType(JobBase.OpType.DISAPPROVE);
                                                break;
                                            case -1:
                                                oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                res = true;
                break;
            case "entity":
                for(HashMap map:data){
                    String jsonStr = Singleton.GSON.toJson(map);
                    Subject subject = Singleton.GSON.fromJson(jsonStr,Subject.class);
                    //ReviewFactory.saveEntityHistory(new EntityHistory(user_id,job_id,subject.getSubjectId(),subject.getReviewedRes()));
                    for(JobBase.TaskRecord oneRecord :whole_task_list){
                        if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && user_id.equals(oneRecord.getExecutorID())){
                            if (null == subject.getAcceptanceRes()){
                                oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                            }else{
                                switch (subject.getAcceptanceRes()){
                                    case 0:
                                        oneRecord.setOpType(JobBase.OpType.APPROVE);
                                        break;
                                    case 1:
                                        oneRecord.setOpType(JobBase.OpType.DISAPPROVE);
                                        break;
                                    case -1:
                                        oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                }
                            }
                        }
                    }
                }
                res = true;
                break;
            default:
                return false;
        }
        // 将状态信息反馈给任务管理器, 保存信息
        for(JobBase.ParticipantInfo pInfo :job.getJoinedCheckers()){
            if(user_id.equals(pInfo.getUserId())){
                pInfo.setCurrentPage(currentPage);
                pInfo.addTimeUsed(reviewSpan);
            }
        }
        JobFactory.saveAJobByJobID(job_id,job);

        return res;
    }

    //验收任务提交
    public Res commitTask(String user_id, String job_id, int currentPage, int reviewSpan, List<HashMap> data) {
        System.out.println("\t commitTask");
        JobBase job = JobFactory.getJobByJobId(job_id);
        String type = job.getType().getName();
        List<JobBase.TaskRecord> whole_task_list = job.getRecordHistory();
//        Boolean res = false;
        Res res = new Res();
        switch (type){
            case "triple":
                for(HashMap map:data){
                    String jsonStr = Singleton.GSON.toJson(map);
                    Subject subject = Singleton.GSON.fromJson(jsonStr,Subject.class);
                    for(Triple triple:subject.getTriples()){
                        //ReviewFactory.saveRelationHistory(new TripleHistory(user_id,job_id,triple.getTripleId(),triple.getReviewedRes()));
                        for(JobBase.TaskRecord oneRecord :whole_task_list){
                            if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && oneRecord.getExecutorID().equals(user_id)){
                                if(triple.getProperty().equals(oneRecord.getPropertyName())){
                                    if(null == triple.getAcceptanceRes()){
                                        oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                    }else{
                                        switch (triple.getAcceptanceRes()){
                                            case 0:
                                                oneRecord.setOpType(JobBase.OpType.APPROVE);
                                                break;
                                            case 1:
                                                oneRecord.setOpType(JobBase.OpType.DISAPPROVE);
                                                break;
                                            case -1:
                                                oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                res.setCode(1);
//                res = true;
                break;
            case "entity":
                for(HashMap map:data){
                    String jsonStr = Singleton.GSON.toJson(map);
                    Subject subject = Singleton.GSON.fromJson(jsonStr,Subject.class);
                    //ReviewFactory.saveEntityHistory(new EntityHistory(user_id,job_id,subject.getSubjectId(),subject.getReviewedRes()));
                    for(JobBase.TaskRecord oneRecord :whole_task_list){
                        if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && user_id.equals(oneRecord.getExecutorID())){
                            if (null == subject.getAcceptanceRes()){
                                oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                            }else{
                                switch (subject.getAcceptanceRes()){
                                    case 0:
                                        oneRecord.setOpType(JobBase.OpType.APPROVE);
                                        break;
                                    case 1:
                                        oneRecord.setOpType(JobBase.OpType.DISAPPROVE);
                                        break;
                                    case -1:
                                        oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                }
                            }
                        }
                    }
                }
//                res = true;
                res.setCode(1);
                break;
            default:
                res.setCode(0);
                return res;
//                return false;
        }
        // 将状态信息反馈给任务管理器, 保存信息
        for(JobBase.ParticipantInfo pInfo :job.getJoinedCheckers()){
            if(pInfo.getUserId().equals(user_id)){
                pInfo.setCurrentPage(currentPage);
                pInfo.addTimeUsed(reviewSpan);
                pInfo.FinishParticipantJob();
            }
        }

//        job.TryToFinishCheckPhase();
        boolean flag = job.TryToFinishCheckPhase();
        JobFactory.saveAJobByJobID(job_id,job);

        if(flag){
            System.out.println("验收任务完成，给验收/审核者发放荣誉值");
            res.setCode(2);
            // 这里表示job finished，接口逻辑判断写在这里

            // 1. 拿到每个checker和reviewer的user_id以及对应的data_id
//            Map<String, List> honor_info = new HashMap<>();
            Map<String, Map> user_object = new HashMap<>();
            Set<String> entid_set = new HashSet<String>();
//            Map<String, Integer> data_amount = new HashMap<>();

            // List<HashMap> data 是job的数据
            switch (type){
                case "triple":
                    for(JobBase.TaskRecord oneRecord :whole_task_list){
//                   System.out.println("\t triple.getTripleId: " + triple.getTripleId());
//                   System.out.println("\t TaskType " + oneRecord.getTaskType());
//                   System.out.println("\t oneRecord.getExecutorID  " + oneRecord.getExecutorID());
                        String executor_id = oneRecord.getExecutorID();
                        String entid = oneRecord.getEntityID();

                        // 97c9eff7ca1646689b4ccec86adc2a25_family, 获得entity id
//                        String entid = (triple.getTripleId().split("_"))[0];

                        // 先判断有没有这个人
                        if (user_object.containsKey(executor_id)){
                            // 有这个人，不管他
                        }
                        else{
                            // 没有这个人，创建一个
                            Map<String, Integer> data_amount = new HashMap<>();
                            user_object.put(executor_id, data_amount);
                        }

                        if((user_object.get(executor_id)).containsKey(entid)){
                            // 如果这个用户，已经有这个entid了
                            int original = (int)((user_object.get(executor_id)).get(entid));
                            (user_object.get(executor_id)).put(entid, original + 1);
                        }
                        else{
                            // 如果这个用户没有当前entid
                            (user_object.get(executor_id)).put(entid, 1);
                        }

                        entid_set.add(entid);

//                        map_temp.clear();
//                        map_temp.put("dataId", entid);
//                        map_temp.put("version", "");
//                        // 统计check的数量信息
//                        if(honor_info.containsKey(executor_id)){
//                            // 已经有这个id
//                            honor_info.get(executor_id).add(map_temp);
//                        }
//                        else{
//                            // 这个id还没有
//                            List<Object> new_list = new ArrayList<>();
//                            new_list.add(map_temp);
//                            honor_info.put(executor_id, new_list);
//                        }

                    }

            }

//            System.out.println("123 \t " + honor_info);

            // 2. string: user_id, string: [data_id list]
            // 参数是 Map<String, List<String>>: honor_info, 作为参数传到外面的controller

            // 3. 从entity里面entid_set对应的document，存储到checked_entity里面去。
            Document oneDocument;

            for(String entity_id: entid_set){
                // 从"entity"查询得到id的one_document, 得到data_doc_list
                Document match_first = new Document();
                match_first.put("@id",entity_id);
                MongoCursor<Document> cursor_first = entityCollection.find(match_first).iterator();
                if (cursor_first.hasNext()){
                    oneDocument = cursor_first.next();
                    Document match_second = new Document();
                    //one_document再存入"checked_entity"
                    match_second.put("@id",entity_id);
                    MongoCursor<Document> cursor_second = checkedEntityCOLLECTION.find(match_second).iterator();
                    // 去重复, 如果找不到该entity_id
                    if (false == cursor_second.hasNext()){
                        checkedEntityCOLLECTION.insertOne(oneDocument);
                    }

                }
            }
            res.setData(user_object);

        }
        return res;
    }

    //验收任务继续
    public Map continueTask(String user_id, String jobId) {
        System.out.println("\t continueTask");
        JobBase jobBase = JobFactory.getJobByJobId(jobId);
        if(jobBase == null){
            return null;
        }
        List<JobBase.TaskRecord> whole_task_list = jobBase.getRecordHistory();
        List<JobBase.TaskRecord> filtered_task_lilst = new ArrayList<JobBase.TaskRecord>();
        for (JobBase.TaskRecord oneTask :whole_task_list){
            if (user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.CHECK){
                filtered_task_lilst.add(oneTask);
            }
        }
        List<Element> elements = ReviewFactory.getElementListFromRecordHistory(filtered_task_lilst, jobBase.getType());

        Integer currentPage = 0;
        Integer reviewSpan = 0;
        for(JobBase.ParticipantInfo pInfo :jobBase.getJoinedReviewers()){
            if(user_id.equals(pInfo.getUserId())){
                currentPage = pInfo.getCurrentPage();
                reviewSpan = pInfo.getTimeUsed();
            }
        }

        Map result = new HashMap<String, String>();
        result.put("jobId", jobBase.getJobUUID());
        result.put("type",jobBase.getType().getName());
        if(jobBase.isJobCompleted()){
            result.put("jobStatus", 1);
        }else{
            result.put("jobStatus", 0);
        }
        result.put("currentPage", currentPage);
        result.put("reviewSpan", reviewSpan);
        result.put("elements", elements);
        return result;
    }

    //获取审核记录信息
    public Map getState(String user_id,String jobDomain) {
        System.out.println("\t getState");
        Map map = new HashMap<String, String>();
        int reviewedEntityNum = 0;
        int reviewedTripleNum = 0;
        int total_jobs = 0;
        int finished_jobs = 0;
        Integer timeSpan = 0;
        List<JobBase> jobs = JobFactory.getCheckJobsByUser(user_id, jobDomain);
        List<Map> reviewRecord = new ArrayList<>();
        for(JobBase job :jobs){
            JobBase.ParticipantInfo target_pInfo = null;
            for(JobBase.ParticipantInfo pInfo :job.getJoinedCheckers()){
                if(user_id.equals(pInfo.getUserId())){
                    target_pInfo = pInfo;
                }
            }
            if(null != target_pInfo){
                total_jobs = total_jobs + 1;
                timeSpan+=target_pInfo.getTimeUsed();
                Map tem = new HashMap();
                tem.put("jobId",job.getJobUUID());
                tem.put("startTime",target_pInfo.getStartTime());
                tem.put("reviewSpan",target_pInfo.getTimeUsed());
                if(target_pInfo.isParticipantFinishedHisJob()){
                    tem.put("jobStatus", 1);
                }else{
                    tem.put("jobStatus", 0);
                }
                tem.put("jobDomain", job.getJobDomain());
                tem.put("currentPage",target_pInfo.getCurrentPage());
                reviewRecord.add(tem);
                if (target_pInfo.isParticipantFinishedHisJob()) {
                    finished_jobs = finished_jobs + 1;
                }
            }
            List<JobBase.TaskRecord> whole_task_list = job.getRecordHistory();
            List<JobBase.TaskRecord> filtered_task_lilst = new ArrayList<JobBase.TaskRecord>();
            for (JobBase.TaskRecord oneTask :whole_task_list){
                if (user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.CHECK){
                    filtered_task_lilst.add(oneTask);
                }
            }
            if(job.getType() == JobBase.JobType.ENTITY){
                reviewedEntityNum = reviewedEntityNum + filtered_task_lilst.size();
            }else{
                reviewedTripleNum = reviewedTripleNum + filtered_task_lilst.size();
            }
        }

        // 审核时间与任务相关, 任务管理器提供接口即可
        map.put("reviewedEntityNum", reviewedEntityNum);
        map.put("reviewedTripleNum", reviewedTripleNum);
        map.put("reviewTimeSpan",timeSpan);
        map.put("reviewRecord",reviewRecord);
        map.put("total",total_jobs);
        map.put("finished",finished_jobs);
        return map;
    }
}
