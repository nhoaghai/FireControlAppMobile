package com.vn.firecontrolapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<Rooms> {

    private Context context;
    private ArrayList<Rooms> arrayList;
    private int layoutResource;

    public CustomArrayAdapter(@NonNull Context context, int resource, ArrayList<Rooms> object) {
        super(context, resource, object);
        this.context = context;
        this.arrayList = object;
        this.layoutResource = resource;
    }


    @SuppressLint({"SetTextI18n", "ViewHolder"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(layoutResource, null);
        //Hàm khởi thêm dữ lieu vào các View từ arrayList thông qua position
        TextView room = (TextView) convertView.findViewById(R.id.room);
        room.setText(arrayList.get(position).getRoom());

        TextView highestTemp = (TextView) convertView.findViewById(R.id.highestTemp);
        highestTemp.setText("HIGHEST TEMP: " + arrayList.get(position).getHighestTemp().toString() + "ºC");

        TextView lowestTemp = (TextView) convertView.findViewById(R.id.lowestTemp);
        lowestTemp.setText("LOWEST TEMP: " + arrayList.get(position).getLowestTemp().toString() + "ºC");

        return convertView;
    }
}
