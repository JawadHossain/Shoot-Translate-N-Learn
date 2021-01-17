package shootTranslateLearn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.tensorflow.lite.examples.detection.R;

public class TranslateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
    }
}