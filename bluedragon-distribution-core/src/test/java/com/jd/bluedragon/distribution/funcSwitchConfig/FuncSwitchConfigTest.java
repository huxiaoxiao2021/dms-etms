package com.jd.bluedragon.distribution.funcSwitchConfig;

import com.jd.bd.dms.automatic.sdk.common.constant.WeightValidateSwitchEnum;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.funcSwitchConfig.dao.FuncSwitchConfigDao;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.impl.FuncSwitchConfigServiceImpl;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Mock
    private CacheService jimdbCacheService;

    @Mock
    private FuncSwitchConfigDao funcSwitchConfigDao;

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
                longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainWeightSwitch(funcSwitchConfigDto.getYn()==YN_OFF? WeightValidateSwitchEnum.ZY_OFF:WeightValidateSwitchEnum.ZY_ON);
                if(longBaseDmsAutoJsfResponse.getStatusCode()!=BaseDmsAutoJsfResponse.SUCCESS_CODE){
                    System.out.println("众邮分拣机开关调用失败,全国");
                }
            }else {
                Integer[] siteCodes = {funcSwitchConfigDto.getSiteCode()};
                longBaseDmsAutoJsfResponse = deviceConfigInfoJsfService.maintainSiteWeightSwitch(siteCodes,funcSwitchConfigDto.getYn()==YN_OFF?WeightValidateSwitchEnum.ZY_OFF:WeightValidateSwitchEnum.ZY_ON);
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


    @Test
    public void test03(){
         ObjectTest obj = new ObjectTest();
         if(StringUtils.isEmpty(obj.getName())){
             System.out.println("这是一个空值");
        }
    }


    public boolean test04() throws Exception {
        try {
            int i = 1/0;
            System.out.println("还能执行到这吗");
        }catch (Exception e){
            System.out.println("抛异常");
            //throw  new Exception("抛出异常");
        }
        return  true;
    }

    @Test
    public void test05() throws Exception {
        FuncSwitchConfigDto funcSwitchConfigDto = new FuncSwitchConfigDto();
        funcSwitchConfigDto.setDimensionCode(1111);
        funcSwitchConfigDto.setYn(1);
        System.out.println(JsonHelper.toJson(funcSwitchConfigDto));
        System.out.println(JsonHelper.toJsonMs(funcSwitchConfigDto));
        System.out.println(test04());
    }



    public class ObjectTest{
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        String name;

    }

    @Test
    public void test06(){
        Integer menuCode = FuncSwitchConfigEnum.FUNCTION_ALL_MAIL.getCode();
        boolean isAllMailFilter = true;
        String cacheKey = DimensionEnum.NATIONAL.getCachePreKey()+menuCode;
         String  cacheValue = jimdbCacheService.get(cacheKey);
        if(org.apache.commons.lang.StringUtils.isNotEmpty(cacheValue)) {
            isAllMailFilter = Boolean.valueOf(cacheValue);
        }else {
            FuncSwitchConfigCondition condition = new FuncSwitchConfigCondition();
            if(menuCode!=null) {
                condition.setMenuCode(menuCode);
            }
            Integer YnValue = funcSwitchConfigDao.queryYnByCondition(condition);
            if(YnValue ==null){
                isAllMailFilter = true;
            }else {
                isAllMailFilter = YnValue== YnEnum.YN_ON.getCode() ? true: false;
                jimdbCacheService.setEx(cacheKey,String.valueOf(isAllMailFilter), Constants.ALL_MAIL_CACHE_SECONDS, TimeUnit.MINUTES);
            }

        }
        System.out.println(isAllMailFilter);
    }

    @Test
    public void test07() throws Exception {
        List<Integer>  zySiteCodesOffList = null;
        //纯配外单-调用分拣机list
       // List<Integer>  cpSiteCodesOffList = null;
        FuncSwitchConfigDto dto = new FuncSwitchConfigDto();
        dto.setSiteCode(11111);
        dto.setDimensionCode(2);
        zySiteCodesOffList  =  checkDimensionGetList(dto,zySiteCodesOffList);
        System.out.println(zySiteCodesOffList.toString());
    }

    public List<Integer> checkDimensionGetList  (FuncSwitchConfigDto dto, List<Integer> siteCodesOffList) throws Exception {
        if(dto.getDimensionCode()==DimensionEnum.SITE.getCode()){
            if(CollectionUtils.isEmpty(siteCodesOffList)){
                siteCodesOffList = new ArrayList<>();
            }
            //调用不拦截
            siteCodesOffList.add(dto.getSiteCode());
        }else if(dto.getDimensionCode()==DimensionEnum.NATIONAL.getCode()){
            throw  new Exception("批量导入不支持全国数据,请手动添加");
        }
        return siteCodesOffList;
    }

    @Test
    public  void test08(){
        BaseEntity<Waybill>  waybillBaseEntity =    test09();
        System.out.println(waybillBaseEntity);
    }

    public BaseEntity<Waybill> test09(){
        BaseEntity<Waybill> waybillBaseEntity = null;
        try {
            int i = 10/0;
        }catch (Exception e){

        }
        return null;
    }
}
    
