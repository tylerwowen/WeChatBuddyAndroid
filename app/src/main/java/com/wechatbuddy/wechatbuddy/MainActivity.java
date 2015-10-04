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
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.getpebble.android.kit.PebbleKit;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends Activity implements PebbleImageTransmitterDelegate {

    private final int SELECT_PHOTO = 1;
    private ImageView imageView;
    private Bitmap QRCode;
    private TextView promptText;
    private ProgressBar transmittingProgressBar;
    private static final UUID PEBBLE_APP_UUID = UUID.fromString("043fe8a1-70df-403b-a7bb-3338db1fa55f");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.QRCodeIMG);
        transmittingProgressBar = (ProgressBar) findViewById(R.id.transmittingProgressBar);
        promptText = (TextView)findViewById(R.id.promptText);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = imageReturnedIntent.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                QRCode = BitmapFactory.decodeStream(imageStream);

                this.processImage();
                imageView.setImageBitmap(QRCode);

                PebbleImageTransmitter pit = new PebbleImageTransmitter(QRCode);
                pit.setDelegate(this);
                pit.sendBitmapToPebble();

            } catch (FileNotFoundException e) {
                return;
            } catch (Exception e) {

                promptText.setText("Oops, unable to find a QR code. \nPlease select an image " +
                        "that contains a QR code.");
                QRCode = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PebbleKit.startAppOnPebble(this, PEBBLE_APP_UUID);
    }

    public void openGallery(View view) {

        boolean isConnected = PebbleKit.isWatchConnected(this);
        if (!isConnected) {
            promptText.setText("Your Pebble is not connected. Please connect it to your " +
                    "phone and try again.");
            return;
        }
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    public void openWebView(View view) {
        Intent intent = new Intent(this, AboutAppActivity.class);
        startActivity(intent);
    }

    private void processImage() throws Exception {
        this.resizeQRCode();
        QRCodeRegenerator regenerator = new QRCodeRegenerator();
        QRCode = regenerator.regenerateQRCodeWithBitmap(this.QRCode);
    }

    private void resizeQRCode() {
        int height = 640 < (int) (this.QRCode.getHeight() * 0.3) ?
                640 : (int) (this.QRCode.getHeight() * 0.3);
        int width = 480 < (int) (this.QRCode.getHeight() * 0.3) ?
                480 : (int) (this.QRCode.getHeight() * 0.3);
        this.QRCode = Bitmap.createScaledBitmap(this.QRCode, width, height, true);
    }

    public void willStartTransmitting() {
        promptText.setText("Sending...");
        transmittingProgressBar.setProgress(0);
        transmittingProgressBar.setVisibility(View.VISIBLE);
    }

    public void didTransmitNumberOfPackages(int numberOfPackages, int total) {
        transmittingProgressBar.setProgress(numberOfPackages * 100 / total);
    }

    public void didFailTransmitting() {
        promptText.setText("Failed to send the QR code. \nPlease try again");
        transmittingProgressBar.setVisibility(View.INVISIBLE);
    }
    public void didFinishTransmitting() {
        promptText.setText("Success! Now you can use you Pebble App only :)");
        transmittingProgressBar.setVisibility(View.INVISIBLE);
    }
}
