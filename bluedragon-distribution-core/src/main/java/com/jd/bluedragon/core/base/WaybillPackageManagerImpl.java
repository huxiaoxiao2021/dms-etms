package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WaybillFlowDetail;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.dto.DeliveryPackageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Service("waybillPackageManager")
public class WaybillPackageManagerImpl implements WaybillPackageManager {

    private final Log logger = LogFactory.getLog(this.getClass());

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
            logger.error(alarmInfo);
            Profiler.businessAlarm("调用运单接口getPackageByParam失败", alarmInfo);
            return null;
        }

        //有包裹数据，则分页读取
        if (null != baseEntity && null != baseEntity.getData() &&
                null != baseEntity.getData().getResult() && baseEntity.getData().getResult().size() > 0) {

            packageList.addAll(changeToDeliveryPackageDBatch(baseEntity.getData().getResult()));

            logger.info("getPackageByWaybillCode获取包裹数据共" + packageList.size() + "条.waybillCode:" + waybillCode);
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
        logger.info("调用运单接口getPackageByParam,分页获取包裹数据,运单号:" + waybillCode);
        BaseEntity<List<DeliveryPackageD>> result = new BaseEntity<List<DeliveryPackageD>>();
        List<DeliveryPackageD> packageList = new ArrayList<DeliveryPackageD>();
        result.setData(packageList);

        //组织请求参数，从第一页开始，每页1000行
        Page<DeliveryPackageDto> pageParam = new Page<DeliveryPackageDto>();
        pageParam.setCurPage(1);
        pageParam.setPageSize(PACKAGE_NUM_ONCE_QUERY);

        //调用运单分页接口
        BaseEntity<Page<DeliveryPackageDto>> baseEntity = waybillPackageApi.getPackageByParam(waybillCode, pageParam);

        //调用接口异常，添加自定义报警
        if (null == baseEntity || baseEntity.getResultCode() != 1) {
            String alarmInfo = "调用运单接口getPackageByParam失败.waybillCode:" + waybillCode;
            if (null != baseEntity) {
                alarmInfo = alarmInfo + ",resultCode:" + baseEntity.getResultCode() + "-" + baseEntity.getMessage();
            }
            logger.error(alarmInfo);
            Profiler.businessAlarm("调用运单接口getPackageByParam失败", alarmInfo);
            return null;
        }

        //有包裹数据，则分页读取
        if (null != baseEntity && null != baseEntity.getData() &&
                null != baseEntity.getData().getResult() && baseEntity.getData().getResult().size() > 0) {

            packageList.addAll(changeToDeliveryPackageDBatch(baseEntity.getData().getResult()));

            logger.info("调用运单接口getPackageByParam,waybillCode:" + waybillCode + ",每次请求数:" +
                    PACKAGE_NUM_ONCE_QUERY + ".返回包裹总数:" + baseEntity.getData().getTotalRow() +
                    ",总页数:" + baseEntity.getData().getTotalPage());

            //读取分页数
            int totalPage = baseEntity.getData().getTotalPage();

            //循环获取剩余数据
            for (int i = 2; i <= totalPage; i++) {
                pageParam.setCurPage(i);
                List<DeliveryPackageDto> dtoList = waybillPackageApi.getPackageByParam(waybillCode, pageParam).getData().getResult();
                packageList.addAll(changeToDeliveryPackageDBatch(dtoList));
            }
            logger.info("getPackageByWaybillCode获取包裹数据共" + packageList.size() + "条.waybillCode:" + waybillCode);
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


    /**
     1、取运单号相关的所有称重量方记录（包裹和运单维度的都要）
     2、剔除重量体积均为0（注意，只剔除都是0的）的无意义的称重量方记录（多为系统卡控需要，实际并未称重）
     3、按时间先后顺序，找到最早称重量方的人ERP
     4、筛选出该ERP操作的所有称重量方记录
     5、若既有整单录入又有包裹录入，以该ERP最后一次重量体积录入时的形式为准
     6、若是整单，则取最后一次整单录入的重量体积为对比对象
     7、若是包裹，则筛选出所有包裹维度称重量方的记录，然后以包裹维度进行去重，仅保留时间靠后的那条，最后汇总得到的重量体积为对比对象
     */
    @JProfiler(jKey = "DMS.BASE.WaybillPackageManagerImpl.getFirstWeightAndVolumeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Cache(key = "DMS.BASE.WaybillPackageManagerImpl.getFirstWeightAndVolumeDetail@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public WaybillFlowDetail getFirstWeightAndVolumeDetail(String waybillCode){

        WaybillFlowDetail waybillFlowDetail = new WaybillFlowDetail();
        Page<PackFlowDetail> page = new Page<>();
        page.setPageSize(1000);
        BaseEntity<Page<PackFlowDetail>>  baseEntity = waybillPackageApi.getOpeDetailByCode(waybillCode,page);
        if(baseEntity != null && baseEntity.getData() != null
                && !CollectionUtils.isEmpty(baseEntity.getData().getResult())){
            List<PackFlowDetail> list = baseEntity.getData().getResult();
            List<PackFlowDetail> timeSortList = new ArrayList<>();
            //排除重量体积均为0;为空情况
            for(PackFlowDetail detail : list){
                if(detail==null || ((detail.getpWeight()!=null&&detail.getpWeight()==0)
                    && (detail.getpLength()!=null&&detail.getpLength()==0&&detail.getpWidth()!=null&&detail.getpWidth()==0&&detail.getpHigh()!=null&&detail.getpHigh()==0))){
                    continue;
                }
                timeSortList.add(detail);
            }
            if(CollectionUtils.isEmpty(timeSortList)){
                return waybillFlowDetail;
            }
            //按操作时间从小到大排序
            Collections.sort(timeSortList, new Comparator<PackFlowDetail>() {
                @Override
                public int compare(PackFlowDetail o1, PackFlowDetail o2) {
                    if(o1.getWeighTime() == null || o2.getWeighTime() == null){
                        return 0;
                    }
                    return o1.getWeighTime().compareTo(o2.getWeighTime());
                }
            });
            PackFlowDetail packFlowDetail = timeSortList.get(0);
            String operateErp = packFlowDetail.getWeighUserErp();
            if(StringUtils.isEmpty(operateErp)){
                operateErp = packFlowDetail.getMeasureUserErp();
                if(StringUtils.isEmpty(operateErp)){
                    return waybillFlowDetail;
                }
            }
            waybillFlowDetail.setOperateErp(operateErp);
            waybillFlowDetail.setOperateSiteCode(packFlowDetail.getOperatorSiteId());
            waybillFlowDetail.setOperateSiteName(packFlowDetail.getOperatorSite());
            waybillFlowDetail.setOperateTime(packFlowDetail.getWeighTime());
            //获取最早称重erp的所有称重记录
            List<PackFlowDetail> realList = new ArrayList<>();
            for(PackFlowDetail detail : timeSortList){
                if(operateErp.equals(detail.getWeighUserErp())){
                    realList.add(detail);
                }
            }
            if(CollectionUtils.isEmpty(realList)){
                return waybillFlowDetail;
            }
            //获取总重量体积并设置
            Map<String,PackFlowDetail> map1 = new HashMap<>();
            Map<String,PackFlowDetail> map2 = new HashMap<>();
            for(PackFlowDetail detail : realList){
                if(WaybillUtil.isWaybillCode(detail.getPackageCode())){
                    map2.put(detail.getPackageCode(),detail);
                }
                map1.put(detail.getPackageCode(),detail);
            }
            if(map1.size() > map2.size()){
                if(map2.size() != 0){
                    //既有整单又有包裹
                    PackFlowDetail flowDetail = map1.get(map1.size() - 1);
                    waybillFlowDetail.setTotalWeight(flowDetail.getpWeight());
                    waybillFlowDetail.setTotalWeight(flowDetail.getpLength()*flowDetail.getpWidth()*flowDetail.getpHigh());
                }else {
                    //包裹
                    Double totalWeight = 0.00;
                    Double totalVolume = 0.00;
                    for(String packageCode : map1.keySet()){
                        PackFlowDetail flowDetail = map1.get(packageCode);
                        totalWeight = totalWeight + flowDetail.getpWeight();
                        totalVolume = totalVolume + (flowDetail.getpLength()*flowDetail.getpWidth()*flowDetail.getpHigh());
                    }
                    waybillFlowDetail.setTotalWeight(totalWeight);
                    waybillFlowDetail.setTotalWeight(totalVolume);
                }
            }else {
                //整单
                PackFlowDetail flowDetail = map1.get(map1.size() - 1);
                waybillFlowDetail.setTotalWeight(flowDetail.getpWeight());
                waybillFlowDetail.setTotalWeight(flowDetail.getpLength()*flowDetail.getpWidth()*flowDetail.getpHigh());
            }
            return waybillFlowDetail;
        }
        return waybillFlowDetail;
    }
}
