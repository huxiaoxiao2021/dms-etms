package com.jd.bluedragon.core.base;

import IceInternal.Ex;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.utils.BusiWaringUtil;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.common.Page;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.PackFlowDetail;
import com.jd.etms.waybill.dto.DeliveryPackageDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service("waybillPackageManager")
public class WaybillPackageManagerImpl implements WaybillPackageManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillPackageApi waybillPackageApi;

//    @Autowired
    private SysConfigService sysConfigService;

//    @Autowired
    private BusiWaringUtil busiWaringUtil;

    /**
     * 调用运单的分页接口一次获取的包裹数量，分页大小定值1000，则获取数据为实时，其他则非实时
     */
    @Value("${parallel_get_package_num_once_query:5}")
    private Integer parallel_get_package_num_once_query;
    private static final Integer PACKAGE_NUM_ONCE_QUERY = 1000;


    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("paralleGetPackage-pool-%d").build();

    private static final ExecutorService executorParalleGetPackage  = new ThreadPoolExecutor(6, 8,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue(),namedThreadFactory);



    /**
     * 根据运单号获取包裹信息
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillPackageApi.getPackListByWaybillCode",mState={JProEnum.TP,JProEnum.FunctionError})
    public BaseEntity<List<DeliveryPackageD>> getPackListByWaybillCode(String waybillCode) {
        return getPackageByWaybillCode(waybillCode);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.waybillPackageApi.getWaybillPackageCodes",mState={JProEnum.TP,JProEnum.FunctionError})
    public List<String> getWaybillPackageCodes(String waybillCode) {
        BaseEntity<List<String>> result = waybillPackageApi.getWaybillPackageCodes(waybillCode);
        if (result == null || result.getResultCode() != 1) {
            String alarmInfo = "调用运单接口getWaybillPackageCodes失败.waybillCode:" + waybillCode;
            if (null != result) {
                alarmInfo = alarmInfo + ",resultCode:" + result.getResultCode() + "-" + result.getMessage();
            }
            log.warn(alarmInfo);
            Profiler.businessAlarm("调用运单接口getWaybillPackageCodes失败", alarmInfo);
            return new ArrayList<>();
        }
        return result.getData();
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

    @Override
    public BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCode(String waybillCode){
        if(uccPropertyConfiguration.isParalleGetPackageSwitch()){
            return getPackageByWaybillCodeParalle(waybillCode);
        }
        return getPackageByWaybillCodeDefault(waybillCode);
    }
    /**
     * 根据运单号获取包裹数据，通过调用运单的分页接口获得
     *
     * @param waybillCode
     * @return
     */
    public BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCodeDefault(String waybillCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillPackageManagerImpl.getPackageByWaybillCode",Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
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

//        busiWaringUtil.bigWaybillWarning(waybillCode,packageList.size());

            return result;
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("查询包裹明细错误[{}]",waybillCode,e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 根据运单号获取包裹数据，通过调用运单的分页接口获得
     *
     * @param waybillCode
     * @return
     */
    public BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCodeParalle(final String waybillCode) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillPackageManagerImpl.getPackageByWaybillCodeParalle",Constants.UMP_APP_NAME_DMSWEB, false, true);
        try {
            log.debug("getPackageByWaybillCodeParalle分页获取包裹数据运单号[{}]" , waybillCode);
            Page<DeliveryPackageDto> firstPageParam = new Page();
            firstPageParam.setCurPage(1);
            firstPageParam.setPageSize(parallel_get_package_num_once_query);
            BaseEntity<Page<DeliveryPackageDto>> baseEntity = waybillPackageApi.getPackageByParam(waybillCode, firstPageParam);
            if (null == baseEntity) {
                log.warn("调用运单接口【waybillPackageApi.getPackageByParam()】获取包裹列表失败[{}]", waybillCode);
                return new BaseEntity(EnumBusiCode.BUSI_FAIL.getCode(),EnumBusiCode.BUSI_FAIL.getDesc());
            }
            if (baseEntity.getResultCode() != 1) {
                log.warn("调用运单getPackageByParam失败waybillCode[{}]resultCode[{}]message[{}]",waybillCode,baseEntity.getResultCode(),baseEntity.getMessage());
                return new BaseEntity(baseEntity.getResultCode(),baseEntity.getMessage());
            }
            if (!isHasData(baseEntity)) {
                log.warn("调用运单getPackageByParam数据为空waybillCode[{}]resultCode[{}]message[{}]",waybillCode,baseEntity.getResultCode(),baseEntity.getMessage());
                return new BaseEntity(EnumBusiCode.BUSI_NOT_EXIST.getCode(),EnumBusiCode.BUSI_NOT_EXIST.getDesc());
            }
            List<DeliveryPackageD> packageList = new ArrayList();
            packageList.addAll(changeToDeliveryPackageDBatch(baseEntity.getData().getResult()));

            log.debug("调用运单接口getPackageByParam-waybillCode[{}]limit[{}]totalRow[{}]totalPage[{}]"
                    ,waybillCode,parallel_get_package_num_once_query,baseEntity.getData().getTotalRow(), baseEntity.getData().getTotalPage());
            //剩余页数并发获取
            int totalPage = baseEntity.getData().getTotalPage();
            if(totalPage > 1){
                packageList.addAll(paralleGetPackages(waybillCode,totalPage,2,parallel_get_package_num_once_query));
            }
            log.debug("getPackageByWaybillCode获取包裹数据共{}条.waybillCode:{}" ,packageList.size(), waybillCode);
//            busiWaringUtil.bigWaybillWarning(waybillCode,packageList.size());
            BaseEntity<List<DeliveryPackageD>> result = new BaseEntity();
            result.setData(packageList);
            return result;
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("并发查询包裹明细报错[{}]",waybillCode,e);
            throw e;
        } finally {
            Profiler.registerInfoEnd(info);
        }
    }

    /**
     * 根据运单号并发获取包裹明细
     * @param waybillCode
     * @param totalPage
     * @param startPage
     * @param pageLimit
     * @return
     */
    private List<DeliveryPackageD> paralleGetPackages(final String waybillCode, int totalPage,int startPage,final int pageLimit) {
        List<DeliveryPackageD> packageList = Lists.newArrayList();
        List<Future> futureList = Lists.newArrayList();
        for (int i = startPage; i <= totalPage; i++) {
            final int pageSize = i;
            Future<List<DeliveryPackageD>> future = executorParalleGetPackage.submit(new Callable(){
                @Override
                public List<DeliveryPackageD> call(){
                    Page<DeliveryPackageDto> pageParam = new Page();
                    pageParam.setCurPage(pageSize);
                    pageParam.setPageSize(pageLimit);
                    log.error("================[{}]",Thread.currentThread().getName());
                    List<DeliveryPackageDto> dtoList = waybillPackageApi.getPackageByParam(waybillCode, pageParam).getData().getResult();
                    return changeToDeliveryPackageDBatch(dtoList);
                }
            });
            futureList.add(future);
        }
        for (Future<List<DeliveryPackageD>> future: futureList) {
            try {
                packageList.addAll(future.get(2000,TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("查询运单包裹信息报错[{}]", waybillCode,e);
            } catch (ExecutionException | TimeoutException e) {
                log.error("查询运单包裹信息报错[{}]", waybillCode,e);
            }
        }
        return packageList;
    }

    private boolean isHasData(BaseEntity<Page<DeliveryPackageDto>> baseEntity) {
        return null != baseEntity.getData() &&
                null != baseEntity.getData().getResult() && baseEntity.getData().getResult().size() > 0;
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

    @JProfiler(jKey = "DMS.BASE.WaybillPackageManagerImpl.getOpeDetailByCode", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public Page<PackFlowDetail> getOpeDetailByCode(String waybillCode,Page<PackFlowDetail> page) {
        BaseEntity<Page<PackFlowDetail>> baseEntity = waybillPackageApi.getOpeDetailByCode(waybillCode, page);
        if(baseEntity != null
                && baseEntity.getResultCode() == 1 && baseEntity.getData() != null){
            return baseEntity.getData();
        }else {
            log.error("根据运单"+waybillCode+"查询运单称重流水数据失败!");
            return null;
        }
    }

    @Override
    public DeliveryPackageD getPackageInfoByPackageCode(String packageCode) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo(  "DMS.BASE.WaybillPackageManagerImpl.getPackageInfoByPackageCode");
        try {
            BaseEntity<DeliveryPackageD> baseEntity = waybillPackageApi.getPackageInfoByPackageCode(packageCode);
            if(baseEntity != null
                    && baseEntity.getResultCode() == 1 && baseEntity.getData() != null){
                return baseEntity.getData();
            }else {
                log.error("根据包裹号:{}"+packageCode+"获取包裹信息数据失败!");
                return null;
            }
        }catch (Exception ex){
            log.error("调用根据包裹号获取包裹信息接口异常！包裹号={}",ex,packageCode);
            Profiler.functionError(callerInfo);
            return null;
        }finally{
            Profiler.registerInfoEnd(callerInfo);
        }

    }
}
