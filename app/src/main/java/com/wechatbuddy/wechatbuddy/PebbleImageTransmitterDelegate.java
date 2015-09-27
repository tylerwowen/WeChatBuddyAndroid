//
//  PebbleImageTransmitter.java
//  WeChatBuddy
//
//  Created by Tyler O, Jessie L, Kenneth C on 8/29/15.
//  Copyright (c) 2015 Tyler O, Jessie L, Kenneth C. All rights reserved.

package com.wechatbuddy.wechatbuddy;

public interface PebbleImageTransmitterDelegate {

    public void willStartTransmitting();

    public void didTransmitNumberOfPackages(int numberOfPackages, int total);

    public void didFailTransmitting();

    public void didFinishTransmitting();

}
