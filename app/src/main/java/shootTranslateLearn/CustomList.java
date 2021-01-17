package shootTranslateLearn;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.nl.translate.TranslateLanguage;

import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Locale;

public class CustomList extends ArrayAdapter<Word>
{
    private Context context;
    private ArrayList<Word> words;
    private ImageButton wordSpeakButton;
    private TextToSpeech textToSpeech;

    public CustomList(Context context, ArrayList<Word> words)
    {
        super(context, 0, words);
        this.context = context;
        this.words = words;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.word_content, parent, false);
        }
        Log.d("Michelle", "CustomList launched");

        Word word = words.get(position);
        wordSpeakButton = view.findViewById(R.id.wordSpeakButton);
        wordSpeakButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                textToSpeech = new TextToSpeech(context,
                        new TextToSpeech.OnInitListener()
                        {
                            @Override
                            public void onInit(int status)
                            {
                                if (status == TextToSpeech.SUCCESS)
                                {
                                    int lang = textToSpeech.setLanguage(getTargetLang(word.getTargetLanguage()));
                                    textToSpeech.setSpeechRate((float) 0.8);
                                    textToSpeech.speak(word.getTranslatedWord(), TextToSpeech.QUEUE_FLUSH, null);

                                }
                            }
                        });
            }
        });

        TextView original_word_textview = view.findViewById(R.id.original_word_textview);
        TextView target_language_textview = view.findViewById(R.id.target_language_textview);
        TextView translated_word_textview = view.findViewById(R.id.translated_word_textview);

        original_word_textview.setText(word.getOriginalWord());
        target_language_textview.setText(word.getTargetLanguage());
        translated_word_textview.setText(word.getTranslatedWord());

        return view;
    }

    public Locale getTargetLang(String targetLang)
    {
        Locale localeLang;
        if (targetLang.equals("French"))
        {
            localeLang = Locale.FRENCH;
        }
        else if (targetLang.equals("German"))
        {
            localeLang = Locale.GERMAN;
        }
        else
        {
            localeLang = Locale.JAPANESE;
        }
        return localeLang;
    }
}
