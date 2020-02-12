package com.openkg.openbase.model;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Graphcheck implements Serializable {
    int ghcheck_id;
    int assign_id;
    int triple_id;
    int triple_position;
    int opinion_id;
    String ghcheck_afteredit;
    String ghcheck_comment;
    Timestamp check_time;

    public Graphcheck() {
    }

    public int getGhcheck_id() {
        return ghcheck_id;
    }

    public void setGhcheck_id(int ghcheck_id) {
        this.ghcheck_id = ghcheck_id;
    }

    public int getAssign_id() {
        return assign_id;
    }

    public void setAssign_id(int assign_id) {
        this.assign_id = assign_id;
    }

    public int getTriple_id() {
        return triple_id;
    }

    public void setTriple_id(int triple_id) {
        this.triple_id = triple_id;
    }

    public int getTriple_position() {
        return triple_position;
    }

    public void setTriple_position(int triple_position) {
        this.triple_position = triple_position;
    }

    public int getOpinion_id() {
        return opinion_id;
    }

    public void setOpinion_id(int opinion_id) {
        this.opinion_id = opinion_id;
    }

    public String getGhcheck_afteredit() {
        return ghcheck_afteredit;
    }

    public void setGhcheck_afteredit(String ghcheck_afteredit) {
        this.ghcheck_afteredit = ghcheck_afteredit;
    }

    public String getGhcheck_comment() {
        return ghcheck_comment;
    }

    public void setGhcheck_comment(String ghcheck_comment) {
        this.ghcheck_comment = ghcheck_comment;
    }

    public Timestamp getCheck_time() {
        return check_time;
    }

    public void setCheck_time(Timestamp check_time) {
        this.check_time = check_time;
    }

    @Override
    public String toString() {
        return "Graphcheck{" +
                "ghcheck_id=" + ghcheck_id +
                ", assign_id=" + assign_id +
                ", triple_id=" + triple_id +
                ", triple_position=" + triple_position +
                ", opinion_id=" + opinion_id +
                ", ghcheck_afteredit='" + ghcheck_afteredit + '\'' +
                ", ghcheck_comment='" + ghcheck_comment + '\'' +
                ", check_time=" + check_time +
                '}';
    }
}
