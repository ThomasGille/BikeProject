package com.example.thomas.bikeproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Favoris extends AppCompatActivity {
    final String fileName = "com.thomas.android.velibtracking_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        final ListView listView = (ListView) findViewById(R.id.listviewFav);
        List < Station> mListe=  new ArrayList<>();
        try {
            mListe = LoadingLemory();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //Creation de l'adapter avec la liste
        FavAdapter adapter = new FavAdapter(this.getApplicationContext(), mListe, this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
    }

    public List LoadingLemory() throws IOException, ClassNotFoundException {
        FileInputStream fis = this.getApplicationContext().openFileInput(fileName);
        ObjectInputStream is = new ObjectInputStream(fis);
        List<Station> liste;
        liste = (List<Station>) is.readObject();
        is.close();
        fis.close();
        return liste;
    }
}
