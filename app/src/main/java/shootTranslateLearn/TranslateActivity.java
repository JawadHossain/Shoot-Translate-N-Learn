package shootTranslateLearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.tensorflow.lite.examples.detection.R;

import java.util.HashMap;
import java.util.Locale;

public class TranslateActivity extends AppCompatActivity
{
    public static final String TAG = "TranslateActivity";
    private TextView selected;
    private ImageView image;
    private Button translate_bttn;
    private TextView translated;
    private ImageButton speakButton;
    private TextToSpeech textToSpeech;
    private Button saveButton;

    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String targetLangCode;
    private String targetLang;
    private Locale localeLang;
    private String detectedObject;
    private String translation;
    private Translator translator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
        saveButton = findViewById(R.id.saveButton);
        firebaseFirestore = FirebaseFirestore.getInstance();
        spinner = findViewById(R.id.language_spinner);
        progressBar = findViewById(R.id.progressBar);

        selected.setText(detectedObject);
        translate_bttn.setEnabled(false);

        String originalWord = intent.getStringExtra("image");
        selected.setText(originalWord);
        byte[] byteArray = intent.getByteArrayExtra("BITMAP");
        Bitmap capturedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image.setImageBitmap(capturedImage);

        String[] languages = {"Choose language", "French", "German", "Japanese"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, languages);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                targetLang = adapter.getItem(position);

                if (targetLang.equals("French"))
                {
                    targetLangCode = TranslateLanguage.FRENCH;
                    localeLang = Locale.FRENCH;
                }
                else if (targetLang.equals("German"))
                {
                    targetLangCode = TranslateLanguage.GERMAN;
                    localeLang = Locale.GERMAN;
                }
                else
                {
                    targetLangCode = TranslateLanguage.JAPANESE;
                    localeLang = Locale.JAPANESE;
                }

                if (!targetLang.equals("Choose language"))
                {
                    translate_bttn.setEnabled(true);
                    translator = null;
                }

                // Set textToSpeech language option for locale
                textToSpeech = new TextToSpeech(getApplicationContext(),
                        new TextToSpeech.OnInitListener()
                        {
                            @Override
                            public void onInit(int status)
                            {
                                if (status == TextToSpeech.SUCCESS)
                                {
                                    int lang = textToSpeech.setLanguage(localeLang);
                                    textToSpeech.setSpeechRate((float) 0.8);
                                }
                            }
                        });

                TranslatorOptions options =
                        new TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.ENGLISH)
                                .setTargetLanguage(targetLangCode)
                                .build();

                translator = Translation.getClient(options);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        speakButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int speak = textToSpeech.speak(translation, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        // save word button
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HashMap<String, String> word_info = new HashMap<>();
                word_info.put("original_word", originalWord);
                word_info.put("target_language", targetLang);
                word_info.put("translated_word", translation);
                firebaseFirestore.collection("words")
                        .document()
                        .set(word_info)
                        .addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Log.d("Michelle", "word_info has been added successfully");
                                Toast.makeText(TranslateActivity.this, "Word saved!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.d("Michelle", "word_info not added");
                            }
                        });
            }
        });

        translate_bttn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (translator != null)
                {
                    progressBar.setVisibility(View.VISIBLE);
                    translateText(translator);
                }
            }
        });

    }

    private void translateText(Translator translator)
    {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void v)
                            {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                Toast.makeText(getApplicationContext(), "Downloaded necessary packages", Toast.LENGTH_SHORT).show();

                                translator.translate(detectedObject)
                                        .addOnSuccessListener(
                                                new OnSuccessListener<String>()
                                                {

                                                    @Override
                                                    public void onSuccess(String translatedText)
                                                    {
                                                        translation = translatedText;
                                                        progressBar.setVisibility(View.GONE);
                                                        translated.setText(translatedText);
                                                        Log.d("Jawad", translatedText.toString() + "hello");
                                                    }

                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        translated.setError("Something went wrong :(");
                                                        progressBar.setVisibility(View.GONE);
                                                        Log.d("Jawad", "failed translating");

                                                    }
                                                });

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "Model couldn’t be downloaded or other internal error.");
                            }
                        });
    }
}