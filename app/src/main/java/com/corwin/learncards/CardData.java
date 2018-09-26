package com.corwin.learncards;

import com.google.gson.annotations.Expose;

public class CardData {
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
}
