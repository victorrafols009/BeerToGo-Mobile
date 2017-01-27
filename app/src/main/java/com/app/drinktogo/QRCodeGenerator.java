package com.app.drinktogo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Created by Ken on 18/01/2017.
 */

public class QRCodeGenerator extends AppCompatActivity {
    private String qrtext = "";
    private EditText qrcontent;
    private ImageView qrdisplay;
    private Button qrshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("QR Code");

//        qrcontent = (EditText) findViewById(R.id.qrtext);
//        qrshow = (Button) findViewById(R.id.qrshow);
        qrdisplay = (ImageView) findViewById(R.id.qrdisplay);

        String qrcontent = getIntent().getStringExtra("qr_code");

//        qrshow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{

//                    BitMatrix bitMatrix = multiFormatWriter.encode(qrcontent.getText().toString(), BarcodeFormat.QR_CODE,200,200);
                    BitMatrix bitMatrix = multiFormatWriter.encode(qrcontent, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrdisplay.setImageBitmap(bitmap);
                }
                catch (WriterException e){
                    e.printStackTrace();
                }
//            }
//        });
    }
}
