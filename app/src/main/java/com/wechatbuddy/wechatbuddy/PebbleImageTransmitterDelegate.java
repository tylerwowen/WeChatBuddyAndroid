//
//  PebbleImageTransmitter.java
//  WeChatBuddy
//
//  Created by Tyler O, Jessie L, Kenneth C on 8/29/15.
//  Copyright (c) 2015 Tyler O, Jessie L, Kenneth C. All rights reserved.

package com.wechatbuddy.wechatbuddy;

public interface PebbleImageTransmitterDelegate {

    void willStartTransmitting();

    void didTransmitNumberOfPackages(int numberOfPackages, int total);

    void didFailTransmitting();

    void didFinishTransmitting();

}
