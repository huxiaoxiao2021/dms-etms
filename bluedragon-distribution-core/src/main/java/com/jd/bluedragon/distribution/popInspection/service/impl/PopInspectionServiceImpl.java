package com.jd.bluedragon.distribution.popInspection.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.IotServiceWSManager;
import com.jd.bluedragon.core.base.LdopWaybillUpdateManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.InspectionPOPRequest;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.popInspection.service.PopInspectionService;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
import com.jd.bluedragon.distribution.popPrint.service.PopQueueService;
import com.jd.bluedragon.distribution.popPrint.service.PopSigninService;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.ConstantEnums;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-12-17 上午09:51:28
 *
 * 类说明
 */
@Service("popInspectionService")
public class PopInspectionServiceImpl implements PopInspectionService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private InspectionService inspectionService;

	@Autowired
	private PopSigninService popSigninService;

	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;

	@Autowired
	private PopQueueService popQueueService;

	@Autowired
	private IotServiceWSManager iotServiceWSManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private LdopWaybillUpdateManager ldopWaybillUpdateManager;

    @Override
	public boolean execute(Task task) {
		if (task == null) {
			return Boolean.FALSE;
		}
		InspectionPOPRequest[] popRequests = null;
		Set<Inspection> inspectionPOPs = new HashSet<Inspection>();
		try {
			popRequests = JsonHelper.jsonToArray(task.getBody(),
					InspectionPOPRequest[].class);
			if (popRequests == null) {
				this.logger.info("PopInspectionServiceImpl 任务task ID: " + task.getId()
						+ ", 主体数据为空");
				return false;
			}
			// 处理数据
			if (!BusinessHelper.checkIntNumRange(popRequests.length)) {
                this.logger.error("PopInspectionServiceImpl 任务task ID: 【" + task.getId()
                        + "】，主体数组长度大于限定值");
                return false;
            }
            handleFeatherLetter(popRequests[0]);
            for (InspectionPOPRequest popRequest : popRequests) {

                try {
                    if (Constants.POP_QUEUE_EXPRESS.equals(popRequest
                            .getQueueType())) {
                        this.logger.info("PopInspectionServiceImpl 托寄处理开始");
                        this.popSigninService.insert(popRequest);
                    } else {
                        if (StringUtils.isBlank(popRequest.getBoxCodeNew())) {
                            this.logger.info("操作人Code："
                                    + popRequest.getUserCode()
                                    + "， 操作人SiteCode："
                                    + popRequest.getSiteCode()
                                    + ", 操作BusinessType"
                                    + popRequest.getBusinessType()
                                    + ", 参数非法");
                            continue;
                        }

                        Inspection ip = new Inspection();
                        ip.setWaybillCode(popRequest.getBoxCodeNew());
                        if (StringUtils.isBlank(popRequest.getPackageBarcode())) {
                            ip.setPackageBarcode(popRequest.getBoxCodeNew());
                        } else {
                            ip.setPackageBarcode(popRequest.getPackageBarcode());
                        }
                        ip.setThirdWaybillCode(popRequest.getBoxCode());
                        ip.setInspectionType(popRequest.getBusinessType());
                        ip.setCreateUserCode(popRequest.getUserCode());
                        ip.setCreateUser(popRequest.getUserName());
                        ip.setCreateTime(new Date()); //修改为创建时间 2016年3月16日10:42:53
                        ip.setCreateSiteCode(popRequest.getSiteCode());

                        ip.setUpdateTime(ip.getCreateTime());
                        ip.setUpdateUser(ip.getCreateUser());
                        ip.setUpdateUserCode(ip.getCreateUserCode());

                        ip.setPopSupId(popRequest.getPopSupId());
                        ip.setPopSupName(popRequest.getPopSupName());
                        ip.setQuantity(popRequest.getQuantity());
                        ip.setCrossCode(popRequest.getCrossCode());
                        ip.setWaybillType(popRequest.getType());
                        if(StringHelper.isNotEmpty(popRequest.getQueueNo()) && popRequest.getQueueNo().length() > Constants.QUEUE_NO_LEGNTH){
                            ip.setQueueNo("OverLengthQueueNo");
                            logger.error("POP收货：QueueNo字段超长，异常值为： " + popRequest.getQueueNo());
                        }else {
                            ip.setQueueNo(popRequest.getQueueNo());
                        }
                        ip.setPopReceiveType(popRequest.getQueueType());
                        //新增 opTime  2016年3月16日10:43:10
                        ip.setOperateTime(DateHelper.getSeverTime(popRequest.getOperateTime()));
                        if (Constants.POP_QUEUE_DRIVER.equals(popRequest
                                .getQueueType())) {
                            if (StringUtils.isNotBlank(popRequest
                                    .getQueueNo())) {
                                try {
                                    PopQueue popQueue = this.popQueueService
                                            .getPopQueueByQueueNo(popRequest
                                                    .getQueueNo());
                                    if (popQueue != null) {
                                        ip.setDriverCode(popQueue
                                                .getExpressCode());
                                        ip.setDriverName(popQueue
                                                .getExpressName());
                                    }
                                } catch (Exception e) {
                                    this.logger.error(
                                            "处理POP收货数据 根据排队号查询排队信息 异常：", e);
                                }
                            }
                        }

                        ip.setBusiId(popRequest.getBusiId());
                        ip.setBusiName(popRequest.getBusiName());

                        if (Waybill.isPopWaybillType(ip.getWaybillType())) {
                            ip.setBusiId(popRequest.getPopSupId());
                            ip.setBusiName(popRequest.getPopSupName());
                        }

                        inspectionPOPs.add(ip);
                    }
                } catch (Exception e) {
                    this.logger.error("处理POP收货数据异常：", e);
                }
            }

		} catch (Exception e) {
			this.logger.error("PopInspectionServiceImpl 任务task ID: " + task.getId()
					+ " 异常, 主体数据非法", e);
			return false;
		}
		if (!inspectionPOPs.isEmpty()) {
			List<Inspection> pops = new CollectionHelper<Inspection>()
					.toList(inspectionPOPs);
			int resultCount = 0;
			int resultRecieveCount = 0;
			for (Inspection pop : pops) {
				resultCount += this.inspectionService.addInspectionPop(pop);
				if (Waybill.isPopWaybillType(pop.getWaybillType())) {
					try {
						resultRecieveCount += this.taskPopRecieveCountService
								.insert(pop);
					} catch (Exception e) {
						this.logger.error(
								"PopInspectionServiceImpl 任务task ID: "
										+ task.getId() + " 推送回传MQ数据异常：", e);
					}
				}
			}
			this.logger.info("批量增加POP收货数据条数为：" + pops.size() + ", 成功处理条数为："
					+ resultCount);
			this.logger.info("批量增加POP实收数据条数为：" + pops.size() + ", 成功处理条数为："
					+ resultRecieveCount);

		} else {
			this.logger.info("PopInspectionServiceImpl 任务task ID 【 " + task.getId()
					+ "】, 转换后数据 inspectionService 为空");
		}
		this.logger
				.info("PopInspectionServiceImpl 任务task ID: " + task.getId() + ", 处理成功");
		
		return true;
	}

    /**
     * 鸡毛信运单处理
     * @param firstRequest 驻场打印数据
     */
    private void handleFeatherLetter(InspectionPOPRequest firstRequest) {
        String waybillCode = firstRequest.getBoxCodeNew();
        com.jd.etms.waybill.domain.Waybill  waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if(waybill == null){
            logger.error("鸡毛信运单处理-查询运单信息为空waybillCode[{}]firstRequest[{}]",waybillCode,JsonHelper.toJson(firstRequest));
            return;
        }
        if(!BusinessUtil.isFeatherLetter(waybill.getWaybillSign())){
            return;
        }
        if(Objects.equals(firstRequest.getCancelFeatherLetter(),Boolean.TRUE)){
            logger.error("鸡毛信运单处理-已经取消鸡毛信waybillCode[{}]firstRequest[{}]",waybillCode,JsonHelper.toJson(firstRequest));
            return;
        }
        if(StringUtils.isEmpty(firstRequest.getFeatherLetterDeviceNo())){
            logger.error("鸡毛信运单处理-设备号为空可能其他运单已经绑定过设备号waybillCode[{}]firstRequest[{}]",waybillCode,JsonHelper.toJson(firstRequest));
            return;
        }
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByStaffId(firstRequest.getUserCode());
        if(baseStaffSiteOrgDto == null){
            logger.info("鸡毛信运单处理-获取员工信息为空waybillCode[{}]userCode[{}]",waybillCode,firstRequest.getUserCode());
            return;
        }
        ConstantEnums.IotBusiness iotBusiness = null;
        if(BusinessHelper.isB2c(waybill.getWaybillSign())){
            iotBusiness = ConstantEnums.IotBusiness.B2C;
        }else if (BusinessHelper.isC2c(waybill.getWaybillSign())){
            iotBusiness = ConstantEnums.IotBusiness.C2C;
        }
        iotServiceWSManager.bindDeviceWaybill(firstRequest.getFeatherLetterDeviceNo(),waybillCode,baseStaffSiteOrgDto.getAccountNumber(),iotBusiness);
    }
}
