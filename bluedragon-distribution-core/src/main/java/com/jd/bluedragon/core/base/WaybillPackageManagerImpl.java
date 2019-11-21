package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.DeliveryPackageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("waybillPackageManager")
public class WaybillPackageManagerImpl implements WaybillPackageManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WaybillPackageApi waybillPackageApi;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 调用运单的分页接口一次获取的包裹数量，分页大小定值1000，则获取数据为实时，其他则非实时
     */
    private static final Integer PACKAGE_NUM_ONCE_QUERY = 1000;

    /**
     * 根据运单号获取包裹信息
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillPackageApi.getPackListByWaybillCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<List<DeliveryPackageD>> getPackListByWaybillCode(String waybillCode) {
        //增加一个开关，支持两万个包裹，需要单独调用运单的分页接口过渡期使用
        if (isGetPackageByPageOpen()) {
            return getPackageByWaybillCode(waybillCode);
        } else {
            return waybillPackageApi.getPackListByWaybillCode(waybillCode);
        }
    }

    @Override
    public BaseEntity<List<DeliveryPackageD>> getPackListByWaybillCodeOfPage(String waybillCode,int pageNo,int pageSize){

        BaseEntity<List<DeliveryPackageD>> result = new BaseEntity<List<DeliveryPackageD>>();
        List<DeliveryPackageD> packageList = new ArrayList<DeliveryPackageD>();
        result.setData(packageList);
        Page<DeliveryPackageDto> pageParam = new Page<DeliveryPackageDto>();
        pageParam.setCurPage(pageNo);
        pageParam.setPageSize(pageSize);

        //调用运单分页接口
        BaseEntity<Page<DeliveryPackageDto>> baseEntity = waybillPackageApi.getPackageByParam(waybillCode, pageParam);

        //调用接口异常，添加自定义报警
        if (null == baseEntity || baseEntity.getResultCode() != 1) {
            String alarmInfo = "调用运单接口getPackageByParam失败.waybillCode:" + waybillCode;
            if (null != baseEntity) {
                alarmInfo = alarmInfo + ",resultCode:" + baseEntity.getResultCode() + "-" + baseEntity.getMessage();
            }
            log.warn(alarmInfo);
            Profiler.businessAlarm("调用运单接口getPackageByParam失败", alarmInfo);
            return null;
        }

        //有包裹数据，则分页读取
        if (null != baseEntity && null != baseEntity.getData() &&
                null != baseEntity.getData().getResult() && baseEntity.getData().getResult().size() > 0) {

            packageList.addAll(changeToDeliveryPackageDBatch(baseEntity.getData().getResult()));

            log.debug("getPackageByWaybillCode获取包裹数据共{}条.waybillCode:{}" ,packageList.size(), waybillCode);
        }

        return result;
    }

    /**
     * 根据运单号列表获取包裹信息
     * @param waybillCodes
     * @return
     */
    public BaseEntity<Map<String, List<DeliveryPackageD>>> batchGetPackListByCodeList(List<String> waybillCodes){
        if (isGetPackageByPageOpen()) {
            BaseEntity<Map<String,List<DeliveryPackageD>>> result = new BaseEntity<Map<String, List<DeliveryPackageD>>>();
            Map<String,List<DeliveryPackageD>> waybillCodeAndPackMap = new HashMap<String, List<DeliveryPackageD>>();
            for(String waybillCode : waybillCodes){
                waybillCodeAndPackMap.put(waybillCode,getPackageByWaybillCode(waybillCode).getData());
            }
            result.setData(waybillCodeAndPackMap);
            return result;

        } else {
            return waybillPackageApi.batchGetPackListByCodeList(waybillCodes);
        }
    }

    /**
     * 根据包裹号列表获取包裹信息
     * @param packageCodes
     * @return
     */
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillPackageApi.queryPackageListForParcodes",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<List<DeliveryPackageD>> queryPackageListForParcodes(List<String> packageCodes){
        return waybillPackageApi.queryPackageListForParcodes(packageCodes);
    }

    /**
     * 包裹称重和体积测量数据上传
     * 来源 PackOpeController
     *
     * @param packOpeJson 称重和体积测量信息
     * @return map data:true or false,code:-1:参数非法 -3:服务端内部处理异常 1:处理成功,message:code对应描述
     */
    @JProfiler(jKey = "DMS.BASE.Jsf.WaybillPackageApi.uploadOpe", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Map<String, Object> uploadOpe(String packOpeJson){
        return waybillPackageApi.uploadOpe(packOpeJson);
    }

    /**
     * 根据运单号获取包裹数据，通过调用运单的分页接口获得
     *
     * @param waybillCode
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.WaybillPackageManagerImpl.getPackageByWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCode(String waybillCode) {
        log.debug("调用运单接口getPackageByParam,分页获取包裹数据,运单号:{}" , waybillCode);
        BaseEntity<List<DeliveryPackageD>> result = new BaseEntity<List<DeliveryPackageD>>();
        List<DeliveryPackageD> packageList = new ArrayList<DeliveryPackageD>();
        result.setData(packageList);

        //组织请求参数，从第一页开始，每页1000行
        Page<DeliveryPackageDto> pageParam = new Page<DeliveryPackageDto>();
        pageParam.setCurPage(1);
        pageParam.setPageSize(PACKAGE_NUM_ONCE_QUERY);

        //调用运单分页接口
        BaseEntity<Page<DeliveryPackageDto>> baseEntity = waybillPackageApi.getPackageByParam(waybillCode, pageParam);

        if (null == baseEntity) {
            log.warn("调用运单接口【waybillPackageApi.getPackageByParam()】获取包裹列表失败，接口异常，参数为：{}", waybillCode);
            Profiler.businessAlarm("dms ask PRC rest failed [waybillPackageApi.getPackageByParam]", waybillCode);
            return new BaseEntity<List<DeliveryPackageD>>(EnumBusiCode.BUSI_FAIL.getCode(),EnumBusiCode.BUSI_FAIL.getDesc());
        }

        //调用接口异常，添加自定义报警
        if (baseEntity.getResultCode() != 1) {
            String alarmInfo = "调用运单接口getPackageByParam失败.waybillCode:" + waybillCode;
            alarmInfo = alarmInfo + ",resultCode:" + baseEntity.getResultCode() + "-" + baseEntity.getMessage();
            log.warn(alarmInfo);
            return new BaseEntity<List<DeliveryPackageD>>(baseEntity.getResultCode(),baseEntity.getMessage());
        }

        //有包裹数据，则分页读取
        if (null != baseEntity.getData() &&
                null != baseEntity.getData().getResult() && baseEntity.getData().getResult().size() > 0) {

            packageList.addAll(changeToDeliveryPackageDBatch(baseEntity.getData().getResult()));

            log.debug("调用运单接口getPackageByParam,waybillCode:{},每次请求数:{}.返回包裹总数:{}，总页数:{}"
                    ,waybillCode,PACKAGE_NUM_ONCE_QUERY,baseEntity.getData().getTotalRow(), baseEntity.getData().getTotalPage());

            //读取分页数
            int totalPage = baseEntity.getData().getTotalPage();

            //循环获取剩余数据
            for (int i = 2; i <= totalPage; i++) {
                pageParam.setCurPage(i);
                List<DeliveryPackageDto> dtoList = waybillPackageApi.getPackageByParam(waybillCode, pageParam).getData().getResult();
                packageList.addAll(changeToDeliveryPackageDBatch(dtoList));
            }
            log.debug("getPackageByWaybillCode获取包裹数据共{}条.waybillCode:{}" ,packageList.size(), waybillCode);
        }

        return result;
    }

    /**
     * 将调用运单分页接口返回的dto转换成DeliveryPackageD
     *
     * @param dtoList
     * @return
     */
    private List<DeliveryPackageD> changeToDeliveryPackageDBatch(List<DeliveryPackageDto> dtoList) {
        List<DeliveryPackageD> packageDList = new ArrayList<DeliveryPackageD>();

        for (DeliveryPackageDto dto : dtoList) {
            DeliveryPackageD packageD = new DeliveryPackageD();

            packageD.setPackageBarcode(dto.getPackageBarcode());
            packageD.setWaybillCode(dto.getWaybillCode());
            packageD.setCky2(dto.getCky2());
            packageD.setStoreId(dto.getStoreId());
            packageD.setPackageState(dto.getPackageState());
            packageD.setPackWkNo(dto.getPackwkNo());
            packageD.setRemark(dto.getRemark());

            packageD.setGoodWeight(dto.getGoodWeight());
            packageD.setGoodVolume(dto.getGoodVolume());
            packageD.setAgainWeight(dto.getAgainWeight());
            packageD.setAgainVolume(dto.getAgainVolume());
            packageD.setPackTime(dto.getPackTime());
            packageD.setWeighTime(dto.getWeighTime());
            packageD.setCreateTime(dto.getCreateTime());
            packageD.setUpdateTime(dto.getUpdateTime());

            packageD.setWeighUserName(dto.getWeighUserName());

            packageDList.add(packageD);
        }

        return packageDList;
    }

    /**
     * 从sysconfig表里获取是否启用分页查询运单包裹的接口
     *
     * @return
     */
    public boolean isGetPackageByPageOpen() {
        List<SysConfig> sysConfigs = sysConfigService.getListByConfigName(Constants.SYS_CONFIG_PACKAGE_PAGE_SWITCH);
        if (sysConfigs != null && !sysConfigs.isEmpty()) {
            String content = sysConfigs.get(0).getConfigContent();
            if (StringHelper.isNotEmpty(content) && "1".equals(content)) {
                return true;
            }
        }
        return false;
    }
}
