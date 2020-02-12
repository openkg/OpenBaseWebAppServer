package com.openkg.openbase.model;

import lombok.Data;

@Data
public class ProjectTask {
    int projtask_id;
    int projchecker_checker;
    int task_importance;
    int task_scale;
    String task_tags;

    public int getProjtask_id() {
        return projtask_id;
    }

    public void setProjtask_id(int projtask_id) {
        this.projtask_id = projtask_id;
    }

    public int getProjchecker_checker() {
        return projchecker_checker;
    }

    public void setProjchecker_checker(int projchecker_checker) {
        this.projchecker_checker = projchecker_checker;
    }

    public int getTask_importance() {
        return task_importance;
    }

    public void setTask_importance(int task_importance) {
        this.task_importance = task_importance;
    }

    public int getTask_scale() {
        return task_scale;
    }

    public void setTask_scale(int task_scale) {
        this.task_scale = task_scale;
    }

    public String getTask_tags() {
        return task_tags;
    }

    public void setTask_tags(String task_tags) {
        this.task_tags = task_tags;
    }
}
