package com.jd.bluedragon.distribution.inventory.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.inventory.dao.InventoryExceptionDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryScanDetailDao;
import com.jd.bluedragon.distribution.inventory.dao.InventoryTaskDao;
import com.jd.bluedragon.distribution.inventory.domain.*;
import com.jd.bluedragon.distribution.inventory.service.InventoryExceptionService;
import com.jd.bluedragon.distribution.inventory.service.InventoryInfoService;
import com.jd.bluedragon.distribution.inventory.service.PackageStatusService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.inventory.domain.InventoryPackage;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.text.MessageFormat;
import java.util.*;

@Service("inventoryExceptionService")
public class InventoryExceptionServiceImpl extends BaseService<InventoryException> implements InventoryExceptionService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("inventoryExceptionDao")
    private InventoryExceptionDao inventoryExceptionDao;

    @Autowired
    private InventoryInfoService inventoryInfoService;

    @Autowired
    private InventoryScanDetailDao inventoryScanDetailDao;

    @Autowired
    private InventoryTaskDao inventoryTaskDao;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private PackageStatusService packageStatusService;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

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
     * @param bufferedWriter
     * @return
     */
    @Override
    public void getExportData(InventoryExceptionCondition condition, BufferedWriter bufferedWriter) {
      try {
          long start = System.currentTimeMillis();
          // 报表头
          Map<String, String> headerMap = getHeaderMap();
          //设置最大导出数量
          Integer MaxSize  =  exportConcurrencyLimitService.uccSpotCheckMaxSize();
          Integer oneQuery = exportConcurrencyLimitService.getOneQuerySizeLimit();
          //设置单次导出数量
          condition.setLimit(oneQuery);
          CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());

          int queryTotal = 0;
          int index = 1;
          while (index <= (MaxSize/oneQuery)+1){
              condition.setOffset((index-1) * oneQuery);
              index ++;
              List<InventoryException> list = inventoryExceptionDao.getExportResultByCondition(condition);
              if(CollectionUtils.isEmpty(list)){
                  break;
              }

              List<InventoryExceptionExportDto> dataList = transForm(list);
              // 输出至csv文件中
              CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
              // 限制导出数量
              queryTotal += dataList.size();
              if(queryTotal > MaxSize ){
                  break;
              }
          }
          long end = System.currentTimeMillis();
          exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.INVENTORY_EXCEPTION_REPORT.getName(),end-start,queryTotal);
      }catch (Exception e){
          log.error("出转运清场异常统计表 export error",e);
      }
    }

    private List<InventoryExceptionExportDto> transForm(List<InventoryException> list) {
        List<InventoryExceptionExportDto> dataList = new ArrayList<InventoryExceptionExportDto>();
        //表格信息
        for(InventoryException inventoryException : list){
            InventoryExceptionExportDto body =  new InventoryExceptionExportDto();
            body.setOrgName(inventoryException.getOrgName());
            body.setInventorySiteName(inventoryException.getInventorySiteName());
            body.setInventoryTaskId(inventoryException.getInventoryTaskId());
            body.setInventoryScopeStr(InventoryScopeEnum.getDescByCode(inventoryException.getInventoryScope()));
            body.setWaybillCode(inventoryException.getWaybillCode());
            body.setPackageCode(inventoryException.getPackageCode());
            body.setDirectionName(inventoryException.getDirectionName());
            body.setExpTypeStr(InventoryExpTypeEnum.getDescByCode(inventoryException.getExpType()));
            body.setExpDesc(inventoryException.getExpDesc());
            body.setLatestPackStatus(inventoryException.getLatestPackStatus());
            body.setExpStatus(inventoryException.getExpStatus() == 0 ? "未处理" : "已处理");
            body.setExpUserErp(inventoryException.getExpUserErp());
            body.setExpOperateTime(DateHelper.formatDate(inventoryException.getExpOperateTime(), Constants.DATE_TIME_FORMAT));
            body.setTaskCreateUser(inventoryException.getTaskCreateUser() == null ? "" : inventoryException.getTaskCreateUser());
            body.setTaskCreateTime(DateHelper.formatDate(inventoryException.getTaskCreateTime(), Constants.DATE_TIME_FORMAT));
            body.setInventoryUserErp(inventoryException.getInventoryUserErp() == null ? "" : inventoryException.getInventoryUserErp());
            body.setInventoryTime(DateHelper.formatDate(inventoryException.getInventoryTime(), Constants.DATE_TIME_FORMAT));
            dataList.add(body);
        }
        return  dataList;
    }

    private Map<String, String> getHeaderMap() {
        Map<String,String> headerMap = new LinkedHashMap<>();
        //添加表头
        headerMap.put("orgName","区域");
        headerMap.put("inventorySiteName","操作场地");
        headerMap.put("inventoryTaskId","任务码");
        headerMap.put("inventoryScopeStr","盘点范围");
        headerMap.put("waybillCode","运单号");
        headerMap.put("packageCode","包裹号");
        headerMap.put("directionName","盘点卡位");
        headerMap.put("expTypeStr","异常类型");
        headerMap.put("expDesc","异常描述");
        headerMap.put("latestPackStatus","最新物流状态");
        headerMap.put("expStatus","处理状态");
        headerMap.put("expUserErp","异常处理人");
        headerMap.put("expOperateTime","异常处理时间");
        headerMap.put("taskCreateUser","盘点任务创建人");
        headerMap.put("taskCreateTime","盘点创建时间");
        headerMap.put("inventoryUserErp","盘点扫描人");
        headerMap.put("inventoryTime","盘点扫描时间");
        return headerMap;
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
            log.warn("任务号【{}】下扫描记录数据为空！",inventoryTaskId);
        }

        //es获取该运单的待盘信息
        List<InventoryPackage> inventoryPackageList = inventoryInfoService.queryNeedInventoryPackageList(inventoryBaseRequest);
        //遍历结果，组装参数
        for (InventoryPackage inventoryPackage : inventoryPackageList) {
            InventoryWaybillDetail inventoryWaybillDetail = new InventoryWaybillDetail();
            String packageCode = inventoryPackage.getPackageCode();
            if (StringHelper.isEmpty(packageCode)) {
                log.warn("【generateInventoryException】包裹号为空，对象值：{}" , JsonHelper.toJson(inventoryPackage));
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
                        log.error("插入盘点异常表异常！",e);
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

                boolean isNotDirectionException = true;
                //先判断是不是流向异常多货
                //自定义需要判断是否是流向异常
                if (inventoryBaseRequest.getInventoryScope() == 1) {
                    SiteWithDirection receiveSite = packageStatusService.getReceiveSiteByWaybillCode(WaybillUtil.getWaybillCode(packageCode), inventoryBaseRequest.getCreateSiteCode());
                    //获取运单或者包裹的流向，如果流向不在流向列表范围内，判定为流向异常
                    if (receiveSite != null && receiveSite.getDirectionCode() != null) {
                        List<Integer> directionCodeList = inventoryBaseRequest.getDirectionCodeList();
                        //不在盘点流向内，流向异常多货
                        if (! directionCodeList.contains(receiveSite.getDirectionCode())) {
                            //流向异常有实物
                            inventoryException.setExpDesc(InventoryExpDescEnum.DIRECTION_EXCEPTION_MORE.getDesc());
                            //流向异常默认已处理
                            inventoryException.setExpStatus(1);
                            inventoryException.setExpOperateTime(new Date());
                            isNotDirectionException = false;
                        }
                    }
                }
                //如果是流向异常，跳过后面异常判断
                if (isNotDirectionException) {
                    List<String> packageCodeList = new ArrayList<>();
                    packageCodeList.add(packageCode);
                    List<InventoryPackage> inventoryPackages = inventoryInfoService.queryPackageStatusList(inventoryBaseRequest, packageCodeList);
                    if (inventoryPackages != null && ! inventoryPackages.isEmpty()) {
                        Integer statusCode =  inventoryPackages.get(0).getStatusCode();
                        if (PackStatusEnum.SEND.getCode().equals(statusCode)) {
                            //已发货有实物
                            inventoryException.setExpDesc(InventoryExpDescEnum.SEND_MORE.getDesc());
                            inventoryException.setLatestPackStatus(PackStatusEnum.SEND.getDesc());
                        } else if (PackStatusEnum.EXCEPTION.getCode().equals(statusCode)) {
                            //异常外呼有实物
                            inventoryException.setExpDesc(InventoryExpDescEnum.EXCEPTION_MORE.getDesc());
                            inventoryException.setLatestPackStatus(PackStatusEnum.EXCEPTION.getDesc());
                        } else {
                            log.warn("包裹状态妈：{}，不是发货货异常外呼状态！",statusCode.toString());
                        }
                    } else {
                        //无任何操作有实物
                        inventoryException.setExpDesc(InventoryExpDescEnum.NO_OPERATION_MORE.getDesc());
                    }
                }

                try {
                    exceptionCount++;
                    this.inventoryExceptionDao.insert(inventoryException);
                } catch (Exception e) {
                    log.error("插入盘点异常表异常！", e);
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
        params.put("expOperateTime", new Date());
        return inventoryExceptionDao.updateExpStatus(params);
    }

    @Override
    public int syncInventoryExceptionWaybillTrace() {

        int count = 0;
        //盘点少货的包裹号需要查询全程跟踪状态
        //获取该始发站点下的所有少货异常记录
        List<InventoryException> inventoryExceptionList = inventoryExceptionDao.getInventoryLossException();

        for (InventoryException inventoryException : inventoryExceptionList) {
            String packageCode = inventoryException.getPackageCode();
            Integer inventorySiteCode = inventoryException.getInventorySiteCode();
            BaseEntity<List<PackageState>> resultDTO = waybillTraceManager.getAllOperations(packageCode);

            if (resultDTO != null && resultDTO.getData() != null) {
                boolean isCurrOperate = false;
                String statusDesc = null;
                List<PackageState> packageStateList = resultDTO.getData();
                if (packageStateList != null && ! packageStateList.isEmpty()) {
                    for (PackageState packageState : packageStateList) {
                        Integer operateSiteId = packageState.getOperatorSiteId();
                        if (inventorySiteCode.equals(operateSiteId)) {
                            //确定当前操作单位有操作
                            isCurrOperate = true;
                        } else {
                            //满足条件则认定当前单号在当前始发操作后，有了其他站点新的操作全程跟踪
                            if (isCurrOperate) {
                                //认为下游有操作，自动更新盘点异常表
                                String operateTypeName = packageState.getRemark();
                                String operateSiteName = packageState.getOperatorSite();
                                statusDesc = MessageFormat.format("{0}【{1}】", operateSiteName, operateTypeName);
                                break;
                            }
                        }
                    }
                    if (isCurrOperate && statusDesc != null) {
                        Map<String, Object> params = new HashMap<>();
                        List<Long> list = new ArrayList<>(1);
                        list.add(inventoryException.getId());
                        params.put("list", list);
                        params.put("latestPackStatus", statusDesc);
                        params.put("expOperateTime", new Date());
                        params.put("expUserErp", "系统自动处理");
                        inventoryExceptionDao.updateExpStatus(params);
                        count++;
                    } else {
                        log.warn("包裹号【{}】的在下游无全程跟踪操作，无法对少货异常自动处理！",packageCode);
                    }
                } else {
                    log.warn("获取包裹号【{}】无全程跟踪记录，无法对少货异常自动处理！",packageCode);
                }
            } else {
                log.warn("获取包裹号【{}】的全程跟踪结果为空，无法对少货异常自动处理！",packageCode);
            }

        }
        return count;
    }

    private InventoryException convert2InventoryException(InventoryBaseRequest inventoryBaseRequest) {

        InventoryException inventoryException = new InventoryException();
        String inventoryTaskId = inventoryBaseRequest.getInventoryTaskId();
        inventoryException.setInventoryTaskId(inventoryTaskId);
        InventoryTask inventoryTask = inventoryTaskDao.getInventoryTaskInfo(inventoryTaskId);
        if (inventoryTask != null) {
            inventoryException.setOrgId(inventoryTask.getOrgId());
            inventoryException.setOrgName(inventoryTask.getOrgName());
            inventoryException.setInventorySiteCode(inventoryTask.getCreateSiteCode());
            inventoryException.setInventorySiteName(inventoryTask.getCreateSiteName());
            inventoryException.setTaskCreateTime(inventoryTask.getCreateTime());
            inventoryException.setTaskCreateUser(inventoryTask.getCreateUserErp());
            inventoryException.setInventoryScope(inventoryTask.getInventoryScope());
        } else {
            inventoryException.setInventoryTaskId(inventoryBaseRequest.getInventoryTaskId());
            inventoryException.setInventorySiteCode(inventoryBaseRequest.getCreateSiteCode());
            inventoryException.setInventorySiteName(inventoryBaseRequest.getCreateSiteName());
        }
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
            return InventoryExpDescEnum.SORTING_CANCEL_LOSS.getDesc();
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

