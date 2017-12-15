package com.greenfox.barbara.fumberz;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    TextView title;
    TextView numberDescriptionView;
    TextView numberNumberView;
    TextView yearDescriptionView;
    TextView yearNumberView;
    TextView mStatusTextView;

    Button mathButton;
    Button yearButton;

    TextToSpeech tts;
    Button numberSpeakButton;
    Button yearSpeakButton;

    private GoogleSignInClient mGoogleSignInClient;
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_not_21);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        // google sign out
        signOutButton = findViewById(R.id.sign_out_button);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // sign in status
        mStatusTextView = findViewById(R.id.status);

        // text to speech
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        updateUI(false, null);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            updateUI(true, account);
        } else {
            updateUI(false, null);
        }
    }

    private void updateUI(boolean isLogin, GoogleSignInAccount account) {
        if (isLogin && account != null) {
            mStatusTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(true, account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
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
            } else {
            }
        } else {
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
