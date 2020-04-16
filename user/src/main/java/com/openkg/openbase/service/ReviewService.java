package com.openkg.openbase.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.openkg.openbase.Manage.ReviewFactory;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.Manage.JobFactory;
import com.openkg.openbase.model.DataStream.Element;
import com.openkg.openbase.model.JobBase;
import com.openkg.openbase.model.Subject;
import com.openkg.openbase.model.Triple;
import com.openkg.openbase.model.User;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ReviewService {

    private static final String USERCOLLECTION = "User";
    private static final MongoCollection<Document> userCollection = Singleton.mongoDBUtil.getdb().getCollection(USERCOLLECTION);


    //审核任务领取
    public Map getReivewJob(String user_id, String jobDomain) {
        System.out.println("\t getReivewJob");
        //根据user_id, 获取job_base, 一个job
        JobBase jobBase = JobFactory.getReviewJob(user_id,jobDomain);
        if(jobBase == null){
            return null;
        }
        List<JobBase.TaskRecord> whole_task_list = new ArrayList<JobBase.TaskRecord>();
        whole_task_list.addAll(jobBase.getRecordHistory()); //返回值 List<TaskRecord>
        List<JobBase.TaskRecord> filtered_task_lilst = new ArrayList<JobBase.TaskRecord>();
        for (JobBase.TaskRecord oneTask :whole_task_list){
            if (user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.REVIEW){
                filtered_task_lilst.add(oneTask);
            }
        }
        if(filtered_task_lilst.isEmpty()){
//            filtered_task_lilst = JobFactory.assignNewTaskRecordsForUserId(jobBase, user_id);
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
                    newOneTask.setExecutorID(user_id);
                    jobBase.appendJobHistory(Arrays.asList(newOneTask));
                    filtered_task_lilst.add(newOneTask);
                }
            }
            JobFactory.saveJobBase(jobBase);
        }

        //这个函数是从job base生成问题的
        //filtered_task_list是 List<JobBase.TaskRecord>类型
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

    //保存任务
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
                        for(JobBase.TaskRecord oneRecord :whole_task_list){
                            if (oneRecord.getTaskType()== JobBase.TaskType.REVIEW && user_id.equals(oneRecord.getExecutorID())){
                                if(triple.getProperty().equals(oneRecord.getPropertyName())){
                                    switch (triple.getReviewedRes()){
                                        case 0:
                                            oneRecord.setOpType(JobBase.OpType.APPROVE);
                                            break;
                                        case 1:
                                            oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                            break;
                                        case 21:
                                        case 22:
                                        case 23:
                                            oneRecord.setOpType(JobBase.OpType.DISAPPROVE);
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
                    for(JobBase.TaskRecord oneRecord :whole_task_list){
                        if (oneRecord.getTaskType()== JobBase.TaskType.REVIEW && user_id.equals(oneRecord.getExecutorID())){
                            switch (subject.getReviewedRes()){
                                case 0:
                                    oneRecord.setOpType(JobBase.OpType.APPROVE);
                                    break;
                                case 1:
                                    oneRecord.setOpType(JobBase.OpType.UNDEFINED);
                                    break;
                                case 21:
                                case 22:
                                case 23:
                                    oneRecord.setOpType(JobBase.OpType.DISAPPROVE);
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
        for(JobBase.ParticipantInfo pInfo :job.getJoinedReviewers()){
            if(user_id.equals(pInfo.getUserId())){
                pInfo.setCurrentPage(currentPage);
                pInfo.addTimeUsed(reviewSpan);
            }
        }
        JobFactory.saveAJobByJobID(job_id,job);

        return res;
    }

    //提交审核任务
    public boolean commitTask(String user_id, String job_id, int currentPage, int reviewSpan, List<HashMap> data) {
        System.out.println("\t commitTask");
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
                        for(JobBase.TaskRecord oneRecord :whole_task_list){
                            if (oneRecord.getTaskType()== JobBase.TaskType.REVIEW && user_id.equals(oneRecord.getExecutorID())){
                                if(triple.getProperty().equals(oneRecord.getPropertyName())){
                                    switch (triple.getReviewedRes()){
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
                res = true;
                break;
            case "entity":
                for(HashMap map:data){
                    String jsonStr = Singleton.GSON.toJson(map);
                    Subject subject = Singleton.GSON.fromJson(jsonStr,Subject.class);
                    for(JobBase.TaskRecord oneRecord :whole_task_list){
                        if (oneRecord.getTaskType()== JobBase.TaskType.REVIEW && user_id.equals(oneRecord.getExecutorID())){
                            switch (subject.getReviewedRes()){
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
                res = true;
                break;
            default:
                return false;
        }
        // 将状态信息反馈给任务管理器, 保存信息
        for(JobBase.ParticipantInfo pInfo :job.getJoinedReviewers()){
            if(user_id.equals(pInfo.getUserId())){
                pInfo.setCurrentPage(currentPage);
                pInfo.addTimeUsed(reviewSpan);
                pInfo.FinishParticipantJob();
            }
        }
        job.TryToFinishReviewPhase();

        JobFactory.saveAJobByJobID(job_id,job);
        return res;
    }

    //继续审核
    public Map continueTask(String user_id, String jobId) {
        System.out.println("\t continueTask");
        JobBase jobBase = JobFactory.getJobByJobId(jobId);
        if(jobBase == null){
            return null;
        }
        List<JobBase.TaskRecord> whole_task_list = jobBase.getRecordHistory();
        System.out.println("1st Code comes here !!!! by daizhen");
        List<JobBase.TaskRecord> filtered_task_lilst = new ArrayList<JobBase.TaskRecord>();
        for (JobBase.TaskRecord oneTask :whole_task_list){
            if (user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.REVIEW){
                filtered_task_lilst.add(oneTask);
            }
        }
        System.out.println("2nd Code comes here !!!! by daizhen");
        List<Element> elements = ReviewFactory.getElementListFromRecordHistory(filtered_task_lilst, jobBase.getType());
        System.out.println("3rd Code comes here !!!! by daizhen");
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
    
    public int updateUserReviewSeconds(String jobDomain) {
    		int total = 0;
            MongoCursor<Document> cursor = userCollection.find().iterator();
            while (cursor.hasNext()){
              User u = User.fromDocument(cursor.next());
                String user_id = u.getUser_id();
                List<JobBase> jobs = JobFactory.getReviewJobsByUser(user_id, jobDomain);
                Integer reviewSpan = 0;
                for(JobBase job :jobs){
                    JobBase.ParticipantInfo target_pInfo = null;
                    for(JobBase.ParticipantInfo pInfo :job.getJoinedReviewers()){
                        if(user_id.equals(pInfo.getUserId())){
                            target_pInfo = pInfo;
                        }
                    }
                    if(null != target_pInfo){
                        reviewSpan+=target_pInfo.getTimeUsed();
                    }
                }
                total += reviewSpan;
            }

    		return total;
    }

    //获取审核记录信息
    public Map getState(String user_id, String jobDomain) {
        System.out.println("\t getState");
        Map map = new HashMap<String, String>();
        int reviewedEntityNum = 0;
        int reviewedTripleNum = 0;
        int total_jobs = 0;
        int finished_jobs = 0;
        Integer timeSpan = 0;
        List<JobBase> jobs = JobFactory.getReviewJobsByUser(user_id, jobDomain);
        List<Map> reviewRecord = new ArrayList<>();
        for(JobBase job :jobs){
            JobBase.ParticipantInfo target_pInfo = null;
            for(JobBase.ParticipantInfo pInfo :job.getJoinedReviewers()){
                if( user_id.equals(pInfo.getUserId())){
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
                if (user_id.equals(oneTask.getExecutorID()) && oneTask.getTaskType() == JobBase.TaskType.REVIEW){
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
