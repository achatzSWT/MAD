package com.mad.achatz.fa_todo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;


public class ToDo implements Parcelable {

    private long dbID;
    private String name;
    private String description;
    private boolean isDone = false;
    private boolean isFavourite = false;
    private Calendar dueDate;

    public ToDo(){
        dueDate = Calendar.getInstance();
    }

    public long getDbId() {
        return dbID;
    }

    public void setDbId(long id) {
        dbID = id;
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

    public Calendar getDueDate() {
        return dueDate;
    }

    public void setDueDate(Calendar dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        boolean[] boolValues = new boolean[]{isDone, isFavourite};
        dest.writeBooleanArray(boolValues);
        dest.writeSerializable(dueDate);
    }

    private static ToDo createTodoFromParcel(Parcel parcel) {
        ToDo todo =  new ToDo();
        todo.setName(parcel.readString());
        todo.setDescription(parcel.readString());
        boolean[] boolvalues = new boolean[2];
        parcel.readBooleanArray(boolvalues);
        todo.setDone(boolvalues[0]);
        todo.setFavourite(boolvalues[1]);
        todo.setDueDate((Calendar)parcel.readSerializable());
        return todo;
    }

    public static final Parcelable.Creator CREATOR =
        new Parcelable.Creator() {
            public ToDo createFromParcel(Parcel in) {
                return ToDo.createTodoFromParcel(in);
            }

            public ToDo[] newArray(int size) {
                return new ToDo[size];
            }
        };
}
