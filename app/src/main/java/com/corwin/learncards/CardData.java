package com.corwin.learncards;

import com.google.gson.annotations.Expose;

public class CardData {

    @Expose
    private Integer lesson;

    @Expose
    private String unknownText;
    @Expose
    private String translation;
    @Expose
    private String transcription;

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
}
