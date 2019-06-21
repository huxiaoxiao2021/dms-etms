package com.jd.bluedragon.distribution.inventory.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.*;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.inventory.service.InventoryInfoService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("inventoryExceptionService")
public class InventoryExceptionServiceImpl extends BaseService<InventoryException> implements InventoryExceptionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("inventoryExceptionDao")
    private InventoryExceptionDao inventoryExceptionDao;

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @Autowired
    private InventoryScanDetailDao inventoryScanDetailDao;

    @Autowired
    private InventoryTaskDao inventoryTaskDao;

    @Override
    public Dao<InventoryException> getDao() {
        return this.inventoryExceptionDao;
    }

    @Override
    public PagerResult<InventoryException> queryByPagerCondition(InventoryExceptionCondition condition) {
        return inventoryExceptionDao.queryByPagerCondition(condition);
    }

    /**
     * 导出
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(InventoryExceptionCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<>();
        //添加表头
        heads.add("区域");
        heads.add("操作场地");
        heads.add("任务码");
        heads.add("运单号");
        heads.add("包裹号");
        heads.add("异常类型");
        heads.add("最新物流状态");
        heads.add("处理状态");
        heads.add("异常描述");
        heads.add("盘点任务创建人");
        heads.add("盘点创建时间");
        heads.add("盘点扫描人");
        heads.add("盘点扫描时间");

        resList.add(heads);
        List<InventoryExceptionDto> list = inventoryExceptionDao.getExportResultByCondition(condition);
        if (list != null && ! list.isEmpty()) {
            //表格信息
            for(InventoryExceptionDto inventoryExceptionDto : list){
                List<Object> body = Lists.newArrayList();
                body.add(inventoryExceptionDto.getOrgName());
                body.add(inventoryExceptionDto.getCreateSiteName());
                body.add(inventoryExceptionDto.getInventoryTaskId());
                body.add(inventoryExceptionDto.getWaybillCode());
                body.add(inventoryExceptionDto.getPackageCode());
                body.add(InventoryExpTypeEnum.getDescByCode(inventoryExceptionDto.getExpType()));
                body.add(inventoryExceptionDto.getLatestPackStatus());
                body.add(inventoryExceptionDto.getExpStatus() == 0 ? "未处理" : "已处理");
                body.add(inventoryExceptionDto.getExpDesc());
                body.add(inventoryExceptionDto.getCreateUserErp() == null ? "" : inventoryExceptionDto.getCreateUserErp());
                body.add(DateHelper.formatDate(inventoryExceptionDto.getCreateTime(), Constants.DATE_TIME_FORMAT));
                body.add(inventoryExceptionDto.getInventoryUserErp() == null ? "" : inventoryExceptionDto.getInventoryUserErp());
                body.add(DateHelper.formatDate(inventoryExceptionDto.getInventoryTime(), Constants.DATE_TIME_FORMAT));
                resList.add(body);
            }
        }
        return resList;
    }

    @Override
    public void generateInventoryException(InventoryBaseRequest inventoryBaseRequest) {

        int exceptionCount = 0;

        Integer type = inventoryBaseRequest.getType();

        //完成盘点的包裹集合
        Set<String> competeInventoryPackageCodeSet = new HashSet<>();
        //已扫包裹的包裹集合
        Set<String> scanPackageCodeSet = null;
        String inventoryTaskId = inventoryBaseRequest.getInventoryTaskId();
        //从数据库中取出当前任务号下，所有包裹扫描记录的包裹号
        List<String> scanPackageCodeList = inventoryScanDetailDao.getScanPackageCodeByParam(inventoryTaskId);
        if (scanPackageCodeList.size() > 0) {
            scanPackageCodeSet = new HashSet<>(scanPackageCodeList);
        } else {
            logger.warn("任务号【" + inventoryTaskId + "】下扫描记录数据为空！");
        }

        //es获取该运单的待盘信息
        List<InventoryPackage> inventoryPackageList = inventoryInfoService.queryNeedInventoryPackageList(inventoryBaseRequest);
        //遍历结果，组装参数
        for (InventoryPackage inventoryPackage : inventoryPackageList) {
            InventoryWaybillDetail inventoryWaybillDetail = new InventoryWaybillDetail();
            String packageCode = inventoryPackage.getPackageCode();
            if (StringHelper.isEmpty(packageCode)) {
                logger.error("【generateInventoryException】包裹号为空，对象值：" + JsonHelper.toJson(inventoryPackage));
                continue;
            }
            inventoryWaybillDetail.setPackageCode(packageCode);

            //存在扫描记录，将实体添加到已扫的map中，后续统计已扫描信息时根据map拿到相应状态信息
            if (scanPackageCodeSet != null && scanPackageCodeSet.contains(packageCode)) {
                //正常已盘
                competeInventoryPackageCodeSet.add(packageCode);
            } else {
                //正常结束时才插入少货
                if (type != null && type == 1) {
                    //少货，组装异常数据插入数据库
                    InventoryException inventoryException = this.convert2InventoryException(inventoryBaseRequest);
                    inventoryException.setPackageCode(packageCode);
                    inventoryException.setWaybillCode(inventoryPackage.getWaybillCode());
                    inventoryException.setDirectionCode(inventoryPackage.getDirectionCode());
                    inventoryException.setDirectionName(inventoryPackage.getDirectionName());
                    inventoryException.setLatestPackStatus(inventoryPackage.getStatusDesc());
                    inventoryException.setExpType(InventoryExpTypeEnum.INVENTORY_EXCEPT_TYPE_LOSS.getCode());
                    inventoryException.setExpDesc(this.getInventoryExpDesc(inventoryPackage.getStatusCode()));
                    try {
                        exceptionCount++;
                        this.inventoryExceptionDao.insert(inventoryException);

                    } catch (Exception e) {
                        logger.error("插入盘点异常表异常！",e);
                    }
                }

            }
        }

        //从数据库中取出该任务下所有扫描记录
        List<InventoryScanDetail> inventoryScanDetailList = inventoryScanDetailDao.getScanDetailByParam(inventoryTaskId);
        //遍历所有结果组装返回值
        for (InventoryScanDetail inventoryScanDetail : inventoryScanDetailList) {
            String packageCode = inventoryScanDetail.getPackageCode();
            //扫描结果过滤掉属于待盘的记录，剩下的为多货情况
            if (! competeInventoryPackageCodeSet.contains(packageCode)) {
                //多货，组装异常数据插入数据库
                InventoryException inventoryException = this.convert2InventoryException(inventoryBaseRequest);
                inventoryException.setPackageCode(packageCode);
                inventoryException.setWaybillCode(inventoryScanDetail.getWaybillCode());
                inventoryException.setDirectionCode(inventoryScanDetail.getDirectionCode());
                inventoryException.setDirectionName(inventoryScanDetail.getDirectionName());
                inventoryException.setInventoryUserCode(inventoryScanDetail.getOperatorCode());
                inventoryException.setInventoryUserName(inventoryScanDetail.getOperatorName());
                inventoryException.setInventoryUserErp(inventoryScanDetail.getOperatorErp());
                inventoryException.setInventoryTime(inventoryScanDetail.getOperateTime());
                inventoryException.setExpType(InventoryExpTypeEnum.INVENTORY_EXCEPT_TYPE_MORE.getCode());

                List<String> packageCodeList = new ArrayList<>();
                packageCodeList.add(packageCode);
                List<InventoryPackage> inventoryPackages = inventoryInfoService.queryPackageStatusList(inventoryBaseRequest, packageCodeList);
                if (inventoryPackages != null && ! inventoryPackages.isEmpty()) {
                    Integer directionCode = inventoryPackages.get(0).getDirectionCode();
                    List<Integer> directionCodeList = inventoryBaseRequest.getDirectionCodeList();
                    if (InventoryScopeEnum.CUSTOMIZE.getCode().equals(inventoryBaseRequest.getInventoryScope()) && ! directionCodeList.contains(directionCode)) {
                        //流向异常有实物
                        inventoryException.setExpDesc(InventoryExpDescEnum.DIRECTION_EXCEPTION_MORE.getDesc());
                    } else {
                        Integer statusCode =  inventoryPackages.get(0).getStatusCode();
                        if (PackStatusEnum.SEND.getCode().equals(statusCode)) {
                            //已发货有实物
                            inventoryException.setExpDesc(InventoryExpDescEnum.SEND_MORE.getDesc());
                        } else if (PackStatusEnum.EXCEPTION.getCode().equals(statusCode)) {
                            //异常外呼有实物
                            inventoryException.setExpDesc(InventoryExpDescEnum.EXCEPTION_MORE.getDesc());
                        } else {
                            logger.warn("包裹状态妈：" + statusCode.toString() + "，不是发货货异常外呼状态！");
                        }
                    }
                } else {
                    //无任何操作有实物
                    inventoryException.setExpDesc(InventoryExpDescEnum.NO_OPERATION_MORE.getDesc());
                }
                try {
                    exceptionCount++;
                    this.inventoryExceptionDao.insert(inventoryException);
                } catch (Exception e) {
                    logger.error("插入盘点异常表异常！", e);
                }
            }
        }
        //更新任务的异常数
        InventoryTask inventoryTask = new InventoryTask();
        inventoryTask.setInventoryTaskId(inventoryTaskId);
        inventoryTask.setExceptionSum(exceptionCount);
        inventoryTaskDao.update(inventoryTask);
    }

    @Override
    public int handleException(List<Long> list, LoginUser loginUser) {
        Map<String, Object> params = new HashMap<>();

        params.put("list", list);
        params.put("expUserCode", loginUser.getStaffNo());
        params.put("expUserErp", loginUser.getUserErp());
        params.put("expUserName", loginUser.getUserName());
        return inventoryExceptionDao.updateExpStatus(params);
    }

    private InventoryException convert2InventoryException(InventoryBaseRequest inventoryBaseRequest) {
        InventoryException inventoryException = new InventoryException();
        inventoryException.setInventoryTaskId(inventoryBaseRequest.getInventoryTaskId());
        inventoryException.setInventorySiteCode(inventoryBaseRequest.getCreateSiteCode());
        inventoryException.setInventorySiteName(inventoryBaseRequest.getCreateSiteName());
        inventoryException.setCreateTime(new Date());
        return inventoryException;
    }

    private String getInventoryExpDesc(Integer packStatus) {

        if (packStatus.equals(PackStatusEnum.RECEIVE.getCode())) {
            //已收货无实物
            return InventoryExpDescEnum.RECEIVE_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.INSPECTION.getCode())) {
            //已验货无实物
            return InventoryExpDescEnum.INSPECTION_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.REPRINT.getCode())) {
            //包裹补打无实物
            return InventoryExpDescEnum.REPRINT_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.SORTING.getCode())) {
            //已分拣无实物
            return InventoryExpDescEnum.SORTING_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.SORTING_CANCEL.getCode())) {
            //取消分拣无实物
            return InventoryExpDescEnum.SEND_CANCEL_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.SEND_CANCEL.getCode())) {
            //取消发货无实物
            return InventoryExpDescEnum.SEND_CANCEL_LOSS.getDesc();
        } else if (packStatus.equals(PackStatusEnum.EXCEPTION.getCode())) {
            //异常外呼无实物
            return InventoryExpDescEnum.EXCEPTION_LOSS.getDesc();
        } else {
            return null;
        }
    }

}

