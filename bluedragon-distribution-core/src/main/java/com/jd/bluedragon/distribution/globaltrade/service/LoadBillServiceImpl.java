package com.jd.bluedragon.distribution.globaltrade.service;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.LoadBillReportResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReadDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReportDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
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
import com.jd.bluedragon.utils.*;
import com.jd.common.util.StringUtils;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.wss.WaybillQueryWS;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service("loadBillService")
public class LoadBillServiceImpl implements LoadBillService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final int SUCCESS = 1; // report的status,1为成功,2为失败

	private static final String WAREHOUSE_ID = "globalTrade.loadBill.warehouseId"; // 仓库ID

	private static final String DMS_CODE = "globalTrade.loadBill.dmsCode"; // 全球购的专用分拣中心

	private static final String CTNO = "globalTrade.loadBill.ctno"; // 申报海关编码。默认：5165南沙旅检

	private static final String GJNO = "globalTrade.loadBill.gjno"; // 申报国检编码。默认：000069申报地国检

	private static final String TPL = "globalTrade.loadBill.tpl"; // 物流企业编码。默认：京配编号

    private static final String ZHUOZHI_PRELOAD_URL = "http://121.33.205.117:18080/customs/rest/custjson/postwmspredata.do";

    private static final Integer GLOBAL_TRADE_PRELOAD_COUNT_LIMIT = 2000;

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

	/* 运单查询 */
	@Autowired
	@Qualifier("waybillQueryWSProxy")
	private WaybillQueryWS waybillQueryWSProxy;

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int initialLoadBill(String sendCode, Integer userId, String userName) throws Exception{
		String dmsCode = PropertiesHelper.newInstance().getValue(DMS_CODE);
		if (StringUtils.isBlank(dmsCode)) {
			logger.error("LoadBillServiceImpl initialLoadBill with dmsCode is null");
			return 0;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sendCodeList", StringHelper.parseList(sendCode, ","));
		params.put("dmsList", StringHelper.parseList(dmsCode, ","));
		List<SendDetail> sendDetailList = sendDatailReadDao.findBySendCodeAndDmsCode(params);
		if (sendDetailList == null || sendDetailList.size() < 1) {
			logger.info("LoadBillServiceImpl initialLoadBill with the num of SendDetail is 0");
			return 0;
		}
		Map<String, DeliveryPackageD> packageMap = getPacakgeByWaybillCode(sendDetailList);
		logger.info("批次号: " + sendCode + " 的包裹数量为: " + packageMap.size() + " 个.");
		List<LoadBill> loadBillList = resolveLoadBill(sendDetailList, packageMap, userId, userName);
		for (LoadBill lb : loadBillList) {
			// 不存在,则添加;存在,则忽略,更新会影响其他功能的更新操作
			if (loadBillDao.findByPackageBarcode(lb.getPackageBarcode()) == null) {
				loadBillDao.add(lb);
			}
		}
		return loadBillList.size();
	}

	private Map<String, DeliveryPackageD> getPacakgeByWaybillCode(List<SendDetail> sendDetailList) throws Exception {
		//waybillQueryWSProxy.batchGetPackListByCodeList();
		Map<String, DeliveryPackageD> packageMap = new HashMap<String, DeliveryPackageD>();
		int times = 0;
		List<String> waybillCodeList = new ArrayList<String>();
		for(int i = 0 ; i < sendDetailList.size(); i++){
			waybillCodeList.add(sendDetailList.get(i).getWaybillCode());
			times++;
			if(times == 50){
				getPackageFromRemote(packageMap, waybillCodeList);
				times = 0;
				waybillCodeList = new ArrayList<String>();//初始化
			}
			if (sendDetailList.size() % 50 != 0 && i == sendDetailList.size() - 1) {
				getPackageFromRemote(packageMap, waybillCodeList);
			}
		}
		return packageMap;
	}

	private void getPackageFromRemote(Map<String, DeliveryPackageD> packageMap, List<String> waybillCodeList) throws Exception {
		String errorMessage = "调用运单接口,通过运单号获取包裹信息失败";
		try{
			BaseEntity<Map<String, List<DeliveryPackageD>>> result =  waybillQueryWSProxy.batchGetPackListByCodeList(waybillCodeList);
			if (result.getResultCode() == 1) {
				Map<String, List<DeliveryPackageD>> remotePackages = result.getData();
				if(remotePackages != null){
					for(List<DeliveryPackageD> packList : remotePackages.values()){
						for(DeliveryPackageD pack : packList){
							 packageMap.put(pack.getPackageBarcode(), pack);
						}
					}
				}
			} else if(result.getResultCode() == -2) {
				logger.error("调用运单接口, 参数错误");
				throw new Exception("调用运单接口, 参数错误");
			} else {
				logger.error(errorMessage);
				throw new Exception(errorMessage);
			}
		} catch(Exception e) {
			logger.error(errorMessage);
			throw new Exception(errorMessage);
		}
	}

	private List<LoadBill> resolveLoadBill(List<SendDetail> sendDetailList, Map<String, DeliveryPackageD> packageMap, Integer userId, String userName) {
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
			lb.setWarehouseId(PropertiesHelper.newInstance().getValue(WAREHOUSE_ID));
			lb.setCtno(PropertiesHelper.newInstance().getValue(CTNO));
			lb.setGjno(PropertiesHelper.newInstance().getValue(GJNO));
			lb.setTpl(PropertiesHelper.newInstance().getValue(TPL));
			
			//注入打包人信息
			DeliveryPackageD pack = packageMap.get(sd.getPackageBarcode());
			if(pack != null){
				String packWkNo = pack.getPackWkNo();
				if(StringUtils.isNotBlank(packWkNo) && packWkNo.indexOf(":") != -1){
					String[] packUser = packWkNo.split(":");
					if(packUser.length == 2){
						lb.setPackageUserCode(Integer.parseInt(packUser[0]));
						lb.setPackageUser(packUser[1]);
					}
				}
				lb.setPackageTime(DateHelper.parseDateTime(pack.getPackDate()));
			}

			loadBillList.add(lb);
		}
		return loadBillList;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int updateLoadBillStatusByReport(LoadBillReport report) {
		logger.info("更新装载单状态 reportId is " + report.getReportId() + ", orderId is " + report.getOrderId());
		loadBillReportDao.add(report);
		return loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report)); // 更新loadbill的approval_code
	}

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Integer preLoadBill(List<Long> id, String trunkNo) throws Exception {
        List<LoadBill> loadBIlls = loadBillDao.getLoadBills(id);

        if(loadBIlls.size() > GLOBAL_TRADE_PRELOAD_COUNT_LIMIT){
            throw new GlobalTradeException("需要装载的订单数量超过数量限制（" + GLOBAL_TRADE_PRELOAD_COUNT_LIMIT +  ")");
        }

        List<Long> preLoadIds = new ArrayList<Long>();
        for(LoadBill loadBill : loadBIlls){
            if(loadBill.getApprovalCode() != null && loadBill.getApprovalCode() != LoadBill.BEGINNING){
                throw new GlobalTradeException("订单 [" + loadBill.getWaybillCode() + "] 已经在 [" + loadBill.getLoadId() + "] 装载");
            }
            preLoadIds.add(loadBill.getId());
        }

        String preLoadBillId = String.valueOf(loadBillDao.selectPreLoadBillId());
        PreLoadBill preLoadBill = toPreLoadBill(loadBIlls,trunkNo,preLoadBillId);

        logger.error("调用卓志预装载接口数据" + JsonHelper.toJson(preLoadBill));

        ClientRequest request = new ClientRequest(ZHUOZHI_PRELOAD_URL);
        request.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON);
        request.body(javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE,JsonHelper.toJson(preLoadBill));
        ClientResponse<String> response = request.post(String.class);
        if (response.getStatus() == HttpStatus.SC_OK) {
            LoadBillReportResponse response1 = JsonHelper.fromJson(response.getEntity(),LoadBillReportResponse.class);
            if(SUCCESS == response1.getStatus().intValue()){
                logger.error("调用卓志接口预装载成功");
                try {
                    loadBillDao.updatePreLoadBillById(preLoadIds, trunkNo, preLoadBillId);
                }catch (Exception ex){
                    logger.error("预装载更新车牌号和装载单ID失败，原因",ex);
                    throw new GlobalTradeException("预装载操作失败，系统异常");
                }
            }else{
                logger.error("调用卓志接口预装载失败原因" + response1.getNotes());
                throw new GlobalTradeException("调用卓志接口预装载失败" + response1.getNotes());
            }
        } else {
            logger.error("调用卓志预装载接口失败" + response.getStatus());
            throw new GlobalTradeException("调用卓志预装载接口失败" + response.getStatus());
        }

        return loadBIlls.size();
    }

    public PreLoadBill toPreLoadBill(List<LoadBill> loadBills, String trunkNo, String preLoadBillId){
        PreLoadBill preLoadBill = new PreLoadBill();
        preLoadBill.setWarehouseId(PropertiesHelper.newInstance().getValue(WAREHOUSE_ID));
        preLoadBill.setPackgeAmount(String.valueOf(loadBills.size()));
        preLoadBill.setCtno(PropertiesHelper.newInstance().getValue(CTNO));
        preLoadBill.setGjno(PropertiesHelper.newInstance().getValue(GJNO));
        preLoadBill.setTpl(PropertiesHelper.newInstance().getValue(TPL));
        preLoadBill.setTruckNo(trunkNo);
        preLoadBill.setLoadId(preLoadBillId);
        preLoadBill.setGenTime(DateHelper.formatDateTime(new Date()));

        List<LoadBill> loadBillList = new ArrayList<LoadBill>();
        for(LoadBill loadBill : loadBills){
            LoadBill lb = new LoadBill();
            lb.setOrderId(loadBill.getOrderId());
            lb.setPackageTime(loadBill.getPackageTime());
            lb.setPackageUser(loadBill.getPackageUser());
            lb.setWeight(loadBill.getWeight());
            loadBillList.add(lb);
        }

        preLoadBill.setOrderList(loadBillList);
        return preLoadBill;
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
                if(1 == response1.getStatus().intValue()){
                    logger.info("调用卓志接口预装载成功");
                    Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
                    loadBillStatusMap.put("orderId", loadBill.getLoadId());
                    loadBillStatusMap.put("approvalCode", LoadBill.APPLIED);
                    loadBillDao.updateLoadBillStatus(loadBillStatusMap);
                }else if(2 == response1.getStatus().intValue()){
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

    private Map<String, Object> getLoadBillStatusMap(LoadBillReport report) {
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
		loadBillStatusMap.put("loadId", report.getLoadId());
		loadBillStatusMap.put("warehouseId", report.getWarehouseId());
		loadBillStatusMap.put("orderIdList", StringHelper.parseList(report.getOrderId(), ","));
        if (report.getStatus() == SUCCESS) {
            loadBillStatusMap.put("approvalCode", LoadBill.GREENLIGHT);
        } else {
            loadBillStatusMap.put("approvalCode", LoadBill.REDLIGHT);
        }
        return loadBillStatusMap;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<LoadBill> findPageLoadBill(Map<String, Object> params) {
        return loadBillReadDao.findPageLoadBill(params);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Integer findCountLoadBill(Map<String, Object> params) {
        return loadBillReadDao.findCountLoadBill(params);
    }

	@Override
	public JdResponse cancelPreloaded(List<LoadBill> request) {
		try {
			for(LoadBill loadBill : request){
				//取消预装载 重置状态
				loadBillDao.updateCancelLoadBillStatus(loadBill);
				//取消预装载 全程跟踪
				sendTask(loadBill);
				//取消预装载 取消分拣
				
				//取消预装载 取消发货
				deliveryService.dellCancelDeliveryMessage(toSendM(loadBill));
				//取消预装载 操作日志
				addOperationLog(loadBill);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("取消装载单处理异常",e);
			return new JdResponse(JdResponse.CODE_SERVICE_ERROR , JdResponse.MESSAGE_SERVICE_ERROR);
		}
		return new JdResponse(JdResponse.CODE_OK , JdResponse.MESSAGE_OK);
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
    public List<LoadBill> findWaybillInLoadBill(LoadBillReport report){
        Map<String, Object> loadBillStatusMap = new HashMap<String, Object>();
        loadBillStatusMap.put("waybillCode", report.getOrderId());
        loadBillStatusMap.put("boxCode", report.getBoxCode());
        this.logger.info("findWaybillInLoadBill 查询数据库预装在信息 状态");
        List<LoadBill> loadBillList=  loadBillReadDao.findWaybillInLoadBill(loadBillStatusMap);
        return loadBillList;
    }

}
