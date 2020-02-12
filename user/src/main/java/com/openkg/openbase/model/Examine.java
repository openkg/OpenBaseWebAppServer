package com.openkg.openbase.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Examine {
    int projexamine_id;
    String ghcheck_id;
    int projchecker_checker;
    int opinion_id;
    String projexamine_comment;
    Timestamp examine_time;
    int triple_id;

    public int getProjexamine_id() {
        return projexamine_id;
    }

    public void setProjexamine_id(int projexamine_id) {
        this.projexamine_id = projexamine_id;
    }

    public String getGhcheck_id() {
        return ghcheck_id;
    }

    public void setGhcheck_id(String ghcheck_id) {
        this.ghcheck_id = ghcheck_id;
    }

    public int getProjchecker_checker() {
        return projchecker_checker;
    }

    public void setProjchecker_checker(int projchecker_checker) {
        this.projchecker_checker = projchecker_checker;
    }

    public int getOpinion_id() {
        return opinion_id;
    }

    public void setOpinion_id(int opinion_id) {
        this.opinion_id = opinion_id;
    }

    public String getProjexamine_comment() {
        return projexamine_comment;
    }

    public void setProjexamine_comment(String projexamine_comment) {
        this.projexamine_comment = projexamine_comment;
    }

    public Timestamp getExamine_time() {
        return examine_time;
    }

    public void setExamine_time(Timestamp examine_time) {
        this.examine_time = examine_time;
    }

    public int getTriple_id() {
        return triple_id;
    }

    public void setTriple_id(int triple_id) {
        this.triple_id = triple_id;
    }
}
