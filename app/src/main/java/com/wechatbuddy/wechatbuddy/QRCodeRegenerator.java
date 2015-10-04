//
//  QRCodeRegenerator.java
//  WeChatBuddy
//
//  Created by Tyler O on 8/29/15.
//  Copyright (c) 2015 Tyler O, Jessie L, Kenneth C. All rights reserved.

package com.wechatbuddy.wechatbuddy;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

class QRCodeRegenerator {
    private Bitmap image = null;          //UIImage *image
    private String data = null;           //NSString *data

    public Bitmap regenerateQRCodeWithBitmap(Bitmap inputIMG) throws Exception {

        this.image = inputIMG;
        this.decodeOriginalImage();
        this.encodeQRCode();

        return this.image;
    }

    private void decodeOriginalImage() throws Exception {

        int[] intArray = new int[image.getWidth()*image.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        image.getPixels(intArray, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        LuminanceSource source = new RGBLuminanceSource(image.getWidth(), image.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();

        Result result = reader.decode(bitmap);
        this.data = result.getText();

    }

    private void encodeQRCode() {

        if(this.data == null) {
            this.image = null;
            return;
        }
        WBQRCodeWriter writer = new WBQRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(this.data, BarcodeFormat.QR_CODE, 116, 116);
            this.image = toBitmap(matrix);

        }
        // TODO: exception handling
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given Matrix on a new Bitmap object.
     * @param matrix the matrix to write.
     * @return the new {@link Bitmap}-object.
     */
    private Bitmap toBitmap(BitMatrix matrix){
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

}
