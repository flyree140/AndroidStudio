package com.example.connectgoogleexcel;

import android.app.ProgressDialog;
import android.os.Bundle;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListItem extends AppCompatActivity {


    ListView listView;
    ListAdapter adapter;
    ProgressDialog loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        listView = findViewById(R.id.lv_items);

        getItems();

    }



    private void getItems() {

            loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbzm9NrOlQJw2aS-OZJv4rKDjOaEOZQ17XpY9J2_QG_5XkrK57A/exec",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseItems(response);
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }
            );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }



    private void parseItems(String jsonResposnce) {
        Toast.makeText(ListItem.this, jsonResposnce,Toast.LENGTH_LONG).show();
        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String itemName = jo.getString("itemName");
                String brand = jo.getString("brand");


                HashMap<String, String> item = new HashMap<>();
                item.put("itemName", itemName);
                item.put("brand", brand);

                list.add(item);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(this, list, R.layout.list_item_row,
                new String[]{"itemName", "brand"}, new int[]{R.id.tv_item_name, R.id.tv_brand});
        listView.setAdapter(adapter);
        loading.dismiss();
    }
}


