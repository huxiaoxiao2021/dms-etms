package com.jd.bluedragon.core.jsf.eclp;

import java.util.List;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.etms.third.service.domain.ReceiptStateParameter;

/**
 * 
 * @ClassName: ThirdJsfInterfaceManager
 * @Description: 调用eclp三方状态回传接口定义
 * @author: wuyoude
 * @date: 2022年12月1日 下午2:37:26
 *
 */
public interface ThirdJsfInterfaceManager {
    /**
     * 调用eclp三方状态回传
     * @param list
     * @return
     */
	JdResult<List<ReceiptStateParameter>> partnerReceiptState(List<ReceiptStateParameter> list);
}
