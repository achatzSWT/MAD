package com.mad.achatz.fa_todo;

import java.util.Date;

/**
 * Created by C-Master on 17.04.2016.
 */
public class ToDo {

    private String name;
    private String description;
    private boolean isDone = false;
    private boolean isFavourite = false;
    private Date dueDate;

    public ToDo(){

    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String d) {
        description = d;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
