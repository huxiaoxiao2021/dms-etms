package com.jd.bluedragon.utils;

import java.util.ArrayList;
import java.util.List;

import com.jd.i18n.order.dict.FieldKeyEnum;
import com.jd.i18n.order.dict.helper.OrdermidQueryHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jd.bluedragon.Constants;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-8-17 下午07:15:11 订单中间件整合服务
 */
public class OrderServiceHelper {

    private final static Log logger = LogFactory.getLog(OrderServiceHelper.class);

    // https://git.jd.com/jdr-tp/tp-csr/blob/master/docs/common_parameters.md


    public static List<String> getWaybillFlag() {
        List<String> flagList = new ArrayList<String>();
        flagList.add(Constants.JI_BEN_XIN_XI);
        flagList.add(Constants.ZHUANG_TAI);
        flagList.add(Constants.GU_KE_XIN_XI);
        flagList.add(Constants.CHU_KU);
        flagList.add(Constants.PEI_SONG_ZI_TI);
        flagList.add(Constants.OTHER);
        return flagList;
    }

    public static List<String> getAllFlag() {
        List<String> queryList = new ArrayList<String>();
        queryList.add(Constants.JI_BEN_XIN_XI);
        queryList.add(Constants.ZHUANG_TAI);
        queryList.add(Constants.GU_KE_XIN_XI);
        queryList.add(Constants.JIN_ER);
        queryList.add(Constants.ZHI_FU);
        queryList.add(Constants.CHU_KU);
        queryList.add(Constants.PEI_SONG_ZI_TI);
        queryList.add(Constants.FA_PIAO);
        queryList.add(Constants.CHAI_FEN);
        queryList.add(Constants.BEI_ZHU);
        queryList.add(Constants.POP);
        queryList.add(Constants.OTHER);
        return queryList;
    }

    /**
     * 国际化订单详情接口调用参数
     *   订单商品详情 M_DETAILS
     * @return
     */
    public static List<String> getQueryDetailKeys() {
        List<String> queryList =  OrdermidQueryHelper.buildQuery(
                FieldKeyEnum.M_DETAIL_NAME,
                FieldKeyEnum.M_DETAIL_NUM,
                FieldKeyEnum.M_DETAIL_PRODUCTID,
                FieldKeyEnum.M_DETAIL_PRICE,
                FieldKeyEnum.M_DETAIL_SKUID,
                FieldKeyEnum.M_DETAIL_PROFITCHANNELID
                );
        return queryList;
    }

    /**
     * 获取国际化订单- 订单接口
     * @return
     */
    public static List<String> getQueryKeys() {
        List<String> queryList =  OrdermidQueryHelper.buildQuery(
                FieldKeyEnum.M_ID,
                FieldKeyEnum.M_OPRATOR,
                FieldKeyEnum.M_DELIVERYCENTERID,
                FieldKeyEnum.M_CUSTOMERNAME,
                FieldKeyEnum.M_E_CUSTOMERNAME,
                FieldKeyEnum.M_ORDERTYPE,
                FieldKeyEnum.M_STOREID,
                FieldKeyEnum.M_SENDPAY,
                FieldKeyEnum.M_PROVINCE,
                FieldKeyEnum.M_CITY
        );
        return queryList;
    }
}
