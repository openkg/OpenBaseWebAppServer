package com.openkg.openbase.model.DataStream;

/**
 * Created by mi on 18-10-19.
 */
public class OriginTriple {
    private Long tripleId;
    private String subjectId;

    private String s;
    private String p;
    private String o;


    public Long getTripleId(){return tripleId;}
    public String getSubjectId(){return subjectId;}
    public String getS(){return s;}
    public String getP(){return p;}
    public String getO(){return o;}

}
