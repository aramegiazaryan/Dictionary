package com.example.aram.dictionary;

public class ListWord {
    private String word;
    private String translation;


    public ListWord(String word, String translation) {
       this.word=word;
       this.translation=translation;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
