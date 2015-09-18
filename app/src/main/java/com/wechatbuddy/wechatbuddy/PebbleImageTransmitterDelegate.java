package com.wechatbuddy.wechatbuddy;

/**
 * Created by weimin on 9/17/15.
 */
public interface PebbleImageTransmitterDelegate {

    public void willStartTransmitting();

    public void didTransmitNumberOfPackges(int numberOfPackages, int total);

    public void didFailTransmitting();

    public void didFinishTransmitting();

}
