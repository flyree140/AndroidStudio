package com.example.connectgoogleexcel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.app.ProgressDialog;

import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonAddItem;
    Button buttonListItem;

    //清單變數
    ListView listView;
    SimpleAdapterForWarnColor adapter;
    ProgressDialog loading;
    EditText editTextSearchItem;

    //進/出貨
    Button scan_btn;

    //將目前Google sheet資料存放至全域變數
    String[][] itemList;

    //掃描到的資料
    String ScanData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);

        buttonListItem = findViewById(R.id.btn_list_item);
        buttonListItem.setOnClickListener(this);

        listView = findViewById(R.id.lv_items);
        editTextSearchItem = findViewById(R.id.et_search);
        getItems();

        //掃描器建立
        scan_btn = findViewById(R.id.scan_btn);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }

        });
    }

    @Override
    public void onClick(View v) {

        if(v==buttonAddItem){
            Intent intent = new Intent(getApplicationContext(),additem.class);
            startActivity(intent);
        }
        if(v==buttonListItem){
            Intent intent = new Intent(getApplicationContext(), ListItem.class);
            startActivity(intent);
        }

    }


    //撈GOOGLE Apps Script資料
    private void getItems() {

        loading =  ProgressDialog.show(this,"載入資料中","請稍後...",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbzm9NrOlQJw2aS-OZJv4rKDjOaEOZQ17XpY9J2_QG_5XkrK57A/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //呼叫解析JSON的函式
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

    //將GOOGLE Apps Script JSON資料 解析並顯示在清單上
    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");
            int totalRows = 7;//新增的欄位要在這加入*** 1.目前Google sheet總共幾行
            itemList = new String[jarray.length()][totalRows];
            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String itemId = jo.getString("itemId");
                String itemName = jo.getString("itemName");
                String category = jo.getString("category");
                String unitPrice = jo.getString("unitPrice");
                String stafetyStock = jo.getString("stafetyStock");
                String count = jo.getString("count");
                //新增的欄位要在這加入*** 2.加入新欄位的解析

                itemList[i][0] = itemId; //所有代號
                itemList[i][1] = itemName; //所有品項名稱
                itemList[i][5] = stafetyStock; //所有安全庫存
                itemList[i][6] = count; //所有目前倉庫數量


                HashMap<String, String> item = new HashMap<>();
                item.put("itemName", itemName);
                item.put("category", category);
                item.put("unitPrice", unitPrice);
                item.put("stafetyStock", stafetyStock);
                item.put("count", count);
                //新增的欄位要在這加入*** 3.將資料拋入清單中
                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapterForWarnColor(this,list,R.layout.list_item_row,
                new String[]{"itemName","category","stafetyStock","count","unitPrice"},new int[]{R.id.tv_item_name,R.id.tv_category,R.id.tv_stafetyStock,R.id.tv_count});
        //新增的欄位要在這加入*** 4.new String[]{"itemName","category"} 拋入清單上(只是將資料放入) /new int[]{R.id.tv_item_name,R.id.tv_category} 要顯示的ID


//        listView.setBackgroundColor(Color.argb(255,255,0,0));
//        listView.setSelector(R.color.colorAccent); 點擊時背景顏色
        listView.setAdapter(adapter);
        loading.dismiss();

        editTextSearchItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                MainActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

//    掃描器作用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            if (result.getContents() == null){
                Toast.makeText(this, "You canaelled the scanning", Toast.LENGTH_LONG).show();
            }else {
                ScanData = result.getContents();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

        workZone();
    }

    //彈跳視窗，編輯進出/貨
    public void workZone() {
        for (int i = 0; i < itemList.length; i++) {
            if (ScanData.equals(itemList[i][0])) {
                //進出貨區--------------------------------------
                Toast.makeText(this, ScanData, Toast.LENGTH_LONG).show();

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("進/出貨管理");
                dialog.setMessage("品項名稱：" + ScanData);
                final EditText input = new EditText(this);
                dialog.setView(input);
                //左邊按鈕
                final int finalI1 = i;
                dialog.setNeutralButton("進貨", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        orderProduct(input.getText().toString().trim(), "in", itemList[finalI1][0]);
                    }

                });


                //中間按鈕
                final int finalI = i;
                dialog.setNegativeButton("出貨", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        orderProduct(input.getText().toString().trim(), "out", itemList[finalI][0]);
                    }

                });

                //右邊按鈕
                dialog.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this, "取消動作", Toast.LENGTH_SHORT).show();
                    }

                });
                dialog.show();
                break;
                //進出貨區--------------------------------------
            } else {
                Toast.makeText(this, itemList[i][0], Toast.LENGTH_LONG).show();
            }

        }
    }

    //將資料拋至GOOGLE Apps Script處理再傳回
    public void orderProduct(final String count, final String type, final String itemId){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzm9NrOlQJw2aS-OZJv4rKDjOaEOZQ17XpY9J2_QG_5XkrK57A/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
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

                //here we pass params
                parmas.put("action","modifyItem");
                parmas.put("itemId",itemId);
                parmas.put("count",count);
                parmas.put("type",type);

                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);
    }

}


