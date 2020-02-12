package com.openkg.openbase.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExamineRecord {
    int triple_id;
    String triple_subject;
    String triple_property;
    String triple_object;
    List<Integer> opinion_ids_sub = new ArrayList<>();
    List<Integer> opinion_ids_pro = new ArrayList<>();
    List<Integer> opinion_ids_obj = new ArrayList<>();
    int opinion_sub;
    int opinion_pro;
    int opinion_obj;
    List<String> comments = new ArrayList<>();
    int projexamine_id_sub;
    int projexamine_id_pro;
    int projexamine_id_obj;

    public int getTriple_id() {
        return triple_id;
    }

    public void setTriple_id(int triple_id) {
        this.triple_id = triple_id;
    }

    public String getTriple_subject() {
        return triple_subject;
    }

    public void setTriple_subject(String triple_subject) {
        this.triple_subject = triple_subject;
    }

    public String getTriple_property() {
        return triple_property;
    }

    public void setTriple_property(String triple_property) {
        this.triple_property = triple_property;
    }

    public String getTriple_object() {
        return triple_object;
    }

    public void setTriple_object(String triple_object) {
        this.triple_object = triple_object;
    }

    public List<Integer> getOpinion_ids_sub() {
        return opinion_ids_sub;
    }

    public void setOpinion_ids_sub(List<Integer> opinion_ids_sub) {
        this.opinion_ids_sub = opinion_ids_sub;
    }

    public List<Integer> getOpinion_ids_pro() {
        return opinion_ids_pro;
    }

    public void setOpinion_ids_pro(List<Integer> opinion_ids_pro) {
        this.opinion_ids_pro = opinion_ids_pro;
    }

    public List<Integer> getOpinion_ids_obj() {
        return opinion_ids_obj;
    }

    public void setOpinion_ids_obj(List<Integer> opinion_ids_obj) {
        this.opinion_ids_obj = opinion_ids_obj;
    }

    public int getOpinion_sub() {
        return opinion_sub;
    }

    public void setOpinion_sub(int opinion_sub) {
        this.opinion_sub = opinion_sub;
    }

    public int getOpinion_pro() {
        return opinion_pro;
    }

    public void setOpinion_pro(int opinion_pro) {
        this.opinion_pro = opinion_pro;
    }

    public int getOpinion_obj() {
        return opinion_obj;
    }

    public void setOpinion_obj(int opinion_obj) {
        this.opinion_obj = opinion_obj;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public int getProjexamine_id_sub() {
        return projexamine_id_sub;
    }

    public void setProjexamine_id_sub(int projexamine_id_sub) {
        this.projexamine_id_sub = projexamine_id_sub;
    }

    public int getProjexamine_id_pro() {
        return projexamine_id_pro;
    }

    public void setProjexamine_id_pro(int projexamine_id_pro) {
        this.projexamine_id_pro = projexamine_id_pro;
    }

    public int getProjexamine_id_obj() {
        return projexamine_id_obj;
    }

    public void setProjexamine_id_obj(int projexamine_id_obj) {
        this.projexamine_id_obj = projexamine_id_obj;
    }

    public ExamineRecord() {
    }
}
