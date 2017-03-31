package com.john.kalimeris.notes;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by production_user13 on 28/9/2016.
 */

public class noteObject implements Parcelable{

    private long _id;
    private String _title;
    private String _description;
    private String _date;
    private String _color;
    private Long _reminderEventId;
    private boolean _check;

    public noteObject(String _title, String _description, String _date) {
        super();
        this._title = _title;
        this._description = _description;
        this._date = _date;
    }

    public noteObject() {
        super();
    }

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        this._id = id;
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String _title) {
        this._title = _title;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String _description) {
        this._description = _description;
    }

    public String getDate() {
        return _date;
    }

    public void setDate(String _date) {
        this._date = _date;
    }

    public String getColor() {
        return _color;
    }

    public void setColor(String _color) {
        this._color = _color;
    }

    public Long getEventId() {
        return _reminderEventId;
    }

    public void setEventId(Long _reminderEventId) {
        this._reminderEventId = _reminderEventId;
    }

    public boolean getCheck() {
        return _check;
    }

    public void setCheck(boolean _check) {
        this._check = _check;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getId());
        dest.writeString(getTitle());
        dest.writeString(getDescription());
        dest.writeString(getDate());
        dest.writeString(getColor());
        dest.writeLong(getEventId());
        dest.writeInt(getCheck() ? 1 : 0);
    }

    public static final Parcelable.Creator<noteObject> CREATOR = new Parcelable.Creator<noteObject>() {
        public noteObject createFromParcel(Parcel in) {
            return new noteObject(in);
        }

        public noteObject[] newArray(int size) {
            return new noteObject[size];
        }
    };

    // example constructor that takes a Parcel and gives you an object populated with it's values
    private noteObject(Parcel in) {
        setId(in.readLong());
        setTitle(in.readString());
        setDescription(in.readString());
        setDate(in.readString());
        setColor(in.readString());
        setEventId(in.readLong());
        setCheck((in.readInt() == 1) ? true : false);
    }

    public static Comparator<noteObject> NotesComparatorTitle = new Comparator<noteObject>() {

        public int compare(noteObject note1, noteObject note2) {

            String title1 = note1.getTitle().toUpperCase();
            String title2 = note2.getTitle().toUpperCase();

            //ascending order
            return title1.compareTo(title2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };

    public static Comparator<noteObject> NotesComparatorDescription = new Comparator<noteObject>() {

        public int compare(noteObject note1, noteObject note2) {

            String description1 = note1.getDescription().toUpperCase();
            String description2 = note2.getDescription().toUpperCase();

            //ascending order
            return description1.compareTo(description2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };

    public static Comparator<noteObject> NotesComparatorDate = new Comparator<noteObject>() {

        public int compare(noteObject note1, noteObject note2) {

            String date1 = note1.getDate().toUpperCase();
            String date2 = note2.getDate().toUpperCase();

            //ascending order
            return date1.compareTo(date2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };

    public static Comparator<noteObject> NotesComparatorColor = new Comparator<noteObject>() {

        public int compare(noteObject note1, noteObject note2) {

            String color1 = note1.getColor().toUpperCase();
            String color2 = note2.getColor().toUpperCase();

            //ascending order
            return color1.compareTo(color2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };

    public static Comparator<noteObject> NotesComparatorCheck = new Comparator<noteObject>() {

        public int compare(noteObject note1, noteObject note2) {

            boolean check1 = note1.getCheck();
            boolean check2 = note2.getCheck();

            //ascending order
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                return Boolean.compare( check1, check2);
            }
            else {
                if( check1 && ! check2 ) {
                    return +1;
                }
                if( ! check1 && check2 ) {
                    return -1;
                }
                return 0;
            }

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
}
