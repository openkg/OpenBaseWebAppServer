package com.openkg.openbase.model.DataStream;

import com.openkg.openbase.common.Singleton;

import java.util.List;

/**
 * Created by mi on 18-10-19.
 */
public class Element {
    private String subjectId;
    private String subject;
    private Integer reviewedRes;
    private String reviewedStatistics;
    private Integer acceptanceRes;
    private List<Triple> triples;
    private String description;

    public static final int SELECTED = 0;
    public static final int NOT_SELECTED = 1;

    public class Triple{
        private String tripleId;
        private String property;
        private String object;
        private Integer reviewedRes;
        private String reviewedStatistics;
        private Integer acceptanceRes;
        public Triple(String tripleId,String property, String object,Integer op){
            this.tripleId = tripleId;
            this.property = property;
            this.object = object;
            this.reviewedRes = op;
        }
        public Triple(String tripleId,String property, String object,Integer op,String reviewedStatistics){
            this.tripleId = tripleId;
            this.property = property;
            this.object = object;
            this.acceptanceRes = op;
            this.reviewedStatistics = reviewedStatistics;
        }

        public String getProperty(){return property;}
        public String getObject(){return object;}
        public Integer getReviewedRes(){return  reviewedRes;}
        public String getReviewedStatistics(){return reviewedStatistics;}
        public Integer getAcceptanceRes(){return acceptanceRes;}
        public String getTripleId(){return tripleId;}
    }

    public Element(String subjectId,String subject,Integer reviewedRes,String description){
        this.subjectId = subjectId;
        this.subject = subject;
        this.reviewedRes = reviewedRes;
        this.description = description;
    }

    public Element(String subjectId,String subject,List<Triple> triples){
        this.subjectId = subjectId;
        this.subject = subject;
        this.triples = triples;
    }

    public Element(String subjectId,String subject,Integer acceptanceRes,String description,String reviewedStatistics){
        this.subjectId = subjectId;
        this.subject = subject;
        this.acceptanceRes = acceptanceRes;
        this.description = description;
        this.reviewedStatistics = reviewedStatistics;
    }



    public Element(){}

    public List<Triple> getTriples(){return triples;}

    public String getSubjectId(){return subjectId;}
    public String getSubject(){return subject;}
    public String getDescription(){return description;}
    public String getReviewedStatistics(){return reviewedStatistics;}
    public Integer getReviewedRes(){return  reviewedRes;}
    public Integer getAcceptanceRes(){return acceptanceRes;}

    public String toString(){return Singleton.GSON.toJson(this);}



}
