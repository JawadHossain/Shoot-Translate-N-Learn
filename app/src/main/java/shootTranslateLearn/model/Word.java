/**
 * Model class which represents a word
 */
package shootTranslateLearn.model;

public class Word
{
    private String originalWord;
    private String targetLanguage;
    private String translatedWord;

    public Word(String originalWord, String targetLanguage, String translatedWord)
    {
        this.originalWord = originalWord;
        this.targetLanguage = targetLanguage;
        this.translatedWord = translatedWord;
    }

    public String getOriginalWord()
    {
        return this.originalWord;
    }

    public String getTargetLanguage()
    {
        return this.targetLanguage;
    }

    public String getTranslatedWord()
    {
        return this.translatedWord;
    }
}
