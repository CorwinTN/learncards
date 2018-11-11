package com.corwin.learncards;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 6576;
    private CardDataList cardsCollection;
    private int cardIndex = 0;
    private CardState cardState = CardState.FOREIGN;
    private TextView foreignWord;
    private TextView translation;
    private TextView transcription;
    private TextView currentMode;
    private TextView cardsCount;
    private TextView cardsSourceInfo;

    private TextView cardsProgress;
    private View cardsProgressContainer;
    private View modeInfoContainer;

    private LearnMode learnMode = LearnMode.SHOW_FOREIGN;
    private CardsSource cardsSource = CardsSource.ALL_CARDS;
    private List<CardData> currentCards = new CopyOnWriteArrayList<>();

    private Map<Integer, Integer> lessons = new HashMap<>();
    private List<Integer> presentedLessens = new ArrayList<>();
    private boolean checkedItems[];
    private List<Integer> selectedLessons = new ArrayList<>();

    private boolean currentState = false;

    private BottomAppBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        foreignWord = findViewById(R.id.unKnownText);
        translation = findViewById(R.id.translationText);
        transcription = findViewById(R.id.transcriptionText);

        currentMode = findViewById(R.id.current_mode);
        cardsCount = findViewById(R.id.cards_count);
        cardsSourceInfo = findViewById(R.id.cards_source);

        cardsProgress = findViewById(R.id.cards_progress);
        cardsProgressContainer = findViewById(R.id.cards_progress_container);
        modeInfoContainer = findViewById(R.id.mode_info);

        initCards();
        toolbar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(toolbar);
        updateModeInfo();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_mode_foreign:
                learnMode = LearnMode.SHOW_FOREIGN;
                break;
            case android.R.id.home:
                BottomNavigationDrawerFragment bottomNavDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavDrawerFragment.show(getSupportFragmentManager(), bottomNavDrawerFragment.getClass().getSimpleName());
                break;
            case R.id.app_bar_mode_transcription:
                learnMode = LearnMode.SHOW_TRANSCRIPTION;
                break;
            case R.id.app_bar_mode_translation:
                learnMode = LearnMode.SHOW_TRANSLATION;
                break;
            case R.id.app_bar_mode_full:
                learnMode = LearnMode.SHOW_FULL;
                break;
            case R.id.app_bar_back:
                moveToPreviouseCard();
                break;
            case R.id.app_bar_source:
                switch (cardsSource) {
                    case ALL_CARDS:
                        cardsSource = CardsSource.ALL_PHRASES;
                        break;
                    case ALL_PHRASES:
                        cardsSource = CardsSource.ALL_CARDS;
                        break;
                }

                break;
        }
        updateModeInfo();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void initCards() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadCards();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCards();
                } else {
                    Toast.makeText(this, "We Need permission Storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void loadCards() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "LearnCards/cards.json");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.d("", "");
            //You'll need to add proper error handling here
        }
        Gson gson = new GsonBuilder().create();
        cardsCollection = gson.fromJson(text.toString(), CardDataList.class);

        for (CardData card : cardsCollection.getCards()) {
            addToLessonsList(card);
        }
        for (CardData card : cardsCollection.getPhrases()) {
            addToLessonsList(card);
        }
        checkedItems = new boolean[lessons.size()];

        updateCard();
        Log.d("", "");

    }

    private void updateCard() {
        hideAllWords();
        if (cardIndex >= 0 && cardIndex < currentCards.size()) {
            switch (cardState) {
                case FULL:
                    foreignWord.setVisibility(View.VISIBLE);
                    translation.setVisibility(View.VISIBLE);
                    transcription.setVisibility(View.VISIBLE);
                    break;
                case FOREIGN:
                    foreignWord.setVisibility(View.VISIBLE);
                    break;
                case TRANSLATE:
                    translation.setVisibility(View.VISIBLE);
                    break;
                case TRANSCRIPTION:
                    transcription.setVisibility(View.VISIBLE);
                    break;
            }
            foreignWord.setText(currentCards.get(cardIndex).getUnknownText());
            translation.setText(currentCards.get(cardIndex).getTranslation());
            transcription.setText(currentCards.get(cardIndex).getTranscription());
        }
    }

    private void hideAllWords() {
        foreignWord.setVisibility(View.INVISIBLE);
        translation.setVisibility(View.INVISIBLE);
        transcription.setVisibility(View.INVISIBLE);
    }

    public void onCardClick(View view) {
        switch (learnMode) {
            case SHOW_FOREIGN:
                switch (cardState) {
                    case FOREIGN:
                        cardState = CardState.TRANSCRIPTION;
                        break;
                    case TRANSCRIPTION:
                        cardState = CardState.TRANSLATE;
                        break;
                    case TRANSLATE:
                        cardState = CardState.FOREIGN;
                        break;
                }
                break;
            case SHOW_TRANSLATION:
                switch (cardState) {
                    case FOREIGN:
                        cardState = CardState.TRANSCRIPTION;
                        break;
                    case TRANSCRIPTION:
                        cardState = CardState.TRANSLATE;
                        break;
                    case TRANSLATE:
                        cardState = CardState.FOREIGN;
                        break;
                }
                break;
            case SHOW_TRANSCRIPTION:
                switch (cardState) {
                    case FOREIGN:
                        cardState = CardState.TRANSLATE;
                        break;
                    case TRANSCRIPTION:
                        cardState = CardState.FOREIGN;
                        break;
                    case TRANSLATE:
                        cardState = CardState.TRANSCRIPTION;
                        break;
                }
                break;
            case SHOW_FULL:
                cardState = CardState.FULL;
                break;
        }
        updateCard();
    }

    private void moveToPreviouseCard(){
        if(cardIndex > 0){
            cardState = getDefaultCardState();
            cardIndex--;
            updateCard();
            updateProgress();
        }
    }

    private void moveToNextCard() {
        cardState = getDefaultCardState();
        cardIndex++;
        if (cardIndex >= currentCards.size()) {
            Toast.makeText(this, "End of cards", Toast.LENGTH_LONG).show();
            cardIndex = -1;
            currentState = false;
            toolbar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
        } else {
            updateCard();
        }
    }

    public void onNext(View view) {
        if (currentState) {
            moveToNextCard();
        } else {
            Collections.shuffle(currentCards);
            currentState = true;
            cardIndex = 0;
            updateCard();
            toolbar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
            toolbar.replaceMenu(R.menu.bottom_nav_lesson_mode);
        }
        updateProgress();
    }

    private void updateProgress(){
        if (currentState){
            cardsProgress.setText("Current card " + cardIndex + "/" + currentCards.size());
            modeInfoContainer.setVisibility(View.GONE);
            cardsProgressContainer.setVisibility(View.VISIBLE);
        } else {
            modeInfoContainer.setVisibility(View.VISIBLE);
            cardsProgressContainer.setVisibility(View.GONE);
        }
    }


    private CardState getDefaultCardState() {
        switch (learnMode) {
            case SHOW_FOREIGN:
                return CardState.FOREIGN;
            case SHOW_TRANSLATION:
                return CardState.TRANSLATE;
            case SHOW_TRANSCRIPTION:
                return CardState.TRANSCRIPTION;
            case SHOW_FULL:
                return CardState.FULL;

        }
        return null;
    }

    private void updateModeInfo() {
        updateLearnModeInfo();
        updateCardsSourceInfo();
    }

    private void updateCardsCountInfo() {
        cardsCount.setText("Cards count: " + currentCards.size());
    }

    private void updateCardsSourceInfo() {
        String modeName = "";
        switch (cardsSource) {
            case ALL_CARDS:
                currentCards.clear();
                modeName = "words";
                currentCards.addAll(cardsCollection.getCards());
                break;
            case ALL_PHRASES:
                currentCards.clear();
                currentCards.addAll(cardsCollection.getPhrases());
                modeName = "phrases";
                break;
            case SELECTED_CARDS:
                modeName = "Selected cards";
                break;
        }
        cardsSourceInfo.setText("Cards source: " + modeName);
        updateCardsCountInfo();
    }

    private void updateLearnModeInfo() {
        String modeName = "";
        switch (learnMode) {
            case SHOW_TRANSCRIPTION:
                modeName = "transcription first";
                break;
            case SHOW_TRANSLATION:
                modeName = "translation first";
                break;
            case SHOW_FOREIGN:
                modeName = "foreign first";
                break;
            case SHOW_FULL:
                modeName = "Full info";
                break;
        }
        currentMode.setText("Learn mode: " + modeName);
    }

    public CardDataList getCardsCollection(){
        return cardsCollection;
    }


    public void onOpenLEssonsSelection(){
        List<String> items = new ArrayList<>();
        String[] stringItems = new String[lessons.size()];
        presentedLessens.clear();
        for (Integer lesson : lessons.keySet()) {
            items.add(lesson.toString() + " - " + lessons.get(lesson) + " cards");
            presentedLessens.add(lesson);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose lessons");
        builder.setMultiChoiceItems(items.toArray(stringItems), checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        builder.setPositiveButton("Ok", onLessonsSelectedListener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNeutralButton("ClearAll", null);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        builder.setAdapter(new ArrayAdapter<CharSequence>(this, 0, android.R.id.text1, stringItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (checkedItems != null) {
                    ((ListView) parent).setItemChecked(position, checkedItems[position]);
                }
                return view;
            }
        }, null);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            dialog.getListView().setItemChecked(i, false);
                        }
                    }
                });
            }
        });
        dialog.show();

    }




    public void setSelectedLessons(List<Integer> selectedLessons){
        cardsSource = CardsSource.SELECTED_CARDS;
        CardDataList cardDataList = getCardsCollection();
        currentCards.clear();
        currentCards.addAll(cardDataList.getCards());
        currentCards.addAll(cardDataList.getPhrases());
        for(CardData card:currentCards){
            if(!selectedLessons.contains(card.getLesson())){
                currentCards.remove(card);
            }
        }
        updateCardsSourceInfo();
    }

    public DialogInterface.OnClickListener onLessonsSelectedListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            selectedLessons.clear();
            for(int checkIndex = 0; checkIndex < checkedItems.length; checkIndex++){
                if(checkedItems[checkIndex]){
                    selectedLessons.add(presentedLessens.get(checkIndex));
                }
            }
            setSelectedLessons(selectedLessons);
            dialogInterface.dismiss();
        }
    };

    private void addToLessonsList(CardData cardData) {
        Integer lesson = cardData.getLesson();
        if (!lessons.containsKey(lesson)) {
            lessons.put(cardData.getLesson(), 0);
        }
        lessons.put(lesson, lessons.get(lesson) + 1);
    }


}
