//Author: Kenneth Chan

package com.wechatbuddy.wechatbuddy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

    public void didTransmitNumberOfPackges(int numberOfPackages, int total){}

    public void didFailTransmitting(){}

    public void didFinishTransmitting(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.imageView);

        final Button button = (Button) findViewById(R.id.load);
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
        //PIT pit = new pit()
        //pit.sendBtP(this.QRCode);
        //pit.getResult();
        //
        PebbleImageTransmitter pit = new PebbleImageTransmitter(this.QRCode);

        pit.setDelegate(this);
        pit.sendBitmaptoPebble();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isConnected = PebbleKit.isWatchConnected(this);
        Toast.makeText(this, "Pebble " + (isConnected ? "is" : "is not") + " connected!", Toast.LENGTH_LONG).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
