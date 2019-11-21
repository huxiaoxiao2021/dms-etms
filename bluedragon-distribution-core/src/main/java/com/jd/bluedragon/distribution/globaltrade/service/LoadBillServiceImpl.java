package com.jd.bluedragon.distribution.globaltrade.service;

import com.google.common.reflect.TypeToken;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
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
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.httpclient.HttpStatus;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int SUCCESS = 1; // report的status,1为成功,2为失败

    private static final String LOAD_BILL_CONFIG = "globalTrade.loadBill.config";

    private static final String ZHUOZHI_PRELOAD_URL = PropertiesHelper.newInstance().getValue("globalTrade.preLoadBill.url"); // 卓志预装载接口

    private static final Integer GLOBAL_TRADE_PRELOAD_COUNT_LIMIT = 2000;

    private static final Integer SQL_IN_EXPRESS_LIMIT = 999;

    private final static int BATCH_ADD_SIZE = 1000;

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
    WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    @JProfiler(jKey = "DMSWEB.LoadBillServiceImpl.initialLoadBill",mState = JProEnum.TP)
    public int initialLoadBill(String sendCode, Integer userId, String userCode, String userName) {
        String loadBillConfigStr = PropertiesHelper.newInstance().getValue(LOAD_BILL_CONFIG);
        if (StringUtils.isBlank(loadBillConfigStr)) {
            log.warn("LoadBillServiceImpl initialLoadBill with loadBillConfig is null");
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
                    log.warn("[全球购]初始化-LoadBillServiceImpl initialLoadBill with the num of SendDetail is 0");
                    return 0;
                }
                return this.doInitial(sendDetailList, loadBillConfig, userId, userName);
            }
        }
        return 0;
    }

