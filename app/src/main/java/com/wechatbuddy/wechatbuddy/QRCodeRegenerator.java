package com.wechatbuddy.wechatbuddy;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * Created by Kenneth on 8/29/2015.
 */
public class QRCodeRegenerator extends  Object{
    private Bitmap image = null;          //UIImage *image
    private String data = null;            //NSString *data

    public QRCodeRegenerator(){
        super();
    }

    /*
    image = BitmapFactory.decodeFile("/path/images/image.jpg");
    ByteArrayOutputStream blob = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.PNG, 0, blob);
    byte[] bitmapdata = blob.toByteArray();
    */

    public Bitmap regenerateQRCodeWithBitmap(Bitmap inputIMG) {
        this.image = inputIMG;
        this.decodeOriginalImage();
        this.encodeQRCode();

        return this.image;
    }

    private void decodeOriginalImage() {

        int[] intArray = new int[image.getWidth()*image.getHeight()];
        //copy pixel data from the Bitmap into the 'intArray' array
        image.getPixels(intArray, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        LuminanceSource source = new RGBLuminanceSource(image.getWidth(), image.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();

        try {
            Result result = reader.decode(bitmap);
            this.data = result.getText();
        }
        catch(ReaderException e) {
            Log.e("DecodingImageERROR", e.getMessage());
        }
    }

    private void encodeQRCode() {

        if(this.data != null) {
            this.image = null;
            return;
        }
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(this.data, BarcodeFormat.QR_CODE, 116, 116);
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
