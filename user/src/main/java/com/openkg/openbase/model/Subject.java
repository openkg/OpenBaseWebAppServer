package com.openkg.openbase.model;


import java.util.List;

public class Subject {
    String jobId;
    String subjectId;
    String subject;
    Integer reviewedRes;
    Integer acceptanceRes;
    int reviewer;
    int accepter;
    List<Triple> triples;

    public Subject() {
    }

    public Subject(String jobId, String subjectId, String subject, Integer reviewedRes, Integer acceptanceRes, int reviewer, int accepter) {
        this.jobId = jobId;
        this.subjectId = subjectId;
        this.subject = subject;
        this.reviewedRes = reviewedRes;
        this.acceptanceRes = acceptanceRes;
        this.reviewer = reviewer;
        this.accepter = accepter;
    }

    public Subject(String jobId, String subjectId, String subject, Integer reviewedRes, Integer acceptanceRes, int reviewer, int accepter, List<Triple> triples) {
        this.jobId = jobId;
        this.subjectId = subjectId;
        this.subject = subject;
        this.reviewedRes = reviewedRes;
        this.acceptanceRes = acceptanceRes;
        this.reviewer = reviewer;
        this.accepter = accepter;
        this.triples = triples;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public void setAcceptanceRes(Integer acceptanceRes) {
        this.acceptanceRes = acceptanceRes;
    }

    public List<Triple> getTriples() {
        return triples;
    }

    public void setTriples(List<Triple> triples) {
        this.triples = triples;
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

    @Override
    public String toString() {
        return "Subject{" +
                "jobId=" + jobId +
                ", subjectId='" + subjectId + '\'' +
                ", subject='" + subject + '\'' +
                ", reviewedRes='" + reviewedRes + '\'' +
                ", acceptanceRes=" + acceptanceRes +
                ", reviewer=" + reviewer +
                ", accepter=" + accepter +
                ", triples=" + triples +
                '}';
    }

}
