package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.Constants;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-17 下午07:15:11 订单中间件整合服务
 */
public class OrderServiceHelper {
    
    private final static Log logger = LogFactory.getLog(OrderServiceHelper.class);

    public static List<String> getFlag1() {
        List<String> flagList = new ArrayList<String>();
        flagList.add(Constants.JI_BEN_XIN_XI);
        flagList.add(Constants.ZHUANG_TAI);
        // flagList.add(Constants.GU_KE_XIN_XI);
        // flagList.add(Constants.JIN_ER);
        // flagList.add(Constants.ZHI_FU);
        flagList.add(Constants.CHU_KU);
        flagList.add(Constants.PEI_SONG_ZI_TI);
        // flagList.add(Constants.FA_PIAO);
        // flagList.add(Constants.CHAI_FEN);
        flagList.add(Constants.BEI_ZHU);
        flagList.add(Constants.QUAN_BU);
        // flagList.add(Constants.POP);
        return flagList;
    }

}