//    @Override
//    public LoadBill getSuccessPreByOrderId(String orderId) {
//        Map<String, Object> parameter = new HashMap<String, Object>();
//        List<Integer> approvalCodes = new ArrayList<Integer>();
//        approvalCodes.add(LoadBill.APPLIED);
//        approvalCodes.add(LoadBill.GREENLIGHT);
//        approvalCodes.add(LoadBill.REDLIGHT);
//        parameter.put("approvalCodes", approvalCodes);
//        parameter.put("orderId", orderId);
//        return loadBillDao.findOneByParameter(parameter);
//    }


    public LoadBill getSuccessPreByWaybillCode(String waybillCode) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        List<Integer> approvalCodes = new ArrayList<Integer>();
        approvalCodes.add(LoadBill.APPLIED);
        approvalCodes.add(LoadBill.GREENLIGHT);
        approvalCodes.add(LoadBill.REDLIGHT);
        parameter.put("approvalCodes", approvalCodes);
        parameter.put("waybillCode", waybillCode);
        return loadBillDao.findOneByParameter(parameter);
    }


    /**
     * 根据发货明细数据信息和配置信息初始化数据
     *
     * @param sendDetailList
     * @param loadBillConfig
     * @param userId
     * @param userName
     * @return
     */
    private int doInitial(List<SendDetail> sendDetailList, LoadBillConfig loadBillConfig, Integer userId, String userName) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.LoadBillServiceImpl.doInitial", false, true);
        long start = System.currentTimeMillis();
        List<LoadBill> addList = new ArrayList<LoadBill>();
        // 站点信息缓存Cache
        Map<Integer, String> dmsCacheMap = new HashMap<Integer, String>();
        // 预装载信息缓存运单号Cache
        Map<String, Boolean> preWaybillCodeCache = new HashMap<String, Boolean>();
        for (SendDetail sendDetail : sendDetailList) {
            LoadBill lb = this.resolveLoadBill(sendDetail, loadBillConfig, userId, userName, dmsCacheMap);
            // 判断该包裹是否已初始化过， 若已初始化则无需处理
            if (loadBillDao.findByPackageBarcode(lb.getPackageBarcode()) == null) {
                // 判断包裹数据量 若一单一件则无需判断是否已预装载过 仅一单多件时需要判断
                if (sendDetail.getPackageNum() != 1) { //一单多件
                    // 已预装载缓存 不存在时查库确认是否已装载 存在时直接剔除
                    Boolean isPre = preWaybillCodeCache.get(lb.getWaybillCode());
                    if (isPre == null) { // 不存在时需要查库
                        // 根据运单号查询 该订单号下是否有其他包裹已预装载
                        if (this.getSuccessPreByWaybillCode(lb.getWaybillCode()) == null) { //未装载
                            preWaybillCodeCache.put(lb.getWaybillCode(), Boolean.FALSE);
                            addList.add(lb);
                        } else { // 已装载 剔除
                            preWaybillCodeCache.put(lb.getWaybillCode(), Boolean.TRUE);
                        }
                    } else if (isPre == Boolean.FALSE) {
                        addList.add(lb);
                    }
                } else { // 一单一件
                    addList.add(lb);
                }
            }
        }
        log.debug("[全球购]-[初始化]-构建可新增loadBill对象共{}条，耗时:{}",sendDetailList.size(), (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        this.batchAdd(addList);
        log.debug("[全球购]-[初始化]-执行入库共{}条，耗时:{}",addList.size(), (System.currentTimeMillis() - start));
        Profiler.registerInfoEnd(info);
        return addList.size();
    }

    /**
     * 批量新增
     *
     * @param loadBillList
     */
    private void batchAdd(List<LoadBill> loadBillList) {
        int size = loadBillList.size();
        if (size > BATCH_ADD_SIZE) {
            int mod = size % BATCH_ADD_SIZE;
            int times = size / BATCH_ADD_SIZE;
            if (mod > 0){
                times ++;
            }
            for (int i = 0; i < times; i++) {
                int start = i * BATCH_ADD_SIZE;
                int end = start + BATCH_ADD_SIZE;
                if (end > size) {
                    end = size;
                }
                loadBillDao.batchAdd(loadBillList.subList(start, end));
            }
        } else {
            loadBillDao.batchAdd(loadBillList);
        }
    }

    private LoadBill resolveLoadBill(SendDetail sd, LoadBillConfig loadBillConfig, Integer userId, String userName, Map<Integer, String> dmsMap) {
        LoadBill lb = new LoadBill();
        // 装载单注入SendDetail的必须字段
        lb.setWaybillCode(sd.getWaybillCode());
        lb.setPackageBarcode(sd.getPackageBarcode());
        lb.setPackageAmount(sd.getPackageNum());

        lb.setOrderId(waybillQueryManager.getOrderCodeByWaybillCode(sd.getWaybillCode(),true));

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
        return lb;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int updateLoadBillStatusByReport(LoadBillReport report) {
        log.debug("更新装载单状态 reportId is {}, waybillCode is {}",report.getReportId(), report.getWaybillCode());
        //将waybillCode分割,长度不超过500
        List<LoadBillReport> reportList = new ArrayList<LoadBillReport>();
        List<String> waybillCodeList = new ArrayList<String>();
        report.setWaybillCode(report.getWaybillCode().replaceAll(",+", ","));
        Matcher matcher = Pattern.compile("[^,][\\w,]{0,498}[^,]((?=,)|$(?=,*))").matcher(report.getWaybillCode());
        while (matcher.find()) {
            LoadBillReport subReport = new LoadBillReport();
            String subWaybillCodes = matcher.group();
            subReport.setReportId(report.getReportId());
            subReport.setLoadId(report.getLoadId());
            subReport.setWarehouseId(report.getWarehouseId());
            subReport.setProcessTime(report.getProcessTime());
            subReport.setStatus(report.getStatus());
            subReport.setNotes(report.getNotes());
            subReport.setWaybillCode(subWaybillCodes);
            reportList.add(subReport);
            waybillCodeList.add("'" + subWaybillCodes.replaceAll(",", "','") + "'");
        }
        loadBillReportDao.addBatch(reportList);
        /**
         * 装载单状态逻辑:只有装载单下的全部订单放行,装载单状态才能放行
         * 问题:卓志可能会丢失装载单下的部分订单数据,导致装载单放行,但部分订单的状态没有更新为放行(当前逻辑:根据装载单和订单更新状态)
         * 补救措施:增加新的状态(失败),表示丢失的状态. 先将装载单下的所有订单更新为失败,然后将接收到的订单更新为放行.
         */
        loadBillDao.updateLoadBillStatus(getLoadBillFailStatusMap(report));
        return loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report, waybillCodeList)); // 更新loadbill的approval_code
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @JProfiler(jKey = "DMSCORE.LoadBillServiceImpl.preLoadBill", mState = JProEnum.TP)
    public Integer preLoadBill(List<Long> id, String userCode, String trunkNo) throws Exception {
        String loadBillConfigStr = PropertiesHelper.newInstance().getValue(LOAD_BILL_CONFIG);
        if (StringUtils.isBlank(loadBillConfigStr)) {
            log.warn("LoadBillServiceImpl preLoadBill with loadBillConfig is null");
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
                    log.error("获取预装载数据失败:{}",id, ex);
                    Profiler.businessAlarm("DMSCORE.LoadBillServiceImpl.selectLoadBillAlarm", "selectLoadBillById出错");
                    Profiler.functionError(info);
                    throw new GlobalTradeException("获取预装载数据失败，系统异常");
                } finally {
                    Profiler.registerInfoEnd(info);
                }

                if (loadBIlls.size() > GLOBAL_TRADE_PRELOAD_COUNT_LIMIT) {
                    throw new GlobalTradeException("需要装载的订单数量超过数量限制（" + GLOBAL_TRADE_PRELOAD_COUNT_LIMIT + ")");
                }

                Set<String> waybillCodeSet = new HashSet<String>();

                for (LoadBill loadBill : loadBIlls) {
                    if (loadBill.getApprovalCode() != null && loadBill.getApprovalCode() != LoadBill.BEGINNING
                            && loadBill.getApprovalCode() != LoadBill.FAILED) {
                        throw new GlobalTradeException("订单 [" + loadBill.getWaybillCode() + "] 已经在装载单 [" + loadBill.getLoadId() + "] 装载");
                    }
                    // 通过运单号去除重复
                    waybillCodeSet.add(loadBill.getWaybillCode());
                }

                String preLoadBillId = String.valueOf(genObjectId.getObjectId(LoadBill.class.getName()));
                PreLoadBill preLoadBill = toPreLoadBill(loadBIlls, loadBillConfig, trunkNo, preLoadBillId);

                if(log.isDebugEnabled()){
                    log.debug("调用卓志预装载接口数据{}" , JsonHelper.toJson(preLoadBill));
                }

                ClientResponse<String> response = getResponse(preLoadBill);
                CallerInfo info1 = Profiler.registerInfo("DMSCORE.LoadBillServiceImpl.updateLoadBillStatus", false, true);
                if (response.getStatus() == HttpStatus.SC_OK) {
                    LoadBillReportResponse response1 = JsonHelper.fromJson(response.getEntity(), LoadBillReportResponse.class);
                    if (SUCCESS == response1.getStatus().intValue()) {
                        log.debug("调用卓志接口预装载成功");
                        try {
                            this.updateLoadBillStatusByWaybillCodes(new ArrayList(waybillCodeSet), trunkNo, preLoadBillId, LoadBill.APPLIED);
                        } catch (Exception ex) {
                            log.error("预装载更新车牌号和装载单ID失败，原因", ex);
                            throw new GlobalTradeException("预装载操作失败，系统异常");
                        }
                    } else {
                        log.warn("调用卓志接口预装载失败原因{}" , response1.getNotes());
                        throw new GlobalTradeException("调用卓志接口预装载失败" + response1.getNotes());
                    }
                } else {
                    log.warn("调用卓志预装载接口失败:{}", response.getStatus());
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
            lb.setOrderId(loadBill.getOrderId()); //设置订单号 现在先并行
            lb.setLogisticsNo(loadBill.getWaybillCode()); //设置物流运单号
            lb.setPackageTime(loadBill.getPackageTime()); //设置打包时间
            lb.setPackageUser(loadBill.getPackageUser()); //设置打包人
            lb.setWeight(loadBill.getWeight()); //设置包裹重量
            loadBillList.add(lb);
        }

        preLoadBill.setOrderCount(loadBillList.size());
        preLoadBill.setOrderList(loadBillList);
        return preLoadBill;
    }

    private Boolean contains(List<LoadBill> loadBillList, LoadBill loadBill) {
        for (LoadBill lb : loadBillList) {
            if (lb.getLogisticsNo().equals(loadBill.getWaybillCode())) {
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
                    log.debug("调用卓志接口预装载成功");
                    Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
                    loadBillStatusMap.put("orderId", loadBill.getLoadId());
                    loadBillStatusMap.put("approvalCode", LoadBill.APPLIED);
                    loadBillDao.updateLoadBillStatus(loadBillStatusMap);
                } else if (2 == response1.getStatus().intValue()) {
                    log.warn("调用卓志接口预装载失败原因:{}" , response1.getNotes());
                    return TaskResult.REPEAT;
                }
            } else {
                log.warn("调用卓志预装载接口失败:{}" , response.getStatusCode());
                return TaskResult.REPEAT;
            }
        } catch (Exception ex) {
            log.error("调用卓志预装载接口失败:{}",JsonHelper.toJson(task), ex);
            return TaskResult.REPEAT;
        }

        return TaskResult.SUCCESS;
    }

    private Map<String, Object> getLoadBillFailStatusMap(LoadBillReport report) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("loadIdList", StringHelper.parseList(report.getLoadId(), ","));
        loadBillStatusMap.put("warehouseId", report.getWarehouseId());
        loadBillStatusMap.put("ciqCheckFlag", report.getCiqCheckFlag());
        loadBillStatusMap.put("custBillNo", report.getCustBillNo());
        loadBillStatusMap.put("approvalCode", LoadBill.FAILED);
        return loadBillStatusMap;
    }

    private Map<String, Object> getLoadBillStatusMap(LoadBillReport report, List<String> waybillCodeList) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("loadIdList", StringHelper.parseList(report.getLoadId(), ","));
        loadBillStatusMap.put("warehouseId", report.getWarehouseId());
        /****更新全部为失败时已经设置过以下两个字段，此处无需重复设置****/
