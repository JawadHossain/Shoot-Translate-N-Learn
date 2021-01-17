package shootTranslateLearn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.examples.detection.R;

import java.util.Locale;

public class TranslateActivity extends AppCompatActivity {

    private TextView selected;
    private ImageView image;
    private Button translate_bttn;
    private TextView translated;
    private ImageButton speakButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity_view);

        selected = findViewById(R.id.objectDetected_textview);
        image = findViewById(R.id.imageView2);
        translate_bttn = findViewById(R.id.translate_button);
        translated = findViewById(R.id.output_textview);
        speakButton = findViewById(R.id.imageButton);

        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("BITMAP");
        Bitmap capturedImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        image.setImageBitmap(capturedImage);

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
    }
}