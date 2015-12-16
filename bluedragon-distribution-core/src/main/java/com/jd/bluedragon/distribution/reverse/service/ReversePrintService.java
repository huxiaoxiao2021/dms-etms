package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;



/**
 * 逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
public interface ReversePrintService {
    /**
     * 处理打印数据
     * @param domain 打印提交数据
     * @return 处理是否成功
     */
    boolean handlePrint(ReversePrintRequest domain);

    /**
     * 获取换单后对应的新运单号
     * @param oldWaybillCode 原单号
     * @return
     */
    String getNewWaybillCode(String oldWaybillCode);

}
