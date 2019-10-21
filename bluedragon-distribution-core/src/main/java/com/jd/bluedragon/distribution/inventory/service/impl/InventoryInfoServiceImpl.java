package com.jd.bluedragon.distribution.inventory.service.impl;

import com.jd.bluedragon.core.base.InventoryJsfManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.*;
import com.jd.bluedragon.distribution.inventory.service.InventoryInfoService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.Pager;
import com.jd.ql.dms.report.inventory.InventoryJsfService;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import com.jd.ql.dms.report.inventory.domain.InventoryWaybillSummary;
import com.jd.ql.dms.report.inventory.request.InventoryQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class InventoryInfoServiceImpl implements InventoryInfoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int pageSize = 1000;

    @Autowired
    private InventoryJsfManager inventoryJsfManager;

    @Autowired
    private InventoryScanDetailDao inventoryScanDetailDao;

    @Autowired
    private InventoryTaskDao inventoryTaskDao;

    @Autowired
    @Qualifier("inventoryTaskCompleteProducer")
    private DefaultJMQProducer inventoryTaskCompleteProducer;

    @Override
    public InventoryWaybillResponse initNeedInventoryWaybillInfo(InventoryBaseRequest inventoryBaseRequest) {

        InventoryWaybillResponse inventoryWaybillResponse = new InventoryWaybillResponse();
        List<InventoryWaybillInfo> inventoryInfoList = new ArrayList<>();

        //es中获取运单维度带盘信息统计
        List<InventoryWaybillSummary> inventoryWaybillSummaryList = this.queryNeedInventoryWaybillSummaryList(inventoryBaseRequest);
        int waybillSum = inventoryWaybillSummaryList.size();
        int packageSum = 0;

        for (InventoryWaybillSummary inventoryWaybillSummary : inventoryWaybillSummaryList) {
            //运单号
            Integer needInventoryCount = inventoryWaybillSummary.getPackageNum();
            //更新待盘包裹数
            packageSum += needInventoryCount;
            InventoryWaybillInfo inventoryWaybillInfo = new InventoryWaybillInfo();
            inventoryWaybillInfo.setWaybillCode(inventoryWaybillSummary.getWaybillCode());
            inventoryWaybillInfo.setNeedInventoryCount(needInventoryCount);
            inventoryWaybillInfo.setNoInventoryCount(needInventoryCount);
            inventoryWaybillInfo.setScannedCount(0);
            inventoryWaybillInfo.setOperateTime(new Date().getTime());
            inventoryInfoList.add(inventoryWaybillInfo);
        }

        InventoryTask inventoryTask = new InventoryTask();
        inventoryTask.setInventoryTaskId(inventoryBaseRequest.getInventoryTaskId());
        inventoryTask.setWaybillSum(waybillSum);
        inventoryTask.setPackageSum(packageSum);
        //更新任务的待盘运单总数和包裹数
        inventoryTaskDao.updateSum(inventoryTask);


        inventoryWaybillResponse.setInventoryInfoList(inventoryInfoList);
        inventoryWaybillResponse.setWaybillSum(waybillSum);
        inventoryWaybillResponse.setPackageSum(packageSum);
        inventoryWaybillResponse.setScannedSum(0);

        return inventoryWaybillResponse;
    }

    @Override
    public InventoryWaybillResponse syncCurrInventoryWaybillInfo(InventoryBaseRequest inventoryBaseRequest) {
        InventoryWaybillResponse inventoryWaybillResponse = new InventoryWaybillResponse();
        List<InventoryWaybillInfo> inventoryInfoList = new ArrayList<>();
        int waybillSum = 0;
        int packageSum = 0;
        int scannedSum = 0;

        Set<String> noInventoryPackageSet = new HashSet<>();
        Map<String, InventoryWaybillInfo> inventoryWaybillInfoMap = new HashMap<>();
        //es中获取运单维度带盘信息统计
        List<InventoryPackage> inventoryPackageList = this.queryNeedInventoryPackageList(inventoryBaseRequest);
        for (InventoryPackage inventoryPackage : inventoryPackageList) {
            String packageCode = inventoryPackage.getPackageCode();
            String waybillCode = inventoryPackage.getWaybillCode();
            if (StringHelper.isEmpty(packageCode) || StringHelper.isEmpty(waybillCode)) {
                logger.error("【syncCurrInventoryWaybillInfo】包裹号或运单号为空，对象值：" + JsonHelper.toJson(inventoryPackage));
                continue;
            }
            //更新待盘包裹号集合
            noInventoryPackageSet.add(inventoryPackage.getPackageCode());
            //更新待盘运单统计集合
            InventoryWaybillInfo inventoryWaybillInfoTemp;

            if (! inventoryWaybillInfoMap.containsKey(waybillCode)) {
                //运单不存在统计集合中，新增一条记录
                inventoryWaybillInfoTemp = new InventoryWaybillInfo(waybillCode);
                inventoryWaybillInfoMap.put(waybillCode, inventoryWaybillInfoTemp);
            } else {
                inventoryWaybillInfoTemp = inventoryWaybillInfoMap.get(waybillCode);
            }
            //待盘数、未盘数加1
            inventoryWaybillInfoTemp.setNeedInventoryCountBaseCurr(1);
            inventoryWaybillInfoTemp.setNoInventoryCountBaseCurr(1);
        }

        String inventoryTaskId = inventoryBaseRequest.getInventoryTaskId();
        //从数据库中取出当前任务号下，所有包裹扫描记录
        List<InventoryScanDetail> inventoryScanDetailList = inventoryScanDetailDao.getScanDetailByParam(inventoryTaskId);
        if (inventoryScanDetailList != null && inventoryScanDetailList.size() > 0) {
            for (InventoryScanDetail inventoryScanDetail : inventoryScanDetailList) {
                String scanPackageCode = inventoryScanDetail.getPackageCode();
                String waybillCode = inventoryScanDetail.getWaybillCode();
                Long scanTime = inventoryScanDetail.getOperateTime().getTime();
                //如果扫描包裹号存在于待盘集合中，说明已盘，对应运单未盘数减1
                if (noInventoryPackageSet.contains(scanPackageCode)) {
                    InventoryWaybillInfo inventoryWaybillInfoTemp = inventoryWaybillInfoMap.get(waybillCode);
                    if (inventoryWaybillInfoTemp != null) {
                        //已盘数加1
                        inventoryWaybillInfoTemp.setScannedCountBaseCurr(1);
                        //未盘数减1
                        inventoryWaybillInfoTemp.setNoInventoryCountBaseCurr(-1);
                        //更新运单下包裹最新的扫描时间
                        inventoryWaybillInfoTemp.setOperateTimeNew(scanTime);
                    } else {
                        logger.warn("包裹号：" + scanPackageCode + "存在待盘信息，但是运单集合不存在，请检查代码逻辑");
                    }
                } else {
                    //扫描包裹不存在与待盘集合中，说明扫描未多货情况，此时在统计集合中新增一条统计记录，后续再有同运单的包裹扫描记录直接从集合中获取
                    InventoryWaybillInfo inventoryWaybillInfoTemp = null;
                    if (! inventoryWaybillInfoMap.containsKey(waybillCode)) {
                        inventoryWaybillInfoTemp = new InventoryWaybillInfo(waybillCode, scanTime);
                        inventoryWaybillInfoMap.put(waybillCode, inventoryWaybillInfoTemp);
                    } else {
                        inventoryWaybillInfoTemp = inventoryWaybillInfoMap.get(waybillCode);
                        inventoryWaybillInfoTemp.setOperateTimeNew(scanTime);
                    }
                    //已盘数增加，多货情况下包裹数和未盘数都是0
                    inventoryWaybillInfoTemp.setScannedCountBaseCurr(1);
                }
            }
        } else {
            logger.warn("盘点任务号：" + inventoryTaskId + "查询扫描记录数据为空");
        }
        //遍历运单信息集合统计数据
        waybillSum = inventoryWaybillInfoMap.size();
        for (InventoryWaybillInfo inventoryWaybillInfo : inventoryWaybillInfoMap.values()) {
            Integer needInventoryCount = inventoryWaybillInfo.getNeedInventoryCount();
            //待盘总数大于0需要对待盘运单总数加1
            packageSum += needInventoryCount;
            scannedSum += inventoryWaybillInfo.getScannedCount();
            if (inventoryWaybillInfo.getOperateTime() == null) {
                inventoryWaybillInfo.setOperateTime(new Date().getTime());
            }
            inventoryInfoList.add(inventoryWaybillInfo);
        }
        Collections.sort(inventoryInfoList, Collections.<InventoryWaybillInfo>reverseOrder());
        inventoryWaybillResponse.setInventoryInfoList(inventoryInfoList);
        inventoryWaybillResponse.setWaybillSum(waybillSum);
        inventoryWaybillResponse.setPackageSum(packageSum);
        inventoryWaybillResponse.setScannedSum(scannedSum);

        return inventoryWaybillResponse;
    }

    @Override
    public List<InventoryWaybillSummary> queryNeedInventoryWaybillSummaryList(InventoryBaseRequest inventoryBaseRequest) {

        List<InventoryWaybillSummary> inventoryWaybillSummaryList = new ArrayList<>();
        Pager<InventoryQueryRequest> pager = initPagerParam(inventoryBaseRequest, 1, this.pageSize);
        try {
            //es中获取运单维度带盘信息统计
            BaseEntity<Pager<InventoryWaybillSummary>> pagerBaseEntity = inventoryJsfManager.queryNeedInventoryWaybillSummaryList(pager);
            if (pagerBaseEntity != null && pagerBaseEntity.getCode() == 200 && pagerBaseEntity.getData() != null) {
                List<InventoryWaybillSummary> inventoryWaybillSummaryListTemp = pagerBaseEntity.getData().getData();
                if (inventoryWaybillSummaryListTemp != null && ! inventoryWaybillSummaryListTemp.isEmpty()) {
                    inventoryWaybillSummaryList.addAll(inventoryWaybillSummaryListTemp);
                    long totalPageNum = (pagerBaseEntity.getData().getTotal() + this.pageSize - 1) / this.pageSize;

                    for (int i = 2; i <= totalPageNum; i++) {
                        pager = initPagerParam(inventoryBaseRequest, i, this.pageSize);
                        //es中获取运单维度带盘信息统计
                        pagerBaseEntity = inventoryJsfManager.queryNeedInventoryWaybillSummaryList(pager);
                        if (pagerBaseEntity != null && pagerBaseEntity.getCode() == 200 && pagerBaseEntity.getData() != null) {
                            inventoryWaybillSummaryListTemp = pagerBaseEntity.getData().getData();
                            inventoryWaybillSummaryList.addAll(inventoryWaybillSummaryListTemp);
                        } else {
                            logger.warn("获取待盘运单统计数据第【" + i + "】页数据为空，共【" + totalPageNum + "】页，JSF方法【queryNeedInventoryWaybillSummaryList】");
                        }
                    }
                } else {
                    logger.warn("获取待盘运单统计数据为空！JSF方法【queryNeedInventoryWaybillSummaryList】");
                }
            } else {
                logger.warn("获取待盘运单统计数据为空！JSF方法【queryNeedInventoryWaybillSummaryList】");
            }
        } catch (Exception e) {
            logger.error("获取待盘运单统计数据失败！JSF方法【queryNeedInventoryWaybillSummaryList】", e);
        }

        return inventoryWaybillSummaryList;
    }

    @Override
    public List<InventoryPackage> queryNeedInventoryPackageList(InventoryBaseRequest inventoryBaseRequest) {

        List<InventoryPackage> inventoryPackageList = new ArrayList<>();
        Pager<InventoryQueryRequest> pager = initPagerParam(inventoryBaseRequest, 1, this.pageSize);
        try {
            //es中获取运单维度带盘信息统计
            BaseEntity<Pager<InventoryPackage>> pagerBaseEntity = inventoryJsfManager.queryNeedInventoryPackageList(pager);

            if (pagerBaseEntity != null && pagerBaseEntity.getCode() == 200 && pagerBaseEntity.getData() != null) {
                List<InventoryPackage> InventoryPackageTemp = pagerBaseEntity.getData().getData();
                if (InventoryPackageTemp != null && ! InventoryPackageTemp.isEmpty()) {
                    inventoryPackageList.addAll(InventoryPackageTemp);
                    long totalPageNum = (pagerBaseEntity.getData().getTotal() + this.pageSize - 1) / this.pageSize;

                    for (int i = 2; i <= totalPageNum; i++) {
                        pager = initPagerParam(inventoryBaseRequest, i, this.pageSize);
                        //es中获取运单维度带盘信息统计
                        pagerBaseEntity = inventoryJsfManager.queryNeedInventoryPackageList(pager);
                        if (pagerBaseEntity != null && pagerBaseEntity.getCode() == 200 && pagerBaseEntity.getData() != null) {
                            InventoryPackageTemp = pagerBaseEntity.getData().getData();
                            inventoryPackageList.addAll(InventoryPackageTemp);
                        } else {
                            logger.warn("获取待盘包裹明细数据第【" + i + "】页数据为空，共【" + totalPageNum + "】页，JSF方法【queryNeedInventoryPackageList】");
                        }
                    }
                } else {
                    logger.warn("获取待盘包裹明细数据为空！JSF方法【queryNeedInventoryPackageList】");
                }
            } else {
                logger.warn("获取待盘包裹明细数据为空！JSF方法【queryNeedInventoryPackageList】");
            }
        } catch (Exception e) {
            logger.error("获取待盘包裹明细数据失败！JSF方法【queryNeedInventoryPackageList】", e);
        }

        return inventoryPackageList;
    }

    @Override
    public List<InventoryPackage> queryPackageStatusList(InventoryBaseRequest inventoryBaseRequest, List<String> packageCodeList) {
        List<InventoryPackage> inventoryPackageList = new ArrayList<>();
        InventoryQueryRequest inventoryQueryRequest = new InventoryQueryRequest();
        inventoryQueryRequest.setPackageCodeList(packageCodeList);
        inventoryQueryRequest.setCreateSiteCode(inventoryBaseRequest.getCreateSiteCode());
        logger.info("queryPackageStatusList请求参数：" + JsonHelper.toJson(inventoryQueryRequest));
        try {
            //es中获取包裹列表中状态信息
            BaseEntity<List<InventoryPackage>> baseEntity = inventoryJsfManager.queryPackageStatusList(inventoryQueryRequest);
            if (baseEntity != null && baseEntity.getCode() == 200 && baseEntity.getData() != null) {
                inventoryPackageList = baseEntity.getData();
            } else {
                logger.warn("获取待盘包裹状态据为空！JSF方法【queryPackageStatusList】，参数：" + JsonHelper.toJson(inventoryQueryRequest));
            }
        } catch (Exception e) {
            logger.error("获取待盘包裹状态据失败！JSF方法【queryPackageStatusList】，参数：" + JsonHelper.toJson(inventoryQueryRequest));
        }


        return inventoryPackageList;
    }

    @Override
    public List<InventoryWaybillDetail> getInventoryWaybillDetail(InventoryBaseRequest inventoryBaseRequest) {
        List<InventoryWaybillDetail> noInventoryWaybillDetailList = new ArrayList<>();
        List<InventoryWaybillDetail> scannedWaybillDetailList = new ArrayList<>();
        String waybillCode = inventoryBaseRequest.getBarCode();
        //暂存包裹状态
        Map<String, String> inventoryPackageStatusMap = new HashMap<>();
        Set<String> scanPackageCodeSet = null;
        String inventoryTaskId = inventoryBaseRequest.getInventoryTaskId();
        //从数据库中取出当前任务号下，该运单所有包裹扫描记录的包裹号
        List<String> scanPackageCodeList = inventoryScanDetailDao.getScanPackageCodeByParam(inventoryTaskId, waybillCode);
        if (scanPackageCodeList.size() > 0) {
            scanPackageCodeSet = new HashSet<>(scanPackageCodeList);
        } else {
            logger.warn("获取运单【" + waybillCode + "】扫描数据为空，任务编号：" + inventoryTaskId);
        }

        //es获取该运单的待盘信息
        List<InventoryPackage> inventoryPackageList = this.queryNeedInventoryPackageList(inventoryBaseRequest);
        //遍历结果，组装参数
        for (InventoryPackage inventoryPackage : inventoryPackageList) {
            InventoryWaybillDetail inventoryWaybillDetail = new InventoryWaybillDetail();
            String packageCode = inventoryPackage.getPackageCode();
            if (StringHelper.isEmpty(packageCode)) {
                logger.error("包裹号为空，对象值：" + JsonHelper.toJson(inventoryPackage));
                continue;
            }
            inventoryWaybillDetail.setPackageCode(packageCode);
            inventoryWaybillDetail.setPackageCodeSuffix(this.getSuffixByPackageCode(packageCode));

            //存在扫描记录，将实体添加到已扫的map中，后续统计已扫描信息时根据map拿到相应状态信息
            if (scanPackageCodeSet != null && scanPackageCodeSet.contains(packageCode)) {
                inventoryPackageStatusMap.put(packageCode, inventoryPackage.getStatusDesc());
            } else {
                //未盘更新包裹状态
                inventoryWaybillDetail.setPackageStatus(inventoryPackage.getStatusDesc());
                inventoryWaybillDetail.setOperateUserErp("");
                inventoryWaybillDetail.setOperateTime("");
                noInventoryWaybillDetailList.add(inventoryWaybillDetail);
            }
        }

        //1：返回未盘
        //2：返回已盘
        Integer type = inventoryBaseRequest.getType();
        if (type == 1) {
            return noInventoryWaybillDetailList;
        } else if (type == 2) {
            //从数据库中取出该运单下的所有扫描记录
            List<InventoryScanDetail> inventoryScanDetailList = inventoryScanDetailDao.getScanDetailByParam(inventoryTaskId, waybillCode);
            //遍历所有结果组装返回值
            for (InventoryScanDetail inventoryScanDetail : inventoryScanDetailList) {
                InventoryWaybillDetail inventoryWaybillDetail = new InventoryWaybillDetail();
                String packageCode = inventoryScanDetail.getPackageCode();
                String packageStatus = inventoryPackageStatusMap.get(packageCode);
                String operateUserErp = inventoryScanDetail.getOperatorErp();
                inventoryWaybillDetail.setPackageCode(packageCode);
                //特殊处理，只要后缀
                inventoryWaybillDetail.setPackageCodeSuffix(this.getSuffixByPackageCode(packageCode));
                //特殊处理，只要MM-dd HH:mm
                inventoryWaybillDetail.setOperateTime(this.getFormatTime(inventoryScanDetail.getOperateTime()));
                inventoryWaybillDetail.setOperateUserErp(operateUserErp == null ? "" : operateUserErp);
                inventoryWaybillDetail.setPackageStatus(packageStatus == null ? "": packageStatus);
                scannedWaybillDetailList.add(inventoryWaybillDetail);
            }
            return scannedWaybillDetailList;
        } else {
            logger.error("type值为：" + type + "不在方法处理范围，type值只能为1或2");
            return new ArrayList<>();
        }
    }

    @Override
    public boolean checkTaskIsComplete(String inventoryTaskId) {
        //根据任务号取进行中的任务，取不到说明任务已结束
        List<InventoryTask> result = inventoryTaskDao.getInventoryTaskByTaskId(inventoryTaskId);
        return result == null || result.isEmpty();
    }

    @Override
    public void completeInventoryTask(InventoryBaseRequest inventoryBaseRequest) {
        InventoryTask inventoryTask = new InventoryTask();
        inventoryTask.setInventoryTaskId(inventoryBaseRequest.getInventoryTaskId());
        inventoryTask.setStatus(inventoryBaseRequest.getType());
        inventoryTask.setEndTime(new Date());
        if (inventoryTaskDao.updateStatus(inventoryTask)) {
            //更新成功，发送mq消息
            String body = JsonHelper.toJson(inventoryBaseRequest);
            try {
                inventoryTaskCompleteProducer.send(inventoryTask.getInventoryTaskId(), body);
            } catch (JMQException e) {
                logger.error("发送盘点任务完成MQ消息失败，消息体：" + body);
            }
        } else {
            logger.warn("更新任务状态失败，参数：" + JsonHelper.toJson(inventoryTask));
        }
    }

    private Pager<InventoryQueryRequest> initPagerParam(InventoryBaseRequest inventoryBaseRequest, int pageNo, int pageSize) {
        //组装分页参数
        Pager<InventoryQueryRequest> pager = new Pager<>();
        pager.setSearchVo(this.convert2InventoryQueryRequest(inventoryBaseRequest));
        pager.setPageNo(pageNo);
        pager.setPageSize(pageSize);
        return pager;
    }

    private InventoryQueryRequest convert2InventoryQueryRequest(InventoryBaseRequest inventoryBaseRequest) {

        InventoryQueryRequest inventoryQueryRequest = new InventoryQueryRequest();
        inventoryQueryRequest.setCreateSiteCode(inventoryBaseRequest.getCreateSiteCode());
        inventoryQueryRequest.setDirectionCodeList(inventoryBaseRequest.getDirectionCodeList());
        //获取任务要求的查询待盘包裹的起始时间范围
        InventoryTask inventoryTask = inventoryTaskDao.getInventoryTaskInfo(inventoryBaseRequest.getInventoryTaskId());

        if (inventoryTask != null && inventoryTask.getCreateTime() != null && inventoryTask.getHourRangeTime() != null) {
            inventoryQueryRequest.setStartTime(inventoryTask.getHourRangeTime());
            inventoryQueryRequest.setEndTime(inventoryTask.getCreateTime());
        }

        String barCode = inventoryBaseRequest.getBarCode();
        if (WaybillUtil.isWaybillCode(barCode)) {
            inventoryQueryRequest.setWaybillCode(barCode);
        }
        List<Integer> statusList = new ArrayList<>();
        if (InventoryScopeEnum.EXCEPTION.getCode().equals(inventoryBaseRequest.getInventoryScope())) {
            //异常区赋值异常外呼状态
            statusList.add(PackStatusEnum.EXCEPTION.getCode());
        } else {
            //自定义和全场区赋值初异常外呼状态和发货状态
            statusList.add(PackStatusEnum.RECEIVE.getCode());
            statusList.add(PackStatusEnum.INSPECTION.getCode());
            statusList.add(PackStatusEnum.REPRINT.getCode());
            statusList.add(PackStatusEnum.SORTING.getCode());
            statusList.add(PackStatusEnum.SORTING_CANCEL.getCode());
            statusList.add(PackStatusEnum.SEND_CANCEL.getCode());

        }
        inventoryQueryRequest.setStatusCodeList(statusList);
        return inventoryQueryRequest;
    }


    private String getSuffixByPackageCode(String packageCode) {
        String suffix = WaybillUtil.getPackageSuffix(packageCode);
        return suffix == null ? packageCode : suffix;
    }

    private String getFormatTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        return formatter.format(date);
    }
}
