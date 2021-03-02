package com.example.examfriend.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Exam implements Parcelable {
    private String examUid, examTitle,examQueCount, authorName, authorUid;

    public Exam(){}

    public Exam(String examUid, String examTitle, String examQueCount, String authorName, String authorUid) {
        this.examUid = examUid;
        this.examTitle = examTitle;
        this.examQueCount = examQueCount;
        this.authorName = authorName;
        this.authorUid = authorUid;
    }

    protected Exam(Parcel in) {
        examUid = in.readString();
        examTitle = in.readString();
        examQueCount = in.readString();
        authorName = in.readString();
        authorUid = in.readString();
    }

    public static final Creator<Exam> CREATOR = new Creator<Exam>() {
        @Override
        public Exam createFromParcel(Parcel in) {
            return new Exam(in);
        }

        @Override
        public Exam[] newArray(int size) {
            return new Exam[size];
        }
    };

    public String getExamUid() {
        return examUid;
    }

    public void setExamUid(String examUid) {
        this.examUid = examUid;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getExamQueCount() {
        return examQueCount;
    }

    public void setExamQueCount(String examQueCount) {
        this.examQueCount = examQueCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(String authorUid) {
        this.authorUid = authorUid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(examUid);
        dest.writeString(examTitle);
        dest.writeString(examQueCount);
        dest.writeString(authorName);
        dest.writeString(authorUid);
    }
}
