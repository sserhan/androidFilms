package com.example.films;

import android.os.Handler;
import android.os.Looper;

import java.lang.ref.WeakReference;

public class MyThreadWR implements Runnable {
    private Film film;
    private String url;
    private WeakReference<CustomFilmsAdapter> adapter;

    public MyThreadWR(Film film, String url, CustomFilmsAdapter adapter) {
        this.film = film;
        this.url = url;
        this.adapter = new WeakReference<>(adapter);
    }
    @Override
    public void run() {
        film.setImage(url);
        Handler h = new Handler(Looper.getMainLooper());
        h.post(() -> {
            if(adapter.get() != null){
                CustomFilmsAdapter customAdapter = adapter.get();
                customAdapter.notifyDataSetChanged();
            }
        });
    }
}
