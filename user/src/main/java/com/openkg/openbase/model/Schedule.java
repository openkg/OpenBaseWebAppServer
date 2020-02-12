package com.openkg.openbase.model;

import lombok.Data;

@Data
public class Schedule {
    int allItem;
    int checkItem;
    int assign_assignee;

    public int getAllItem() {
        return allItem;
    }

    public void setAllItem(int allItem) {
        this.allItem = allItem;
    }

    public int getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(int checkItem) {
        this.checkItem = checkItem;
    }

    public int getAssign_assignee() {
        return assign_assignee;
    }

    public void setAssign_assignee(int assign_assignee) {
        this.assign_assignee = assign_assignee;
    }
}
