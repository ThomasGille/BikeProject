package com.example.thomas.bikeproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    List mListe;
    ListView maListViewPerso;
    SwipeRefreshLayout mSwipeRefreshLayout;
    EditText inputSearch;
    Context context;
    String City;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.context=this.getApplicationContext();
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        this.mListe = (List<Station>) getIntent().getSerializableExtra("liste");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListe(true); // on fait recharger les data
            }
        });

        getListe(false); // la liste à déja étée chargée dans le splashScreen


        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                // TODO : set the mListe into the mListeViewPerso
                // getListe(false); // la liste à déja été chargée, la listView par contre entre tronquée
                Toast.makeText(context, "Pull to refresh the list", Toast.LENGTH_SHORT).show();
                ((CustomAdapter) MainActivity.this.maListViewPerso.getAdapter()).getFilter().filter(cs);

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(this, Settings.class);
            startActivityForResult(myIntent, 0);
            return true;
        }
        else if (id == R.id.action_refresh) {
            getListe(true);
            return true;
        }
        else if (id == R.id.action_favoris) {
            Intent myIntent = new Intent(this, Favoris.class);
            startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean IsInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public void getListe(boolean reload) {
        if (IsInternetOn(this)) {
            maListViewPerso = (ListView) findViewById(R.id.listviewperso);
            MyAsyncTask mTache = new MyAsyncTask();
            try {
                mTache.execute(maListViewPerso, this.getBaseContext(), MainActivity.this,reload,mListe,this.City).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No internet connection, try again", Toast.LENGTH_SHORT).show();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /*
    recupère la ville préférée
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data.hasExtra("City")) {
            this.City= data.getExtras().getString("City");
        }
        getListe(true);
    }
}

