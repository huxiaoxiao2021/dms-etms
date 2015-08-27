package com.jd.bluedragon.distribution.globaltrade.service;

import java.util.*;

import com.jd.bluedragon.distribution.api.request.LoadBillReportResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.globaltrade.domain.PreLoadBill;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.domain.TaskResult;
import com.jd.bluedragon.utils.DateHelper;
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

import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReadDao;
import com.jd.bluedragon.distribution.globaltrade.dao.LoadBillReportDao;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBill;
import com.jd.bluedragon.distribution.globaltrade.domain.LoadBillReport;
import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.MediaType;

@Service("loadBillService")
public class LoadBillServiceImpl implements LoadBillService {

    private final Log logger = LogFactory.getLog(this.getClass());

    private static final int SUCCESS = 1; // report的status,1为成功,2为失败

    private static final String WAREHOUSE_ID = "globalTrade.loadBill.warehouseId";

    private static final String CTNO = "globalTrade.loadBill.ctno";

    private static final String GJNO = "globalTrade.loadBill.gjno";

    private static final String TPL = "globalTrade.loadBill.tpl";

    private static final String ZHUOZHI_PRELOAD_URL = "http://121.33.205.117:18080/customs/rest/custjson/postwmspredata.do";

    private static final Integer GLOBAL_TRADE_PRELOAD_COUNT_LIMIT = 2000;

    @Autowired
    private LoadBillDao loadBillDao;

    @Autowired
    private LoadBillReadDao loadBillReadDao;

    @Autowired
    private LoadBillReportDao loadBillReportDao;

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    @Autowired
    private SiteService siteService;

    @Override
    public int add(LoadBill loadBill) {
        return 0;
    }

    @Override
    public int update(LoadBill loadBill) {
        return 0;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int initialLoadBill(String sendCode, Integer userId, String userName) {
        List<SendDetail> sendDetailList = sendDatailReadDao.findBySendCode(sendCode);
        if (sendDetailList == null || sendDetailList.size() < 1) {
            logger.info("LoadBillServiceImpl initialLoadBill with the num of SendDetail is 0");
            return 0;
        }
        List<LoadBill> loadBillList = resolveLoadBill(sendDetailList, userId, userName);
        loadBillDao.addBatch(loadBillList);
        return loadBillList.size();
    }

    private List<LoadBill> resolveLoadBill(List<SendDetail> sendDetailList, Integer userId, String userName) {
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
            lb.setLoadId(sd.getWaybillCode());// loadId暂时用WaybillCode
            lb.setWarehouseId(PropertiesHelper.newInstance().getValue(WAREHOUSE_ID));
            lb.setCtno(PropertiesHelper.newInstance().getValue(CTNO));
            lb.setGjno(PropertiesHelper.newInstance().getValue(GJNO));
            lb.setTpl(PropertiesHelper.newInstance().getValue(TPL));
            loadBillList.add(lb);
        }
        return loadBillList;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void updateLoadBillStatusByReport(LoadBillReport report) {
        logger.info("更新装载单状态 reportId is " + report.getReportId() + ", orderId is " + report.getOrderId());
        loadBillReportDao.add(report);
        loadBillDao.updateLoadBillStatus(getLoadBillStatusMap(report)); // 更新loadbill的approval_code
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
                throw new GlobalTradeException("订单 [" + loadBill.getWaybillCode() + "] 已经装载");
            }
            preLoadIds.add(loadBill.getId());
        }

        String preLoadBillId = String.valueOf(loadBillDao.selectPreLoadBillId());
        PreLoadBill preLoadBill = toPreLoadBill(loadBIlls,trunkNo,preLoadBillId);

        logger.error("调用卓志预装载接口数据" + JsonHelper.toJson(preLoadBill));
        RestTemplate template = new RestTemplate();

        ResponseEntity<LoadBillReportResponse> response = template.postForEntity(ZHUOZHI_PRELOAD_URL, preLoadBill, LoadBillReportResponse.class);

        if (response.getStatusCode().value() == HttpStatus.SC_OK) {
            LoadBillReportResponse response1 = response.getBody();
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
            logger.error("调用卓志预装载接口失败" + response.getStatusCode());
            throw new GlobalTradeException("调用卓志预装载接口失败" + response.getStatusCode());
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
        loadBillStatusMap.put("orderId", report.getOrderId());
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

}
