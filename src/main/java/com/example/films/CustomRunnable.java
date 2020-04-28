package com.example.films;

/*
 * Custom Thread qui modifie l'image d'un film selon un url
 */
public class CustomRunnable implements Runnable {
    private Film film;
    private String url;
    private CustomFilmsAdapter adapter;

    public CustomRunnable(Film film, String url, CustomFilmsAdapter adapter) {
        this.film = film;
        this.url = url;
        this.adapter = adapter;
    }

    public void run(){
        film.setImage(url);
        adapter.notifyDataSetChanged();
    }

}
