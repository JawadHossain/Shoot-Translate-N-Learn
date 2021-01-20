package shootTranslateLearn.view.customview;

import java.util.List;

import shootTranslateLearn.tensorflow.lite.examples.detection.tflite.Detector;

public interface ResultsView {
  public void setResults(final List<Detector.Recognition> results);
}
