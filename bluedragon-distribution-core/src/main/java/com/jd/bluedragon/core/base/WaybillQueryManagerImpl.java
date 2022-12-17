package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.inventory.service.PackageStatusService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusiWaringUtil;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.cache.BigWaybillPackageListCache;
import com.jd.coo.ucc.common.utils.JsonUtils;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.api.WaybillUpdateApi;
import com.jd.etms.waybill.domain.*;
import com.jd.etms.waybill.dto.*;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.ql.trace.api.WaybillTraceBusinessQueryApi;
import com.jd.ql.trace.api.core.APIResultDTO;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("waybillQueryManager")
public class WaybillQueryManagerImpl implements WaybillQueryManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.waybill.";

    @Autowired
    private WaybillQueryApi waybillQueryApi;

    @Autowired
    private WaybillTraceApi waybillTraceApi;

    @Autowired
    private WaybillPickupTaskApi waybillPickupTaskApi;

    @Qualifier("waybillPackageManager")
    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Qualifier("waybillTraceBusinessQueryApi")
    @Autowired
    private WaybillTraceBusinessQueryApi waybillTraceBusinessQueryApi;

    @Autowired
    private PackageStatusService packageStatusService;

    @Autowired
    private WaybillUpdateApi waybillUpdateApi;

    @Autowired
    private EclpItemManager eclpItemManager;

    @Autowired
    private BusiWaringUtil busiWaringUtil;

    /**
     * 大包裹运单缓存开关
     */
    private final static String BIG_WAYBILL_PACKAGE_CACHE_SWITCH = PropertiesHelper.newInstance().getValue("big.waybill.package.cache.switch");

    private static final String SWITCH_ON = "1";

    private static Map<String,String> mapValues = new HashMap<>(16,0.75f);
    static {
        mapValues.put("保价金额","保价");
        mapValues.put("代收货款","代收");
        mapValues.put("重货上楼","上楼");
        mapValues.put("包装服务","包装");
        mapValues.put("暂存服务","预约");
        mapValues.put("特安服务","特安");
        mapValues.put("大件开箱通电","通电");
        mapValues.put("大件送装一体","送装");
        mapValues.put("大件取旧服务","取旧");
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillQueryApi.getWaybillByReturnWaybillCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<Waybill> getWaybillByReturnWaybillCode(String waybillCode) {
        return waybillQueryApi.getWaybillByReturnWaybillCode(waybillCode);
    }

    @Override
    public BaseEntity<BigWaybillDto> getDataByChoiceNoCache(String waybillCode, WChoice wChoice) {
        //拆分运单接口监控
        StringBuilder profilerKey = new StringBuilder("DMS.BASE.waybillQueryApi.getDataByChoice");
        if(wChoice!=null){
            profilerKey.append(wChoice.getQueryWaybillC() != null && wChoice.getQueryWaybillC()?"-C":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillE() != null && wChoice.getQueryWaybillE()?"-E":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillM() != null && wChoice.getQueryWaybillM()?"-M":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryPackList() != null && wChoice.getQueryPackList()?"-Pl":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryGoodList() != null && wChoice.getQueryGoodList()?"-Gl":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryPickupTask() != null && wChoice.getQueryPickupTask()?"-Pu":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryQByNewCode() != null && wChoice.getQueryQByNewCode()?"-Qn":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryServiceBillPay() != null && wChoice.getQueryServiceBillPay()?"-Sb":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillExtend() != null && wChoice.getQueryWaybillExtend()?"-Ex":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillFinance() != null && wChoice.getQueryWaybillFinance()?"-F":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillP() != null && wChoice.getQueryWaybillP()?"-P":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillS() != null && wChoice.getQueryWaybillS()?"-S":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillT() != null && wChoice.getQueryWaybillT()?"-T":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryCoupon() != null && wChoice.getQueryCoupon()?"-Cp":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillCost() != null && wChoice.getQueryWaybillCost()?"-Co":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillVas() != null && wChoice.getQueryWaybillVas()?"-V":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryGoodsWithVas() != null && wChoice.getQueryGoodsWithVas()?"-Wv":StringUtils.EMPTY);
            profilerKey.append(wChoice.getQueryWaybillBs() != null && wChoice.getQueryWaybillBs()?"-Bs":StringUtils.EMPTY);
        }
        CallerInfo info = Profiler.registerInfo(profilerKey.toString(), Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            return waybillQueryApi.getDataByChoice(waybillCode, wChoice);
        }catch (Exception e){
            Profiler.functionError(info);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);

        }
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode, WChoice wChoice) {
        Boolean isQueryPackList = wChoice.getQueryPackList();
        Boolean isQueryWaybillC = wChoice.getQueryWaybillC();

        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.getDataByChoiceNoPackList", Constants.UMP_APP_NAME_DMSWEB,false, true);
        if(wChoice.getQueryPackList() != null && wChoice.getQueryPackList()){
            //包裹独立设置KEY
            info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.getDataByChoiceWithPackList", Constants.UMP_APP_NAME_DMSWEB,false, true);
        }
        this.updateWChoiceSetting(wChoice);
        try{
            BaseEntity<BigWaybillDto> baseEntity = this.getDataByChoiceNoCache(waybillCode, wChoice);
            if (baseEntity.getResultCode() == 1 && baseEntity.getData() != null) {

                if(baseEntity.getData().getWaybill() != null && baseEntity.getData().getWaybill().getGoodNumber() != null){
                    busiWaringUtil.bigWaybillWarning(waybillCode,baseEntity.getData().getWaybill().getGoodNumber());
                }
                // 只有接口查询包裹信息并且waybill对象不为空时，进行缓存查询
                if (isQueryPackList != null && isQueryPackList) {
                    // 是否需要从缓存获取包裹信息
                    if (this.isNeedGetFromCache(baseEntity.getData().getWaybill())) {
                        boolean isDone = setPackageListFromCache(waybillCode, isQueryPackList, isQueryWaybillC, wChoice, baseEntity);
                        if(isDone){
                            return baseEntity;
                        }
                    }
                    // 根据运单号获取包裹信息
                    BaseEntity<List<DeliveryPackageD>> packListBaseEntity = waybillPackageManager.getPackListByWaybillCode(waybillCode);
                    if (packListBaseEntity.getResultCode() == 1) {
                        baseEntity.getData().setPackageList(packListBaseEntity.getData());
                    }
                }
            }
            // 回滚wChoice查询配置及返回的数据信息
            this.revertWChoiceSettingAndData(isQueryPackList, isQueryWaybillC, wChoice, baseEntity);
            return baseEntity;
        }catch (Exception e){
            log.error("getDataByChoice error! waybillCode{}",waybillCode,e);
            Profiler.functionError(info);
            throw e;
        }finally {
            Profiler.registerInfoEnd(info);
        }

    }

    /**
     * 更新查询条件信息
     *
     * @param wChoice
     */
    private void updateWChoiceSetting(WChoice wChoice) {
        if (wChoice.getQueryPackList() != null && wChoice.getQueryPackList()) {
            if (wChoice.getQueryWaybillC() == null || wChoice.getQueryWaybillC() == false) {
                wChoice.setQueryWaybillC(true);
            }
        }
        wChoice.setQueryPackList(false);
    }

    /**
     * 从缓存获取包裹信息
     *
     * @param isQueryPackList
     * @param isQueryWaybillC
     * @param wChoice
     * @param result
     */
    private boolean setPackageListFromCache(String waybillCode, Boolean isQueryPackList, Boolean isQueryWaybillC, WChoice wChoice, BaseEntity<BigWaybillDto> result) {
        try {
            result.getData().setPackageList(BigWaybillPackageListCache.getPackageListFromCache(waybillCode));
            // 回滚wChoice查询配置及返回的数据信息
            this.revertWChoiceSettingAndData(isQueryPackList, isQueryWaybillC, wChoice, result);
            return true;
        } catch (Exception e) {
            log.error("[大包裹运单缓存]获取包裹信息时发生异常，运单号:{}" , waybillCode, e);
        }
        return false;
    }
    /**
     * 查询配置及数据回滚
     *
     * @param isQueryPackList
     * @param isQueryWaybillC
     * @param wChoice
     * @param result
     */
    private void revertWChoiceSettingAndData(Boolean isQueryPackList, Boolean isQueryWaybillC, WChoice wChoice, BaseEntity<BigWaybillDto> result) {
        // 改回原值
        wChoice.setQueryPackList(isQueryPackList);
        wChoice.setQueryWaybillC(isQueryWaybillC);
        if (isQueryWaybillC == null || isQueryWaybillC == false) {
            if (result.getData() != null) {
                result.getData().setWaybill(null);
            }
        }
    }

    /**
     * 判断是否需要从缓存中获取包裹数据信息
     *
     * @param waybill
     * @return
     */
    private boolean isNeedGetFromCache(Waybill waybill) {
        if (SWITCH_ON.equals(BIG_WAYBILL_PACKAGE_CACHE_SWITCH) && waybill != null && waybill.getGoodNumber() != null && waybill.getGoodNumber() > BigWaybillPackageListCache.BIG_WAYBILL_PACKAGE_LIMIT) {
            return true;
        }
        return false;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
                                                     Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM,
                                                     Boolean isPackList) {
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(isWaybillC);
        wChoice.setQueryWaybillE(isWaybillE);
        wChoice.setQueryWaybillM(isWaybillM);
        wChoice.setQueryPackList(isPackList);
        return getDataByChoice(waybillCode, wChoice);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode,
                                                     Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM,
                                                     Boolean isGoodList, Boolean isPackList, Boolean isPickupTask,
                                                     Boolean isServiceBillPay) {
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(isWaybillC);
        wChoice.setQueryWaybillE(isWaybillE);
        wChoice.setQueryWaybillM(isWaybillM);
        wChoice.setQueryGoodList(isGoodList);
        wChoice.setQueryPackList(isPackList);
        wChoice.setQueryPickupTask(isPickupTask);
        wChoice.setQueryServiceBillPay(isServiceBillPay);
        return getDataByChoice(waybillCode, wChoice);
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDatasByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> waybillCodes, Boolean isWaybillC, Boolean isWaybillE, Boolean isWaybillM, Boolean isPackList) {
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(isWaybillC);
        wChoice.setQueryWaybillE(isWaybillE);
        wChoice.setQueryWaybillM(isWaybillM);
        wChoice.setQueryPackList(isPackList);
        return getDatasByChoice(waybillCodes, wChoice);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean sendBdTrace(BdTraceDto bdTraceDto) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.sendBdTrace", false, true);
        try {
            BaseEntity baseEntity = waybillTraceApi.sendBdTrace(bdTraceDto);
            if (baseEntity != null) {
                if (baseEntity.getResultCode() == -1) {
                    //此种情况为运单数据库或redis异常，系统异常级别，非业务级别
                    throw new RuntimeException(baseEntity.getMessage());
                }else if (baseEntity.getResultCode() != 1) {
                    this.log.warn(JsonHelper.toJson(bdTraceDto));
                    this.log.warn(bdTraceDto.getWaybillCode());
                    this.log.warn("分拣数据回传全程跟踪sendBdTrace异常：{}" , baseEntity.getMessage());
                    return false;
                }
            } else {
                this.log.warn("分拣数据回传全程跟踪接口sendBdTrace异常:{}" , bdTraceDto.getWaybillCode());
                return false;
            }
        } catch (Exception e) {
            log.error("分拣数据回传全程跟踪sendBdTrace异常：{}" , bdTraceDto.getWaybillCode(), e);
            Profiler.functionError(info);
            throw new RuntimeException(e.getMessage(),e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        try{
            packageStatusService.recordPackageStatus(null,bdTraceDto);
            packageStatusService.filterAndSendDmsHasnoPresiteWaybillMq(null,bdTraceDto);
        }catch (Exception e){
            log.error("包裹状态发送MQ消息异常.{}" , JSON.toJSONString(bdTraceDto),e);
        }
        return true;
    }
    /**
     * 根据运单号查询产品能力信息
     * https://cf.jd.com/pages/viewpage.action?pageId=506496819
     *
     * @param waybillCode 运单号
     * @return
     */
    @Override
    public BaseEntity<List<WaybillProductDto>> getProductAbilityInfoByWaybillCode(String waybillCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.getProductAbilityInfoByWaybillCode", false, true);

        BaseEntity<List<WaybillProductDto>>  baseEntity = null;
        try{
            baseEntity = waybillQueryApi.getProductAbilityInfoByWaybillCode(waybillCode);
            if (baseEntity != null) {
                if (baseEntity.getResultCode() != 1) {
                    log.warn("getProductAbilityInfoByWaybillCode fail waybill{} result{}",waybillCode,JsonHelper.toJson(baseEntity));
                }else{
                    return baseEntity;
                }
            }else{
                log.warn("getProductAbilityInfoByWaybillCode fail  waybill{} result is null",waybillCode);
            }
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("getProductAbilityInfoByWaybillCode errpr waybill{} result{}",waybillCode);
        }finally {
            Profiler.registerInfoEnd(info);
        }

        return null;
    }

    /**
     * 校验产品能力是否存在
     * @param waybillProductDtos 产品能力列表
     * @param productAbility 产品能力指
     * @return
     */
    @Override
    public boolean checkWaybillProductAbility(List<WaybillProductDto> waybillProductDtos, String productAbility) {
        if(waybillProductDtos == null || CollectionUtils.isEmpty(waybillProductDtos)){
            return false;
        }
        for(WaybillProductDto productDto : waybillProductDtos) {
            if (CollectionUtils.isEmpty(productDto.getAbilityItems())) {
                continue;
            }
            for (WaybillAbilityDto abilityDto : productDto.getAbilityItems()) {
                if (CollectionUtils.isEmpty(abilityDto.getAttrItems())) {
                    continue;
                }
                for (WaybillAbilityAttrDto abilityAttrDto : abilityDto.getAttrItems()) {
                    if (productAbility.equals(abilityAttrDto.getAttrCode())) {
                        //能力项匹配成功
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Integer checkReDispatch(String waybillCode) {
        Integer result = REDISPATCH_NO;
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.checkReDispatch", false, true);
        BaseEntity<List<PackageState>> baseEntity = null;
        try {
            // http://cf.jd.com/pages/viewpage.action?pageId=73834851 取件单批量查询接口
            baseEntity = waybillTraceApi.getPkStateByWCodeAndState(waybillCode, WAYBILL_STATUS_REDISPATCH);
            if (baseEntity != null) {
                if (baseEntity.getResultCode() != 1) {
                    this.log.warn("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：{},{},{}"
                            ,waybillCode,baseEntity.getResultCode() ,baseEntity.getMessage());
                    result = REDISPATCH_ERROR;
                } else {
                    if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
                        result = REDISPATCH_YES;
                    } else {
                        result = REDISPATCH_NO;
                    }
                }
            } else {
                this.log.warn("检查是否反调度WaybillQueryManagerImpl.checkReDispatch返回空：{}" , waybillCode);
                result = REDISPATCH_ERROR;
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            this.log.error("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：{}" , waybillCode, e);
            result = REDISPATCH_ERROR;
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }

    @Override
    public String getChangeWaybillCode(String oldWaybillCode) {
        String changedWaybillCode = null;
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.checkReDispatch", false, true);
        BaseEntity<Map<String, String>> baseEntity = null;
        try {
            List<String> waybillCodes = new ArrayList<String>();
            waybillCodes.add(oldWaybillCode);
            //http://cf.jd.com/pages/viewpage.action?pageId=74538367 运单指查询面单接口
            baseEntity = waybillPickupTaskApi.batchQuerySurfaceCodes(waybillCodes);
            if (baseEntity != null) {
                if (baseEntity.getResultCode() != 1) {
                    this.log.warn("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState异常：{},{},{}"
                            ,oldWaybillCode,baseEntity.getResultCode() , baseEntity.getMessage());
                } else if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
                    changedWaybillCode = baseEntity.getData().get(oldWaybillCode);
                }
            } else {
                this.log.warn("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState返回空：{}" , oldWaybillCode);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            this.log.error("获取取件单对应的面单号W单号WaybillQueryManagerImpl.checkReDispatch异常：{}" , oldWaybillCode, e);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return changedWaybillCode;
    }

    /**
     * 根据运单号获取运单数据信息给打印用
     *
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BaseEntity<BigWaybillDto> getWaybillDataForPrint(String waybillCode) {
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(Boolean.TRUE);
        wChoice.setQueryWaybillE(Boolean.TRUE);
        wChoice.setQueryWaybillM(Boolean.TRUE);
        wChoice.setQueryPackList(Boolean.TRUE);
        wChoice.setQueryWaybillExtend(Boolean.TRUE);
        wChoice.setQueryWaybillP(Boolean.TRUE);
        wChoice.setQueryWaybillVas(Boolean.TRUE);
        return this.getDataByChoice(waybillCode, wChoice);
    }

    /**
     * 根据操作单号和状态查询B网全程跟踪数据,包含extend扩展属性。
     *
     * @param operatorCode 运单号
     * @param state        状态码
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.queryBillBTraceAndExtendByOperatorCode",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public List<BillBusinessTraceAndExtendDTO> queryBillBTraceAndExtendByOperatorCode(String operatorCode, String state) {
        APIResultDTO<List<BillBusinessTraceAndExtendDTO>> resultDTO = waybillTraceBusinessQueryApi.queryBillBTraceAndExtendByOperatorCode(operatorCode, state);
        if (resultDTO.isSuccess()) {
            return resultDTO.getResult();
        }
        return null;
    }


    /**
     * 通过包裹号获得运单信息
     *
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillQueryApi.getWaybillByPackCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<Waybill> getWaybillByPackCode(String code) {
        return waybillQueryApi.getWaybillByPackCode(code);
    }

    /**
     * 通过包裹号获得运单信息和包裹信息
     *
     * @return
     */
    @Override
    public BaseEntity<BigWaybillDto> getWaybillAndPackByWaybillCode(String waybillCode) {
        WChoice wChoice = new WChoice();
        wChoice.setQueryWaybillC(Boolean.TRUE);
        wChoice.setQueryWaybillM(Boolean.TRUE);
        wChoice.setQueryPackList(Boolean.TRUE);
        return getDataByChoice(waybillCode, wChoice);
    }

    /**
     * 通过运单号获得运单信息
     *
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.WaybillQueryApi.getWaybillByWaybillCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<Waybill> getWaybillByWaybillCode(String waybillCode) {
        return waybillQueryApi.getWaybillByWaybillCode(waybillCode);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.WaybillQueryApi.getWaybillByWayCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public Waybill getWaybillByWayCode(String waybillCode) {
        BaseEntity<Waybill> baseEntity = waybillQueryApi.getWaybillByWaybillCode(waybillCode);
        if(baseEntity == null){
            log.warn("查询运单信息接口返回空waybillCode[{}]",waybillCode);
            return null;
        }
        if(baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() || baseEntity.getData() == null){
            log.warn("查询运单信息接口失败waybillCode[{}]code[{}]",waybillCode,baseEntity.getResultCode());
            return null;
        }

        return baseEntity.getData();
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.WaybillQueryApi.queryWaybillByWaybillCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public Waybill queryWaybillByWaybillCode(String waybillCode) {
        BaseEntity<Waybill> baseEntity = waybillQueryApi.queryWaybillByWaybillCode(waybillCode);
        if (baseEntity == null) {
            log.warn("查询运单信息接口返回空waybillCode[{}]", waybillCode);
            return null;
        }
        if (baseEntity.getResultCode() != EnumBusiCode.BUSI_SUCCESS.getCode() || baseEntity.getData() == null) {
            log.warn("查询运单信息接口失败waybillCode[{}]code[{}]",waybillCode,baseEntity.getResultCode());
            return null;
        }
        return baseEntity.getData();
    }

    public Waybill getOnlyWaybillByWaybillCode(String waybillCode) {
        BaseEntity<Waybill> result = getWaybillByWaybillCode(waybillCode);
        if(result.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && result.getData() != null){
            return result.getData();
        }else{
            log.warn("根据运单号获取运单信息，接口返回异常状态码，ResultCode:{},message:{}" ,result.getResultCode(), result.getMessage());
            return null;
        }
    }

    /**
     * 根据旧运单号获取新运单信息（逆向不支持2w包裹，暂时不做修改）
     *
     * @param oldWaybillCode 旧的运单号
     * @param wChoice        获取的运单信息中是否包含waybillC数据
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getReturnWaybillByOldWaybillCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BaseEntity<BigWaybillDto> getReturnWaybillByOldWaybillCode(String oldWaybillCode, WChoice wChoice) {
        return waybillQueryApi.getReturnWaybillByOldWaybillCode(oldWaybillCode, wChoice);
    }

    /**
     * 批量获取运单信息
     *
     * @param waybillCodes 运单号列表
     * @return
     */
    @Override
    public BaseEntity<List<BigWaybillDto>> getDatasByChoice(List<String> waybillCodes, WChoice wChoice) {
        if (waybillCodes != null && waybillCodes.size() == 1) {
            BaseEntity<BigWaybillDto> baseEntity = this.getDataByChoice(waybillCodes.get(0), wChoice);
            BaseEntity<List<BigWaybillDto>> result = new BaseEntity<List<BigWaybillDto>>();
            if (null != baseEntity) {
                List<BigWaybillDto> bigWaybillDtoList = new ArrayList<BigWaybillDto>(1);
                bigWaybillDtoList.add(baseEntity.getData());

                result.setData(bigWaybillDtoList);
                result.setMessage(baseEntity.getMessage());
                result.setResultCode(baseEntity.getResultCode());
            }
            return result;
        } else {
            return this.doBatchGetDatasByChoice(waybillCodes, wChoice);
        }
    }

    /**
     * 根据运单号List批量获取运单信息
     *
     * @param waybillCodes
     * @param wChoice
     * @return
     */
    private BaseEntity<List<BigWaybillDto>> doBatchGetDatasByChoice(List<String> waybillCodes, WChoice wChoice) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.doBatchGetDatasByChoice", false, true);
        BaseEntity<List<BigWaybillDto>> results;
        try {
            if (waybillPackageManager.isGetPackageByPageOpen()) {
                Boolean isQueryPackList = wChoice.getQueryPackList();
                if (null == isQueryPackList) {
                    isQueryPackList = false;
                }
                wChoice.setQueryPackList(false);
                results = waybillQueryApi.getDatasByChoice(waybillCodes, wChoice);

                if (isQueryPackList && null != results) {
                    for (BigWaybillDto bigWaybillDto : results.getData()) {
                        if (null != bigWaybillDto.getWaybill() && StringHelper.isNotEmpty(bigWaybillDto.getWaybill().getWaybillCode())) {
                            BaseEntity<List<DeliveryPackageD>> packageDBaseEntity = waybillPackageManager.getPackageByWaybillCode(bigWaybillDto.getWaybill().getWaybillCode());
                            if (null != packageDBaseEntity && null != packageDBaseEntity.getData() && packageDBaseEntity.getData().size() > 0) {
                                bigWaybillDto.setPackageList(packageDBaseEntity.getData());
                            }
                        }
                    }
                }
            } else {
                results = waybillQueryApi.getDatasByChoice(waybillCodes, wChoice);
            }
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return results;
    }

    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getSkuSnListByOrderId",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    @Override
    public BaseEntity<List<SkuSn>> getSkuSnListByOrderId(String waybillCode) {
        return waybillQueryApi.getSkuSnListByOrderId(waybillCode);
    }

    /**
     * 获取履约单下所有运单
     *
     * @param parentWaybillCode 父订单号（履约单号）
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getOrderParentChildList",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public List<String> getOrderParentChildList(String parentWaybillCode) {
        List<String> waybillCodes = new ArrayList<String>();
        //神一样的接口设计
        BaseEntity<List<OrderParentChildDto>> baseEntity = waybillQueryApi.getOrderParentChildList(parentWaybillCode);
        if (baseEntity.getResultCode() == 1 && baseEntity.getData() != null) {
            for (OrderParentChildDto orderParentChildDto : baseEntity.getData()) {
                waybillCodes.add(orderParentChildDto.getOrderId());
            }
        } else {
            log.warn(" 根据父订单号查询父单对应的所有子订单号失败！parentWaybillCode={}" , parentWaybillCode);
        }
        return waybillCodes;
    }

    /**
     * 根据运单号获取订单号
     *
     * @param waybillCode
     * @param source      source说明：
     *                    1.如果waybillCode为正向运单，则直接返回订单号
     *                    2.如果waybillCode为返单号，并且source为true时，返回原运单的订单号
     *                    3.如果waybillCode为返单号，并且source为false时，返回为空
     * @return 订单号
     */
    public String getOrderCodeByWaybillCode(String waybillCode, boolean source) {
        CallerInfo callerInfo = null;
        try {
            callerInfo = ProfilerHelper.registerInfo("DMS.BASE.WaybillQueryManagerImpl.getOrderCodeByWaybillCode", Constants.UMP_APP_NAME_DMSWEB);
            BaseEntity<String> baseEntity = waybillQueryApi.getOrderCodeByWaybillCode(waybillCode, source);
            if (baseEntity.getResultCode() != 1) {
                log.warn("根据运单号调用运单接口获取订单号失败.waybillCode:{},source:{}.返回值code:{},message：{}"
                        ,waybillCode,source,baseEntity.getResultCode(), baseEntity.getMessage());
                return null;
            }
            return baseEntity.getData();
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            log.error("根据运单号调用运单接口获取订单号异常.waybillCode:{},source:{}" ,waybillCode,source,e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    /**
     * 根据运单号和属性获取运单扩展属性
     *
     * @param waybillCodes
     * @param properties   运单的扩展属性
     * @return
     */
    public List<WaybillExtPro> getWaybillExtByProperties(List<String> waybillCodes, List<String> properties) {
        CallerInfo callerInfo = null;
        try {
            callerInfo = ProfilerHelper.registerInfo("DMS.BASE.WaybillQueryManagerImpl.getWaybillExtByProperties", Constants.UMP_APP_NAME_DMSWEB);
            BaseEntity<List<WaybillExtPro>> baseEntity = waybillQueryApi.getWaybillExtByProperties(waybillCodes, properties);
            if (baseEntity.getResultCode() != 1) {
                log.warn("根据运单号调用运单接口运单扩展信息失败.waybillCodes:{},properties:{}.返回值code:{},message：{}"
                        ,JsonHelper.toJson(waybillCodes),JsonHelper.toJson(properties),baseEntity.getResultCode(), baseEntity.getMessage());
                return null;
            }
            return baseEntity.getData();
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            log.error("根据运单号调用运单接口获取扩展信息异常.waybillCodes:{},properties:{}"
                    ,JsonHelper.toJson(waybillCodes),JsonHelper.toJson(properties), e);
            return null;
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
    }

    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.ifForceCheckByWarehouse",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public Boolean ifForceCheckByWarehouse(Integer cky2, Integer storeId) {
        BaseEntity<Boolean> baseEntity = waybillQueryApi.ifForceCheckByWarehouse(cky2, storeId);
        if (baseEntity.getResultCode() == 1) {
            return baseEntity.getData();
        } else {
            log.warn("根据配送中心ID和仓ID查询是否强制换单，接口返回异常状态码，ResultCode:{},message:{}" ,baseEntity.getResultCode(), baseEntity.getMessage());
        }
        return null;
    }

    /**
     * 查询运单号是否存在
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.queryExist",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public Boolean queryExist(String waybillCode){
        return waybillQueryApi.queryExist(waybillCode);
    }


    /**
     * 根据运单号查询waybillSign
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMS.JSF.Waybill.waybillQueryApi.getWaybillSignByWaybillCode",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public BaseEntity<String> getWaybillSignByWaybillCode(String waybillCode){
        return waybillQueryApi.getWaybillSignByWaybillCode(waybillCode);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getSkuPackRelation" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<SkuPackRelationDto> getSkuPackRelation(String sku) {
        return waybillQueryApi.getSkuPackRelation(sku);
    }

    /**
     * 修改包裹数量
     * @param waybillCode
     * @param list
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.waybillQueryApi.batchUpdatePackageByWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<Boolean> batchUpdatePackageByWaybillCode(String waybillCode,List list){
        return waybillUpdateApi.batchUpdatePackageByWaybillCode(waybillCode, list);
    }

    /*
     *
     * 查询运单接口获取包裹列表
     *
     * */
    @Override
    public List<DeliveryPackageD> findWaybillPackList(String waybillCode) {
        List<DeliveryPackageD> deliveryPackageDList = null;
        CallerInfo info = null;
        try {
            info = ProfilerHelper.registerInfo("DMS.JSF.Waybill.waybillQueryApi.findWaybillPackList", Constants.UMP_APP_NAME_DMSWEB);
            WChoice wChoice = new WChoice();
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.getDataByChoice(waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                deliveryPackageDList = baseEntity.getData().getPackageList();
            }

        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("运单号【{}】调用运单WSS异常",waybillCode, e);
        } finally {
            Profiler.registerInfoEnd(info);
        }

        return deliveryPackageDList;
    }

    /**
     * 根据运单号获取商家ID
     * @param waybillCode
     * @return
     */
    @Override
    public Integer getBusiId(String waybillCode) {
        Integer busiId = null;
        BaseEntity<BigWaybillDto> baseEntity = this.getDataByChoice(waybillCode, true, false, false, false);
        if (baseEntity != null && Constants.RESULT_SUCCESS == baseEntity.getResultCode() && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
            busiId = baseEntity.getData().getWaybill().getBusiId();
        }

        return busiId;
    }

    /**
     * 根据运单号查询运单增值服务信息
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getWaybillVasInfosByWaybillCode" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<List<WaybillVasDto>> getWaybillVasInfosByWaybillCode(String waybillCode) {
        return waybillQueryApi.getWaybillVasInfosByWaybillCode(waybillCode);
    }
    /**
     * 根据运单号查询出关联的原单和返单单号
     */
	@Override
	public JdResult<List<String>> getOriginalAndReturnWaybillCodes(String waybillCode) {
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "waybillQueryApi.getOriginalAndReturnWaybillCodes");
		JdResult<List<String>> result = new JdResult<List<String>>();
		try {
			BaseEntity<LinkedList<String>>  rpcResult = waybillQueryApi.getOriginalAndReturnWaybillCodes(waybillCode);
			if(rpcResult != null
					&& rpcResult.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode()){
				result.setData(rpcResult.getData());
				result.toSuccess();
			}else{
				log.warn("调用运单多次换单查询接口失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("调用运单多次换单查询接口失败！");
			}
		} catch (Exception e) {
			log.error("调用运单多次换单查询接口异常！",e);
			result.toError("调用运单多次换单查询接口异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}

    /***
     * 根据运单号获取服务单号
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getServiceCodeInfoByWaybillCode" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<List<WaybillServiceRelationDto>> getServiceCodeInfoByWaybillCode(String waybillCode) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "waybillQueryApi.getServiceCodeInfoByWaybillCode");
        BaseEntity<List<WaybillServiceRelationDto>> baseEntity = null;
        try {
            baseEntity =  waybillQueryApi.getServiceCodeInfoByWaybillCode(waybillCode);
        } catch (Exception e) {
            log.error("调用运单获取服务单号接口异常！入参waybillCode:{}",waybillCode,e);
            Profiler.functionError(callerInfo);
        }finally{
            Profiler.registerInfoEnd(callerInfo);
        }
       return baseEntity;
    }



    /**
     * 获取运单的附属信息
     * 附件、图片等，需根据对应附件类型查询
     * @param waybill
     * @param attachmentType
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getWaybillAttachmentByWaybillCodeAndType" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
	public  BaseEntity<List<WaybillAttachmentDto>> getWaybillAttachmentByWaybillCodeAndType(String waybill, Integer attachmentType){
        return waybillQueryApi.getWaybillAttachmentByWaybillCodeAndType(waybill,attachmentType);
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.doGetPackageVasInfo" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Map<String,String> doGetPackageVasInfo(String wayBillCode) {
        //查询出一个属于一个包裹的所有商品的增值服务明细 key->包裹号 value->增值服务信息
        Map<String,String> packageUpVasMap = new HashMap<>();
        //获取包裹增值服务
        GoodsQueryDto goodsQueryDto = new GoodsQueryDto();
        goodsQueryDto.setQueryGoodsVas(true);
        goodsQueryDto.setWaybillCode(wayBillCode);
        try {
            BaseEntity<List<GoodsDto>> baseEntity = waybillQueryApi.queryGoodsDataByWCode(goodsQueryDto);
            log.info("查询运单下商品明细-支持扩展属性,运单号:{},获取数据:{}",wayBillCode,JsonHelper.toJson(baseEntity));
            if (baseEntity != null && baseEntity.getData() != null && baseEntity.getResultCode() == 1) {
                List<GoodsDto> dataList = baseEntity.getData();
                if(dataList!= null && dataList.size()>0){
                    if(log.isWarnEnabled()){
                        this.log.warn("获取到运单下商品明细信息,运单号:{}",wayBillCode);
                    }
                    for (GoodsDto goodsDto:dataList) {
                        //保存商品增值服务明细list
                        StringBuilder sbString = new StringBuilder();
                        String packPickUpVas = packageUpVasMap.get(goodsDto.getPackBarcode());
                        //商品增值服务明细
                        List<WaybillPickupVasDto> waybillPickupVasDtoList = goodsDto.getWaybillPickupVasDtoList();
                        if(CollectionUtils.isNotEmpty(waybillPickupVasDtoList)){
                            for(WaybillPickupVasDto waybillPickupVasDto :waybillPickupVasDtoList){
                                Map<String,Object> vasExtMap = waybillPickupVasDto.getVasExt();
                                if(vasExtMap != null && !vasExtMap.isEmpty()){
                                    if(vasExtMap.get("vasName") != null) sbString.append(" ").append(simpleValues(vasExtMap.get("vasName")));
                                }
                            }
                        }
                        if(StringUtils.isNotBlank(packPickUpVas)){
                            packageUpVasMap.put(goodsDto.getPackBarcode(),new StringBuilder(packPickUpVas).append(sbString).toString());
                        }else{
                            packageUpVasMap.put(goodsDto.getPackBarcode(),sbString.toString());
                        }
                    }
                }
            }else{
                if(log.isWarnEnabled()){
                    log.warn("查询运单下商品明细支持扩展属性接口失败！return:{}",JsonHelper.toJson(baseEntity));
                }
            }
        }catch (Exception e){
            if(log.isWarnEnabled()){
                log.error("查询运单下商品明细支持扩展属性接口失败！运单号:{},异常信息:{}",wayBillCode,e);
            }
        }
        log.info("获取商品增值信息成功,运单号:{},返回增值信息:{}",wayBillCode,packageUpVasMap);
        return packageUpVasMap;
    }

    private static String simpleValues(Object vasName){

        String newVasName = vasName.toString().trim();
        String simpleValues = mapValues.get(newVasName);
        if(StringUtils.isNotBlank(simpleValues)){
            return simpleValues;
        }else {
            return newVasName;
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.WaybillQueryManagerImpl.doGetPackageGoodsVasInfo",mState={JProEnum.TP,JProEnum.FunctionError})
    public Map<String, String> doGetPackageGoodsVasInfo(String wayBillCode) {
        WChoice wChoice = new WChoice();
        //只查询运单下的商品列表
        wChoice.setQueryGoodList(true);
        try {
            BaseEntity<BigWaybillDto> baseEntity = this.getDataByChoice(wayBillCode, wChoice);
            log.info("调用运单明细自定义查询接口成功,运单号:{},返回数据:{}",wayBillCode, JsonUtils.toJson(baseEntity));
            if (baseEntity != null && baseEntity.getResultCode() == 1 && baseEntity.getData() != null) {
                BigWaybillDto waybillDto = baseEntity.getData();
                //商品明细
                List<Goods> goodsList = waybillDto.getGoodsList();
                //获取商品信息
                if(CollectionUtils.isNotEmpty(goodsList)){
                    Map<String,String> packageNameVasMap = new HashMap<>(goodsList.size());
                    //保存商品名称明细list
                    for(Goods good : goodsList){
                        StringBuilder sbString = new StringBuilder();
                        String packageName = packageNameVasMap.get(good.getPackBarcode());
                        if(StringUtils.isNotBlank(good.getGoodName())) sbString.append(good.getGoodName());
                        //说明该包裹下不止一个商品 获取其他商品信息并拼接
                        if(StringUtils.isNotBlank(packageName)){
                            packageNameVasMap.put(good.getPackBarcode(),new StringBuilder(packageName).append(" ").append(sbString).toString());
                        }else{//该包裹下目前为止仅一个商品
                            packageNameVasMap.put(good.getPackBarcode(),sbString.toString());
                        }
                    }
                    log.info("WaybillQueryManagerImpl-doGetPackageGoodsVasInfo-运单号:{},返回map:{}",wayBillCode,packageNameVasMap);
                    return packageNameVasMap;
                }else {
                    log.warn("调用运单明细自定义查询接口查询出goods信息为空");
                }
            }else{
                log.warn("调用运单明细自定义查询接口失败,运单号:{},返回信息:{}",wayBillCode,JsonUtils.toJson(baseEntity));
            }
        }catch (Exception ex){
            log.error("WaybillQueryManagerImpl-doGetPackageGoodsVasInfo异常,运单号:{},异常信息",wayBillCode,ex);
        }
        return new HashMap<>();
    }

    /**
     * 根据运单信息获取运输产品类型
     * 判断waybill_sign,按以下规则设置产品名称
     序号	标记位	   产品名称	面单打印形式
     1	55位=0且31位=5	微小件	特惠送
     2	55位=0且31位=B	函速达	函速达
     3	31=7且29位=8	特瞬送同城	极速达
     4	55位=0且31位=7且29位=8	同城速配	同城速配
     5	31=0	特惠送	特惠送
     6	55位=0且31位=3	特瞬送城际	城际即日
     7	55位=0且31位=A	生鲜特惠	生鲜特惠
     8	55位=0且31位=9	生鲜特快	生鲜特快
     9	55位=1且84位=3代表航空 55位=1且84位≠3代表非航空	生鲜专送	生鲜专送
     10	（55位=0且31位=1且116位=0）或（55位=0且31位=1且116位=1）	特快送	特快送
     11	（55位=0且31位=2且16位=1,2,3,7,8）或（55位=0且31位=1且116位=2）	特快送"同城即日"	特快送即日
     12	55位=0且31位=4或55位=0且31位=1且116位=3	特快送“特快次晨”	特快送次晨
     13	31=6	京准达+其他主产品	京准达
     14	31=6	京准达	京准达
     15	55位=0且31位=1且116位=4	特快陆运	特快送
     16	55位=0且31位=C	特惠小包	特惠包裹
     * @param waybill
     * @return
     */
    @Override
    public String getTransportMode(Waybill waybill){
        String res="";

        if(waybill == null){
            return res;
        }
        String waybillSign = waybill.getWaybillSign();
        if(StringHelper.isEmpty(waybillSign)){
            return res;
        }

        if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_0)){
            //5-特惠送
            res = TextConstants.PRODUCT_NAME_THS;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_6)){
            //13\14-京准达
            res = TextConstants.PRODUCT_NAME_JZD;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_F)){
            //31 = F  特惠小件
            res = TextConstants.PRODUCT_NAME_THXJ;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_G)){
            //31 = G  冷链专送
            res = TextConstants.PRODUCT_NAME_LLZS;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_H)){
            //31 = H  特快包裹
        	res = TextConstants.PRODUCT_NAME_TKBG;
        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_7)
                && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_29, WaybillSignConstants.CHAR_29_8)){
            if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_0)){
                //4-同城速配
                res = TextConstants.PRODUCT_NAME_TCSP;
            }else{
                //3-极速达
                res = TextConstants.PRODUCT_NAME_JSD;
            }
        }
        if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_0)){
            if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_5)){
                //1-特惠送
                res = TextConstants.PRODUCT_NAME_THS;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_B)){
                //2-函速达
                res = TextConstants.PRODUCT_NAME_HSD;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_3)){
                //6-城际即日
                res = TextConstants.PRODUCT_NAME_CJJR;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_A)){
                //7-生鲜特惠
                res = TextConstants.PRODUCT_NAME_SXTH;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_9)){
                //8-生鲜特快
                res = TextConstants.PRODUCT_NAME_SXTK;
                // 8.1 - 生鲜特快下运营类型
                if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_2)) {
                    res += TextConstants.PRODUCT_NAME_SXTK_JR;
                }
                if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3)
                    && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_4)) {
                    // 生鲜特快次晨
                    res += TextConstants.PRODUCT_NAME_SXTK_CC;
                }
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_C)){
                //16-特惠包裹
                res = TextConstants.PRODUCT_NAME_THBG;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                    && BusinessUtil.isSignInChars(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_0,WaybillSignConstants.CHAR_116_1)){
                //10-特快送
                res = TextConstants.PRODUCT_NAME_TKS;
            }else if((BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_2))
                    ||
                    (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                            && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_2))){
                //11-特快送即日
                res = TextConstants.PRODUCT_NAME_TKSJR;
            }else if(
                        (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_4)
                        && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_4)
                        ) ||
                        (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                            && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3)
                            && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_4)
                        )
                    ){
                //12-特快送次晨
                res = TextConstants.PRODUCT_NAME_TKSCC;
            }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                    && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_4)){
                //15-特快送
                res = TextConstants.PRODUCT_NAME_TKS;
            }
            else if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_4)
                    && !BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_4)) {
                // 特快送
                res = TextConstants.PRODUCT_NAME_TKS;
            }
            else if (BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_31, WaybillSignConstants.CHAR_31_1)
                    && BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_116, WaybillSignConstants.CHAR_116_3)
                    && !BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_16, WaybillSignConstants.CHAR_16_4)) {
                // 特快送
                res = TextConstants.PRODUCT_NAME_TKS;
            }

        }else if(BusinessUtil.isSignChar(waybillSign, WaybillSignConstants.POSITION_55, WaybillSignConstants.CHAR_55_1)){
            //9-生鲜专送
            res = TextConstants.PRODUCT_NAME_SXZS;
        }

        return res;
    }

    /**
     * 根据运单信息获取托寄物名称
     * @param bigWaybillDto
     * @return
     */
    @Override
    public String getConsignmentNameByWaybillDto(BigWaybillDto bigWaybillDto){
        // 1.查询运单商品信息
        String name = this.getConsignmentNameFromGoods(bigWaybillDto.getGoodsList());
        if (name != null) {
            log.info("getConsignmentNameByWaybillDto getConsignmentNameFromGoods result: {}", name);
            return name;
        }
        // 2.查询ECLP全程跟踪
        name = this.getConsignmentNameFromECLP(bigWaybillDto.getWaybill().getBusiOrderCode());
        if (name != null) {
            log.info("getConsignmentNameByWaybillDto getConsignmentNameFromECLP result: {}", name);
            return name;
        }
        // 3.查询运单托寄物信息
        name = this.getConsignmentNameFromWaybillExt(bigWaybillDto.getWaybill().getWaybillExt());
        if (name != null) {
            log.info("getConsignmentNameByWaybillDto getConsignmentNameFromWaybillExt result: {}", name);
            return name;
        }
        return null;
    }

    /**
     * 查询运单商品信息
     *
     * @param goods
     * @return
     */
    private String getConsignmentNameFromGoods(List<Goods> goods) {
        if (goods != null && goods.size() > 0) {
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < goods.size(); i++) {
                //明细内容： 商品编码SKU：商品名称*数量
                name.append(goods.get(i).getSku());
                name.append(":");
                name.append(goods.get(i).getGoodName());
                name.append(" * ");
                name.append(goods.get(i).getGoodCount());
                if (i != goods.size() - 1) {
                    //除了最后一个，其他拼完加个,
                    name.append(",");
                }
            }
            return name.toString();
        }
        return null;
    }

    /***
     * 调用ECLP获取商品信息
     *
     * @param busiOrderCode
     * @return
     */
    private String getConsignmentNameFromECLP(String busiOrderCode) {
        //第二步 查eclp
        //如果运单上没有明细 就判断是不是eclp订单 如果是，调用eclp接口
        if (WaybillUtil.isECLPByBusiOrderCode(busiOrderCode)) {
            StringBuilder name = new StringBuilder();
            List<ItemInfo> itemInfoList = eclpItemManager.getltemBySoNo(busiOrderCode);
            if (itemInfoList != null && itemInfoList.size() > 0) {
                for (int i = 0; i < itemInfoList.size(); i++) {
                    //明细内容： 商品名称*数量 优先取deptRealOutQty，如果该字段为空取realOutstoreQty  eclp负责人宫体雷
                    name.append(itemInfoList.get(i).getGoodsName());
                    name.append(" * ");
                    name.append(itemInfoList.get(i).getDeptRealOutQty() == null ? itemInfoList.get(i).getRealOutstoreQty() : itemInfoList.get(i).getDeptRealOutQty());
                    if (i != itemInfoList.size() - 1) {
                        //除了最后一个，其他拼完加个,
                        name.append(",");
                    }
                }
                return name.toString();
            }
        }
        return null;
    }

    /**
     * 查询运单托寄物信息
     *
     * @param waybillExt
     * @return
     */
    private String getConsignmentNameFromWaybillExt(WaybillExt waybillExt) {
        //第三步 查运单的托寄物
        if (waybillExt != null && org.apache.commons.lang.StringUtils.isNotEmpty(waybillExt.getConsignWare())) {
            StringBuilder name = new StringBuilder();
            name.append(waybillExt.getConsignWare());
            name.append(waybillExt.getConsignCount() == null ? "" : " * " + waybillExt.getConsignCount());
            return name.toString();
        }
        return null;
    }

    /**
     * 根据运单号查询包装耗材信息
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getBoxChargeByWaybillCode",
        mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public BaseEntity<List<BoxChargeDto>> getBoxChargeByWaybillCode(String waybillCode){
        return WaybillUtil.isWaybillCode(waybillCode) ? waybillQueryApi.getBoxChargeByWaybillCode(waybillCode) : null;
    }

    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getWaybillVasWithExtendInfo",
            mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    @Override
    public BaseEntity<List<WaybillVasDto>> getWaybillVasWithExtendInfo(String waybillCode) {
        WaybillVasChoice waybillVasChoice= new WaybillVasChoice();
        waybillVasChoice.setQueryVas(true);
        waybillVasChoice.setQueryVasExtend(true);
        return waybillQueryApi.getWaybillVasWithExtendInfo(waybillCode,waybillVasChoice);
    }

}
