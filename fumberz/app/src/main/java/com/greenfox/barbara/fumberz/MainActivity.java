package com.greenfox.barbara.fumberz;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView title;

    TextView numberDescriptionView;
    TextView numberNumberView;
    TextView yearDescriptionView;
    TextView yearNumberView;

    Button mathButton;
    Button yearButton;

    TextToSpeech tts;
    Button numberSpeakButton;
    Button yearSpeakButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_not_21);

        tts = new TextToSpeech(this, this);

        //title
        title = findViewById(R.id.titleView);

        //number fact components
        numberDescriptionView = findViewById(R.id.numberDescriptionView);
        numberNumberView = findViewById(R.id.numberView);

        //year fact components
        yearDescriptionView = findViewById(R.id.yearDescriptionView);
        yearNumberView = findViewById(R.id.yearView);

        //mathButton
        mathButton = findViewById(R.id.mathButton);
        mathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUrl();
                enqueueMath();
            }
        });

        //yearButton
        yearButton = findViewById(R.id.yearButton);
        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUrl();
                enqueueYear();
            }
        });

        //speakButton
        numberSpeakButton = findViewById(R.id.numberSpeakButton);
        numberSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speakOutNumber();
            }
        });

        yearSpeakButton = findViewById(R.id.yearSpeakButton);
        yearSpeakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOutYear();
            }
        });
    }

    @Override
    public void onInit(int text) {

        if (text == TextToSpeech.SUCCESS) {
            int language = tts.setLanguage(Locale.UK);
            if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
                numberSpeakButton.setEnabled(true);
                speakOutNumber();
                yearSpeakButton.setEnabled(true);
                speakOutYear();
            }
            else {
            }
        }
        else {
        }
    }

    public void speakOutNumber() {
        String text = numberDescriptionView.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void speakOutYear() {
        String text = yearDescriptionView.getText().toString();
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private Api getUrl() {
        //building the retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //creating the api
        Api api = retrofit.create(Api.class);

        return api;
    }

        private void enqueueMath() {
            //making call for math?json
            Call<NumberFact> numberFactCall = getUrl().getNumberFact();

            //enqueueing the math?json call
            numberFactCall.enqueue(new Callback<NumberFact>() {
                @Override
                public void onResponse(Call<NumberFact> call, Response<NumberFact> response) {
                    NumberFact numberFact = response.body();

                    String factDescription = numberFact.getText();
                    String factNumber = numberFact.getNumber().toString();

                    numberDescriptionView.setText(factDescription);
                    numberNumberView.setText(factNumber);

                }

                @Override
                public void onFailure(Call<NumberFact> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void enqueueYear() {
            //making call for year?json
            Call<YearFact> yearFactCall = getUrl().getYearFact();

            //enqueueing the year?json call
            yearFactCall.enqueue(new Callback<YearFact>() {
                @Override
                public void onResponse(Call<YearFact> call, Response<YearFact> response) {
                    YearFact yearFact = response.body();

                    String yearDescription = yearFact.getText();
                    String yearNumber = yearFact.getNumber().toString();

                    yearDescriptionView.setText(yearDescription);
                    yearNumberView.setText(yearNumber);

                }

                @Override
                public void onFailure(Call<YearFact> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
}
