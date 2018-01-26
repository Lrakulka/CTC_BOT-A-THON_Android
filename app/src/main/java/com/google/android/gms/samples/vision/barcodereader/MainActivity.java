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

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.samples.vision.barcodereader.domain.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private TextView statusMessage;
    private TextView barcodeValue;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private Map<String, Product> availableProductBarcodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusMessage = (TextView) findViewById(R.id.status_message);
        barcodeValue = (TextView) findViewById(R.id.barcode_value);

        findViewById(R.id.read_barcode).setOnClickListener(this);

        availableProductBarcodes = getAvailableProductBarcodes();
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

                    statusMessage.setText(R.string.barcodes_success);
                    barcodeValue.setText(barcodes.toString());

                    barcodes = filterBarCodes(barcodes);
                    updateOrderList(barcodes);
                    Log.d(TAG, "Barcodes read: " + barcodes.toString());
                } else {
                    statusMessage.setText(R.string.barcodes_failure);
                    Log.d(TAG, "No barcodes captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcodes_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
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

    private Map<String, Product> getAvailableProductBarcodes() {
        Map<String, Product> availableProductBarcodes = new HashMap();


        return availableProductBarcodes;
    }
}
