package com.jd.bluedragon.distribution.globaltrade.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.jd.jsf.gd.util.StringUtils;

@Service("loadBillService")
public class LoadBillServiceImpl implements LoadBillService {

	private final Log logger = LogFactory.getLog(this.getClass());

	private static final int SUCCESS = 1; // report的status,1为成功,2为失败

	private static final String WAREHOUSE_ID = "globalTrade.loadBill.warehouseId"; // 仓库ID

	private static final String DMS_CODE = "globalTrade.loadBill.dmsCode"; // 全球购的专用分拣中心

	private static final String CTNO = "globalTrade.loadBill.ctno"; // 申报海关编码。默认：5165南沙旅检

	private static final String GJNO = "globalTrade.loadBill.gjno"; // 申报国检编码。默认：000069申报地国检

	private static final String TPL = "globalTrade.loadBill.tpl"; // 物流企业编码。默认：京配编号

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
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public int initialLoadBill(String sendCode, Integer userId, String userName) {
		String dmsCode = PropertiesHelper.newInstance().getValue(DMS_CODE);
		if (StringUtils.isBlank(dmsCode)) {
			logger.error("LoadBillServiceImpl initialLoadBill with dmsCode is null");
			return 0;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("sendCode", sendCode);
		params.put("dmsCode", Integer.parseInt(dmsCode));
		List<SendDetail> sendDetailList = sendDatailReadDao.findBySendCodeAndDmsCode(params);
		if (sendDetailList == null || sendDetailList.size() < 1) {
			logger.info("LoadBillServiceImpl initialLoadBill with the num of SendDetail is 0");
			return 0;
		}
		List<LoadBill> loadBillList = resolveLoadBill(sendDetailList, userId, userName);
		for (LoadBill lb : loadBillList) {
			// 不存在,则添加;存在,则忽略,更新会影响其他功能的更新操作
			if (loadBillDao.findByPackageBarcode(lb.getPackageBarcode()) == null) {
				loadBillDao.add(lb);
			}
		}
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
