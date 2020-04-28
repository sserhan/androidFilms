package com.example.films;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView listView;
    CustomFilmsAdapter adapter;
    private static final String imageUrl = "https://i.picsum.photos/id/670/200/200.jpg";

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
        Button buttonExecutor = findViewById(R.id.buttonExexcutor);
        buttonExecutor.setOnClickListener(this);
        Button buttonExecWR = findViewById(R.id.buttonExecWR);
        buttonExecWR.setOnClickListener(this);
        Button buttonHTR = findViewById(R.id.buttonHTR);
        buttonHTR.setOnClickListener(this);
        Button buttonHTM = findViewById(R.id.buttonHTM);
        buttonHTM.setOnClickListener(this);
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
            //Lancement avec un Thread
            case R.id.buttonThread:
                Handler h = new Handler(Looper.getMainLooper());
                for(int i = 0; i<adapter.getCount();i++) {
                    h.post(new CustomRunnable(adapter.getItem(i),imageUrl,adapter));
                }
            // Lancement avec une Thread dans un pool de 5
            case R.id.buttonExexcutor:
                ExecutorService pool = Executors.newFixedThreadPool(5);
                for(int i = 0; i<adapter.getCount();i++){
                    pool.execute(new MyThread(adapter.getItem(i),imageUrl,adapter));
                }
            case R.id.buttonExecWR:
                ExecutorService pool2 = Executors.newFixedThreadPool(5);
                for(int i = 0; i<adapter.getCount();i++){
                    pool2.execute(new MyThreadWR(adapter.getItem(i),imageUrl,adapter));
                }
            case R.id.buttonHTR:
                HandlerThread handlerThread = new HandlerThread("myHandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper());
                for(int i = 0; i<adapter.getCount();i++){
                    handler.post(new CustomRunnable(adapter.getItem(i), imageUrl, adapter));
                }
            case R.id.buttonHTM:
                HandlerThread msgHandlerThread = new HandlerThread("MessageHandlerThread");
                msgHandlerThread.start();
                Looper looper = msgHandlerThread.getLooper();
                Handler msgHandler = new Handler(looper){
                    @Override
                    public void handleMessage(Message msg){
                        switch(msg.what) {
                            case 1:
                                Log.d("Handler HTM", "Update de l'image de " + msg.obj);
                                break;
                            default:
                                Log.d("Handler HTM", "Aucun message");
                                break;
                        }
                    }
                };
                for(int i = 0; i<adapter.getCount();i++){
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = adapter.getItem(i).getName();
                    msgHandler.sendMessage(msg);
                    msgHandler.post(new CustomRunnable(adapter.getItem(i),imageUrl,adapter));
                }
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
