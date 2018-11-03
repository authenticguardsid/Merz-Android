package com.realmattersid;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;


public class QrCodeActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    BarcodeReader barcodeReader;
    String GENIUNE_CODE = "GENIUNE";
    String FAKE_CODE = "FAKE";
    String rvalid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_scanner);
    }

    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();
        validation_code(barcode.displayValue);

    }

    @Override
    public void onScannedMultiple(List<Barcode> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onCameraPermissionDenied() {
        finish();
    }

    @Override
    public void onScanError(String s) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }

    public void validation_code(final String scancode){

        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/scanner.class.php?action=valid&code="+scancode, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            rvalid = data.getString("rvalid");
                            String gcode = data.getString("gcode");
                            String partner = data.getString("partner");
                        } catch (JSONException e) {

                        }
                    }
                    if (rvalid.equals(GENIUNE_CODE)){
                        Intent intent_geniune = new Intent(QrCodeActivity.this, ResultCodeActivity.class);
                        intent_geniune.putExtra("key", scancode);
                        startActivity(intent_geniune);
                    }
                    if (rvalid.equals(FAKE_CODE)){
                        Intent intent_fake = new Intent(QrCodeActivity.this, FakeCodeActivity.class);
                        startActivity(intent_fake);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(arrayRequest);
    }
}
