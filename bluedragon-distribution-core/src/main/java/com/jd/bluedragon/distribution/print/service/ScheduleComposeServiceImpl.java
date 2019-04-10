package com.jd.bluedragon.distribution.print.service;

import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.StringHelper;

/**
 * Created by wangtingwei on 2015/12/24.
 */
@Service("scheduleComposeService")
public class ScheduleComposeServiceImpl implements ComposeService {

    @Override
    public void handle(final PrintWaybill waybill, Integer dmsCode, Integer targetSiteCode) {
        if(null!=targetSiteCode&&targetSiteCode>0){
            waybill.setPrepareSiteCode(targetSiteCode);
            waybill.setPrintSiteCode(targetSiteCode);

            if(StringHelper.isNotEmpty(waybill.getNewAddress())){
                waybill.setPrintAddress(waybill.getNewAddress());
            }

        }
    }
}
