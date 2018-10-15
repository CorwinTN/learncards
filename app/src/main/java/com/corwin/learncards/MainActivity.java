package com.corwin.learncards;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private LearnMode learnMode = LearnMode.SHOW_FOREIGN;
    private CardsSource cardsSource = CardsSource.ALL_CARDS;
    private List<CardData> currentCards = new ArrayList<>();
    private boolean currentState = false;

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


        initCards();
        setSupportActionBar((Toolbar) findViewById(R.id.bottom_app_bar));
        updateModeInfo();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_mode_1:
                learnMode = LearnMode.SHOW_FOREIGN;
                break;
            case android.R.id.home:
                BottomNavigationDrawerFragment bottomNavDrawerFragment = new BottomNavigationDrawerFragment();
                bottomNavDrawerFragment.show(getSupportFragmentManager(), bottomNavDrawerFragment.getClass().getSimpleName());
                break;
            case R.id.app_bar_mode_2:
                learnMode = LearnMode.SHOW_TRANSCRIPTION;
                break;
            case R.id.app_bar_mode_3:
                learnMode = LearnMode.SHOW_TRANSLATION;
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

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

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

        updateCard();
        Log.d("", "");

    }

    private void updateCard() {
        hideAllWords();
        if (cardIndex >= 0 && cardIndex < currentCards.size()) {
            switch (cardState) {
                case FOREIGN:
                    foreignWord.setVisibility(View.VISIBLE);
                    break;
                case TRANSLATE:
//                    foreignWord.setVisibility(View.VISIBLE);
                    translation.setVisibility(View.VISIBLE);
                    break;
                case TRANSCRIPTION:
//                    foreignWord.setVisibility(View.VISIBLE);
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
        }
        updateCard();
    }

    private void moveToNextCard() {
        cardState = getDefaultCardState();
        cardIndex++;
        if (cardIndex >= currentCards.size()) {
            Toast.makeText(this, "End of cards", Toast.LENGTH_LONG).show();
            cardIndex = -1;
            currentState = false;
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
        currentCards.clear();
        String modeName = "";
        switch (cardsSource) {
            case ALL_CARDS:
                modeName = "words";
                currentCards.addAll(cardsCollection.getCards());
                break;
            case ALL_PHRASES:
                currentCards.addAll(cardsCollection.getPhrases());
                modeName = "phrases";
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
        }
        currentMode.setText("Learn mode: " + modeName);
    }

    public CardDataList getCardsCollection(){
        return cardsCollection;
    }
}
