package com.corwin.learncards;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class LessonsListFragment extends DialogFragment {
    private Map<Integer, Integer> lessons = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lessons_list, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        CardDataList cardDataList = activity.getCardsCollection();
        for (CardData card : cardDataList.getCards()){
            addToLessonsList(card);
        }
        for (CardData card : cardDataList.getPhrases()){
            addToLessonsList(card);
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    private void addToLessonsList(CardData cardData){
        Integer lesson = cardData.getLesson();
        if(!lessons.containsKey(lesson)){
            lessons.put(cardData.getLesson(), 0);
        }
        lessons.put(lesson, lessons.get(lesson) + 1);
    }
}
