package com.openkg.openbase.service;

import com.openkg.openbase.Manage.JobFactory;
import com.openkg.openbase.Manage.ReviewFactory;
import com.openkg.openbase.common.Singleton;
import com.openkg.openbase.model.DataStream.Element;
import com.openkg.openbase.model.JobBase;
import com.openkg.openbase.model.Subject;
import com.openkg.openbase.model.Triple;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class CheckService {

    private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //验收任务领取
    public Map getJobData(String user_id,String jobDomain) {
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
                            if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && oneRecord.getExecutorID()==user_id){
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
    public boolean commitTask(String user_id, String job_id, int currentPage, int reviewSpan, List<HashMap> data) {
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
                            if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && oneRecord.getExecutorID()==user_id){
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
                        if (oneRecord.getTaskType()== JobBase.TaskType.CHECK && oneRecord.getExecutorID()==user_id){
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
            if(pInfo.getUserId() == user_id){
                pInfo.setCurrentPage(currentPage);
                pInfo.addTimeUsed(reviewSpan);
                pInfo.FinishParticipantJob();
            }
        }
        job.TryToFinishCheckPhase();

        JobFactory.saveAJobByJobID(job_id,job);
        return res;
    }

    //验收任务继续
    public Map continueTask(String user_id, String jobId) {
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
