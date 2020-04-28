package com.example.films;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom Adapter qui permet le remplissage de la de la View contenant la liste de films
 */
public class CustomFilmsAdapter extends ArrayAdapter<Film> {
    public CustomFilmsAdapter(Context context, ArrayList<Film> films) {
        super(context, 0, films);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_fims, parent, false);
        }
        //Récupère tous les champs des éléments de la ListView
        Film film = getItem(position);
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvAnnee = (TextView) convertView.findViewById(R.id.tvAnnee);
        TextView tvReal = (TextView) convertView.findViewById(R.id.tvRealisateur);
        TextView tvProd = (TextView) convertView.findViewById(R.id.tvProducteur);
        TextView tvCout = (TextView) convertView.findViewById(R.id.tvCout);
        ImageView tvImage = convertView.findViewById(R.id.tvImage);
        //Assigne les attributs de Film aux champs de la ListView
        assert film != null;
        tvName.setText(film.getName());
        tvAnnee.setText(String.valueOf(film.getDate()));
        tvReal.setText(film.getRealisateur());
        tvProd.setText(film.getProducteur());
        tvCout.setText(film.getCout());
        Picasso.with(getContext()).load(film.getImage()).into(tvImage);

        //Renvoie la vue
        return convertView;
    }
}