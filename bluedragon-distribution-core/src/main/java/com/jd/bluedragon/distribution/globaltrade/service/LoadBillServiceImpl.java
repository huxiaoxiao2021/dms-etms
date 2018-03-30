package com.jd.bluedragon.distribution.globaltrade.service;

import com.google.common.reflect.TypeToken;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReadDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReportDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillConfig;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.globaltrade.domain.PreLoadBill;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.utils.*;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("loadBillService")
public class LoadBillServiceImpl implements LoadBillService {

    private final Log logger = LogFactory.getLog(this.getClass());

    private static final int SUCCESS = 1; // report的status,1为成功,2为失败

    private static final String LOAD_BILL_CONFIG = "globalTrade.loadBill.config";

    private static final String ZHUOZHI_PRELOAD_URL = PropertiesHelper.newInstance().getValue("globalTrade.preLoadBill.url"); // 卓志预装载接口

    private static final Integer GLOBAL_TRADE_PRELOAD_COUNT_LIMIT = 2000;

    private static final Integer SQL_IN_EXPRESS_LIMIT = 999;

    @Autowired
    private LoadBillDao loadBillDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    private SiteService siteService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    DeliveryService deliveryService;
    @Autowired
    private LoadBillReportDao loadBillReportDao;

    @Autowired
    private LoadBillReadDao loadBillReadDao;

