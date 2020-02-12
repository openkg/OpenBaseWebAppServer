package com.openkg.openbase.model.historyModule;

import com.openkg.openbase.common.Singleton;

/**
 * Created by mi on 18-10-19.
 */
public class EntityHistory {
    private Integer uid;
    private Integer jobId;
    private String entityId;
    private Integer op;

    public Integer getUid(){return uid;}

    public Integer getJobId(){return jobId;}

    public String getEntityId(){return entityId;}

    public Integer getOp(){return op;}

    public String toString(){return Singleton.GSON.toJson(this);}

    public EntityHistory(Integer uid, Integer jobId, String entityId, Integer op){
        this.uid=uid;
        this.jobId = jobId;
        this.entityId = entityId;
        this.op = op;
    }
}
