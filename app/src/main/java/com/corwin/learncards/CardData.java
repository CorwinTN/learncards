package com.corwin.learncards;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

public class CardData implements Parcelable {

    @Expose
    private Integer lesson;

    @Expose
    private String unknownText;
    @Expose
    private String translation;
    @Expose
    private String transcription;

    @Expose
    private String id;

    protected CardData(Parcel in) {
        lesson = in.readInt();
        unknownText = in.readString();
        translation = in.readString();
        transcription = in.readString();
        id = in.readString();
    }

    public static final Creator<CardData> CREATOR = new Creator<CardData>() {
        @Override
        public CardData createFromParcel(Parcel in) {
            return new CardData(in);
        }

        @Override
        public CardData[] newArray(int size) {
            return new CardData[size];
        }
    };

    public String getUnknownText() {
        return unknownText;
    }

    public String getTranslation() {
        return translation;
    }

    public String getTranscription() {
        return transcription;
    }

    public Integer getLesson() {
        return lesson;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(lesson);
        dest.writeString(unknownText);
        dest.writeString(translation);
        dest.writeString(transcription);
        dest.writeString(id);
    }
}
