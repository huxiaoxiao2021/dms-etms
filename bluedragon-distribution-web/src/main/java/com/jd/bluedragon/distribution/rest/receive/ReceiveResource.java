package com.jd.bluedragon.distribution.rest.receive;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.api.response.PopJoinResponse;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.receive.service.impl.ReceiveServiceImpl;
import com.jd.bluedragon.distribution.rest.pop.PopJoinResource.PopJoinQuery;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReceiveResource {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	ReceiveService receiveService;

    /**
     * 按条件查询POP收货交接清单
     *
     * @param receiveJoinQuery
     *            分页对象
     * @return
     */
	@SuppressWarnings("unchecked")
	@POST
	@Path("/receiveJoin/queryReceiveJoinList")
	public PopJoinResponse<PopJoinQuery> queryPopJoinList(
			ReceiveJoinQuery receiveJoinQuery) {
		Boolean checkParam = Boolean.FALSE;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			
			if (receiveJoinQuery == null || receiveJoinQuery.getCreateSiteCode() == 0
					|| receiveJoinQuery.getStartTime() == null
					|| receiveJoinQuery.getEndTime() == null) {
				checkParam = Boolean.TRUE;
			}
			paramMap = ObjectMapHelper.makeObject2Map(receiveJoinQuery);

		} catch (Exception e) {
			checkParam = Boolean.TRUE;
		}

		if (Boolean.TRUE.equals(checkParam)) {
			this.log.warn("按条件查询POP收货交接清单 --> 传入参数非法");
			return new PopJoinResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR, receiveJoinQuery);
		}

		try {
			Pager pager = receiveJoinQuery.getPager();

			// 设置分页参数
			if (pager == null) {
				pager = new Pager(Pager.DEFAULT_PAGE_NO,
						Pager.DEFAULT_POP_PAGE_SIZE);
			} else {
				if (pager.getPageNo() == null || pager.getPageNo() <= 0) {
					pager.setPageNo(Pager.DEFAULT_PAGE_NO);
				}
				if (pager.getPageSize() == null || pager.getPageSize() <= 0
						|| pager.getPageSize() > Pager.DEFAULT_POP_PAGE_SIZE) {
					pager.setPageSize(Pager.DEFAULT_POP_PAGE_SIZE);
				}
			}

			pager = new Pager(pager.getPageNo(), pager.getPageSize());

			receiveJoinQuery.setPager(pager);

			paramMap.put("pageNo", pager.getPageNo());
			paramMap.put("pageSize", pager.getPageSize());
			paramMap.put("startIndex", pager.getStartIndex());
			paramMap.put("endIndex", pager.getEndIndex());

			List<Receive> receives = null;

			if (!paramMap.isEmpty()) {
				// 获取总数量
				int totalSize = this.receiveService
						.findReceiveJoinTotalCount(paramMap);
				this.log.info("按条件查询POP收货交接清单 --> 获取总数量为：{}", totalSize);
				if (totalSize > 0) {
					pager.setTotalSize(totalSize);
					receives = this.receiveService
							.findReceiveJoinList(paramMap);
					pager.setData(receives);
				} else {
					this.log.info("按条件查询POP收货交接清单 --> paramMap：{}, 调用服务成功，数据为空",paramMap);
					return new PopJoinResponse(PopJoinResponse.CODE_OK_NULL,
							PopJoinResponse.MESSAGE_OK_NULL, receiveJoinQuery);
				}
			}
			return new PopJoinResponse(PopJoinResponse.CODE_OK,
					PopJoinResponse.MESSAGE_OK, receiveJoinQuery);

		} catch (Exception e) {
			this.log.error("按条件查询POP收货交接清单异常：", e);
			return new PopJoinResponse(PopJoinResponse.CODE_SERVICE_ERROR,
					PopJoinResponse.MESSAGE_SERVICE_ERROR, receiveJoinQuery);
		}
	}


    /**
     *  干线收货发送发车信息到计费系统（通过MQ）
     *  @param type 是根据车次号还是包裹号查询
     *  @param code 车次号或者包裹号
     *  @return
     * */
    @GET
    @Path("/receiveresource/queryarterybillinfo/{type}/{code}/{dmsID}/{dmsName}/{userCode}/{userName}")
    public DeparturePrintResponse queryArteryBillInfo(@PathParam("type") String type,@PathParam("code") String code,
    		@PathParam("dmsID")Integer dmsID,@PathParam("dmsName")String dmsName,
    		@PathParam("userCode")String userCode, @PathParam("userName")String userName){

        DeparturePrintResponse departurePrintResponse = new DeparturePrintResponse();
        if(StringHelper.isEmpty(type) || (!ReceiveServiceImpl.CARCODE_MARK.equals(type)
                                            && !ReceiveServiceImpl.BOXCODE_MARK.equals(type))){
            log.warn("获取干线计费信息失败，查询方式不符合要求。查询方式 :{}", type);
            departurePrintResponse.setCode(DeparturePrintResponse.CODE_PARAM_ERROR);
            departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_PARAM_ERROR);
            return departurePrintResponse;
        }

        if(StringHelper.isEmpty(code)){
            log.warn("获取干线计费信息失败, 查询参数错误。查询参数 {}", code);
            departurePrintResponse.setCode(DeparturePrintResponse.CODE_PARAM_ERROR);
            departurePrintResponse.setMessage(DeparturePrintResponse.MESSAGE_PARAM_ERROR);
            return departurePrintResponse;
        }

        String message = dmsID + "&" + dmsName + "&" + userCode + "&" + userName;
        return receiveService.dellSendMq2ArteryBillingSys(type,code,message);
    }


	public static class ReceiveJoinQuery {
		/**
		 * 分拣中心编号
		 */
		private Integer createSiteCode;

		/**
		 * 开始时间
		 */
		private Date startTime;

		/**
		 * 结束时间
		 */
		private Date endTime;


		/* 操作人code */
		private Integer createUserCode;

		/* 操作人 */
		private String createUser;

		/**
		 * 排队号
		 */
		private String queueNo;

		
		private Pager<List<Receive>> pager;

		public Integer getCreateSiteCode() {
			return createSiteCode;
		}

		public void setCreateSiteCode(Integer createSiteCode) {
			this.createSiteCode = createSiteCode;
		}

		public Date getStartTime() {
			return startTime != null ? (Date) startTime.clone() : null;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime != null ? (Date) startTime.clone()
					: null;
		}

		public Date getEndTime() {
			return endTime != null ? (Date) endTime.clone() : null;
		}

		public void setEndTime(Date endTime) {
			this.endTime = endTime != null ? (Date) endTime.clone() : null;
		}

		
		public Pager<List<Receive>> getPager() {
			return pager;
		}

		public void setPager(Pager<List<Receive>> pager) {
			this.pager = pager;
		}

		public Integer getCreateUserCode() {
			return createUserCode;
		}

		public void setCreateUserCode(Integer createUserCode) {
			this.createUserCode = createUserCode;
		}

		public String getCreateUser() {
			return createUser;
		}

		public void setCreateUser(String createUser) {
			this.createUser = createUser;
		}

		public String getQueueNo() {
			return queueNo;
		}

		public void setQueueNo(String queueNo) {
			this.queueNo = queueNo;
		}
	}
	
}
