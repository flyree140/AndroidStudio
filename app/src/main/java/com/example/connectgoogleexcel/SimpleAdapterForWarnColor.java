package com.example.connectgoogleexcel;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class SimpleAdapterForWarnColor extends SimpleAdapter {


    public SimpleAdapterForWarnColor(Context context, List<HashMap<String, String>> items, int resource, String[] from, int[] to) {
        super(context, items, resource, from, to);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        HashMap<String,String> map =(HashMap)getItem(position);
//        String itemId = map.get("itemId").toString();
//        String itemName = map.get("itemName").toString();
//        String category = map.get("category").toString();
//        int stafetyStock = Integer.parseInt(map.get("stafetyStock"));
//        int count = Integer.parseInt(map.get("count"));
//        if(count - stafetyStock <= 0 )
//          123
        if(Integer.parseInt(map.get("count")) - Integer.parseInt(map.get("stafetyStock")) <= 0)
        {
            // do something change color
            row.setBackgroundColor (Color.argb(255,252,201,185)); // some color
        }
        else
        {
            // default state
            row.setBackgroundColor (Color.argb(255,162,215,221)); // default color
        }
        return row;
    }
}

