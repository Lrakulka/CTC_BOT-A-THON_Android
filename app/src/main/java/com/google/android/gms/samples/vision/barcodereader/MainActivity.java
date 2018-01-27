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
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.adapter.CustomAdapter;
import com.google.android.gms.samples.vision.barcodereader.dto.DataModel;
import com.google.android.gms.samples.vision.barcodereader.domain.Product;
import com.google.android.gms.samples.vision.barcodereader.dto.PhoneTemplateDto;
import com.google.android.gms.samples.vision.barcodereader.dto.UserData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int RC_USER_DATA_GETTER = 9002;
    private static final String TAG = "BarcodeMain";
    private static final String URL = "http://ecsc00a00eec.epam.com:8080/product";
    private static final String URL_PLACE_ORDER = "http://ecsc00a00eec.epam.com:8080/template/phoneOrder";

    private static CustomAdapter adapter;
    private RequestQueue queue;
    private Map<String, Product> availableProductBarcodes;
    private ArrayList<DataModel> dataModels;
    private UserData userData;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.read_barcode).setOnClickListener(this);

        queue = Volley.newRequestQueue(this);
        listView = (ListView) findViewById(R.id.order_list);

        getAvailableProductBarcodes();
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

    public void setUserData(View view) {
        if (view.getId() == R.id.user_data) {
            // launch barcode activity.
            Intent intent = new Intent(this, UserDataActivity.class);
            startActivityForResult(intent, RC_USER_DATA_GETTER);
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
        switch (requestCode) {
            case (RC_BARCODE_CAPTURE): {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        List<String> barcodes = data
                                .getStringArrayListExtra(BarcodeCaptureActivity.BarcodeObjects);

                        Map<String, Integer> barcodesQuantity = filterBarCodes(barcodes);

                        updateOrderList(barcodesQuantity, availableProductBarcodes);

                        Log.d(TAG, "Barcodes read: " + barcodesQuantity.toString());
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
                break;
            }
            case (RC_USER_DATA_GETTER): {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (data != null) {
                        Long userId = data.getLongExtra(UserDataActivity.UserID, 0);
                        String startDate = data.getStringExtra(UserDataActivity.StartDate);
                        Integer timeUnitAmount = data.getIntExtra(UserDataActivity.TimeUnitAmount, 0);
                        String timeUnit = data.getStringExtra(UserDataActivity.TimeUnit);

                        userData = new UserData(userId, startDate, timeUnitAmount, timeUnit);
                        Toast.makeText(this, "User data received. User id: " + userId,
                                Toast.LENGTH_LONG)
                                .show();
                        Log.d(TAG, "User data received. User id: " + userId);
                    } else {
                        Toast.makeText(this, "No user data captured", Toast.LENGTH_LONG)
                                .show();
                        Log.d(TAG, "No user data captured, intent data is null");
                    }
                } else {
                    Toast.makeText(this, "Can't get user data", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void updateOrderList(Map<String, Integer> barcodesQuantity,
                                 Map<String, Product> availableProductBarcodes) {

        if (dataModels == null) {
            dataModels = new ArrayList<>();
        }

        for (String barcode : availableProductBarcodes.keySet()) {
            if (barcode == null || barcode.isEmpty()) {
                continue;
            }
            Product product = availableProductBarcodes.get(barcode);
            DataModel dataModel = new DataModel(
                    product.getName(),
                    barcode,
                    barcodesQuantity.containsKey(barcode) ? barcodesQuantity.get(barcode) : 0,
                    product.getImageUri(),
                    product.getCategory().getName() + " Price " + product.getPrice().toString());
            if (dataModels.contains(dataModel)) {
                DataModel originDataModel = dataModels.get(dataModels.indexOf(dataModel));
                originDataModel.setProductQuantity(originDataModel.getProductQuantity()
                        + dataModel.getProductQuantity());
            } else {
                dataModels.add(dataModel);
            }
        }

        Collections.sort(dataModels, new Comparator<DataModel>() {
            @Override
            public int compare(DataModel o1, DataModel o2) {
                return o2.getProductQuantity().compareTo(o1.getProductQuantity());
            }
        });

        adapter = new CustomAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
    }

    private Map<String, Integer> filterBarCodes(List<String> barcodes) {
        StringBuilder unknownBarcodes = new StringBuilder();
        Map<String, Integer> BarcodeQuantity = new HashMap<>();
        for (String barcode : barcodes) {
            if (availableProductBarcodes.containsKey(barcode)) {
                BarcodeQuantity.put(barcode,
                        (BarcodeQuantity.containsKey(barcode) ? BarcodeQuantity.get(barcode) : 0)
                                + 1);
            } else {
                unknownBarcodes.append(" ").append(barcode);
            }
        }
        if (unknownBarcodes.length() != 0) {
            unknownBarcodes.insert(0, "Not available codes:");
            Toast.makeText(this, unknownBarcodes.toString(), Toast.LENGTH_LONG).show();
        }
        return BarcodeQuantity;
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

                        updateOrderList(Collections.EMPTY_MAP, availableProductBarcodes);
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

    public void sendOrder(View view) {
        final Context context = this;
        if (isOrderValid(dataModels) || isUserDataValid(userData)) {
            Toast.makeText(context, "Can't place order. Order is empty or user data incorrect",
                    Toast.LENGTH_LONG).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL_PLACE_ORDER,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context, "Order placed", Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Can't make place order on the server",
                                Toast.LENGTH_LONG).show();
                    }
                }) {

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return getOrderBody(adapter.getDataList());
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
        queue.add(stringRequest);
    }

    private boolean isUserDataValid(UserData userData) {
        return userData == null || userData.getStartDate().isEmpty()
                || userData.getTimeUnit().isEmpty() || userData.getUserId() <= 0
                || userData.getTimeUnitAmount() <= 0;
    }

    private boolean isOrderValid(List<DataModel> dataModels) {
        return dataModels == null || dataModels.isEmpty();
    }

    private byte[] getOrderBody(ArrayList<DataModel> dataList) {
        PhoneTemplateDto phoneTemplateDto = new PhoneTemplateDto();
        phoneTemplateDto.setUserId(userData.getUserId());
        phoneTemplateDto.setStartDate(userData.getStartDate());
        phoneTemplateDto.setTimeUnit(userData.getTimeUnit());
        phoneTemplateDto.setTimeUnitAmount(userData.getTimeUnitAmount());
        List<PhoneTemplateDto.PhoneTemplateItem> barCodes = new ArrayList<>();
        for (DataModel dataModel : dataModels) {
            if (dataModel.getProductQuantity() <= 0) {
                continue;
            }
            PhoneTemplateDto.PhoneTemplateItem phoneTemplateItem =
                    new PhoneTemplateDto.PhoneTemplateItem(dataModel.getProductBarcode(),
                            dataModel.getProductQuantity());
            barCodes.add(phoneTemplateItem);
        }
        phoneTemplateDto.setBarCodes(barCodes);
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(phoneTemplateDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json.getBytes();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
    }
}
