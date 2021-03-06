package com.example.films;

import android.os.Handler;
import android.os.Looper;

/**
 * Thread mettant à jour l'image d'un film en utilisant un Handler
 */
public class MyThread implements Runnable {
    private Film film;
    private String url;
    private CustomFilmsAdapter adapter;
    public MyThread(Film f, String u, CustomFilmsAdapter a) {
        this.film = f;
        this.url = u;
        this.adapter = a;
    }
    public void run() {
        film.setImage(url);
        new Handler(Looper.getMainLooper()).post(() ->
        {
            adapter.notifyDataSetChanged();
        });
    }
}

