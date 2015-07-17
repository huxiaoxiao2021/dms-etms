package com.jd.bluedragon.distribution.waybill.service;

import com.jd.etms.message.produce.client.MessageClient;

/**
 * Created by yanghongqiang on 2014/12/16.
 */
public class WaybillModifyServiceImpl implements WaybillModifyService {


    MessageClient messageClient;

    public boolean SendwaybillMessage(String messageBody) {

        return  true;
    }
}
