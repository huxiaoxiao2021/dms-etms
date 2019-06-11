package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.bluedragon.utils.cache.BigWaybillPackageListCache;
import com.jd.etms.waybill.api.WaybillPickupTaskApi;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.SkuSn;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillExtPro;
import com.jd.etms.waybill.dto.*;
import com.jd.ql.trace.api.WaybillTraceBusinessQueryApi;
import com.jd.ql.trace.api.core.APIResultDTO;
import com.jd.ql.trace.api.domain.BillBusinessTraceAndExtendDTO;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("waybillQueryManager")
public class WaybillQueryManagerImpl implements WaybillQueryManager {

    private final Log logger = LogFactory.getLog(this.getClass());

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

    /**
     * 大包裹运单缓存开关
     */
    private final static String BIG_WAYBILL_PACKAGE_CACHE_SWITCH = PropertiesHelper.newInstance().getValue("big.waybill.package.cache.switch");

    private static final String SWITCH_ON = "1";

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillQueryApi.getWaybillByReturnWaybillCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<Waybill> getWaybillByReturnWaybillCode(String waybillCode) {
        return waybillQueryApi.getWaybillByReturnWaybillCode(waybillCode);
    }

    @Override
    public BaseEntity<BigWaybillDto> getDataByChoiceNoCache(String waybillCode, WChoice wChoice) {
        return waybillQueryApi.getDataByChoice(waybillCode, wChoice);
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.getDataByChoice", mState = {JProEnum.TP, JProEnum.FunctionError})
    public BaseEntity<BigWaybillDto> getDataByChoice(String waybillCode, WChoice wChoice) {
        Boolean isQueryPackList = wChoice.getQueryPackList();
        Boolean isQueryWaybillC = wChoice.getQueryWaybillC();
        this.updateWChoiceSetting(wChoice);

        BaseEntity<BigWaybillDto> baseEntity = this.getDataByChoiceNoCache(waybillCode, wChoice);
        if (baseEntity.getResultCode() == 1 && baseEntity.getData() != null) {
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
            logger.error("[大包裹运单缓存]获取包裹信息时发生异常，运单号:" + waybillCode, e);
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
    public boolean sendOrderTrace(String businessKey, int msgType, String title, String content, String operatorName, Date operateTime) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.sendOrderTrace", false, true);
        try {
            OrderTraceDto orderTraceDto = new OrderTraceDto();
            orderTraceDto.setBusinessKey(businessKey);
            orderTraceDto.setMsgType(msgType);
            orderTraceDto.setTitle(title);
            orderTraceDto.setContent(content);
            orderTraceDto.setOperatorName(operatorName);
            orderTraceDto.setOperateTime(operateTime == null ? new Date() : operateTime);
            BaseEntity<Boolean> baseEntity = waybillTraceApi.sendOrderTrace(orderTraceDto);
            if (baseEntity != null) {
                if (!baseEntity.getData()) {
                    this.logger.warn("分拣数据回传全程跟踪sendOrderTrace异常：" + baseEntity.getMessage() + baseEntity.getData());
                    Profiler.functionError(info);
                    return false;
                }
            } else {
                this.logger.warn("分拣数据回传全程跟踪接口sendOrderTrace异常");
                Profiler.functionError(info);
                return false;
            }
        } catch (Exception e) {
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean sendBdTrace(BdTraceDto bdTraceDto) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillQueryManagerImpl.sendBdTrace", false, true);
        try {
            BaseEntity baseEntity = waybillTraceApi.sendBdTrace(bdTraceDto);
            if (baseEntity != null) {
                if (baseEntity.getResultCode() != 1) {
                    this.logger.warn(JsonHelper.toJson(bdTraceDto));
                    this.logger.warn(bdTraceDto.getWaybillCode());
                    this.logger.warn("分拣数据回传全程跟踪sendBdTrace异常：" + baseEntity.getMessage());
                    //Profiler.functionError(info);
                    return false;
                }
            } else {
                this.logger.warn("分拣数据回传全程跟踪接口sendBdTrace异常" + bdTraceDto.getWaybillCode());
                //Profiler.functionError(info);
                return false;
            }
        } catch (Exception e) {
            logger.error("分拣数据回传全程跟踪sendBdTrace异常：" + bdTraceDto.getWaybillCode(), e);
            Profiler.functionError(info);
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return true;
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
                    this.logger.warn("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：" + waybillCode + ","
                            + baseEntity.getResultCode() + "," + baseEntity.getMessage());
                    result = REDISPATCH_ERROR;
                } else {
                    if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
                        result = REDISPATCH_YES;
                    } else {
                        result = REDISPATCH_NO;
                    }
                }
            } else {
                this.logger.warn("检查是否反调度WaybillQueryManagerImpl.checkReDispatch返回空：" + waybillCode);
                result = REDISPATCH_ERROR;
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            this.logger.error("检查是否反调度WaybillQueryManagerImpl.checkReDispatch异常：" + waybillCode, e);
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
                    this.logger.warn("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState异常：" + oldWaybillCode + ","
                            + baseEntity.getResultCode() + "," + baseEntity.getMessage());
                } else if (baseEntity.getData() != null && baseEntity.getData().size() > 0) {
                    changedWaybillCode = baseEntity.getData().get(oldWaybillCode);
                }
            } else {
                this.logger.warn("获取取件单对应的面单号W单号waybillTraceApi.getPkStateByWCodeAndState返回空：" + oldWaybillCode);
            }
        } catch (Exception e) {
            Profiler.functionError(info);
            this.logger.error("获取取件单对应的面单号W单号WaybillQueryManagerImpl.checkReDispatch异常：" + oldWaybillCode, e);
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
            logger.error(" 根据父订单号查询父单对应的所有子订单号失败！parentWaybillCode=" + parentWaybillCode);
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
                logger.error("根据运单号调用运单接口获取订单号失败.waybillCode:" + waybillCode + ",source:" + source +
                        ".返回值code:" + baseEntity.getResultCode() + ",message" + baseEntity.getMessage());
                return null;
            }
            return baseEntity.getData();
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            logger.error("根据运单号调用运单接口获取订单号异常.", e);
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
                logger.error("根据运单号调用运单接口运单扩展信息失败.waybillCodes:" +
                        JsonHelper.toJson(waybillCodes) +
                        ",properties:" + JsonHelper.toJson(properties) +
                        ".返回值code:" + baseEntity.getResultCode() +
                        ",message" + baseEntity.getMessage());
                return null;
            }
            return baseEntity.getData();
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            logger.error("根据运单号调用运单接口获取扩展信息异常.", e);
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
            logger.warn("根据配送中心ID和仓ID查询是否强制换单，接口返回异常状态码，ResultCode:" + baseEntity.getResultCode() + ",message:" + baseEntity.getMessage());
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
}
