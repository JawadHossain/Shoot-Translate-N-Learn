package shootTranslateLearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity_view);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String detectedObject = bundle.getString("image");

        selected = findViewById(R.id.objectDetected_textview);
        image = findViewById(R.id.imageView2);
        translate_bttn = findViewById(R.id.translate_button);
        translated = findViewById(R.id.output_textview);
        speakButton = findViewById(R.id.imageButton);

        textToSpeech = new TextToSpeech(getApplicationContext(),
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            int lang = textToSpeech.setLanguage(Locale.ENGLISH);
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

        progressBar = findViewById(R.id.progressBar);
        selected.setText(detectedObject);

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.GERMAN)
                        .build();

        progressBar.setVisibility(View.VISIBLE);

        final Translator englishGermanTranslator = Translation.getClient(options);

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v)
                            {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                progressBar.setVisibility(View.GONE);
                                Log.d("Jawad", "downloaded ");

                                englishGermanTranslator.translate(detectedObject)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>() {

                                                    @Override
                                                    public void onSuccess(String text)
                                                    {
                                                        Log.d("Jawad", text.toString()+"hello");
                                                    }

                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Jawad", "failed translating");

                                                    }
                                                });

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Model couldn’t be downloaded or other internal error.");
                                progressBar.setVisibility(View.GONE);
                            }
                        });
    }
}