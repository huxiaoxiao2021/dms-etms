package com.jd.bluedragon.distribution.rest.pop;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.PopSigninRequest;
import com.jd.bluedragon.distribution.api.response.PopSigninResponse;
import com.jd.bluedragon.distribution.popPrint.domain.PopSignin;
import com.jd.bluedragon.distribution.popPrint.dto.PopSigninDto;
import com.jd.bluedragon.distribution.popPrint.service.PopSigninService;
import com.jd.common.util.StringUtils;
/**
 * 
* 类描述： POP收货，三方交接单对外接口服务
* 创建者： libin
* 项目名称： bluedragon-distribution-web
* 创建时间： 2013-2-19 下午1:47:49
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PopSigninResource {
	private static Logger log = LoggerFactory.getLogger(PopSigninResource.class);
	@Autowired
	private PopSigninService popSigninService;

	/**
	 * 
	* 方法描述 : 三方交接清单查询
	* 创建者：libin 
	* 项目名称： bluedragon-distribution-web
	* 类名： PopSigninResource.java
	* 创建时间： 2013-2-19 下午1:44:07
	* @param popSigninRequest
	* @return PopSigninResponse
	 */
	@POST
	@GET
	@Path("/popSignin/getPopSigninList")
	public PopSigninResponse getPopSigninList(PopSigninRequest popSigninRequest) {
		log.debug("查询三方交接打印.....");
		PopSigninResponse response = new PopSigninResponse();
		response.setCode(response.CODE_OK);
		PopSigninDto popSigninDto = new PopSigninDto();
		if (StringUtils.isNotBlank(popSigninRequest.getQueueNo())) {
			popSigninDto.setQueueNo(popSigninRequest.getQueueNo());
		}
		if (popSigninRequest.getCreateUserCode() != null) {
			popSigninDto.setCreateUserCode(popSigninRequest.getCreateUserCode());
		}
		if (popSigninRequest.getExpressCode() != null) {
			popSigninDto.setExpressCode(popSigninRequest.getExpressCode());
		}
		if (StringUtils.isNotEmpty(popSigninRequest.getThirdWaybillCode())) {
			popSigninDto.setThirdWaybillCode(popSigninRequest.getThirdWaybillCode());
		}
		if (StringUtils.isNotEmpty(popSigninRequest.getSignStartTime())) {
			popSigninDto.setSignStartTime(popSigninRequest.getSignStartTime());
		}
		if (StringUtils.isNotEmpty(popSigninRequest.getSignEndTime())) {
			popSigninDto.setSignEndTime(popSigninRequest.getSignEndTime());
		}
		if (popSigninRequest.getPageNo() != null) {
			Integer pageSize = popSigninRequest.getPageSize();
			if (pageSize == null || pageSize.equals(0)) {
				pageSize = Pager.DEFAULT_PAGE_SIZE;
			}
			@SuppressWarnings("rawtypes")
			Pager pager = new Pager(popSigninRequest.getPageNo(), pageSize);
			int count = this.popSigninService.getCount(popSigninDto);
			pager.setTotalSize(count);
			popSigninDto.setStart(pager.getStartIndex());
			popSigninDto.setEnd(pager.getEndIndex());
            popSigninDto.setPageSize(pager.getPageSize());
			response.setPageNo(popSigninRequest.getPageNo());
			response.setTotalSize(count);
			response.setTotalNo(pager.getTotalNo()==null?0:pager.getTotalNo());
			response.setPageSize(pager.getPageSize());
		}

		List<PopSignin> list = this.popSigninService.getPopSigninList(popSigninDto);
		response.setData(list);
		return response;
	}

	/**
	 * 
	* 方法描述 : 测试插入
	* 创建者：libin 
	* 项目名称： bluedragon-distribution-web
	* 类名： PopSigninResource.java
	* 版本： v1.0
	* 创建时间： 2013-2-19 下午1:43:49
	* @return String
	 */
	@POST
	@GET
	@Path("/popSignin/insert")
	public String insertPopSignin() {
		PopSignin popSignin = new PopSignin();
		popSignin.setCreateSiteCode(1610);
		popSignin.setCreateSiteName("奓山分拣中心");
		popSignin.setCreateUser("测试");
		popSignin.setCreateUserCode(45302);
		popSignin.setExpressCode("467");
		popSignin.setExpressName("顺丰快递");
		popSignin.setQueueNo("027F004-201301310004");
		popSignin.setThirdWaybillCode("11111111");
		popSignin.setOperateTime(new Date());
		this.popSigninService.insert(popSignin);
		return "200";
	}

}
