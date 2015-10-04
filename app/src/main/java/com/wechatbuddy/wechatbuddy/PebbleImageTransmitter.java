//
//  PebbleImageTransmitter.java
//  WeChatBuddy
//
//  Created by Tyler O, Jessie L, Kenneth C on 8/29/15.
//  Copyright (c) 2015 Tyler O, Jessie L, Kenneth C. All rights reserved.

package com.wechatbuddy.wechatbuddy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PebbleImageTransmitter {

    /* The key used to transmit download data. Contains byte array. */
    private static final int DL_DATA = 100;
    /* The key used to start a new image transmission. Contains uint32 size */
    private static final int DL_BEGIN = 101;
    /* The key used to finalize an image transmission. Data not defined. */
    private static final int DL_END = 102;
    /* The key used to tell the JS how big chunks should be */
    private static final int DL_CHUNK_SIZE = 116;

    private static final UUID PEBBLE_APP_UUID = UUID.fromString("043fe8a1-70df-403b-a7bb-3338db1fa55f");

    private Bitmap QRCode;
    private MainActivity delegate;
    private List<PebbleDictionary> packages;

    public PebbleImageTransmitter(Bitmap bitmap){
        this.QRCode = bitmap;
    }

    public void sendBitmapToPebble(){

        packages = new ArrayList<PebbleDictionary>();

        makeSmallPackages();
        sendPackages();
    }

    public void setDelegate(MainActivity ma) {
        this.delegate = ma;
    }

    private void makeSmallPackages(){

        byte[] byteArray = getBytes();

        addDictionaryForType(DL_BEGIN, byteArray.length);
        addDataDictionaries(byteArray);
        addDictionaryForType(DL_END, 0);
    }

    private byte[] getBytes() {

        int width = QRCode.getWidth();
        int widthWithPadding = 128;
        int height = QRCode.getHeight();

        byte[] bytes = new byte[(widthWithPadding * height +7) / 8];

        for (int y = 0; y < height; y++) {
            int offset = y * widthWithPadding;
            for (int x = 0; x < width; x++) {
                int pixel = QRCode.getPixel(x, y);
                if (pixel == Color.WHITE) {
                    setBitAtIndex(bytes, offset + x);
                }
            }
        }
        return bytes;
    }

    private void setBitAtIndex(byte[] byteArray, int index) {
        int arrayIndex = index / 8;
        int bitIndex = index % 8;
        byte original = byteArray[arrayIndex];
        byte updated = (byte) (original | (byte) (1 << bitIndex));
        byteArray[arrayIndex] = updated;
    }

    private void addDictionaryForType(int key, int value) {
        PebbleDictionary dictionary = new PebbleDictionary();
        dictionary.addInt32(key, value);
        packages.add(dictionary);
    }

    private void addDataDictionaries(byte[] byteArray) {

        int length = byteArray.length;

        for(int i = 0; i < length; i += DL_CHUNK_SIZE){

            byte[] miniByteArr = new byte[Math.min(DL_CHUNK_SIZE, length - i)];
            System.out.println(i + DL_CHUNK_SIZE);
            System.arraycopy(byteArray, i, miniByteArr, 0, Math.min(DL_CHUNK_SIZE, length - i));
            PebbleDictionary dataDictionary = new PebbleDictionary();

            dataDictionary.addBytes(DL_DATA, miniByteArr);
            packages.add(dataDictionary);
        }
    }

    private void sendPackages() {

        delegate.willStartTransmitting();

        PebbleKit.registerReceivedAckHandler(delegate, new PebbleKit.PebbleAckReceiver(PEBBLE_APP_UUID) {

            @Override
            public void receiveAck(Context context, int transactionId) {
                Log.i("PebbleImageTransmitter", "Received ack for transaction " + transactionId);
                transactionId++;
                if (transactionId < packages.size()) {
                    PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, packages.get(transactionId), transactionId);
                    delegate.didTransmitNumberOfPackages(transactionId, packages.size());
                } else if (transactionId == packages.size()) {
                    context.unregisterReceiver(this);
                    delegate.didFinishTransmitting();
                }

            }
        });

        PebbleKit.registerReceivedNackHandler(delegate, new PebbleKit.PebbleNackReceiver(PEBBLE_APP_UUID) {

            @Override
            public void receiveNack(Context context, int transactionId) {
                Log.i("PebbleImageTransmitter", "Received nack for transaction " + transactionId);
                context.unregisterReceiver(this);
                delegate.didFailTransmitting();
            }
        });

        PebbleKit.sendDataToPebbleWithTransactionId(delegate, PEBBLE_APP_UUID, packages.get(0), 0);
    }

}