    @Autowired
    private IGenerateObjectId genObjectId;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int initialLoadBill(String sendCode, Integer userId, String userCode, String userName) {
        String loadBillConfigStr = PropertiesHelper.newInstance().getValue(LOAD_BILL_CONFIG);
        if (StringUtils.isBlank(loadBillConfigStr)) {
            logger.error("LoadBillServiceImpl initialLoadBill with loadBillConfig is null");
            return 0;
        }

        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(userCode);
        if (dto != null) {
            Map<Integer, LoadBillConfig> loadBillConfigMap = JsonHelper.fromJsonUseGson(loadBillConfigStr, new TypeToken<Map<Integer, LoadBillConfig>>() {
            }.getType());

            LoadBillConfig loadBillConfig = loadBillConfigMap.get(dto.getSiteCode());
            if (loadBillConfig != null) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("sendCodeList", StringHelper.parseList(sendCode, ","));
                params.put("dmsList", new Integer[]{dto.getSiteCode()});
                List<SendDetail> sendDetailList = sendDatailReadDao.findBySendCodeAndDmsCode(params);
                if (sendDetailList == null || sendDetailList.size() < 1) {
                    logger.info("LoadBillServiceImpl initialLoadBill with the num of SendDetail is 0");
                    return 0;
                }
                List<LoadBill> loadBillList = resolveLoadBill(sendDetailList, loadBillConfig, userId, userName);
                for (LoadBill lb : loadBillList) {
                    // 不存在,则添加;存在,则忽略,更新会影响其他功能的更新操作
                    if (loadBillDao.findByPackageBarcode(lb.getPackageBarcode()) == null) {
                        loadBillDao.add(lb);
                    }
                }
                return loadBillList.size();
            }
        }
        return 0;
    }

    private List<LoadBill> resolveLoadBill(List<SendDetail> sendDetailList, LoadBillConfig loadBillConfig, Integer userId, String userName) {
        if (sendDetailList == null || sendDetailList.size() < 1) {
            return new ArrayList<LoadBill>();
        }
        List<LoadBill> loadBillList = new ArrayList<LoadBill>();
        Map<Integer, String> dmsMap = new HashMap<Integer, String>();
        for (SendDetail sd : sendDetailList) {
            LoadBill lb = new LoadBill();
            // 装载单注入SendDetail的必须字段
            lb.setWaybillCode(sd.getWaybillCode());
            lb.setPackageBarcode(sd.getPackageBarcode());
            lb.setPackageAmount(sd.getPackageNum());
            lb.setOrderId(sd.getWaybillCode());
            // 如果是ECLP订单，则获取商家订单号
            if (SerialRuleUtil.isMatchReceiveWaybillNo(sd.getWaybillCode())) {
                String vendorOrderId = getVendorOrderId(sd.getWaybillCode());
                if (null != vendorOrderId) {
                    lb.setOrderId(vendorOrderId);
                }
            }
            lb.setBoxCode(sd.getBoxCode());
            lb.setDmsCode(sd.getCreateSiteCode());
            lb.setSendTime(sd.getCreateTime()); // 包裹发货数据的创建时间,就是发货时间
            lb.setSendCode(sd.getSendCode());
            lb.setWeight(sd.getWeight());
            lb.setPackageUserCode(sd.getCreateUserCode());
            lb.setPackageUser(sd.getCreateUser());
            lb.setPackageTime(sd.getCreateTime());
            lb.setApprovalCode(LoadBill.BEGINNING); // 装载单初始化状态
            if (dmsMap.containsKey(sd.getCreateSiteCode())) {
                lb.setDmsName(dmsMap.get(sd.getCreateSiteCode()));
            } else {
                BaseStaffSiteOrgDto site = siteService.getSite(sd.getCreateSiteCode());
                if (site != null) {
                    dmsMap.put(sd.getCreateSiteCode(), site.getSiteName());
                    lb.setDmsName(site.getSiteName());
                }
            }
            // 注入装载单其他信息
            lb.setCreateUserCode(userId);
            lb.setCreateUser(userName);
            // 仓库ID
            lb.setWarehouseId(loadBillConfig.getWarehouseId());
            // 申报海关编码
            lb.setCtno(loadBillConfig.getCtno());
            // 申报国检编码
            lb.setGjno(loadBillConfig.getGjno());
            // 物流企业编码
            lb.setTpl(loadBillConfig.getTpl());
            loadBillList.add(lb);
        }
        return loadBillList;
    }


    private String getVendorOrderId(String waybillCode) {
        try {
            return waybillService.getWaybill(waybillCode).getWaybill().getVendorId();
        } catch (Exception e) {
            logger.error(String.format("获取运单[%s]的订单号失败，原因", waybillCode), e);
            return null;
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int updateLoadBillStatusByReport(LoadBillReport report) {
        logger.info("更新装载单状态 reportId is " + report.getReportId() + ", orderId is " + report.getOrderId());
        //将orderId分割,长度不超过500
        List<LoadBillReport> reportList = new ArrayList<LoadBillReport>();
        List<String> orderIdList = new ArrayList<String>();
        report.setOrderId(report.getOrderId().replaceAll(",+", ","));
        Matcher matcher = Pattern.compile("[^,][\\w,]{0,498}[^,]((?=,)|$(?=,*))").matcher(report.getOrderId());
        while (matcher.find()) {
            LoadBillReport subReport = new LoadBillReport();
            String subOrderIds = matcher.group();
            subReport.setReportId(report.getReportId());
            subReport.setLoadId(report.getLoadId());
            subReport.setWarehouseId(report.getWarehouseId());
            subReport.setProcessTime(report.getProcessTime());
            subReport.setStatus(report.getStatus());
            subReport.setNotes(report.getNotes());
            subReport.setOrderId(subOrderIds);
            reportList.add(subReport);
            orderIdList.add("'" + subOrderIds.replaceAll(",", "','") + "'");
        }
        loadBillReportDao.addBatch(reportList);
        /**
         * 装载单状态逻辑:只有装载单下的全部订单放行,装载单状态才能放行
         * 问题:卓志可能会丢失装载单下的部分订单数据,导致装载单放行,但部分订单的状态没有更新为放行(当前逻辑:根据装载单和订单更新状态)
         * 补救措施:增加新的状态(失败),表示丢失的状态. 先将装载单下的所有订单更新为失败,然后将接收到的订单更新为放行.
         */
        loadBillDao.updateLoadBillStatus(getLoadBillFailStatusMap(report, orderIdList));
        return loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report, orderIdList)); // 更新loadbill的approval_code
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @JProfiler(jKey = "DMSCORE.LoadBillServiceImpl.preLoadBill", mState = JProEnum.TP)
    public Integer preLoadBill(List<Long> id, String userCode, String trunkNo) throws Exception {
        String loadBillConfigStr = PropertiesHelper.newInstance().getValue(LOAD_BILL_CONFIG);
        if (StringUtils.isBlank(loadBillConfigStr)) {
            logger.error("LoadBillServiceImpl preLoadBill with loadBillConfig is null");
            return 0;
        }

        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(userCode);
        if (dto != null) {
            Map<Integer, LoadBillConfig> loadBillConfigMap = JsonHelper.fromJsonUseGson(loadBillConfigStr, new TypeToken<Map<Integer, LoadBillConfig>>() {
            }.getType());
            LoadBillConfig loadBillConfig = loadBillConfigMap.get(dto.getSiteCode());
            if (loadBillConfig != null) {
                List<LoadBill> loadBIlls;
                CallerInfo info = Profiler.registerInfo("DMSCORE.LoadBillServiceImpl.selectLoadBill", false, true);
                try {
                    loadBIlls = selectLoadbillById(id); //每次取#{SQL_IN_EXPRESS_LIMIT}
                } catch (Exception ex) {
                    logger.error("获取预装载数据失败", ex);
                    Profiler.businessAlarm("DMSCORE.LoadBillServiceImpl.selectLoadBillAlarm", "selectLoadBillById出错");
                    Profiler.functionError(info);
                    throw new GlobalTradeException("获取预装载数据失败，系统异常");
                } finally {
                    Profiler.registerInfoEnd(info);
                }

                if (loadBIlls.size() > GLOBAL_TRADE_PRELOAD_COUNT_LIMIT) {
                    throw new GlobalTradeException("需要装载的订单数量超过数量限制（" + GLOBAL_TRADE_PRELOAD_COUNT_LIMIT + ")");
                }

                Set<String> orderIdSet = new HashSet<String>();

                for (LoadBill loadBill : loadBIlls) {
                    if (loadBill.getApprovalCode() != null && loadBill.getApprovalCode() != LoadBill.BEGINNING
                            && loadBill.getApprovalCode() != LoadBill.FAILED) {
                        throw new GlobalTradeException("订单 [" + loadBill.getWaybillCode() + "] 已经在装载单 [" + loadBill.getLoadId() + "] 装载");
                    }
                    // 通过订单号去除重复
                    orderIdSet.add(loadBill.getOrderId());
                }

                String preLoadBillId = String.valueOf(genObjectId.getObjectId(LoadBill.class.getName()));
                PreLoadBill preLoadBill = toPreLoadBill(loadBIlls, loadBillConfig, trunkNo, preLoadBillId);

                logger.error("调用卓志预装载接口数据" + JsonHelper.toJson(preLoadBill));

                ClientResponse<String> response = getResponse(preLoadBill);
                CallerInfo info1 = Profiler.registerInfo("DMSCORE.LoadBillServiceImpl.updateLoadBillStatus", false, true);
                if (response.getStatus() == HttpStatus.SC_OK) {
                    LoadBillReportResponse response1 = JsonHelper.fromJson(response.getEntity(), LoadBillReportResponse.class);
                    if (SUCCESS == response1.getStatus().intValue()) {
                        logger.error("调用卓志接口预装载成功");
                        try {
                            this.updateLoadBillStatusByOrderIds(new ArrayList(orderIdSet), trunkNo, preLoadBillId, LoadBill.APPLIED);
                        } catch (Exception ex) {
                            logger.error("预装载更新车牌号和装载单ID失败，原因", ex);
                            throw new GlobalTradeException("预装载操作失败，系统异常");
                        }
                    } else {
                        logger.error("调用卓志接口预装载失败原因" + response1.getNotes());
                        throw new GlobalTradeException("调用卓志接口预装载失败" + response1.getNotes());
                    }
                } else {
                    logger.error("调用卓志预装载接口失败" + response.getStatus());
                    Profiler.businessAlarm("DMSCORE.LoadBillServiceImpl.updateLoadBillStatusAlarm", "调用卓志预装载接口出错");
                    throw new GlobalTradeException("调用卓志预装载接口失败" + response.getStatus());
                }
                Profiler.registerInfoEnd(info1);
                return loadBIlls.size();
            }
        }
        return 0;
    }

    public ClientResponse<String> getResponse(PreLoadBill preLoadBill) {
        CallerInfo info = Profiler.registerInfo("DMSCORE.LoadBillServiceImpl.getResponse", false, true);
        ClientRequest request = new ClientRequest(ZHUOZHI_PRELOAD_URL);
        request.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON);
        request.body(javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE, JsonHelper.toJson(preLoadBill));
        ClientResponse<String> response = null;
        try {
            response = request.post(String.class);
        } catch (Exception e) {
            Profiler.functionError(info);
            e.printStackTrace();
        } finally {
            Profiler.registerInfoEnd(info);
        }
        return response;
    }

    public PreLoadBill toPreLoadBill(List<LoadBill> loadBills, LoadBillConfig loadBillConfig, String trunkNo, String preLoadBillId) {
        PreLoadBill preLoadBill = new PreLoadBill();
        preLoadBill.setWarehouseId(loadBillConfig.getWarehouseId());
        preLoadBill.setCtno(loadBillConfig.getCtno());
        preLoadBill.setGjno(loadBillConfig.getGjno());
        preLoadBill.setTpl(loadBillConfig.getTpl());
        preLoadBill.setPackgeAmount(String.valueOf(loadBills.size()));
        preLoadBill.setTruckNo(trunkNo);
        preLoadBill.setLoadId(preLoadBillId);
        preLoadBill.setGenTime(DateHelper.formatDateTime(new Date()));

        List<LoadBill> loadBillList = new ArrayList<LoadBill>();
        for (LoadBill loadBill : loadBills) {
            if (contains(loadBillList, loadBill)) continue;
            LoadBill lb = new LoadBill();
            lb.setOrderId(loadBill.getOrderId());
            lb.setPackageTime(loadBill.getPackageTime());
            lb.setPackageUser(loadBill.getPackageUser());
            lb.setWeight(loadBill.getWeight());
            loadBillList.add(lb);
        }

        preLoadBill.setOrderCount(loadBillList.size());
        preLoadBill.setOrderList(loadBillList);
        return preLoadBill;
    }

    private Boolean contains(List<LoadBill> loadBillList, LoadBill loadBill) {
        for (LoadBill lb : loadBillList) {
            if (lb.getOrderId().equals(loadBill.getOrderId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public TaskResult dealPreLoadBillTask(Task task) {
        LoadBill loadBill = JsonHelper.fromJson(task.getBody(), LoadBill.class);
        if (null == loadBill) {
            return TaskResult.FAILED;
        }

        try {
            RestTemplate template = new RestTemplate();
            ResponseEntity<LoadBillReportResponse> response = template.postForEntity(ZHUOZHI_PRELOAD_URL, loadBill, LoadBillReportResponse.class);
            if (response.getStatusCode().value() == 200) {
                LoadBillReportResponse response1 = response.getBody();
                if (1 == response1.getStatus().intValue()) {
                    logger.info("调用卓志接口预装载成功");
                    Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
                    loadBillStatusMap.put("orderId", loadBill.getLoadId());
                    loadBillStatusMap.put("approvalCode", LoadBill.APPLIED);
                    loadBillDao.updateLoadBillStatus(loadBillStatusMap);
                } else if (2 == response1.getStatus().intValue()) {
                    logger.info("调用卓志接口预装载失败原因" + response1.getNotes());
                    return TaskResult.REPEAT;
                }
            } else {
                logger.error("调用卓志预装载接口失败" + response.getStatusCode());
                return TaskResult.REPEAT;
            }
        } catch (Exception ex) {
            logger.error("调用卓志预装载接口失败", ex);
            return TaskResult.REPEAT;
        }

        return TaskResult.SUCCESS;
    }

    private Map<String, Object> getLoadBillFailStatusMap(LoadBillReport report, List<String> orderIdList) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("loadIdList", StringHelper.parseList(report.getLoadId(), ","));
        loadBillStatusMap.put("warehouseId", report.getWarehouseId());
        loadBillStatusMap.put("ciqCheckFlag", report.getCiqCheckFlag());
        loadBillStatusMap.put("custBillNo", report.getCustBillNo());
        loadBillStatusMap.put("approvalCode", LoadBill.FAILED);
        return loadBillStatusMap;
    }

    private Map<String, Object> getLoadBillStatusMap(LoadBillReport report, List<String> orderIdList) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("loadIdList", StringHelper.parseList(report.getLoadId(), ","));
        loadBillStatusMap.put("warehouseId", report.getWarehouseId());
        /****更新全部为失败时已经设置过以下两个字段，此处无需重复设置****/
//		loadBillStatusMap.put("ciqCheckFlag", report.getCiqCheckFlag());
//		loadBillStatusMap.put("custBillNo", report.getCustBillNo());
        loadBillStatusMap.put("orderIdList", orderIdList);
        if (report.getStatus() == SUCCESS) {
            loadBillStatusMap.put("approvalCode", LoadBill.GREENLIGHT);
        } else {
            loadBillStatusMap.put("approvalCode", LoadBill.REDLIGHT);
        }
        return loadBillStatusMap;
    }

    @Override
    public List<LoadBill> findPageLoadBill(Map<String, Object> params) {
        return loadBillReadDao.findPageLoadBill(params);
    }

    @Override
    public Integer findCountLoadBill(Map<String, Object> params) {
        return loadBillReadDao.findCountLoadBill(params);
    }

    @Override
    public JdResponse cancelPreloaded(List<LoadBill> request) {
        try {
            for (LoadBill loadBill : request) {
                //取消预装载 重置状态
                loadBillDao.updateCancelLoadBillStatus(loadBill);
                //取消预装载 全程跟踪
                sendTask(loadBill);
                //取消预装载 取消分拣

                //取消预装载 取消发货
                deliveryService.dellCancelDeliveryMessage(toSendM(loadBill), false);
                //取消预装载 操作日志
                addOperationLog(loadBill);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("取消装载单处理异常", e);
            return new JdResponse(JdResponse.CODE_SERVICE_ERROR, JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

    private void sendTask(LoadBill loadBill) {
        WaybillStatus tWaybillStatus = new WaybillStatus();
        tWaybillStatus.setOperator(loadBill.getPackageUser());
        tWaybillStatus.setOperatorId(loadBill.getPackageUserCode());
        tWaybillStatus.setOperateTime(loadBill.getApprovalTime());
        tWaybillStatus.setWaybillCode(loadBill.getWaybillCode());
        tWaybillStatus.setCreateSiteCode(loadBill.getDmsCode());
        tWaybillStatus.setCreateSiteName(loadBill.getDmsName());
        tWaybillStatus.setOperateType(WaybillStatus.WAYBILL_TRACK_CANCLE_LOADBILL);
        taskService.add(this.toTask(tWaybillStatus));
    }

    private Task toTask(WaybillStatus tWaybillStatus) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_POP);
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword2(String.valueOf(tWaybillStatus.getOperateType()));
        task.setKeyword1(tWaybillStatus.getWaybillCode());
        task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
        task.setBody(JsonHelper.toJson(tWaybillStatus));
        task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
        task.setOwnSign(BusinessHelper.getOwnSign());
        StringBuffer fingerprint = new StringBuffer();
        fingerprint
                .append(tWaybillStatus.getCreateSiteCode())
                .append("_")
                .append((tWaybillStatus.getReceiveSiteCode() == null ? "-1"
                        : tWaybillStatus.getReceiveSiteCode())).append("_")
                .append(tWaybillStatus.getOperateType()).append("_")
                .append(tWaybillStatus.getWaybillCode()).append("_")
                .append(tWaybillStatus.getOperateTime());
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        return task;
    }

    /**
     * 插入pda操作日志表
     *
     * @param loadBill
     */
    private void addOperationLog(LoadBill loadBill) {
        OperationLog operationLog = new OperationLog();
        operationLog.setCreateSiteCode(loadBill.getDmsCode());
        operationLog.setCreateTime(loadBill.getApprovalTime());
        operationLog.setCreateUser(loadBill.getPackageUser());
        operationLog.setCreateUserCode(loadBill.getPackageUserCode());
        operationLog.setLogType(OperationLog.LOG_TYPE_CAN_GLOBAL);
        operationLog.setOperateTime(loadBill.getApprovalTime());
        operationLog.setPackageCode(loadBill.getPackageBarcode());
        operationLog.setWaybillCode(loadBill.getWaybillCode());
        operationLogService.add(operationLog);
    }

    private SendM toSendM(LoadBill loadBill) {
        SendM sendM = new SendM();
        sendM.setBoxCode(loadBill.getWaybillCode());
        sendM.setCreateSiteCode(loadBill.getDmsCode());
        sendM.setUpdaterUser(loadBill.getPackageUser());
        sendM.setUpdateUserCode(loadBill.getPackageUserCode());
        sendM.setUpdateTime(new Date());
        sendM.setYn(0);
        return sendM;
    }

    @Override
    public LoadBill findLoadbillByID(Long id) {
        // TODO Auto-generated method stub
        return loadBillDao.findLoadbillByID(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public List<LoadBill> findWaybillInLoadBill(LoadBillReport report) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("waybillCode", BusinessHelper.getWaybillCode(report.getOrderId()));
        loadBillStatusMap.put("boxCode", report.getBoxCode());
        this.logger.info("findWaybillInLoadBill 查询数据库预装在信息 状态");
        List<LoadBill> loadBillList = loadBillReadDao.findWaybillInLoadBill(loadBillStatusMap);
        return loadBillList;
    }

    /**
     * 分批次查询预装载数据 SQL_IN_EXPRESS_LIMIT
     *
     * @param id
     * @return
     * @see #SQL_IN_EXPRESS_LIMIT
     */
    public List<LoadBill> selectLoadbillById(List<Long> id) {
        List<LoadBill> loadBills = new ArrayList<LoadBill>();
        Map<Integer, List<Long>> splitLoadbill = splitLoadbillId(id);
        for (Iterator<Integer> iterator = splitLoadbill.keySet().iterator(); iterator.hasNext(); ) {
            Integer key = iterator.next();
            List<Long> loadId = splitLoadbill.get(key);
            loadBills.addAll(loadBillDao.getLoadBills(loadId));
        }
        return loadBills;
    }


    /**
     * 分批次更新预装载数据 SQL_IN_EXPRESS_LIMIT
     *
     * @param id
     * @param trunkNo
     * @param preLoadId
     * @param status
     * @return
     * @see #SQL_IN_EXPRESS_LIMIT
     */
    public Integer updateLoadbillStatusById(List<Long> id, String trunkNo, String preLoadId, Integer status) {
        Integer effectCount = 0;
        Map<Integer, List<Long>> splitLoadbill = splitLoadbillId(id);
        for (Iterator<Integer> iterator = splitLoadbill.keySet().iterator(); iterator.hasNext(); ) {
            Integer key = iterator.next();
            List<Long> loadId = splitLoadbill.get(key);
            effectCount += loadBillDao.updatePreLoadBillById(loadId, trunkNo, preLoadId, status);
        }
        return effectCount;
    }

    public Integer updateLoadBillStatusByOrderIds(List<String> orderIds, String trunkNo, String preLoadId, Integer status) {
        Integer effectCount = 0;
        Map<Integer, List<String>> splitLoadBill = splitLoadBillByWaybillCode(orderIds);
        for (Iterator<Integer> iterator = splitLoadBill.keySet().iterator(); iterator.hasNext(); ) {
            Integer key = iterator.next();
            List<String> orderIdList = splitLoadBill.get(key);
            effectCount += loadBillDao.updatePreLoadBillByOrderIds(orderIdList, trunkNo, preLoadId, status);
        }
        return effectCount;
    }

    /**
     * 把指定的ID列表分割成 SQL_IN_EXPRESS_LIMIT 大小，避免SQL IN语句数量限制
     *
     * @param id
     * @return
     * @see #SQL_IN_EXPRESS_LIMIT
     */
    public Map<Integer, List<Long>> splitLoadbillId(List<Long> id) {
        Integer limit = SQL_IN_EXPRESS_LIMIT;
        Integer index = 0;
        Integer arrayIndex = 0;
        List<Long> subList;
        Map<Integer, List<Long>> splitedLoadBillid = new HashMap<Integer, List<Long>>();
        for (; ; ) {
            try {
                subList = id.subList(index, index + limit);
                if (!subList.isEmpty()) {
                    splitedLoadBillid.put(arrayIndex, subList);
                }
            } catch (IndexOutOfBoundsException ex) {
                subList = id.subList(index, id.size());
                if (!subList.isEmpty()) {
                    splitedLoadBillid.put(arrayIndex, subList);
                }
                break;
            }
            index = index + limit;
            arrayIndex++;
        }
        return splitedLoadBillid;
    }

    /**
     * 把指定的ID列表分割成 SQL_IN_EXPRESS_LIMIT 大小，避免SQL IN语句数量限制
     *
     * @param waybillCodes
     * @return
     * @see #SQL_IN_EXPRESS_LIMIT
     */
    public Map<Integer, List<String>> splitLoadBillByWaybillCode(List<String> waybillCodes) {
        Integer limit = SQL_IN_EXPRESS_LIMIT;
        Integer index = 0;
        Integer arrayIndex = 0;
        List<String> subList;
        Map<Integer, List<String>> splitedLoadBillid = new HashMap<Integer, List<String>>();
        for (; ; ) {
            try {
                subList = waybillCodes.subList(index, index + limit);
                if (!subList.isEmpty()) {
                    splitedLoadBillid.put(arrayIndex, subList);
                }
            } catch (IndexOutOfBoundsException ex) {
                subList = waybillCodes.subList(index, waybillCodes.size());
                if (!subList.isEmpty()) {
                    splitedLoadBillid.put(arrayIndex, subList);
                }
                break;
            }
            index = index + limit;
            arrayIndex++;
        }
        return splitedLoadBillid;
    }

}
