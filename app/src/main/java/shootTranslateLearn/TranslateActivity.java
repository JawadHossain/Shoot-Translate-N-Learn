package shootTranslateLearn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.examples.detection.R;

public class TranslateActivity extends AppCompatActivity {

    private TextView selected;
    private ImageView image;
    private Button translate_bttn;
    private TextView translated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_activity_view);

        selected = findViewById(R.id.objectDetected_textview);
        image = findViewById(R.id.imageView2);
        translate_bttn = findViewById(R.id.translate_button);
        translated = findViewById(R.id.output_textview);

    }
}