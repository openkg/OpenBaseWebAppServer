package com.openkg.openbase.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Taskassign model
 * Created by yfy on 18-8-5
 */

@Data
public class Taskassign {
    int assign_id;
    int projtask_id;
    int assign_assignee;
    Timestamp assignment_time;
    Timestamp acceptance_time;
    int page;

    public int getProjtask_id() {
        return projtask_id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getAssign_id() {
        return assign_id;
    }

    public void setAssign_id(int assign_id) {
        this.assign_id = assign_id;
    }
}
