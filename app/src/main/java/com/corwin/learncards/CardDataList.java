package com.corwin.learncards;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CardDataList {
    @Expose
    private List<CardData> cards;

    @Expose
    private List<CardData> phrases;

    public List<CardData> getCards() {
        return cards;
    }

    public List<CardData> getPhrases() {
        return phrases;
    }
}
