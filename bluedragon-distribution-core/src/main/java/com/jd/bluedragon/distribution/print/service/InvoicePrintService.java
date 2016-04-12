package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.InvoiceParameters;

/**
 * 在线发票打印数据服务
 * Created by wangtingwei on 2016/4/8.
 */
public interface InvoicePrintService {

    /**
     * 生成发票数据
     * @param parameters 请求参数
     * @return
     */
    InvokeResult<String> generateInvoice(InvoiceParameters parameters);
}
