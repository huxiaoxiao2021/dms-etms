package com.jd.bluedragon.distribution.rest.pop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.ExpressResponse;
import com.jd.bluedragon.distribution.api.request.PopQueueRequest;
import com.jd.bluedragon.distribution.api.response.PopQueueResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
import com.jd.bluedragon.distribution.popPrint.service.PopQueueService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * 
* 类描述： POP收货号生成及管理接口服务
* 创建者： libin
* 项目名称： bluedragon-distribution-web
* 创建时间： 2013-1-14 上午11:43:35
* 版本号： v1.0
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PopQueueResource {
	private static Logger log = LoggerFactory.getLogger(PopQueueResource.class);
//	public final static SimpleDateFormat SimpleDateFormat_Short = new SimpleDateFormat("yyyyMMdd");
	@Autowired
	private BaseService baseService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	@Autowired
	private PopQueueService popQueueService;
	private static int suffixLength = 4;

	/**
	 * 
	* 方法描述 : 打印排队号
	* 创建者：libin 
	* 版本： v1.0
	* 创建时间： 2013-1-14 下午2:14:30
	* @param siteCode 分拣中心编码
	* @param type  类型，1是商家直送，2是快递公司
	* @return PopQueueResponse
	 */
	@GET
	@POST
	@Path("/popQueue/add")
	public PopQueueResponse add(PopQueueRequest popQueueRequest) {
		PopQueueResponse response = new PopQueueResponse();
		try {
			if (popQueueRequest.getCreateSiteCode() == null) {
				response.setCode(response.CODE_INTERNAL_ERROR);
				response.setMessage("传入站点编码为空");
				return response;
			}
			BaseStaffSiteOrgDto createSite = this.baseMajorManager
					.getBaseSiteBySiteId(popQueueRequest
					.getCreateSiteCode());

			if (createSite == null) {
				response.setCode(response.CODE_INTERNAL_ERROR);
				response.setMessage("不存在" + popQueueRequest.getCreateSiteCode() + "站点信息");
				return response;
			}
			if (popQueueRequest.getCreateUserCode() == null) {
				response.setCode(response.CODE_INTERNAL_ERROR);
				response.setMessage("传入创建人编码为空");
				return response;
			}
			BaseStaffSiteOrgDto user = this.baseService.getBaseStaffByStaffId(popQueueRequest.getCreateUserCode());

			if (user == null) {
				response.setCode(response.CODE_INTERNAL_ERROR);
				response.setMessage("不存在" + popQueueRequest.getCreateUserCode() + "人员信息");
				return response;
			}
			String dmsCode = createSite.getDmsSiteCode();
			int currentWaitNo = popQueueService.getCurrentWaitNo(popQueueRequest.getCreateSiteCode());
			String dateStr = getDateStr();
			String queueNo = getFlowNumber(dmsCode, dateStr, currentWaitNo, popQueueRequest.getQueueType());
			PopQueue popQueue = new PopQueue();
			popQueue.setExpressCode(popQueueRequest.getExpressCode());
			popQueue.setCreateSiteCode(popQueueRequest.getCreateSiteCode());
			popQueue.setCreateSiteName(createSite.getSiteName());
			popQueue.setExpressName(popQueueRequest.getExpressName());
			popQueue.setQueueType(popQueueRequest.getQueueType());
			popQueue.setQueueNo(queueNo);
			popQueue.setWaitNo(currentWaitNo + 1);
			popQueue.setCreateUserCode(popQueueRequest.getCreateUserCode());
			popQueue.setCreateUser(user.getStaffName());
			Date operateTime = new Date();
			popQueue.setOperateTime(operateTime);
			int n = this.popQueueService.insertPopQueue(popQueue);
			if (n < 1) {
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("操作数据库出错！");
				return response;
			}
			response.setCode(PopQueueResponse.CODE_OK);
			response.setMessage("");
			response.setQueueNo(queueNo);
			response.setExpressCode(popQueueRequest.getExpressCode());
			response.setExpressName(popQueueRequest.getExpressName());
			response.setOperateTime(DateHelper.formatDate(operateTime, "yyyy-MM-dd HH:mm"));
			response.setQueueType(popQueueRequest.getQueueType());
		} catch (Exception e) {
			log.error("打印排队号出错了:{}" , JsonHelper.toJson(popQueueRequest), e);
			response.setCode(response.CODE_INTERNAL_ERROR);
			response.setMessage(response.MESSAGE_SERVICE_ERROR);
		}
		return response;
	}

	/**
	 * 
	* 方法描述 : 根据排队号更新收货开始时间
	* 创建者：libin 
	* 版本： v1.0
	* 创建时间： 2013-1-15 下午4:38:50
	* @param flowNumber
	* @return PopQueueResponse
	 */
	@GET
	@POST
	@Path("/popQueue/start/{queueNo}")
	public PopQueueResponse update(@PathParam("queueNo") String queueNo) {
		PopQueueResponse response = new PopQueueResponse();
		response.setQueueNo(queueNo);

		try {
			if (StringUtils.isEmpty(queueNo)) {
				log.info("queueNo传入参数为空");
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("传入排队号为空");
				return response;
			}
			PopQueue popQueue = this.popQueueService.getPopQueueByQueueNo(queueNo);
			if (popQueue == null) {
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("排队号不存在");
				return response;
			}
			response.setExpressCode(popQueue.getExpressCode());
			response.setExpressName(popQueue.getExpressName());
			if (!popQueue.getQueueStatus().equals(1)) {
				response.setCode(PopQueueResponse.CODE_OK);
				return response;
			}
			PopQueue updatePopQueue = new PopQueue();
			updatePopQueue.setStartTime(new Date());
			updatePopQueue.setQueueNo(queueNo);
			updatePopQueue.setQueueStatus(2);
			int n = this.popQueueService.updatePopQueue(updatePopQueue);
			if (n < 1) {
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("操作失败，未更新数据");
				return response;
			}
			response.setCode(PopQueueResponse.CODE_OK);
			response.setMessage("");
			response.setQueueNo(queueNo);
		} catch (Exception e) {
			log.error("更新排队号开始收货时间出错了：{}" , queueNo, e);
			response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
			response.setMessage(response.MESSAGE_SERVICE_ERROR);
		}
		return response;

	}

	/**
	 * 
	* 方法描述 : 更新收货完整状态及完成时间
	* 创建者：libin 
	* 类名： PopQueueResource.java
	* 版本： v1.0
	* 创建时间： 2013-1-16 下午4:25:06
	* @param flowNumber
	* @return PopQueueResponse
	 */
	@SuppressWarnings("static-access")
	@GET
	@POST
	@Path("/popQueue/finish/{queueNo}")
	public PopQueueResponse updateReicieveFinishState(@PathParam("queueNo") String queueNo) {
		PopQueueResponse response = new PopQueueResponse();
		try {
			if (StringUtils.isEmpty(queueNo)) {
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("queueNo传入参数为空");
				return response;

			}
			PopQueue popQueue = this.popQueueService.getPopQueueByQueueNo(queueNo);
			if (popQueue == null) {
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("未获取" + queueNo + "对象");
				return response;
			}

			PopQueue updatePopQueue = new PopQueue();
			updatePopQueue.setEndTime(new Date());
			updatePopQueue.setQueueNo(queueNo);
			updatePopQueue.setQueueStatus(3);
			int n = this.popQueueService.updatePopQueue(updatePopQueue);
			if (n < 1) {
				response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
				response.setMessage("排队号不存在");
			}
			response.setQueueNo(queueNo);
			response.setCode(PopQueueResponse.CODE_OK);
			response.setMessage("");
		} catch (Exception e) {
			log.error("更新排队号开始收货时间出错了：{}" , queueNo, e);
			response.setCode(PopQueueResponse.CODE_INTERNAL_ERROR);
			response.setMessage(response.MESSAGE_SERVICE_ERROR);
		}
		return response;

	}

	/**
	 * 
	* 方法描述 : 获取快递公司列表，根据编号进行精确查询及名称进行模糊查询
	* 创建者：libin 
	* 项目名称： bluedragon-distribution-web
	* 类名： PopQueueResource.java
	* 创建时间： 2013-2-18 上午11:11:12
	* @param popQueueRequest
	* @return ExpressResponse
	 */
	@POST
	@Path("/popQueue/getExpressList")
	public ExpressResponse getExpressList(PopQueueRequest popQueueRequest) {
		ExpressResponse response = new ExpressResponse();
		response.setCode(response.CODE_OK);
		String keyword = popQueueRequest.getKeyword();
		List<BaseStaffSiteOrgDto> dataList = this.baseService.getPopBaseSiteByOrgId(543);
		if (StringUtils.isNotEmpty(keyword)) {
			List<BaseStaffSiteOrgDto> newList = new ArrayList<BaseStaffSiteOrgDto>();
			if (StringUtils.isNumeric(keyword)) {
				for (BaseStaffSiteOrgDto dto : dataList) {
					if (dto.getSiteCode().equals(Integer.parseInt(keyword))) {
						newList.add(dto);
						break;
					}

				}
			} else {
				for (BaseStaffSiteOrgDto dto : dataList) {
					if (dto.getSiteName().contains(keyword)) {
						newList.add(dto);
					}

				}
			}

			response.setData(newList);
			return response;
		}
		response.setData(dataList);
		return response;
	}

	public  String getDateStr() {
		return DateHelper.formatDate(new Date(), "yyyyMMdd");
	}

	public  String getFlowNumber(String dmsCode, String dateStr, int currentWaitNo, Integer queueType) {
		String tempWaitNo = String.valueOf(currentWaitNo + 1);
		while (tempWaitNo.length() < suffixLength)
			tempWaitNo = "0" + tempWaitNo;
		String flowNumber = dmsCode + "-" + queueType + "-" + dateStr + tempWaitNo;
		return flowNumber;

	}

	

}
