package com.example.films;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Entité FIlm
 */
public class Film implements Serializable {

    private String image;
    private String name;
    private int date;
    private String realisateur;
    private String producteur;
    private String cout;

    public Film(String name, int date, String realisateur, String producteur, String cout, String image) {
        this.name = name;
        this.date = date;
        this.realisateur = realisateur;
        this.producteur = producteur;
        this.cout = cout;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public String getProducteur() {
        return producteur;
    }

    public void setProducteur(String producteur) {
        this.producteur = producteur;
    }

    public String getCout() {
        return cout;
    }

    public void setCout(String cout) {
        this.cout = cout;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static ArrayList<Film> getFilms() {
        ArrayList<Film> films = new ArrayList<Film>();
        films.add(new Film("Mission Impossible", 1996, "Brian De Palma", "Paramount", "80 millions $","https://www.tutorialspoint.com/java/images/java-mini-logo.jpg"));
        films.add(new Film("James Bond", 2010,"Sam Mendez","Sony Pictures","150 millions $","https://www.tutorialspoint.com/java/images/java-mini-logo.jpg"));
        films.add(new Film("Matrix", 1999,"Wachovskis","Warner Bros", "63 millions $","https://www.tutorialspoint.com/java/images/java-mini-logo.jpg"));
        return films;
    }
}
