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
public class MyAsyncTask extends AsyncTask {
    ListView maListViewPerso;
    List mListe;
    CustomAdapter adapter;
    Context mContext;
    Activity mActivity;
    String City;
    @Override
    protected Object doInBackground(Object[] params) {
        maListViewPerso = (ListView) params[0];
        mContext = (Context) params[1];
        mActivity = (Activity)params[2];
        boolean reload=(boolean) params [3];
        mListe=(List) params[4];
        City =(String) params[5];
        if(mListe==null)
            mListe=new ArrayList();
        //si fait appel à reload avec swipe refresh
        if(reload) {
// connexions http, traitements lourds, etc.
            URL url = null;
            try {
                url = new URL("https://api.jcdecaux.com/vls/v1/stations?contract="+City+"&apiKey=80ddc1cff88b645913acbfbfeeea8897a142b951");
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
        }
        return mListe;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if(maListViewPerso!=null) {
            //Creation de l'adapter avec la liste
            adapter = new CustomAdapter(mContext, mListe, mActivity);
            maListViewPerso.setAdapter(adapter);
            maListViewPerso.setOnItemClickListener(adapter);
            maListViewPerso.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                    double lat = (((Station) mListe.get(pos)).getmPosition().getLat());
                    double lng = (((Station) mListe.get(pos)).getmPosition().getLng());
                    Intent myIntent = new Intent(mActivity, MapsActivity.class);
                    myIntent.putExtra("lat", lat);
                    myIntent.putExtra("lng", lng);
                    myIntent.putExtra("liste", (Serializable) mListe);
                    mActivity.startActivity(myIntent);

                    return true;
                }
            });
        }
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

    public Station readMessage(JsonReader reader) throws IOException {
        int number = -1;
        String name = null;
        String address = null;
        Station.Position mPosition = null;
        String status = null;
        String contract_name = null;
        int bike_stands = -1;
        int available_bike_stands = -1;
        int available_bikes = -1;
        long last_update = -1;
        boolean banking = false;
        boolean bonus = false;

        reader.beginObject();
        while (reader.hasNext()) {
            String tmp = reader.nextName();
            if (tmp.equals("number")) {
                number = reader.nextInt();
            } else if (tmp.equals("bike_stands")) {
                bike_stands = reader.nextInt();
            } else if (tmp.equals("available_bike_stands")) {
                available_bike_stands = reader.nextInt();
            } else if (tmp.equals("available_bikes")) {
                available_bikes = reader.nextInt();
            } else if (tmp.equals("last_update")) {
                last_update = reader.nextLong();
            } else if (tmp.equals("name")) {
                name = reader.nextString();
            } else if (tmp.equals("address")) {
                address = reader.nextString();
            } else if (tmp.equals("status")) {
                status = reader.nextString();
            } else if (tmp.equals("contract_name")) {
                contract_name = reader.nextString();
            } else if (tmp.equals("position")) {
                mPosition = readPos(reader);
            } else if (tmp.equals("banking")) {
                banking = reader.nextBoolean();
            } else if (tmp.equals("bonus")) {
                bonus = reader.nextBoolean();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Station(number, name, mPosition, bonus, address, status, contract_name, bike_stands, available_bike_stands, available_bikes, last_update, banking);
    }

    public Station.Position readPos(JsonReader reader) throws IOException {
        double lat = -1, lng = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("lat")) {
                lat = reader.nextDouble();
            } else if (name.equals("lng")) {
                lng = reader.nextDouble();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Station.Position(lat, lng);
    }
}

