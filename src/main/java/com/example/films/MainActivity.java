package com.example.films;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    CustomFilmsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
        Button buttonGen = findViewById(R.id.buttonGenerate);
        buttonGen.setOnClickListener(this);
        Button buttonRemove = findViewById(R.id.buttonRemove);
        buttonRemove.setOnClickListener(this);
        Button buttonAtsk = findViewById(R.id.buttonAtsk);
        buttonAtsk.setOnClickListener(this);
        Button buttonAtskExec = findViewById(R.id.buttonAtskExec);
        buttonAtskExec.setOnClickListener(this);
        Button buttonThreads = findViewById(R.id.buttonThread);
        buttonThreads.setOnClickListener(this);
        initAdapter();
    }

    // Initialisation de l'adapter pour la ListView au lancement
    private void initAdapter() {
        CustomFilmsAdapter adapter = new CustomFilmsAdapter(this, new ArrayList<Film>());
        ListView listView = (ListView) findViewById(R.id.lvFilms);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view){
        listView = (ListView) findViewById(R.id.lvFilms);
        adapter = (CustomFilmsAdapter) listView.getAdapter();
        switch (view.getId()){
            //Bouton d'ajout qui va générer un film par défaut
            case R.id.buttonAdd:
                Random rand = new Random();
                int nbRand = rand.nextInt(500);
                adapter.add(new Film("Film" + nbRand,2000 + rand.nextInt(21),"realisateur" + nbRand, "producteur"+nbRand,nbRand + "millions $","https://www.tutorialspoint.com/java/images/java-mini-logo.jpg"));
                break;
            // Ajout d'une liste prédfini de Film dans la classe Film
            case R.id.buttonGenerate:
                ArrayList<Film> arrayOfUsers = Film.getFilms();
                adapter.addAll(arrayOfUsers);
                break;
             // Supprime tous les films de l'adaptateur
            case R.id.buttonRemove:
                adapter.clear();
                break;
            // Lancement d'une AsyncTask basique remplacant les images
            case R.id.buttonAtsk:
                new MyAsyncTask().execute();
            // Lancement d'une AsyncTask avec le pool THREAD_POOL_EXECUTOR
            case R.id.buttonAtskExec:
                new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            case R.id.buttonThread:
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<adapter.getCount();i++){
                            adapter.getItem(i).setImage("https://i.picsum.photos/id/670/200/200.jpg");
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        }
        listView.setAdapter(adapter);
    }

    public class MyAsyncTask extends AsyncTask<Void, Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=0;i<adapter.getCount();i++){
                adapter.getItem(i).setImage("https://i.picsum.photos/id/670/200/200.jpg");
            }
            adapter.notifyDataSetChanged();
            return null;
        }

    }



}
