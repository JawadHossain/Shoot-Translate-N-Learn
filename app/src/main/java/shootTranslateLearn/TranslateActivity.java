package shootTranslateLearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.tensorflow.lite.examples.detection.R;

import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {
    public static final String TAG = "TranslateActivity";
    private TextView selected;
    private ImageView image;
    private Button translate_bttn;
    private TextView translated;
    private ImageButton speakButton;
    private TextToSpeech textToSpeech;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String targetLang;
    private Locale localeLang;
    private String detectedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        detectedObject = bundle.getString("image");

        selected = findViewById(R.id.objectDetected_textview);
        image = findViewById(R.id.imageView2);
        translate_bttn = findViewById(R.id.translate_button);
        translated = findViewById(R.id.output_textview);
        speakButton = findViewById(R.id.imageButton);
        spinner = findViewById(R.id.language_spinner);
        progressBar = findViewById(R.id.progressBar);

        selected.setText(detectedObject);
        translate_bttn.setEnabled(false);

        String[] languages = {"Choose language", "French", "German", "Spanish"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = adapter.getItem(position);
                if (language.equals("French")) {
                    targetLang = TranslateLanguage.FRENCH;
                    localeLang = Locale.FRENCH;
                } else if (language.equals("German")) {
                    targetLang = TranslateLanguage.GERMAN;
                    localeLang = Locale.GERMAN;
                } else {
                    targetLang = TranslateLanguage.SPANISH;
                    localeLang = Locale.GERMAN;
                }

                if (!language.equals("Choose language")) {
                    translate_bttn.setEnabled(true);
                }

                TranslatorOptions options =
                        new TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.ENGLISH)
                                .setTargetLanguage(targetLang)
                                .build();

                final Translator translator = Translation.getClient(options);

                progressBar.setVisibility(View.VISIBLE);
                translateText(translator);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int lang = textToSpeech.setLanguage(localeLang);
                            textToSpeech.setSpeechRate((float) 0.5);
                        }
                    }
                });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int speak = textToSpeech.speak("apple", TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        translate_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void translateText(Translator translator) {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v)
                            {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                Toast.makeText(getApplicationContext(), "Downloaded necessary packages", Toast.LENGTH_SHORT).show();

                                translator.translate(detectedObject)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {

                                                    @Override
                                                    public void onSuccess(String translatedText)
                                                    {
                                                        progressBar.setVisibility(View.GONE);
                                                        translated.setText(translatedText);
                                                        Log.d("Jawad", translatedText.toString()+"hello");
                                                    }

                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        translated.setError("Something went wrong :(");
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.d("Jawad", "failed translating");

                                                    }
                                                });

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "Model couldn’t be downloaded or other internal error.");
                            }
                        });
    }
}