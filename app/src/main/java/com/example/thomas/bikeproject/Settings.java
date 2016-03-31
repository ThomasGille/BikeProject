package com.example.thomas.bikeproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class Settings extends AppCompatActivity {
    Spinner spinner;
    String SelectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.SelectedItem="Lyon";

        //Récupération du Spinner déclaré dans le fichier main.xml de res/layout
        spinner = (Spinner) findViewById(R.id.spinner);
        //Création d'une liste d'élément à mettre dans le Spinner(pour l'exemple)
        List <String> mListe = new ArrayList<>();
        AsyncVille mTache = new AsyncVille();
        try {
            mListe = (List < String>)mTache.execute(mListe).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

		/*Le Spinner a besoin d'un adapter pour sa presentation alors on lui passe le context(this) et
                un fichier de presentation par défaut( android.R.layout.simple_spinner_item)
		Avec la liste des elements (exemple) */
        ArrayAdapter adapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                mListe
        );
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {
                SelectedItem = adapter.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });


        /* On definit une présentation du spinner quand il est déroulé         (android.R.layout.simple_spinner_dropdown_item) */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Enfin on passe l'adapter au Spinner et c'est tout
        spinner.setAdapter(adapter);


    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("City",SelectedItem);
        setResult(0,data);
        super.finish();
    }
}
