package com.mad.achatz.fa_todo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.Comparator;


public class ToDo implements Parcelable {

    private long dbID;
    private String name;
    private String description;
    private boolean isDone = false;
    private boolean isFavourite = false;
    private Calendar dueDate;

    public ToDo(){
        dueDate = Calendar.getInstance();
        dueDate.set(Calendar.SECOND, 0);
        dueDate.set(Calendar.MILLISECOND, 0);
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


    public boolean isOverdue() {
        Calendar now = Calendar.getInstance();
        long diff = dueDate.getTimeInMillis() - now.getTimeInMillis();
        return diff < 0;
    }


    /* Comparators */
    public static Comparator<ToDo> FavDateComparator = new Comparator<ToDo>() {
        @Override
        public int compare(ToDo lhs, ToDo rhs) {
            if (lhs.isDone() && ! rhs.isDone())
                return 1;

            if (!lhs.isDone() && rhs.isDone())
                return -1;

            if (lhs.isFavourite() && ! rhs.isFavourite())
                return -1;

            if (!lhs.isFavourite() && rhs.isFavourite())
                return 1;

            long lhsTime = lhs.getDueDate().getTimeInMillis();
            long rhsTime = rhs.getDueDate().getTimeInMillis();
            return (lhsTime < rhsTime) ? -1 : 1;
        }
    };

    public static Comparator<ToDo> DateFavComparator = new Comparator<ToDo>() {
        @Override
        public int compare(ToDo lhs, ToDo rhs) {
            if (lhs.isDone() && ! rhs.isDone())
                return 1;

            if (!lhs.isDone() && rhs.isDone())
                return -1;

            Calendar calLhs = lhs.getDueDate();
            Calendar calRhs = rhs.getDueDate();

            long lhsTime = calLhs.get(Calendar.YEAR)*365 + calLhs.get(Calendar.DAY_OF_YEAR);
            long rhsTime = calRhs.get(Calendar.YEAR)*365 + calRhs.get(Calendar.DAY_OF_YEAR);

            if (lhsTime < rhsTime) {
                return -1;
            } else if (lhsTime > rhsTime) {
                return 1;
            }

            if (lhs.isFavourite() && ! rhs.isFavourite())
                return -1;

            if (!lhs.isFavourite() && rhs.isFavourite())
                return 1;

            lhsTime = lhs.getDueDate().getTimeInMillis();
            rhsTime = rhs.getDueDate().getTimeInMillis();
            return (lhsTime < rhsTime) ? -1 : 1;
        }
    };


    /* Methoden für Parcelable interface */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dbID);
        dest.writeString(name);
        dest.writeString(description);
        boolean[] boolValues = new boolean[]{isDone, isFavourite};
        dest.writeBooleanArray(boolValues);
        dest.writeSerializable(dueDate);
    }

    private static ToDo createTodoFromParcel(Parcel parcel) {
        ToDo todo =  new ToDo();
        todo.setDbId(parcel.readLong());
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
