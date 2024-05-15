package com.jd.bluedragon.distribution.print.waybill.handler;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.handler.AbstractInterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/4/17
 * @description: 切换云打印流量开关执行逻辑
 */
@Slf4j
@Service("cloudPrintSwitchHandler")
public class CloudPrintSwitchHandler extends AbstractInterceptHandler<WaybillPrintContext,String> {

    private final static String VERSION_CODE_OF_LOWEST= "20240125WM";

    private final static String VERSION_CODE_WM= "WM";

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    /**
     * 执行处理，返回处理结果
     *
     * @param target
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.CloudPrintSwitchHandler.handle",mState={JProEnum.TP,JProEnum.FunctionError})
    public InterceptResult<String> handle(WaybillPrintContext target) {
        InterceptResult<String> interceptResult = target.getResult();
        try{
            //检查是否使用云打印，并放入上下文中
            target.setUseCloudPrint(useCloudPrint(target));
            //设置客户端是否使用云打印标识
            if(target.getResponse() == null){
                WaybillPrintResponse response = new WaybillPrintResponse();
                response.setUseCloudPrint(target.getUseCloudPrint());
                target.setResponse(response);
            }else{
                target.getResponse().setUseCloudPrint(target.getUseCloudPrint());
            }
        }catch (Exception e){
            log.error("切换云打印流量开关执行异常，入参：｛｝", JSON.toJSONString(target.getRequest()),e);
        }
        return interceptResult;
    }


    /**
     * 检查是否可以使用云打印
     *
     * @param target 运单打印上下文
     * @return 如果可以使用云打印则返回true，否则返回false
     */
    private boolean useCloudPrint(WaybillPrintContext target){
        String versionCode = target.getRequest().getVersionCode();
        Integer operateType = target.getRequest().getOperateType();
        Integer siteCode = target.getRequest().getSiteCode();
        Integer userCode = target.getRequest().getUserCode();
        String userErp = target.getRequest().getUserERP();

        if (StringUtils.isBlank(versionCode)) {
            // 处理空值情况，可能抛出异常或返回错误
            return false;
        }
        int versionCodeNow = Integer.parseInt(versionCode.replace(VERSION_CODE_WM, StringUtils.EMPTY));
        int versionCodeLowest = Integer.parseInt(VERSION_CODE_OF_LOWEST.replace(VERSION_CODE_WM, StringUtils.EMPTY));
        if(versionCodeNow <= versionCodeLowest){
            // 客户端版本过低
            return false;
        }
        //缺少关键字段 理论上可以使用这个代替版本号校验，但是为了逻辑上更清晰就都保留即可，后续切换全部云打印后整个CloudPrintSwitchHandler都会删除
        if(StringUtils.isBlank(target.getRequest().getRealBarCode())){
            return false;
        }
        //调用开关配置功能读取切换流量
        //人
        if(checkFuncSwitchByErp(operateType,siteCode,userCode ,userErp)){
            return true;
        }
        //场地
        if(checkFuncSwitchBySite(operateType,siteCode)){
            return true;
        }

        return false;
    }


    /**
     * 判断当前站点是否配置云打印开关
     * @param siteCode
     * @return
     */
    private boolean checkFuncSwitchBySite(Integer operateType,Integer siteCode) {
        try {
            if(siteCode == null){
                return false;
            }
            FuncSwitchConfigDto dto = new FuncSwitchConfigDto();
            FuncSwitchConfigEnum configEnum = FuncSwitchConfigEnum.getEnumByKey(operateType);
            if(configEnum == null){
                log.error("未匹配到切换云打印流量开关!,｛｝,｛｝",operateType,siteCode);
                return false;
            }
            dto.setMenuCode(configEnum.getCode());
            dto.setDimensionCode(DimensionEnum.SITE.getCode());
            dto.setSiteCode(siteCode);
            return funcSwitchConfigService.checkIsConfigured(dto);
        }catch (Exception e){
            log.error("查询当前站点是否配置切换云打印流量异常!,｛｝,｛｝",operateType,siteCode,e);
        }
        return false;
    }

    /**
     * 判断当前操作人是否配置云打印开关
     * @param erp
     * @return
     */
    private boolean checkFuncSwitchByErp(Integer operateType,Integer siteCode,Integer userCode, String erp) {
        try {
            if(StringUtils.isBlank(erp) && userCode == null){
                return false;
            }
            if(StringUtils.isBlank(erp)){
                //通过code转换
                BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByStaffId(userCode);
                if(dto == null){
                    return false;
                }
                erp = dto.getErp();
            }
            FuncSwitchConfigDto dto = new FuncSwitchConfigDto();
            FuncSwitchConfigEnum configEnum = FuncSwitchConfigEnum.getEnumByKey(operateType);
            if(configEnum == null){
                log.error("未匹配到切换云打印流量开关!,｛｝,｛｝",operateType,erp);
                return false;
            }
            dto.setMenuCode(configEnum.getCode());
            dto.setDimensionCode(DimensionEnum.PERSON.getCode());
            dto.setSiteCode(siteCode);
            dto.setOperateErp(erp);
            return funcSwitchConfigService.checkIsConfigured(dto);
        }catch (Exception e){
            log.error("查询当前站点是否配置切换云打印流量异常!,｛｝,｛｝",operateType,erp,e);
        }
        return false;
    }


}
