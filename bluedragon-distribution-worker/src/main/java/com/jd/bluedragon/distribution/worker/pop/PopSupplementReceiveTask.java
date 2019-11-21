package com.jd.bluedragon.distribution.worker.pop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.jd.bluedragon.distribution.popPrint.dao.PopPrintDao;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.taobao.pamirs.schedule.IScheduleTaskDealMulti;
import com.taobao.pamirs.schedule.TBScheduleManagerFactory;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-8 上午11:09:49
 * 
 *             POP已打印未收货处理（补全收货信息）
 */
public class PopSupplementReceiveTask implements
		IScheduleTaskDealMulti<PopPrint> {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	protected DataSource dataSource;
	protected String taskType;
	protected String ownSign;
	private TBScheduleManagerFactory managerFactory;
	private Integer maxExecuteCount = 3;
	private Integer limitMin = 5;
	private Integer limitHour = 2;

	@Autowired
	private PopPrintService popPrintService;

	@Autowired
	private InspectionService inspectionService;
	
	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;

    @Autowired
    private PopPrintDao popPrintDao;

	@Override
	public boolean execute(Object[] objs, String arg1) throws Exception {
		if (objs == null || objs.length == 0) {
			log.warn("平台订单已打印未收货处理 --> 平台订单已打印未收货处理 任务为空");
			return false;
		}

		for (Object taskObj : objs) {
			PopPrint popPrint = (PopPrint) taskObj;
			if (!WaybillUtil.isWaybillCode(popPrint.getWaybillCode())) {
				log.warn("平台订单已打印未收货处理 --> 打印单号【{}】，运单号【{}】， 操作人SiteCode【{}】，为非平台订单"
						,popPrint.getPopPrintId(),popPrint.getWaybillCode(),popPrint.getCreateSiteCode());
				continue;
			}
			
			if (Waybill.isPopWaybillType(popPrint.getWaybillType())
					&& !Constants.POP_QUEUE_EXPRESS.equals(popPrint
							.getPopReceiveType())) {
				try {
					this.taskPopRecieveCountService
							.insert(popPrintToInspection(popPrint));
					this.log.info("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】收货补全回传POP成功",popPrint.getCreateSiteCode(),popPrint.getWaybillCode());
				} catch (Exception e) {
					this.log.error("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】收货补全回传POP,补全异常",popPrint.getCreateSiteCode(),popPrint.getWaybillCode(),e);
				}
			}
			try {
				this.inspectionService
						.addInspectionPop(popPrintToInspection(popPrint));
				this.log.info("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】 收货信息不存在，补全成功"
						,popPrint.getCreateSiteCode(),popPrint.getWaybillCode());
			} catch (Exception e) {
				this.log.error("平台订单已打印未收货处理 --> 分拣中心-运单【{}-{}】 收货信息不存在，补全异常"
						,popPrint.getCreateSiteCode() ,popPrint.getWaybillCode() , e);
			}
		}
		return true;
	}

	public Inspection popPrintToInspection(PopPrint popPrint) {
		try {
			Inspection inspection = new Inspection();
			inspection.setWaybillCode(popPrint.getWaybillCode());
			if (Constants.POP_QUEUE_SITE.equals(popPrint.getPopReceiveType())) {
				inspection.setInspectionType(Constants.BUSSINESS_TYPE_SITE);
			} else {
				inspection.setInspectionType(Constants.BUSSINESS_TYPE_POP);
			}
			inspection.setCreateUserCode(popPrint.getCreateUserCode());
			inspection.setCreateUser(popPrint.getCreateUser());
//			inspection
//					.setCreateUserCode((popPrint.getPrintPackCode() == null || popPrint
//							.getPrintPackCode().equals(0)) ? popPrint
//							.getPrintInvoiceCode() : popPrint
//							.getPrintPackCode());
//			inspection.setCreateUser(Constants.POP_RECEIVE_NAME);
//			if (Constants.POP_QUEUE_EXPRESS.equals(popPrint.getPopReceiveType()) 
//					|| Constants.POP_QUEUE_SITE.equals(popPrint.getPopReceiveType())) {
//				inspection.setCreateUser(StringUtils.isBlank(popPrint
//						.getPrintPackUser()) ? popPrint.getPrintInvoiceUser()
//								: popPrint.getPrintPackUser());
//			}
			inspection
					.setCreateTime((popPrint.getPrintPackTime() == null) ? popPrint
							.getPrintInvoiceTime()
							: popPrint.getPrintPackTime());
			inspection.setCreateSiteCode(popPrint.getCreateSiteCode());
			
			inspection.setUpdateTime(inspection.getCreateTime());
			inspection.setUpdateUser(inspection.getCreateUser());
			inspection.setUpdateUserCode(inspection.getCreateUserCode());

			inspection.setPopSupId(popPrint.getPopSupId());
			inspection.setPopSupName(popPrint.getPopSupName());
			inspection.setQuantity(popPrint.getQuantity());
			inspection.setCrossCode(popPrint.getCrossCode());
			inspection.setWaybillType(popPrint.getWaybillType());
			inspection.setPopReceiveType(popPrint.getPopReceiveType());
			inspection.setPopFlag(1);
			inspection.setThirdWaybillCode(popPrint.getThirdWaybillCode());
			inspection.setQueueNo(popPrint.getQueueNo());
			
			inspection
					.setPackageBarcode((popPrint.getPackageBarcode() == null) ? popPrint
							.getWaybillCode()
							: popPrint.getPackageBarcode());
			inspection.setBoxCode(popPrint.getBoxCode());
			inspection.setDriverCode(popPrint.getDriverCode());
			inspection.setDriverName(popPrint.getDriverName());
			inspection.setBusiId(popPrint.getBusiId());
			inspection.setBusiName(popPrint.getBusiName());
			inspection.setOperateTime(popPrint.getPrintPackTime());
            if(null==inspection.getOperateTime()){
                inspection.setOperateTime(popPrint.getCreateTime());
            }
			if (Waybill.isPopWaybillType(inspection.getWaybillType())) {
				inspection.setBusiId(popPrint.getPopSupId());
				inspection.setBusiName(popPrint.getPopSupName());
			}
			
			return inspection;
		} catch (Exception e) {
			log.error("平台订单已打印未收货处理 --> 转换打印信息异常：{}", JsonHelper.toJson(popPrint), e);
			return null;
		}
	}

	@Override
	public List<PopPrint> selectTasks(String arg0, int queueNum,
			List<String> queryCondition, int fetchNum) throws Exception {
		if (queryCondition.size() == 0) {
			return Collections.emptyList();
		}

		List<PopPrint> popPrints = new ArrayList<PopPrint>();
		try {
			if (queryCondition.size() != queueNum) {
				fetchNum = fetchNum * queueNum / queryCondition.size();
			}

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("fetchNum", fetchNum);
			paramMap.put("limitMin", limitMin);
			paramMap.put("limitHour", limitHour);
			paramMap.put("ownSign", this.ownSign);
			List<PopPrint> popPrintList = popPrintDao.findLimitListNoReceive(paramMap);

            for (PopPrint popPrint : popPrintList) {
				if (!isMyTask(queueNum, popPrint.getPopPrintId(),
						queryCondition)) {
					continue;
				}
				popPrints.add(popPrint);
			}

            return popPrintService.findLimitListNoReceive(popPrints, paramMap);
		} catch (Exception e) {
            this.log.error("出现异常， 异常信息为：{}" , e.getMessage(), e);
        }
        return popPrints;
    }

	private boolean isMyTask(long taskCount, long id, List<?> subQueues) {
		if (taskCount == subQueues.size())
			return true;
		long m = id % taskCount;
		for (Object o : subQueues) {
			if (m == Long.parseLong(o.toString()))
				return true;
		}
		return false;
	}

	public void init() throws Exception {
		managerFactory.createTBScheduleManager(taskType, ownSign);
	}

	@Override
	public Comparator<PopPrint> getComparator() {
		return new Comparator<PopPrint>() {
			public int compare(PopPrint t1, PopPrint t2) {
				if (null != t1 && null != t2 && t1.getPopPrintId() != null
						&& t1.getPopPrintId().equals(t2.getPopPrintId())) {
					return 0;
				} else {
					return 1;
				}
			}

		};
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getOwnSign() {
		return ownSign;
	}

	public void setOwnSign(String ownSign) {
		this.ownSign = ownSign;
	}

	public TBScheduleManagerFactory getManagerFactory() {
		return managerFactory;
	}

	public void setManagerFactory(TBScheduleManagerFactory managerFactory) {
		this.managerFactory = managerFactory;
	}

	public Integer getMaxExecuteCount() {
		return maxExecuteCount;
	}

	public void setMaxExecuteCount(Integer maxExecuteCount) {
		this.maxExecuteCount = maxExecuteCount;
	}

	public Integer getLimitMin() {
		return limitMin;
	}

	public void setLimitMin(Integer limitMin) {
		this.limitMin = limitMin;
	}

	public Integer getLimitHour() {
		return limitHour;
	}

	public void setLimitHour(Integer limitHour) {
		this.limitHour = limitHour;
	}
}
