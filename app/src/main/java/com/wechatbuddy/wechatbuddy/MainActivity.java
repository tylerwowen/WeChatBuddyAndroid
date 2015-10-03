//
//  MainActivity.java
//  WeChatBuddy
//
//  Created by Tyler O, Jessie L, Kenneth C on 8/29/15.
//  Copyright (c) 2015 Tyler O, Jessie L, Kenneth C. All rights reserved.
//

package com.wechatbuddy.wechatbuddy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends Activity implements PebbleImageTransmitterDelegate{

    private final int SELECT_PHOTO = 1;
    private ImageView imageView;
    private Bitmap QRCode;

    public void willStartTransmitting(){
        Toast.makeText(this, "delegate works", Toast.LENGTH_SHORT).show();
    }

    public void didTransmitNumberOfPackages(int numberOfPackages, int total){}

    public void didFailTransmitting(){}

    public void didFinishTransmitting(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.QRCodeIMG);

        final Button button = (Button) findViewById(R.id.gallery_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        this.QRCode = BitmapFactory.decodeStream(imageStream);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
        this.processImage();
        imageView.setImageBitmap(this.QRCode);

        PebbleImageTransmitter pit = new PebbleImageTransmitter(this.QRCode);

        pit.setDelegate(this);
        pit.sendBitmapToPebble();

    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isConnected = PebbleKit.isWatchConnected(this);
        Toast.makeText(this, "Pebble " + (isConnected ? "is" : "is not") + " connected!", Toast.LENGTH_LONG).show();
    }

    private void processImage() {

        this.resizeQRCode();
        QRCodeRegenerator regenerator = new QRCodeRegenerator();
        this.QRCode = regenerator.regenerateQRCodeWithBitmap(this.QRCode);
    }

    private void resizeQRCode() {
        int height = 640 < (int)(this.QRCode.getHeight() * 0.3) ? 640 : (int)(this.QRCode.getHeight() * 0.3);
        int width = 480 < (int)(this.QRCode.getHeight() * 0.3) ? 480 : (int)(this.QRCode.getHeight() * 0.3);
        this.QRCode = Bitmap.createScaledBitmap(this.QRCode, width, height, true);
    }
}
