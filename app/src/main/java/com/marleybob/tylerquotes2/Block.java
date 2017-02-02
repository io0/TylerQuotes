package com.marleybob.tylerquotes2;

/**
 * Created by Marley Bob on 1/28/2017.
 */
public class Block {
    private String quote;
    private String date;
    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getQuote() {

        return quote;
    }

    public String getDate() {
        return date;
    }
}
