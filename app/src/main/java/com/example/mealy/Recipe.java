package com.example.mealy;

public class Recipe {

    private Integer index;
    private String url1;
    private String url2;
    private String title;
    private String rating;
    private Integer total;
    private Integer prepare;
    private String difficulty;
    private Integer kcal;
    private String[] allergies;
    private String[] tags;
    private String ingredients;
    private Integer score;

    public Integer getIndex() {
        return index;
    }

    public String getUrl1() {
        return url1;
    }

    public String getUrl2() {
        return url2;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public Integer getTotal() {
        return total;
    }

    public Integer getPrepare() {
        return prepare;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getKcal() { return kcal; }

    public String[] getAllergies() { return allergies; }

    public String[] getTags() {
        return tags;
    }

    public String getIngredients() { return ingredients; }

    public Integer getScore() { return score; }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public void setPrepare(Integer prepare) {
        this.prepare = prepare;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setKcal(Integer kcal) { this.kcal = kcal; }

    public void setAllergies(String[] allergies) { this.allergies = allergies; }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public void setScore(Integer score) { this.score = score; }
}
