package com.example.examfriend.pojos;

import android.os.Parcel;
import android.os.Parcelable;

public class Question implements Parcelable {

    private String queCount, queTitle,queImgUrl, queAnswer, queAuthor, answerAuthor, queImgAuthor;

    public Question(){}

    public Question(String queCount, String queTitle, String queImgUrl, String queAnswer, String queAuthor, String answerAuthor, String queImgAuthor) {
        this.queCount = queCount;
        this.queTitle = queTitle;
        this.queImgUrl = queImgUrl;
        this.queAnswer = queAnswer;
        this.queAuthor = queAuthor;
        this.answerAuthor = answerAuthor;
        this.queImgAuthor = queImgAuthor;
    }

    protected Question(Parcel in) {
        queCount = in.readString();
        queTitle = in.readString();
        queImgUrl = in.readString();
        queAnswer = in.readString();
        queAuthor = in.readString();
        answerAuthor = in.readString();
        queImgAuthor = in.readString();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    public String getQueCount() {
        return queCount;
    }

    public void setQueCount(String queCount) {
        this.queCount = queCount;
    }

    public String getQueTitle() {
        return queTitle;
    }

    public void setQueTitle(String queTitle) {
        this.queTitle = queTitle;
    }

    public String getQueImgUrl() {
        return queImgUrl;
    }

    public void setQueImgUrl(String queImgUrl) {
        this.queImgUrl = queImgUrl;
    }

    public String getQueAnswer() {
        return queAnswer;
    }

    public void setQueAnswer(String queAnswer) {
        this.queAnswer = queAnswer;
    }

    public String getQueAuthor() {
        return queAuthor;
    }

    public void setQueAuthor(String queAuthor) {
        this.queAuthor = queAuthor;
    }

    public String getAnswerAuthor() {
        return answerAuthor;
    }

    public void setAnswerAuthor(String answerAuthor) {
        this.answerAuthor = answerAuthor;
    }

    public String getQueImgAuthor() {
        return queImgAuthor;
    }

    public void setQueImgAuthor(String queImgAuthor) {
        this.queImgAuthor = queImgAuthor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(queCount);
        dest.writeString(queTitle);
        dest.writeString(queImgUrl);
        dest.writeString(queAnswer);
        dest.writeString(queAuthor);
        dest.writeString(answerAuthor);
        dest.writeString(queImgAuthor);
    }
}
