package com.openkg.openbase.model.historyModule;


import com.openkg.openbase.common.Singleton;

/**
 * Created by mi on 18-10-19.
 */
public class TripleHistory {
    private Integer uid;
    private Integer jobId;
    private Long tripleId;
    private Integer op;

    public Integer getUid(){return uid;}

    public Integer getJobId(){return jobId;}

    public Long getTripleId(){return tripleId;}

    public Integer getOp(){return op;}

    public String toString(){return Singleton.GSON.toJson(this);}

    public TripleHistory(Integer uid, Integer jobId, Long tripleId, Integer op){
        this.uid=uid;
        this.jobId = jobId;
        this.tripleId = tripleId;
        this.op = op;
    }
}
