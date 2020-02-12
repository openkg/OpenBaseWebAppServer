package com.openkg.openbase.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Triple {
    String tripleId;
    int job_id;
    String property;
    String object;
    Integer reviewedRes;
    Integer acceptanceRes;
    int reviewer;
    int accepter;

    public Triple() {
    }

    public Triple(String tripleId, int job_id, String property, String object, Integer reviewedRes, int acceptanceRes, int reviewer, int accepter) {
        this.tripleId = tripleId;
        this.job_id = job_id;
        this.property = property;
        this.object = object;
        this.reviewedRes = reviewedRes;
        this.acceptanceRes = acceptanceRes;
        this.reviewer = reviewer;
        this.accepter = accepter;
    }

    public String getProperty() {
        if(null != property && !property.isEmpty()){
            return  property;
        }else{
            property = tripleId.split("_")[1];
            return property;
        }
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getReviewedRes() {
        return reviewedRes;
    }

    public void setReviewedRes(Integer reviewedRes) {
        this.reviewedRes = reviewedRes;
    }

    public Integer getAcceptanceRes() {
        return acceptanceRes;
    }

    public void setAcceptanceRes(int acceptanceRes) {
        this.acceptanceRes = acceptanceRes;
    }

    public String getTripleId() {
        return tripleId;
    }

    public void setTripleId(String tripleId) {
        this.tripleId = tripleId;
    }

    public int getReviewer() {
        return reviewer;
    }

    public void setReviewer(int reviewer) {
        this.reviewer = reviewer;
    }

    public int getAccepter() {
        return accepter;
    }

    public void setAccepter(int accepter) {
        this.accepter = accepter;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }
}
