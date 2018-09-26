package com.corwin.learncards;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 6576;
    private CardDataList cardsCollection;
    private int cardIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initCards();
    }

    private void initCards(){
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

    private void loadCards(){
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

    private void updateCard(){
        if(cardIndex >= 0 && cardIndex < cardsCollection.getCards().size()){
            TextView unknownText = findViewById(R.id.unKnownText);
            unknownText.setText(cardsCollection.getCards().get(cardIndex).getUnknownText());
        }
    }

    public void onCardClick(View view) {
        cardIndex++;
        if(cardIndex >= cardsCollection.getCards().size()){
            Toast.makeText(this, "End of cards", Toast.LENGTH_LONG).show();
            cardIndex = -1;
        } else {
            updateCard();
        }
    }
}
