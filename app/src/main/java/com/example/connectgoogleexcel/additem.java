package com.example.connectgoogleexcel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class additem extends AppCompatActivity implements View.OnClickListener {


    EditText editTextItemName,editTextCategory,editTextunitPrice,editTextstafetyStock,editTextcount;//新增的欄位要在這加入*** 1.
    Button buttonAddItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_item);

        editTextItemName = findViewById(R.id.et_item_name);
        editTextCategory = findViewById(R.id.et_category);
        editTextunitPrice = findViewById(R.id.et_unitPrice);
        editTextstafetyStock = findViewById(R.id.et_stafetyStock);
        editTextcount = findViewById(R.id.et_count);
        //新增的欄位要在這加入*** 2.

        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);


    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(this,"新增項目","請稍後...");
        final String name = editTextItemName.getText().toString().trim();
        final String category = editTextCategory.getText().toString().trim();
        final String unitPrice = editTextunitPrice.getText().toString().trim();
        final String stafetyStock = editTextstafetyStock.getText().toString().trim();
        final String count = editTextcount.getText().toString().trim();
        //新增的欄位要在這加入*** 3.



        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzm9NrOlQJw2aS-OZJv4rKDjOaEOZQ17XpY9J2_QG_5XkrK57A/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(additem.this,response,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //標籤及要傳送的值  parmas.put("標籤","資料"); 新增的欄位要在這加入*** 4.
                parmas.put("action","addItem");
                parmas.put("itemName",name);
                parmas.put("category",category);
                parmas.put("unitPrice",unitPrice);
                parmas.put("stafetyStock",stafetyStock);
                parmas.put("count",count);
                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }




    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            addItemToSheet();

            //Define what to do when button is clicked
        }
    }
}