//		loadBillStatusMap.put("ciqCheckFlag", report.getCiqCheckFlag());
//		loadBillStatusMap.put("custBillNo", report.getCustBillNo());
        loadBillStatusMap.put("waybillCodeList", waybillCodeList);
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
            log.error("取消装载单处理异常", e);
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
        loadBillStatusMap.put("waybillCode", WaybillUtil.getWaybillCode(report.getWaybillCode()));
        loadBillStatusMap.put("boxCode", report.getBoxCode());
        this.log.debug("findWaybillInLoadBill 查询数据库预装在信息 状态");
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
     * 根据运单号集合更新装载单状态
     * @param waybillCodes
     * @param trunkNo
     * @param preLoadId
     * @param status
     * @return
     */
    public Integer updateLoadBillStatusByWaybillCodes(List<String> waybillCodes, String trunkNo, String preLoadId, Integer status) {
        Integer effectCount = 0;
        Map<Integer, List<String>> splitLoadBill = splitLoadBillByWaybillCode(waybillCodes);
        for (Iterator<Integer> iterator = splitLoadBill.keySet().iterator(); iterator.hasNext(); ) {
            Integer key = iterator.next();
            List<String> waybillCodeList = splitLoadBill.get(key);
            effectCount += loadBillDao.updatePreLoadBillByWaybillCodes(waybillCodeList, trunkNo, preLoadId, status);
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
        Integer total = id.size();
        Integer arrayIndex = 0;
        Map<Integer, List<Long>> splitedLoadBillid = new HashMap<Integer, List<Long>>();
        for (int index = 0; index < total; index = index + limit) {
            List<Long> subList;
            int end = index + limit;
            if(end > total){
                subList = id.subList(index, total);
            }else{
                subList = id.subList(index, end);
            }
            if (subList != null && !subList.isEmpty()) {
                splitedLoadBillid.put(arrayIndex, subList);
            }
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
        Integer total = waybillCodes.size();
        Integer arrayIndex = 0;
        Map<Integer, List<String>> splitedLoadBillid = new HashMap<Integer, List<String>>();
        for (int index = 0; index < total; index = index + limit) {
            List<String> subList;
            int end = index + limit;
            if(end > total){
                subList = waybillCodes.subList(index, total);
            }else{
                subList = waybillCodes.subList(index, end);
            }
            if (subList != null && !subList.isEmpty()) {
                splitedLoadBillid.put(arrayIndex, subList);
            }
            arrayIndex++;
        }
        return splitedLoadBillid;
    }

}
