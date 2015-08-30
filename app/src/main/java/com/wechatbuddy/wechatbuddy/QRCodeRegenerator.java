package com.wechatbuddy.wechatbuddy;

import android.graphics.Bitmap;

/**
 * Created by Kenneth on 8/29/2015.
 */
public class QRCodeRegenerator {
    private Bitmap image;          //UIImage *image
    private String data;            //NSString *data

    public QRCodeRegenerator(){

    }

    /*
    image = BitmapFactory.decodeFile("/path/images/image.jpg");
    ByteArrayOutputStream blob = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 0, blob);
    byte[] bitmapdata = blob.toByteArray();
    */

    public Bitmap regenerateQRCodeWithUIImage(Bitmap inputIMG) {
        this.image = inputIMG;
        this.decodeOriginalImage();
        this.encodeQRCode();

        return this.image;
    }
}
