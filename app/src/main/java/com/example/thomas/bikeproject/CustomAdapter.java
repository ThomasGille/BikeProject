package com.example.thomas.bikeproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Thomas on 03/03/2016.
 */

public class CustomAdapter extends BaseAdapter implements  AdapterView.OnItemClickListener ,Filterable {

    List myList;
    Context context;
    Activity mActivity;
    final String fileName = "com.thomas.android.velibtracking_preferences";

    // on passe le context afin d'obtenir un LayoutInflater pour utiliser notre
    // row_layout.xml
    // on passe les valeurs de notre à l'adapter
    public CustomAdapter(Context context, List myList,Activity activity) {
        this.myList = myList;
        this.context = context;
        this.mActivity=activity;
    }

    // retourne le nombre d'objet présent dans notre liste
    @Override
    public int getCount() {
        return myList.size();
    }

    // retourne un élément de notre liste en fonction de sa position
    @Override
    public Object getItem(int position) {
        return myList.get(position);
    }

    // retourne l'id d'un élément de notre liste en fonction de sa position
    @Override
    public long getItemId(int position) {
        return myList.indexOf(getItem(position));
    }

    // retourne la vue d'un élément de la liste
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder = null;

        // au premier appel ConvertView est null, on inflate notre layout
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.row_list, parent, false);

            // nous plaçons dans notre MyViewHolder les vues de notre layout
            mViewHolder = new MyViewHolder();
            mViewHolder.textViewName = (TextView) convertView
                    .findViewById(R.id.textViewName);
            mViewHolder.textViewFreeBike = (TextView) convertView
                    .findViewById(R.id.textViewFreeBike);


            // nous attribuons comme tag notre MyViewHolder à convertView
            convertView.setTag(mViewHolder);
        } else {
            // convertView n'est pas null, nous récupérons notre objet MyViewHolder
            // et évitons ainsi de devoir retrouver les vues à chaque appel de getView
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        // nous récupérons l'item de la liste demandé par getView
        Station listItem = (Station) getItem(position);

        // nous pouvons attribuer à nos vues les valeurs de l'élément de la liste

        int cpt =GetRealName(listItem.getName());


        mViewHolder.textViewName.setText(listItem.getName().substring(cpt));
        mViewHolder.textViewFreeBike.setText("Available bikes : " + listItem.getAvailable_bikes() + " / " + listItem.getBike_stands() + "\n");

        // nous retournons la vue de l'item demandé
        return convertView;
    }

    private int GetRealName(String text) {
        int cpt=0;
        char c = text.charAt(cpt);
        while(c!= '-'){
            cpt++;
            c=text.charAt(cpt);
        }
        return cpt+2;
        //il faut enlever le '-' et l'espace
    }

    @Override
    public Filter getFilter() {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence)
            {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0)
                {
                    results.values = myList;
                    results.count = myList.size();
                }
                else
                {
                    List<Station> filterResultsData = new ArrayList();

                    for(Station data :(List<Station>) myList)
                    {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional

                        if(data.getName().contains(charSequence.toString().toUpperCase()))
                        {
                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                myList = (List<Station>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    // MyViewHolder va nous permettre de ne pas devoir rechercher
    // les vues à chaque appel de getView, nous gagnons ainsi en performance
    private class MyViewHolder {
        TextView textViewName, textViewFreeBike;
        //ImageView imageView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int thisposition = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder
                .setTitle(((Station) myList.get(position)).getName())
                .setMessage(
                        "Status :" + ((Station) myList.get(position)).getStatus() + "\n" +
                                "Available bikes : " + ((Station) myList.get(position)).getAvailable_bikes() + " / " + ((Station) myList.get(position)).getBike_stands() + "\n" +
                                "Available bike stands : " + ((Station) myList.get(position)).getAvailable_bike_stands() + " / " + ((Station) myList.get(position)).getBike_stands() + "\n" +
                                "\n" +
                                "Address : " + ((Station) myList.get(position)).getAddress() + "\n" +
                                "Banking : " + ((Station) myList.get(position)).isBanking() + "\n" +
                                "Bonus : " + ((Station) myList.get(position)).isBonus()
                )
                        //.setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Thank you", null);
                if(((Station) myList.get(thisposition)).isFavoris()){
                    builder.setNegativeButton("Delete from bookmark", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Station) myList.get(thisposition)).setFavoris(false);
                            try {
                                writeMemory();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                    builder.setNegativeButton("Add to bookmark", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Station) myList.get(thisposition)).setFavoris(true);
                            try {
                                writeMemory();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

        builder.show();

    }

    public void writeMemory() throws IOException, ClassNotFoundException {
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream os = new ObjectOutputStream(fos);
        List < Station >  liste =new ArrayList<>();
        for (Station tmp :(List < Station>) myList)
            if (tmp.isFavoris()){
                liste.add(tmp);
            }
        os.writeObject(liste);
        os.close();
        fos.close();
    }
}
