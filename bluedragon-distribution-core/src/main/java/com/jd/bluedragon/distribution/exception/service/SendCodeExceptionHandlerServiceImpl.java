package com.jd.bluedragon.distribution.exception.service;

import com.jd.bluedragon.distribution.api.domain.SendCodeSummary;
import com.jd.bluedragon.distribution.api.request.SendCodeExceptionRequest;
import com.jd.bluedragon.distribution.api.response.SendCodeExceptionResponse;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/22
 */
@Service("sendCodeExceptionHandlerService")
public class SendCodeExceptionHandlerServiceImpl implements SendCodeExceptionHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendCodeExceptionHandlerServiceImpl.class);

    @Override
    public List<String> querySendCodesByBarCode(Integer siteCode, String barCode) {
        if (null == siteCode || siteCode <= 0 || StringHelper.isEmpty(barCode)) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        /* 1.从分拣的报表中获取批次信息 */



        /* 2.如果上面未获取到信息的话，再从终端站点的接口中获取批次信息 */
        if (result.isEmpty()) {

        }

        return result;
    }

    @Override
    public SendCodeExceptionResponse summaryPackageBySendCodes(SendCodeExceptionRequest request) {
        if (null == request || request.getSendCodes() == null || request.getSendCodes().isEmpty()) {
            return null;
        }
        SendCodeExceptionResponse response = null;
        /* 1.如果是分拣的批次则从分拣的报表中获取批次的汇总信息 */

        /* 2.如果是站点的批次则从分拣的报表中获取批次的汇总信息 */

        return response;
    }

    @Override
    public SendCodeSummary querySendCodeDetailByCondition(SendCodeExceptionRequest request) {




        return null;
    }
}
