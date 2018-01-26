/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.adapter.CustomAdapter;
import com.google.android.gms.samples.vision.barcodereader.domain.DataModel;
import com.google.android.gms.samples.vision.barcodereader.domain.Product;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    public static final String URL = "http://ecsc00a00eec.epam.com/product";

    private RequestQueue queue;
    private Map<String, Product> availableProductBarcodes;


    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.read_barcode).setOnClickListener(this);

        queue = Volley.newRequestQueue(this);

        getAvailableProductBarcodes();



        listView=(ListView)findViewById(R.id.order_list);

        dataModels= new ArrayList<>();

        dataModels.add(new DataModel("Apple Pie", "Android 1.0", "1","September 23, 2008"));
        dataModels.add(new DataModel("Banana Bread", "Android 1.1", "2","February 9, 2009"));
        dataModels.add(new DataModel("Cupcake", "Android 1.5", "3","April 27, 2009"));
        dataModels.add(new DataModel("Donut","Android 1.6","4","September 15, 2009"));
        dataModels.add(new DataModel("Eclair", "Android 2.0", "5","October 26, 2009"));
        dataModels.add(new DataModel("Froyo", "Android 2.2", "8","May 20, 2010"));
        dataModels.add(new DataModel("Gingerbread", "Android 2.3", "9","December 6, 2010"));
        dataModels.add(new DataModel("Honeycomb","Android 3.0","11","February 22, 2011"));
        dataModels.add(new DataModel("Ice Cream Sandwich", "Android 4.0", "14","October 18, 2011"));
        dataModels.add(new DataModel("Jelly Bean", "Android 4.2", "16","July 9, 2012"));
        dataModels.add(new DataModel("Kitkat", "Android 4.4", "19","October 31, 2013"));
        dataModels.add(new DataModel("Lollipop","Android 5.0","21","November 12, 2014"));
        dataModels.add(new DataModel("Marshmallow", "Android 6.0", "23","October 5, 2015"));

        adapter= new CustomAdapter(dataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);

                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    List<String> barcodes = data
                            .getStringArrayListExtra(BarcodeCaptureActivity.BarcodeObjects);

                    barcodes = filterBarCodes(barcodes);
                    updateOrderList(barcodes);
                    Log.d(TAG, "Barcodes read: " + barcodes.toString());
                } else {
                    Toast.makeText(this, R.string.barcodes_failure, Toast.LENGTH_LONG)
                            .show();
                    Log.d(TAG, "No barcodes captured, intent data is null");
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcodes_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_LONG)
                        .show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateOrderList(List<String> barcodes) {
        //TODO: fill with user List
    }

    private List<String> filterBarCodes(List<String> barcodes) {
        StringBuilder unknownBarcodes = new StringBuilder();
        List<String> correctBarcodes = new ArrayList<>();
        for (String barcode : barcodes) {
            if (availableProductBarcodes.containsKey(barcode)) {
                correctBarcodes.add(barcode);
            } else {
                unknownBarcodes.append(" ").append(barcode);
            }
        }
        if (unknownBarcodes.length() != 0) {
            unknownBarcodes.insert(0, "Not available codes:");
            Toast.makeText(this, unknownBarcodes.toString(), Toast.LENGTH_LONG).show();
        }
        return correctBarcodes;
    }

    private void getAvailableProductBarcodes() {
        final Context context = this;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {

                    private ObjectMapper objectMapper = new ObjectMapper();

                    @Override
                    public void onResponse(String response) {
                        Product[] products = new Product[0];
                        try {
                            products = objectMapper.readValue(response, Product[].class);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(context, "Can't parse data from server",
                                    Toast.LENGTH_LONG).show();
                        }
                        availableProductBarcodes = new HashMap<>();
                        for (Product product : products) {
                            availableProductBarcodes.put(product.getBarCode(), product);
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Can't make request to the server",
                                Toast.LENGTH_LONG).show();
                    }
        });
        queue.add(stringRequest);
    }
}
