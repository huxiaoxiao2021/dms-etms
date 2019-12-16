package com.jd.bluedragon.distribution.rest.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopJoinResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午03:54:11
 * <p/>
 * POP收货交接清单
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class PopJoinResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InspectionService inspectionService;

    @Autowired
    private BaseService baseService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private PopPrintService popPrintService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
    @Autowired
    private WaybillTraceApi waybillTraceApi;

    @Autowired
    private BaseMinorManager baseMinorManager;

    /**
     * 按条件查询POP收货交接清单
     *
     * @param popJoinQuery 分页对象
     * @return
     */
    @SuppressWarnings("unchecked")
    @POST
    @Path("/popJoin/queryPopJoinList")
    public PopJoinResponse<PopJoinQuery> queryPopJoinList(
            PopJoinQuery popJoinQuery) {
        Boolean checkParam = Boolean.FALSE;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (popJoinQuery == null) {
                checkParam = Boolean.TRUE;
            }
            if (popJoinQuery == null || popJoinQuery.getCreateSiteCode() == 0
                    || popJoinQuery.getStartTime() == null
                    || popJoinQuery.getEndTime() == null) {
                checkParam = Boolean.TRUE;
            }
            paramMap = ObjectMapHelper.makeObject2Map(popJoinQuery);

        } catch (Exception e) {
            checkParam = Boolean.TRUE;
        }

        if (Boolean.TRUE.equals(checkParam)) {
            this.log.warn("按条件查询POP收货交接清单 --> 传入参数非法");
            return new PopJoinResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR, popJoinQuery);
        }

        try {
            Pager pager = popJoinQuery.getPager();

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

            popJoinQuery.setPager(pager);

            paramMap.put("pageNo", pager.getPageNo());
            paramMap.put("pageSize", pager.getPageSize());
            paramMap.put("startIndex", pager.getStartIndex());
            paramMap.put("endIndex", pager.getEndIndex());

            List<Inspection> inspections = null;

            if (!paramMap.isEmpty()) {
                // 获取总数量
                int totalSize = this.inspectionService
                        .findPopJoinTotalCount(paramMap);
                this.log.info("按条件查询POP收货交接清单 --> 获取总数量为：{}", totalSize);
                if (totalSize > 0) {
                    pager.setTotalSize(totalSize);
                    inspections = this.inspectionService
                            .findPopJoinList(paramMap);
                    pager.setData(inspections);
                } else {
                    this.log.info("按条件查询POP收货交接清单 --> paramMap：{}, 调用服务成功，数据为空",paramMap);
                    return new PopJoinResponse(PopJoinResponse.CODE_OK_NULL,
                            PopJoinResponse.MESSAGE_OK_NULL, popJoinQuery);
                }
            }
            return new PopJoinResponse(PopJoinResponse.CODE_OK,
                    PopJoinResponse.MESSAGE_OK, popJoinQuery);

        } catch (Exception e) {
            this.log.error("按条件查询POP收货交接清单异常：", e);
            return new PopJoinResponse(PopJoinResponse.CODE_SERVICE_ERROR,
                    PopJoinResponse.MESSAGE_SERVICE_ERROR, popJoinQuery);
        }
    }

    /**
     * 按条件查询POP收货交接清单
     *
     * @param popJoinQuery 分页对象
     * @return
     */
    @SuppressWarnings("unchecked")
    @POST
    @Path("/popJoin/queryBPopJoinList")
    public PopJoinResponse<PopJoinQuery> queryBPopJoinList(
            PopJoinQuery popJoinQuery) {
        Boolean checkParam = Boolean.FALSE;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (popJoinQuery == null) {
                checkParam = Boolean.TRUE;
            }
            if (popJoinQuery == null || popJoinQuery.getCreateSiteCode() == 0
                    || popJoinQuery.getStartTime() == null
                    || popJoinQuery.getEndTime() == null) {
                checkParam = Boolean.TRUE;
            }
            paramMap = ObjectMapHelper.makeObject2Map(popJoinQuery);

        } catch (Exception e) {
            checkParam = Boolean.TRUE;
        }

        if (Boolean.TRUE.equals(checkParam)) {
            this.log.error("按条件查询POP收货交接清单queryBPopJoinList --> 传入参数非法");
            return new PopJoinResponse(JdResponse.CODE_PARAM_ERROR,
                    JdResponse.MESSAGE_PARAM_ERROR, popJoinQuery);
        }

        try {
            Pager pager = popJoinQuery.getPager();

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

            popJoinQuery.setPager(pager);

            paramMap.put("pageNo", pager.getPageNo());
            paramMap.put("pageSize", pager.getPageSize());
            paramMap.put("startIndex", pager.getStartIndex());
            paramMap.put("endIndex", pager.getEndIndex());

            List<Inspection> inspections = null;

            if (!paramMap.isEmpty()) {
                // 获取总数量
                int totalSize = this.inspectionService
                        .findBPopJoinTotalCount(paramMap);
                this.log.info("按条件查询POP收货交接清单queryBPopJoinList --> 获取总数量为：{}", totalSize);
                if (totalSize > 0) {
                    pager.setTotalSize(totalSize);
                    inspections = this.inspectionService
                            .findBPopJoinList(paramMap);
                    pager.setData(inspections);
                } else {
                    this.log.info("按条件查询POP收货交接清单queryBPopJoinList --> paramMap：{}, 调用服务成功，数据为空",paramMap);
                    return new PopJoinResponse(PopJoinResponse.CODE_OK_NULL,
                            PopJoinResponse.MESSAGE_OK_NULL, popJoinQuery);
                }
            }
            return new PopJoinResponse(PopJoinResponse.CODE_OK,
                    PopJoinResponse.MESSAGE_OK, popJoinQuery);

        } catch (Exception e) {
            this.log.error("按条件查询POP收货交接清单queryBPopJoinList异常：", e);
            return new PopJoinResponse(PopJoinResponse.CODE_SERVICE_ERROR,
                    PopJoinResponse.MESSAGE_SERVICE_ERROR, popJoinQuery);
        }
    }

    /**
     * 按条件查询B商家名称列表
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @POST
    @Path("/popJoin/queryBusiList")
    public PopJoinResponse<List<String>> queryBusiList(
            PopJoinQuery popJoinQuery) {

        try {
            List<String> resultBusiNames = new ArrayList<String>();

            List<BasicTraderInfoDTO> baseTradeInfoDtos = null;

            if (popJoinQuery == null || StringUtils.isBlank(popJoinQuery.getBusiName())) {
                baseTradeInfoDtos = this.baseMinorManager.getBaseAllTrader();
            } else {
                baseTradeInfoDtos = this.baseMinorManager.getBaseTraderByName(popJoinQuery.getBusiName());
            }
            for (BasicTraderInfoDTO baseTradeInfoDto : baseTradeInfoDtos) {
                if (baseTradeInfoDto != null && StringUtils.isNotBlank(baseTradeInfoDto.getTraderName())) {
                    resultBusiNames.add(baseTradeInfoDto.getTraderName());
                }
            }

            return new PopJoinResponse(PopJoinResponse.CODE_OK,
                    PopJoinResponse.MESSAGE_OK, resultBusiNames);

        } catch (Exception e) {
            this.log.error("按条件查询POP收货交接清单异常：", e);
            return new PopJoinResponse(PopJoinResponse.CODE_SERVICE_ERROR,
                    PopJoinResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @POST
    @Path("/popJoin/batchForcePopPrint")
    public PopJoinResponse<Map<String, Integer>> batchForcePopPrint(
            List<String> waybillCodes) {
        if (waybillCodes == null || waybillCodes.size() <= 0
                || waybillCodes.size() > 500) {
            this.log.warn("batchForcePopPrint --> 传入参数非法");
            return new PopJoinResponse<Map<String, Integer>>(
                    JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
        }

        try {
            Map<String, Integer> paramMap = new HashMap<String, Integer>();
            List<String> waybillCodeList = new ArrayList<String>();
            List<PopPrint> popPrints = null;

            for (String waybillCode : waybillCodes) {
                PopPrint tempPopPrint = this.popPrintService
                        .findByWaybillCode(waybillCode);
                if (tempPopPrint != null) {
                    paramMap.put(waybillCode, JdResponse.CODE_OK_NULL);
                } else {
                    waybillCodeList.add(waybillCode);
                }
            }

            if (waybillCodeList.size() <= 0) {
                return new PopJoinResponse<Map<String, Integer>>(
                        JdResponse.CODE_OK_NULL, JdResponse.MESSAGE_OK_NULL,
                        paramMap);
            }

            try {
                BaseEntity<List<PackageState>> baseEntity = this.waybillTraceApi
                        .getPkStateByWCodes(waybillCodeList);
                if (baseEntity != null && baseEntity.getData() != null) {
                    popPrints = convPopPrint(baseEntity.getData());

                    if (popPrints == null || popPrints.size() <= 0) {
                        paramMap.put("waybillCodes", JdResponse.CODE_OK_NULL);
                        return new PopJoinResponse<Map<String, Integer>>(
                                JdResponse.CODE_OK_NULL,
                                JdResponse.MESSAGE_OK_NULL, paramMap);
                    }
                }
            } catch (Exception e) {

            }

            if (popPrints != null)
                for (PopPrint popPrint : popPrints) {
                    try {
                        WChoice wChoice = new WChoice();
                        wChoice.setQueryWaybillC(true);
                        wChoice.setQueryWaybillE(true);
                        wChoice.setQueryWaybillM(true);
                        BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager
                                .getDataByChoice(popPrint.getWaybillCode(), wChoice);
                        if (baseEntity != null && baseEntity.getData() != null) {
                            com.jd.etms.waybill.domain.Waybill waybillWS = baseEntity
                                    .getData().getWaybill();
                            if (waybillWS != null) {
                                popPrint.setPopSupId(waybillWS.getConsignerId());
                                popPrint.setPopSupName(waybillWS.getConsigner());
                                popPrint.setQuantity(waybillWS.getGoodNumber());
                                popPrint.setWaybillType(waybillWS.getWaybillType());
                                popPrint.setCrossCode(waybillWS.getSlideCode());
                                popPrint
                                        .setPopReceiveType(Constants.POP_QUEUE_EXPRESS);
                                this.popPrintService.add(popPrint);
                                //推补发货任务
                                if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
                                    popPrintService.pushInspection(popPrint);
                                }
                                paramMap.put(popPrint.getWaybillCode(),
                                        JdResponse.CODE_OK);
                            }
                        }
                    } catch (Exception e) {
                        this.log.error("forcePopReceive --> 异常：", e);
                    }
                }

            return new PopJoinResponse<Map<String, Integer>>(
                    JdResponse.CODE_OK, JdResponse.MESSAGE_OK, paramMap);

        } catch (Exception e) {
            this.log.error("forcePopReceive --> 异常：", e);
            return new PopJoinResponse<Map<String, Integer>>(
                    PopJoinResponse.CODE_SERVICE_ERROR,
                    PopJoinResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    @POST
    @Path("/popJoin/findSitePrintDetail")
    public PopJoinResponse<PopPrintJoinQuery> findSitePrintDetail(PopPrintJoinQuery popJoinQuery) {
        Boolean checkParam = Boolean.FALSE;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        try {
            if (popJoinQuery == null) {
                checkParam = Boolean.TRUE;
            }
            if (popJoinQuery == null || popJoinQuery.getCreateSiteCode() == 0
                    || popJoinQuery.getStartTime() == null
                    || popJoinQuery.getEndTime() == null) {
                checkParam = Boolean.TRUE;
            }
            paramMap = ObjectMapHelper.makeObject2Map(popJoinQuery);

        } catch (Exception e) {
            checkParam = Boolean.TRUE;
        }

        if (Boolean.TRUE.equals(checkParam)) {
            this.log.warn("按条件查询POP收货交接清单findSitePrintDetail --> 传入参数非法");
			return new PopJoinResponse<PopPrintJoinQuery>(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR, popJoinQuery);
        }

        try {
            Pager pager = popJoinQuery.getPager();

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

            popJoinQuery.setPager(pager);

            paramMap.put("pageNo", pager.getPageNo());
            paramMap.put("pageSize", pager.getPageSize());
            paramMap.put("startIndex", pager.getStartIndex());
            paramMap.put("endIndex", pager.getEndIndex());
            List<PopPrint> popPrints=null;
            if (!paramMap.isEmpty()) {
                // 获取总数量
                int totalSize = this.popPrintService
                        .findSitePrintDetailCount(paramMap);
                this.log.info("按条件查询POP收货交接清单findSitePrintDetail --> 获取总数量为：{}", totalSize);
                if (totalSize > 0) {
                    pager.setTotalSize(totalSize);
                   popPrints = popPrintService.findSitePrintDetail(paramMap);
                }
                pager.setData(popPrints);
            }
            else{
                this.log.info("按条件查询POP收货交接清单findSitePrintDetail --> paramMap：{}, 调用服务成功，数据为空",paramMap);
                return new PopJoinResponse(PopJoinResponse.CODE_OK_NULL,
                        PopJoinResponse.MESSAGE_OK_NULL, popJoinQuery);
            }
            return  new PopJoinResponse<PopPrintJoinQuery>(JdResponse.CODE_OK,JdResponse.MESSAGE_OK,popJoinQuery);

        }catch (Exception e){
            this.log.error("站点pop打印清单查询失败！",e);
            return  new PopJoinResponse<PopPrintJoinQuery>(PopJoinResponse.CODE_SERVICE_ERROR,PopJoinResponse.MESSAGE_SERVICE_ERROR,popJoinQuery);
        }
    }


    @POST
    @Path("/popJoin/forcePopReceive")
    public PopJoinResponse<Map<String, Integer>> forcePopReceive(
            List<String> waybillCodes) {
        if (waybillCodes == null || waybillCodes.size() <= 0
                || waybillCodes.size() > 500) {
            this.log.warn("forcePopReceive --> 传入参数非法");
            return new PopJoinResponse<Map<String, Integer>>(
                    JdResponse.CODE_PARAM_ERROR, JdResponse.MESSAGE_PARAM_ERROR);
        }

        try {
            Map<String, Integer> paramMap = new HashMap<String, Integer>();

            List<Inspection> inspections = this.inspectionService
                    .findPopByWaybillCodes(waybillCodes);

            for (Inspection inspection : inspections) {
                String waybillCode = inspection.getWaybillCode();
                BaseStaffSiteOrgDto bDto = this.baseService
                        .getSiteBySiteID(inspection.getCreateSiteCode());
                WaybillStatus tWaybillStatus = new WaybillStatus();
                tWaybillStatus.setPackageCode(waybillCode);
                tWaybillStatus
                        .setCreateSiteCode(inspection.getCreateSiteCode());
                tWaybillStatus.setCreateSiteName(bDto.getSiteName());
                tWaybillStatus.setCreateSiteType(bDto.getSiteType());
                tWaybillStatus.setOperatorId(inspection.getCreateUserCode());
                tWaybillStatus.setOperateTime(inspection.getCreateTime());
                tWaybillStatus.setOperator(inspection.getCreateUser());
                tWaybillStatus.setOrgId(bDto.getOrgId());
                tWaybillStatus.setOrgName(bDto.getOrgName());
                tWaybillStatus.setWaybillCode(waybillCode);
                tWaybillStatus
                        .setOperateType(WaybillStatus.WAYBILL_STATUS_CODE_FORWARD_INSPECTION);

                int resultCode = 0;

                try {
                    resultCode = this.taskService.add(this.toTask(JsonHelper
                                    .toJson(tWaybillStatus), tWaybillStatus,
                            Constants.POP_HANDOVER_OPERATE_TYPE));
                } catch (Exception e) {
                    this.log.error("forcePopReceive add Task --> 异常 waybillCode ：{}",waybillCode, e);
                }

                paramMap.put(waybillCode, resultCode);
            }
            return new PopJoinResponse<Map<String, Integer>>(
                    JdResponse.CODE_OK, JdResponse.MESSAGE_OK, paramMap);

        } catch (Exception e) {
            this.log.error("forcePopReceive --> 异常：", e);
            return new PopJoinResponse<Map<String, Integer>>(
                    PopJoinResponse.CODE_SERVICE_ERROR,
                    PopJoinResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    private Task toTask(String jsonVal, WaybillStatus tWaybillStatus,
                        Integer smallType) {
        Task task = new Task();
        task.setTableName(Task.TABLE_NAME_WAYBILL);
        task.setSequenceName(Task.TABLE_NAME_WAYBILL_SEQ);
        task.setKeyword1(tWaybillStatus.getWaybillCode());
        task.setKeyword2(tWaybillStatus.getPackageCode());
        task.setCreateSiteCode(tWaybillStatus.getCreateSiteCode());
        task.setBody(jsonVal);
        task.setType(tWaybillStatus.getOperateType());
        task.setOwnSign(BusinessHelper.getOwnSign());
        StringBuffer fingerprint = new StringBuffer();
        fingerprint.append(tWaybillStatus.getCreateSiteCode()).append("_")
                .append(
                        (tWaybillStatus.getReceiveSiteCode() == null ? "-1"
                                : tWaybillStatus.getReceiveSiteCode())).append(
                "_").append(tWaybillStatus.getOperateType())
                .append("_").append(tWaybillStatus.getWaybillCode())
                .append("_").append(tWaybillStatus.getOperateTime())
                .append("_").append(smallType);
        if (tWaybillStatus.getPackageCode() != null
                && !"".equals(tWaybillStatus.getPackageCode())) {
            fingerprint.append("_").append(tWaybillStatus.getPackageCode());
        }
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        PopJoinQuery dad = new PopJoinQuery();
        return task;

    }

    private List<PopPrint> convPopPrint(List<PackageState> data) {

        if (data == null || data.size() <= 0) {
            this.log.info("获取运单相关信息[List<PackageState>]数据为空");
            return null;
        }

        List<PopPrint> popPrints = new ArrayList<PopPrint>();
        for (PackageState packageState : data) {
            if ("12".equals(packageState.getState())
                    && "POP标签打印".endsWith(packageState.getRemark())) {
                PopPrint popPrint = new PopPrint();
                popPrint.setWaybillCode(packageState.getWaybillCode());
                popPrint.setCreateSiteCode(packageState.getOperatorSiteId());
                popPrint.setCreateSiteName(packageState.getOperatorSite());
                popPrint.setPrintPackCode(packageState.getOperatorUserId());
                popPrint.setPrintPackUser(packageState.getOperatorUser());
                popPrint.setPrintPackTime(packageState.getCreateTime());
                popPrints.add(popPrint);
            }
        }

        return popPrints;
    }

    public static class PopJoinQuery {
        /**
         * 分拣中心编号
         */
        private Integer createSiteCode;

        /**
         * 三方运单号
         */
        private String thirdWaybillCode;

        /**
         * 运单类型
         */
        private Integer waybillType;

        /**
         * 开始时间
         */
        private Date startTime;

        /**
         * 结束时间
         */
        private Date endTime;

        /**
         * POP商家ID
         */
        private Integer popSupId;

        /**
         * POP商家名称
         */
        private String popSupName;

        /* 操作人code */
        private Integer createUserCode;

        /* 操作人 */
        private String createUser;

        /**
         * 排队号
         */
        private String queueNo;

        /**
         * POP收货类型： 商家直送：1 托寄送货：2 司机上门收货：3
         */
        private Integer popReceiveType;

        /**
         * B商家ID
         */
        private Integer busiId;

        /**
         * B商家名称
         */
        private String busiName;


        private Pager<List<Inspection>> pager;

        public Integer getCreateSiteCode() {
            return createSiteCode;
        }

        public void setCreateSiteCode(Integer createSiteCode) {
            this.createSiteCode = createSiteCode;
        }

        public String getThirdWaybillCode() {
            return thirdWaybillCode;
        }

        public void setThirdWaybillCode(String thirdWaybillCode) {
            this.thirdWaybillCode = thirdWaybillCode;
        }

        public Integer getWaybillType() {
            return waybillType;
        }

        public void setWaybillType(Integer waybillType) {
            this.waybillType = waybillType;
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

        public Integer getPopSupId() {
            return popSupId;
        }

        public void setPopSupId(Integer popSupId) {
            this.popSupId = popSupId;
        }

        public String getPopSupName() {
            return popSupName;
        }

        public void setPopSupName(String popSupName) {
            this.popSupName = popSupName;
        }

        public Pager<List<Inspection>> getPager() {
            return pager;
        }

        public void setPager(Pager<List<Inspection>> pager) {
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

        public Integer getPopReceiveType() {
            return popReceiveType;
        }

        public void setPopReceiveType(Integer popReceiveType) {
            this.popReceiveType = popReceiveType;
        }

        public Integer getBusiId() {
            return busiId;
        }

        public void setBusiId(Integer busiId) {
            this.busiId = busiId;
        }

        public String getBusiName() {
            return busiName;
        }

        public void setBusiName(String busiName) {
            this.busiName = busiName;
        }
    }

    public static class PopPrintJoinQuery {
        /**
         * 分拣中心编号
         */
        private Integer createSiteCode;

        /**
         * 三方运单号
         */
        private String thirdWaybillCode;

        /**
         * 运单类型
         */
        private Integer waybillType;

        /**
         * 开始时间
         */
        private Date startTime;

        /**
         * 结束时间
         */
        private Date endTime;

        /**
         * POP商家ID
         */
        private Integer popSupId;

        /**
         * POP商家名称
         */
        private String popSupName;

        /* 操作人code */
        private Integer createUserCode;

        /* 操作人 */
        private String createUser;

        /**
         * 排队号
         */
        private String queueNo;

        /**
         * POP收货类型： 商家直送：1 托寄送货：2 司机上门收货：3
         */
        private Integer popReceiveType;

        /**
         * B商家ID
         */
        private Integer busiId;

        /**
         * B商家名称
         */
        private String busiName;


        private Pager<List<PopPrint>> pager;

        public Integer getCreateSiteCode() {
            return createSiteCode;
        }

        public void setCreateSiteCode(Integer createSiteCode) {
            this.createSiteCode = createSiteCode;
        }

        public String getThirdWaybillCode() {
            return thirdWaybillCode;
        }

        public void setThirdWaybillCode(String thirdWaybillCode) {
            this.thirdWaybillCode = thirdWaybillCode;
        }

        public Integer getWaybillType() {
            return waybillType;
        }

        public void setWaybillType(Integer waybillType) {
            this.waybillType = waybillType;
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

        public Integer getPopSupId() {
            return popSupId;
        }

        public void setPopSupId(Integer popSupId) {
            this.popSupId = popSupId;
        }

        public String getPopSupName() {
            return popSupName;
        }

        public void setPopSupName(String popSupName) {
            this.popSupName = popSupName;
        }

        public Pager<List<PopPrint>> getPager() {
            return pager;
        }

        public void setPager(Pager<List<PopPrint>> pager) {
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

        public Integer getPopReceiveType() {
            return popReceiveType;
        }

        public void setPopReceiveType(Integer popReceiveType) {
            this.popReceiveType = popReceiveType;
        }

        public Integer getBusiId() {
            return busiId;
        }

        public void setBusiId(Integer busiId) {
            this.busiId = busiId;
        }

        public String getBusiName() {
            return busiName;
        }

        public void setBusiName(String busiName) {
            this.busiName = busiName;
        }
    }
}
