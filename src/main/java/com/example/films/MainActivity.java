package com.example.films;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Classe Main qui initialiser la page d'accueil et implémente ses évenements
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CustomFilmsAdapter adapter;
    private AlertDialog dialogMDP;
    private AlertDialog dialogMdpLoad;
    private SecretKey aesSK;
    private static final String imageUrl = "https://i.picsum.photos/id/670/200/200.jpg";
    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // On vérifie si la permission de stockage externe est activé ou non.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Déclaration de tous les boutons de la page
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
        Button buttonAutorise = findViewById(R.id.buttonAutorise);
        buttonAutorise.setOnClickListener(this);
        Button buttonSer = findViewById(R.id.buttonSer);
        buttonSer.setOnClickListener(this);
        Button buttonDeSer = findViewById(R.id.buttonDeSer);
        buttonDeSer.setOnClickListener(this);
        Button buttonSerCSS = findViewById(R.id.buttonSerCSS);
        buttonSerCSS.setOnClickListener(this);
        Button buttonDeSerCSS = findViewById(R.id.buttonDeSerCSS);
        buttonDeSerCSS.setOnClickListener(this);

        //Initialisation de l'adapter
        initAdapter();

        //Initialisation des deux dialogues pour les mots de passe
        initDialog();
        initDialog2();
    }

    // Initialisation de l'adapter pour la ListView au lancement
    private void initAdapter() {
        CustomFilmsAdapter adapter = new CustomFilmsAdapter(this, new ArrayList<Film>());
        ListView listView = (ListView) findViewById(R.id.lvFilms);
        listView.setAdapter(adapter);
    }
    // Initialisation de la dialog qui permet de sauvegarder une liste de film
    private void initDialog(){
        //Création de la dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View v = getLayoutInflater().inflate(R.layout.dialog_pwd, null);
        builder.setView(v);
        builder.setTitle("Mot de passe");
        // Défnition du OnClick qui va récupérer le mot de passe et le crypter.
        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText textEdit = (EditText) v.findViewById(R.id.password);
            String mdp = textEdit.getText().toString();
            try {
                KeyGenerator kg = KeyGenerator.getInstance("AES");
                kg.init (128);
                aesSK = kg.generateKey();
                Cipher c = Cipher.getInstance("AES");
                c.init(Cipher.ENCRYPT_MODE,aesSK);
                FileOutputStream fis = this.openFileOutput("motdepasse",Context.MODE_PRIVATE);
                CipherOutputStream out = new CipherOutputStream(fis,c);
                out.write(mdp.getBytes());
                out.flush();
                out.close();
                fis.close();
                saveListView();
                Toast.makeText(MainActivity.this, "Liste Sauvegardé", Toast.LENGTH_SHORT).show();
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
                e.printStackTrace();
            }
            dialog.cancel();
        });
        dialogMDP = builder.create();
    }
    // Initialisation de la dialog qui permet de charger une liste de films.
    private void initDialog2(){
        //Création de la dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = this.getLayoutInflater().inflate(R.layout.dialog2_pwd,null);
        builder.setView(v);
        builder.setTitle("Mot de passe");
        // Défnition du OnClick qui va récupérer le mot de passe et le décrypter pour le comparer au mot de passe saisi.
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText text = (EditText) v.findViewById(R.id.passwordLoad);
                String mdp = text.getText().toString();
                try {
                    Cipher c = Cipher.getInstance("AES");
                    c.init(Cipher.DECRYPT_MODE,aesSK);
                    FileInputStream  fos = openFileInput("motdepasse");
                    CipherInputStream  in = new CipherInputStream (fos,c);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] b = new byte[1024];
                    int numberOfBytedRead;
                    while ((numberOfBytedRead = in.read(b)) >= 0) {
                        baos.write(b, 0, numberOfBytedRead);
                    }

                    String decrypt = new String(baos.toByteArray());
                    if(decrypt.equals(mdp)){
                        loadListView();
                    }else{
                        Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                    in.close();
                    fos.close();
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        });
        dialogMdpLoad = builder.create();
    }

    // Définition des métodes onClick de tous les boutons de la page.
    @Override
    public void onClick(View view){
        ListView listView = (ListView) findViewById(R.id.lvFilms);
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
                break;
            // Lancement d'une AsyncTask avec le pool THREAD_POOL_EXECUTOR
            case R.id.buttonAtskExec:
                new MyAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            //Lancement avec un Thread
            case R.id.buttonThread:
                Handler h = new Handler(Looper.getMainLooper());
                for(int i = 0; i<adapter.getCount();i++) {
                    h.post(new CustomRunnable(adapter.getItem(i),imageUrl,adapter));
                }
                break;
            // Lancement avec une Thread dans un pool de 5
            case R.id.buttonExexcutor:
                ExecutorService pool = Executors.newFixedThreadPool(5);
                for(int i = 0; i<adapter.getCount();i++){
                    pool.execute(new MyThread(adapter.getItem(i),imageUrl,adapter));
                }
                break;
            // Lancement avec une Thread utilisant les Weak References
            case R.id.buttonExecWR:
                ExecutorService pool2 = Executors.newFixedThreadPool(5);
                for(int i = 0; i<adapter.getCount();i++){
                    pool2.execute(new MyThreadWR(adapter.getItem(i),imageUrl,adapter));
                }
                break;
            // Lancement avec un HandlerThread
            case R.id.buttonHTR:
                HandlerThread handlerThread = new HandlerThread("myHandlerThread");
                handlerThread.start();
                Handler handler = new Handler(handlerThread.getLooper());
                for(int i = 0; i<adapter.getCount();i++){
                    handler.post(new CustomRunnable(adapter.getItem(i), imageUrl, adapter));
                }
                break;
             // Lancement avec un HandlerThread qui va génerer des messages
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
                break;
            // Boutton pour autoriser la permission d'écriture à l'application
            case R.id.buttonAutorise:
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                }else{
                    Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
                }
                break;
            // Sauvegarde de la liste de films
            case R.id.buttonSer:
                saveListView();
                break;
            // Chargement de la liste de films
            case R.id.buttonDeSer:
                loadListView();
                break;
            // OUverture d'une dialog pour saisir un mot de passe puis enregistrer la liste de films
            case R.id.buttonSerCSS:
                dialogMDP.show();
                break;
            // Ouverture d'une dialog pour saisir un mot de passe puis récupérer la liste de films
            case R.id.buttonDeSerCSS:
                dialogMdpLoad.show();
                break;
        }
        listView.setAdapter(adapter);
    }

    // Méthode qui s'active lorsqu'une demande de permission est terminée pour informer l'utilisateur de son choix.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    // Taches asynchrone pour la question 2-2
    public class MyAsyncTask extends AsyncTask<Void, Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for(int i=0;i<adapter.getCount();i++){
                adapter.getItem(i).setImage(imageUrl);
            }
            adapter.notifyDataSetChanged();
            return null;
        }
    }
    // Fonction de sauvegarde de la liste de films affichés à l'écran
    public void saveListView(){
        try {
            FileOutputStream fos = this.openFileOutput("listFilm", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            ArrayList<Film> listFilm = new ArrayList<>();
            for(int i = 0; i<adapter.getCount();i++){
                listFilm.add(adapter.getItem(i));
            }
            os.writeObject(listFilm);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Fonction pour charger une liste de films stockés par la fonction précédente.
    public void loadListView(){
        try {
            FileInputStream fis = this.openFileInput("listFilm");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<Film> listLoadFilm = (ArrayList<Film>) ois.readObject();
            adapter.clear();
            adapter.addAll(listLoadFilm);
            ois.close();
            fis.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
