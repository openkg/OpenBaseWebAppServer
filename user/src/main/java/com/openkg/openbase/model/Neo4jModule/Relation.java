package com.openkg.openbase.model.Neo4jModule;

/**
 * Created by mi on 18-10-22.
 */
public class Relation {
    private String start;
    private String end;
    private String property;

    public Relation(String start,String end,String property){
        this.start = start;
        this.end= end;
        this.property = property;
    }

    public String getStart(){return start;}
    public String getEnd(){return end;}
    public String getProperty(){return property;}
}
