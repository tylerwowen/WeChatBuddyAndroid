package com.wechatbuddy.wechatbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kenneth on 8/29/2015.
 */
public class PebbleImageTransmitter {

    /* The key used to transmit download data. Contains byte array. */
    private static final int DL_DATA = 100;
    /* The key used to start a new image transmission. Contains uint32 size */
    private static final int DL_BEGIN = 101;
    /* The key used to finalize an image transmission. Data not defined. */
    private static final int DL_END = 102;
    /* The key used to tell the JS how big chunks should be */
    private static final int DL_CHUNK_SIZE = 103;
    /* The key used to request a PBI */
    private static final int DL_URL = 104;

    private static final int MAX_OUTGOING_SIZE = 120;

    private Bitmap QRCode;
    private MainActivity delegate;
    protected List<PebbleDictionary> packages;
    private int currentIndex;
    private UUID PEBBLE_APP_UUID = UUID.fromString("043fe8a1-70df-403b-a7bb-3338db1fa55f");


    public PebbleImageTransmitter(Bitmap bitmap){
        this.QRCode = bitmap;
    }

    public void sendBitmaptoPebble(){

        packages = new ArrayList<PebbleDictionary>();
        currentIndex = 0;

        makeSmallPackages();
        sendPackages();
    }

    public void setDelegate(MainActivity ma) {
        this.delegate = ma;

    }

    private void makeSmallPackages(){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        QRCode.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        int length = byteArray.length;

        PebbleDictionary dictionaryStart = new PebbleDictionary();

        dictionaryStart.addInt32(DL_BEGIN, length);
        packages.add(dictionaryStart);

        for(int i=0; i<length; i+=MAX_OUTGOING_SIZE){
            byte[] miniByteArr = new byte[MAX_OUTGOING_SIZE];
            System.out.println(i+MAX_OUTGOING_SIZE);
            System.arraycopy(byteArray, i, miniByteArr, 0, Math.min(MAX_OUTGOING_SIZE, length - i));
            PebbleDictionary dataDictionary = new PebbleDictionary();

            dataDictionary.addBytes(DL_DATA, miniByteArr);
            packages.add(dataDictionary);
        }
        PebbleDictionary dictionaryEnd = new PebbleDictionary();

        dictionaryEnd.addInt32(DL_END, 0);
        packages.add(dictionaryEnd);
    }

    private void sendPackages(){

        delegate.willStartTransmitting();

        BroadcastReceiver pebbleImageTransmitter = PebbleKit.registerReceivedAckHandler(delegate, new PebbleKit.PebbleAckReceiver(PEBBLE_APP_UUID) {

            @Override
            public void receiveAck(Context context, int transactionId) {

                Log.i("PebbleImageTransmitter", "Received ack for transaction " + transactionId);
                transactionId++;
                if (transactionId < packages.size() - 1) {
                    PebbleKit.sendDataToPebbleWithTransactionId(context, PEBBLE_APP_UUID, packages.get(transactionId), transactionId);
                }
            }
        });

        PebbleKit.registerReceivedNackHandler(delegate, new PebbleKit.PebbleNackReceiver(PEBBLE_APP_UUID) {

            @Override
            public void receiveNack(Context context, int transactionId) {
                Log.i("PebbleImageTransmitter", "Received nack for transaction " + transactionId);
            }
        });

        PebbleKit.sendDataToPebbleWithTransactionId(delegate, PEBBLE_APP_UUID, packages.get(0), 0);

    }

}