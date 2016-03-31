package com.example.thomas.bikeproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 07/03/2016.
 */
public class AsyncVille extends AsyncTask {
    List mListe;
    @Override
    protected Object doInBackground(Object[] params) {

        mListe=(List) params[0];
        if(mListe==null)
            mListe=new ArrayList();
        //si fait appel à reload avec swipe refresh

// connexions http, traitements lourds, etc.
            URL url = null;
            try {
                url = new URL("https://api.jcdecaux.com/vls/v1/contracts?apiKey=80ddc1cff88b645913acbfbfeeea8897a142b951");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    mListe = readJsonStream(urlConnection.getInputStream());

                    urlConnection.disconnect(); // on ferme la connection
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        return mListe;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    public List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List readMessagesArray(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public String readMessage(JsonReader reader) throws IOException {
        String name = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String tmp = reader.nextName();

             if (tmp.equals("name")) {
                name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new String(name);
    }


}

