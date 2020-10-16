package com.jd.bluedragon.distribution.funcSwitchConfig;

import com.jd.bd.dms.automatic.sdk.common.constant.WeightValidateSwitchEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/10/16 15:30
 */
@RunWith(MockitoJUnitRunner.class)
public class FuncSwitchConfigTest {

    @InjectMocks
    private FuncSwitchConfigServiceImpl funcSwitchConfigService;

    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;

    private static final Integer YN_OFF = 1;

    @Test
    public void test01(){
        FuncSwitchConfigDto funcSwitchConfigDto = new FuncSwitchConfigDto();
        funcSwitchConfigDto.setMenuCode(5001);
        funcSwitchConfigDto.setDimensionCode(2);
        funcSwitchConfigDto.setYn(1);

      //众邮调度分拣机开关
        if(FuncSwitchConfigEnum.FUNCTION_ALL_MAIL.getCode()==funcSwitchConfigDto.getMenuCode()){
            BaseDmsAutoJsfResponse<Long> longBaseDmsAutoJsfResponse = null;
            if(funcSwitchConfigDto.getDimensionCode()== DimensionEnum.NATIONAL.getCode()){
                longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainWeightSwitch(funcSwitchConfigDto.getYn()==YN_OFF? WeightValidateSwitchEnum.OFF:WeightValidateSwitchEnum.ON);
                if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                    System.out.println("众邮分拣机开关调用失败,全国");
                }
            }else {
                Integer[] siteCodes = {funcSwitchConfigDto.getSiteCode()};
                longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainSiteWeightSwitch(siteCodes,funcSwitchConfigDto.getYn()==YN_OFF?WeightValidateSwitchEnum.OFF:WeightValidateSwitchEnum.ON);
                if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                    System.out.println("众邮分拣机开关调用失败,站点:"+siteCodes);
                }
            }
        }
    }
    

    @Test
    public void test02(){
        List<Integer> siteCodes = new ArrayList<>();
        siteCodes.add(111);
        Integer[] siteCodesArray = new Integer[siteCodes.size()];
        siteCodes.toArray(siteCodesArray);
        for (int s = 0; s < siteCodesArray.length; s++) {
            System.out.println(siteCodesArray[s]);
        }
    }
}
    
