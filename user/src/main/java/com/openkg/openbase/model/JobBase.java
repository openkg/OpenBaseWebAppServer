package com.openkg.openbase.model;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.openkg.openbase.common.Singleton;
import org.bson.Document;
import org.springframework.scheduling.config.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Created by mi on 18-10-15.
 */
 public class JobBase {
    private static final String reviewPrefix = "reviewers";
    private static final String checkPrefix = "checkers";

    private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String jobUUID;
    private Boolean jobCompletelyFinished;
    private JobType jobType;
    private Integer leftReviewerCount;
    private Integer leftCheckerCount;
    private Boolean reviewPhaseFinished;
    private Boolean checkPhaseFinished;
    private List<ParticipantInfo> joinedReviewers;
    private List<ParticipantInfo> joinedCheckers;
    private List<TaskRecord> recordHistory;
    private String jobDomain; // 目前和前端约定好的字段是 "kg4ai" "buddism" "agriculture" "people"

    public void generateUUID(){
        if(null == jobUUID || jobUUID.isEmpty()){
            jobUUID = UUID.randomUUID().toString();
        }
    }
    public String getJobDomain(){return jobDomain;}
    public List<ParticipantInfo> getJoinedReviewers(){
        return joinedReviewers;
    }

    public List<ParticipantInfo> getJoinedCheckers(){
        return joinedCheckers;
    }

    public String getJobUUID(){
        return jobUUID;
    }

    public List<TaskRecord> getRecordHistory(){
        return recordHistory;
    }
    public void setJobHistory(List<TaskRecord> recordHistory){
        this.recordHistory = recordHistory;
    }
    public void appendJobHistory(List<TaskRecord> recordHistory){
        this.recordHistory.addAll(recordHistory);
    }

    public JobBase(JobType jobType, Integer reviewerCount, Integer checkerCount, String jobDomain){
        this.jobType = jobType;
        this.leftReviewerCount = reviewerCount;
        this.leftCheckerCount = checkerCount;
        this.reviewPhaseFinished = false;
        this.checkPhaseFinished = false;
        this.joinedCheckers = new ArrayList<ParticipantInfo>();
        this.joinedReviewers = new ArrayList<ParticipantInfo>();
        this.recordHistory = new ArrayList<TaskRecord>();
        this.jobDomain = jobDomain;
        this.jobCompletelyFinished = false;
    }

    public JobBase(){

    }

    public boolean joinNewReviewer(String uid){
        if(leftReviewerCount > 0){
            leftReviewerCount = leftReviewerCount - 1;
            ParticipantInfo new_pInfo = new ParticipantInfo(uid);
            this.joinedReviewers.add(new_pInfo);
            return true;
        }
        return false;
    }

    public String toString(){
        return Singleton.GSON.toJson(this);
    }

    public class ParticipantInfo {
        private String uid;
        private String startTime;
        private Integer timeUsed;
        private boolean isThisParticipantAlreadyFinishedHisJob;
        private Integer currentPage;

        public ParticipantInfo(String uid){
            this.uid = uid;
            this.timeUsed = 0;
            this.startTime = sdf.format(new Date());
            this.isThisParticipantAlreadyFinishedHisJob = false;
            this.currentPage = 0;
        }

        public String toString(){
            return Singleton.GSON.toJson(this);
        }

        public String getStartTime(){
            return startTime;
        }

        public String getUserId(){
            return uid;
        }

        public void setCurrentPage(Integer currentPage){
            this.currentPage = currentPage;
        }

        public Integer getCurrentPage(){
            return this.currentPage;
        }

        public void addTimeUsed(Integer timeElapsed){
            this.timeUsed = this.timeUsed + timeElapsed;
        }

        public void FinishParticipantJob(){
            System.out.println("\t\t 完成了审核/验收任务！！！！");
            this.isThisParticipantAlreadyFinishedHisJob = true;
        }

        public boolean isParticipantFinishedHisJob(){
            return this.isThisParticipantAlreadyFinishedHisJob;
        }

        public Integer getTimeUsed(){
            return this.timeUsed;
        }

    }

    public class TaskRecord{
        private String entityID;
        private String propertyName; // Summary 或者 ...
        private String executorID;
        private TaskType taskType; // REVIEW or CHECK
        private OpType opType; // DISAPPROVE 或者 ...
        private List<String> opPropertyValues;
        private List<String> opPropertyRelations;

        public String toString(){
            return Singleton.GSON.toJson(this);
        }
        public String getExecutorID(){
            return executorID;
        }
        public void setExecutorID(String user_id){this.executorID=user_id;}
        public TaskType getTaskType(){
            return taskType;
        }
        public void setTaskType(TaskType taskType){this.taskType=taskType;}
        public String getPropertyName(){
            return propertyName;
        }
        public OpType getOpType(){
            return opType;
        }
        public String getEntityID(){
            return entityID;
        }
        public void setOpType(OpType opType){
            this.opType = opType;
        }

        public TaskRecord CopyToNewRecord(){
            TaskRecord newRecord = new TaskRecord();
            newRecord.entityID = this.entityID;
            newRecord.propertyName = this.propertyName;
            newRecord.executorID = this.executorID;
            newRecord.taskType = this.taskType;
            newRecord.opType = OpType.UNDEFINED;
            if(null != this.opPropertyValues){
                newRecord.opPropertyValues = new ArrayList<String>();
                newRecord.opPropertyValues.addAll(this.opPropertyValues);
            }else{
                newRecord.opPropertyValues = null;
            }
            if(null != this.opPropertyRelations){
                newRecord.opPropertyRelations = new ArrayList<String>();
                newRecord.opPropertyRelations.addAll(this.opPropertyRelations);
            }else{
                newRecord.opPropertyRelations = null;
            }
            return  newRecord;
        }
    }


    public JobType getType(){return jobType;}
    public boolean isJobCompleted(){
        if(reviewPhaseFinished && checkPhaseFinished){
            jobCompletelyFinished = true;
        }
        return jobCompletelyFinished;
    }

    public void TryToFinishReviewPhase(){
        if(leftReviewerCount <= 0){
            boolean all_reviewers_done = true;
            for (ParticipantInfo pInfo :joinedReviewers){
                if(!pInfo.isThisParticipantAlreadyFinishedHisJob){
                    all_reviewers_done = false;
                }
            }
            if(all_reviewers_done){
                reviewPhaseFinished = true;
            }
        }
    }

    public boolean TryToFinishCheckPhase(){
        if(leftCheckerCount <= 0){
            boolean all_checkers_done = true;
            for (ParticipantInfo pInfo :joinedCheckers){
                if(!pInfo.isThisParticipantAlreadyFinishedHisJob){
//                    System.out.println("\t\t false");
                    all_checkers_done = false;
                }
            }
            if(all_checkers_done){
//                System.out.println("\t\t 111111");
                checkPhaseFinished = true;
            }
            if(reviewPhaseFinished && checkPhaseFinished){
//                System.out.println("\t\t 2222222");
                System.out.println("\t\t 完成了验收任务！！！！");
                jobCompletelyFinished = true;
                return true;
            }
        }
        return false;
    }

    public boolean isReviewPhaseFinished(){
        if(leftReviewerCount <= 0 && joinedReviewers != null && !joinedReviewers.isEmpty()){
            boolean all_reviewers_done = true;
            for (ParticipantInfo pInfo :joinedReviewers){
                if(!pInfo.isThisParticipantAlreadyFinishedHisJob){
                    all_reviewers_done = false;
                }
            }
            if(all_reviewers_done){
                return true;
            }
        }
        return false;
    }
    public  boolean isCheckPhaseFinished(){
        if(leftCheckerCount <= 0 && joinedCheckers != null && !joinedCheckers.isEmpty()){
            boolean all_checkers_done = true;
            for (ParticipantInfo pInfo :joinedCheckers){
                if(!pInfo.isThisParticipantAlreadyFinishedHisJob){
                    all_checkers_done = false;
                }
            }
            if(all_checkers_done){
                return true;
            }
        }
        return false;
    }

    public List<TaskRecord> parseToTripleTaskRecord(Document entityDocument, String user_id){
        List<TaskRecord> tr_list = new ArrayList<>();
        Set<String> keys =  entityDocument.keySet();
        for(String k :keys){
            if (!k.equals("summary") && !k.equals("_id") && !k.startsWith("@") && !k.equals("appID")){
                TaskRecord tr = new TaskRecord();
                tr.entityID = entityDocument.getString("@id");
                tr.propertyName = k;
                tr.executorID = user_id;
                tr.opType = OpType.UNDEFINED;
                tr.taskType = TaskType.REVIEW;
                tr.opPropertyValues = new ArrayList<>();
                tr.opPropertyRelations = new ArrayList<>();
                tr_list.add(tr);
            }
        }
        return tr_list;
    }
    public TaskRecord parseToEntityTaskRecord(Document entityDocument, String user_id){
        TaskRecord tr = new TaskRecord();
        tr.entityID = entityDocument.getString("@id");
        tr.propertyName = "summary";
        tr.executorID = user_id;
        tr.opType = OpType.UNDEFINED;
        tr.taskType = TaskType.REVIEW;
        tr.opPropertyValues = new ArrayList<>();
        tr.opPropertyRelations = new ArrayList<>();
        return  tr;
    }

    public enum JobType{
        ENTITY(0, "entity"), TRIPLE(1, "triple");
        private Integer code;
        private String name;
        JobType(Integer code, String name){
            this.code = code;
            this.name = name;
        }

        public String getName(){
            return  name;
        }
    }
    public enum TaskType{
        REVIEW(0, "review"), CHECK(1, "check");
        private Integer code;
        private String name;
        TaskType(Integer code, String name){
            this.code = code;
            this.name = name;
        }

        public String getName(){
            return  name;
        }
    }
    public enum OpType{
        APPROVE(0, "approve"), DISAPPROVE(2, "disapprove"), UNDEFINED(1, "undefined");
        private Integer code;
        private String name;
        OpType(Integer code, String name){
            this.code = code;
            this.name = name;
        }

        public String getName(){
            return  name;
        }
        public Integer getCode(){
            return code;
        }
    }
}
