package shootTranslateLearn;

public class Word {
    private String originalWord;
    private String targetLanguage;
    private String translatedWord;

    Word(String originalWord, String targetLanguage, String translatedWord){
        this.originalWord = originalWord;
        this.targetLanguage = targetLanguage;
        this.translatedWord = translatedWord;
    }

    String getOriginalWord(){
        return this.originalWord;
    }

    String getTargetLanguage(){
        return this.targetLanguage;
    }

    String getTranslatedWord(){
        return this.translatedWord;
    }
}
