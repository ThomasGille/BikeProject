package com.example.thomas.bikeproject;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SplashScreen extends AppCompatActivity {
    List mListe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);


        getListe(true);
        // Define the Handler that receives messages from the thread and update the progress
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent myIntent = new Intent(SplashScreen.this, MainActivity.class);
                    myIntent.putExtra("liste", (Serializable) mListe);
                    startActivity(myIntent);
                    finish();
            }
        },1000);

    }

    public void getListe(boolean reload) {
        if (IsInternetOn(this)) {
            MyAsyncTask mTache = new MyAsyncTask();
            try {
                mListe = (List) mTache.execute(null, this.getBaseContext(), SplashScreen.this,reload,mListe,"Lyon").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No internet connection, try again", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean IsInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
