package com.comtip.tip.tube4uv3;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TipRayong on 26/7/2559.
 */
public class CustomArrayList extends ArrayAdapter {
    private Activity context;
    private ArrayList<String[]> arrayListData;

    public CustomArrayList(Activity context, ArrayList<String[]> arrayListData) {
        super(context, R.layout.listview_custom, arrayListData);
        this.context = context;
        this.arrayListData = arrayListData;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listviewItem = inflater.inflate(R.layout.listview_custom, null, true);
        TextView textviewCustom = (TextView) listviewItem.findViewById(R.id.textviewCustom);
        textviewCustom.setText(arrayListData.get(position)[1]);
        return listviewItem;
    }

}




