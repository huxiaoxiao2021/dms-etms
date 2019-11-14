package com.jd.bluedragon.distribution.rest.waybill;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.areadest.AreaDestJsfService;
import com.jd.bd.dms.automatic.sdk.modules.areadest.dto.AreaDestJsfRequest;
import com.jd.bd.dms.automatic.sdk.modules.areadest.dto.AreaDestJsfVo;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.core.base.LdopWaybillUpdateManager;
import com.jd.bluedragon.core.base.OBCSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.EditWeightRequest;
import com.jd.bluedragon.distribution.api.request.ModifyOrderInfo;
import com.jd.bluedragon.distribution.api.request.PopAddPackStateRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.BaseResponse;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingDto;
import com.jd.bluedragon.distribution.cross.service.CrossSortingService;
import com.jd.bluedragon.distribution.eclpPackage.service.EclpPackageApiService;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.popPrint.domain.PopAddPackStateTaskBody;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.print.request.WaybillRescheduleRequest;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.distribution.reverse.domain.TwiceExchangeCheckDto;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.saf.WaybillSafService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.CancelFeatherLetterRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionCondition;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionException;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionQueryTypeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionRangeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionRequest;
import com.jd.bluedragon.distribution.waybill.domain.WaybillNoCollectionResult;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.LabelPrinting;
import com.jd.bluedragon.distribution.waybill.service.WaybillNoCollectionInfoService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.distribution.web.kuaiyun.weight.WeighByWaybillController;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.distribution.weight.domain.PackWeightVO;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.service.WeightAndVolumeCheckService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.LableType;
import com.jd.bluedragon.utils.OriginalType;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.RestHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResponseDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class WaybillResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
	private WaybillCommonService waybillCommonService;

    @Autowired
	private PopPrintService popPrintService;

    @Autowired
    private AirTransportService airTransportService;

	@Autowired
	private LabelPrinting labelPrinting;

    @Autowired
    private BaseMajorManager baseMajorManager;

	@Autowired
	private BaseMinorManager baseMinorManager;

    @Autowired
	@Qualifier("obcsManager")
	private OBCSManager obcsManager;

    @Autowired
    @Qualifier("dmsModifyOrderInfoMQ")
    private DefaultJMQProducer dmsModifyOrderInfoMQ;

	@Autowired
	private TaskService taskService;

	@Autowired
	private CrossSortingService crossSortingService;

	@Autowired
	private WeighByWaybillController weighByWaybillController;

	public static final Integer DMSTYPE = 10; // 建包

	@Autowired
	private JsfSortingResourceService jsfSortingResourceService;

	@Autowired
	WaybillTraceManager waybillTraceManager;

	@Autowired
	@Qualifier("ldopManager")
	private LDOPManager ldopManager;

	@Autowired
	private EclpPackageApiService eclpPackageApiService;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    private ReceiveWeightCheckService receiveWeightCheckService;

	@Autowired
	private WeightAndVolumeCheckService weightAndVolumeCheckService;

	@Autowired
	private SiteService siteService;
	/**
	 * 运单路由字段使用的分隔符
	 */
	private static final  String WAYBILL_ROUTER_SPLITER = "\\|";


	/* 运单查询 */
	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	private WaybillSafService waybillSafService;

	@Autowired
	private AreaDestJsfService areaDestJsfService;

	@Autowired
	private WaybillNoCollectionInfoService waybillNoCollectionInfoService;

    @Autowired
    private WaybillPrintService waybillPrintService;

    @Autowired
    private LdopWaybillUpdateManager ldopWaybillUpdateManager;

    /**
     * 根据运单号获取运单包裹信息接口
     *
     * @param waybillCode
     * @return
     */
    @GET
    @Path("/waybillAndPack/{startDmsCode}/{waybillCode}")
    public WaybillResponse<Waybill> getWaybillAndPack(@PathParam("startDmsCode") Integer startDmsCode,
    		@PathParam("waybillCode") String waybillCode) {
        // 判断传入参数
        if (startDmsCode == null || startDmsCode.equals(0) || StringUtils.isEmpty(waybillCode)) {
            this.logger.error("根据初始分拣中心-运单号【" + startDmsCode + "-" + waybillCode + "】获取运单包裹信息接口 --> 传入参数非法");
            return new WaybillResponse<Waybill>(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
        }
        // 调用服务
        try {
            Waybill waybill = this.waybillCommonService.findWaybillAndPack(waybillCode);

            if (waybill == null) {
                this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功, 无数据");
                return new WaybillResponse<Waybill>(JdResponse.CODE_OK_NULL,
    					JdResponse.MESSAGE_OK_NULL);
            }

            // 获取该运单号的打印记录
            try {

            	PopPrint popPrint = null;
            	try{
            		popPrint = this.popPrintService.findByWaybillCode(waybillCode);
				} catch (Exception e) {
					// dms db may be unconnected
				}
                this.logger.info("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录，popPrint：" + popPrint);
                if (popPrint != null) {
                    // 设置是否打印包裹
                    if (popPrint.getPrintPackCode() != null
                            && popPrint.getPrintPackTime() != null) {
                        waybill.setIsPrintPack(Waybill.IS_PRINT_PACK);
                    }
                    // 设置是否打印发票
                    if (popPrint.getPrintInvoiceCode() != null
                            && popPrint.getPrintInvoiceTime() != null) {
                        waybill.setIsPrintInvoice(Waybill.IS_PRINT_INVOICE);
                    }

                    waybill
						.setPrintCount(popPrint.getPrintCount() == null ? new Integer(0)
							: popPrint.getPrintCount());
                }
            } catch (Exception e) {
                this.logger
                        .error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 调用该运单号的打印记录(数据库)异常：", e);
            }

            this.setWaybillStatus(waybill);

            //this.setWaybillCrossCode(waybill, startDmsCode);

            // 设置航空标识
            this.setAirSigns(waybill, startDmsCode);

            this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");
            return new WaybillResponse<Waybill>(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK, waybill);

        } catch (Exception e) {
            // 调用服务异常
            this.logger.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
            return new WaybillResponse<Waybill>(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }


    /**
     * 根据运单号或包裹号获取运单包裹信息接口
     *
     * @param startDmsCode Or package
     * @return
     */
    @GET
    @Path("/waybillOrPack/{startDmsCode}/{waybillCodeOrPackage}")
    public WaybillResponse<Waybill> getWaybillOrPack(@PathParam("startDmsCode") Integer startDmsCode,
    		@PathParam("waybillCodeOrPackage") String waybillCodeOrPackage) {
        // 判断传入参数
        if (startDmsCode == null || startDmsCode.equals(0) || StringUtils.isEmpty(waybillCodeOrPackage)) {
            this.logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-" + waybillCodeOrPackage + "】获取运单包裹信息接口 --> 传入参数非法");
            return new WaybillResponse<Waybill>(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
        }

        // 转换运单号
        String waybillCode = WaybillUtil.getWaybillCode(waybillCodeOrPackage);

        // 调用服务
        try {
            Waybill waybill = this.waybillCommonService.findWaybillAndPack(waybillCode);

            if (waybill == null) {
                this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功, 无数据");
                return new WaybillResponse<Waybill>(JdResponse.CODE_OK_NULL,
    					JdResponse.MESSAGE_OK_NULL);
            }

            // 获取该运单号的打印记录
            try {
            	List<Pack> packs = waybill.getPackList();
            	if (packs != null && !packs.isEmpty()) {
            		if (BusinessHelper.checkIntNumRange(packs.size())) {
            			List<PopPrint> popPrintList = new ArrayList<PopPrint>();
            			try{
            				popPrintList = this.popPrintService.findAllByWaybillCode(waybillCode);
						} catch (Exception e) {
							// dms db may be unconnected
						}
	            		for (PopPrint popPrint : popPrintList) {
	            			if (Constants.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
	            				for (int i = 0; i < waybill.getPackList().size(); i++) {
	            					if (popPrint.getPackageBarcode().equals(packs.get(i).getPackCode())) {
	            						packs.get(i).setIsPrintPack(Waybill.IS_PRINT_PACK);
	            					}
	            				}
	            			} else if (Constants.PRINT_INVOICE_TYPE.equals(popPrint.getOperateType())) {
	            				waybill.setIsPrintInvoice(Waybill.IS_PRINT_INVOICE);
	            			}
	            		}
	            		this.logger.info("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录，popPrintList：" + popPrintList);
            		} else {
            			this.logger.error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录 运单包裹数大于限定值");
            		}
            	}
            } catch (Exception e) {
                this.logger
                        .error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 调用该运单号的打印记录(数据库)异常：", e);
            }

            // 增加SOP订单EMS全国直发
            if (Constants.POP_SOP_EMS_CODE.equals(waybill.getSiteCode())) {
            	waybill.setSiteName(Constants.POP_SOP_EMS_NAME);
            }

            this.setWaybillStatus(waybill);

            //this.setWaybillCrossCode(waybill, startDmsCode);

            this.setAirSigns(waybill, startDmsCode);

            this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");
            return new WaybillResponse<Waybill>(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK, waybill);

        } catch (Exception e) {
            // 调用服务异常
            this.logger.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
            return new WaybillResponse<Waybill>(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

    private void setAirSigns(Waybill waybill, Integer startDmsCode) {
    	if (waybill == null || StringUtils.isBlank(waybill.getWaybillCode()) || startDmsCode == null) {
    		return;
    	}

    	// 设置航空标识
		if (waybill.getBusiId() != null && !waybill.getBusiId().equals(0)) {
			this.logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId()
					+ "-" + startDmsCode + "-" + waybill.getSiteCode()
					+ "】根据基础资料调用设置航空标识开始");
			waybill.setAirSigns(this.airTransportService.getAirConfig(
					waybill.getBusiId(), startDmsCode,
					waybill.getSiteCode()));
			this.logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId()
					+ "-" + startDmsCode + "-" + waybill.getSiteCode()
					+ "】根据基础资料调用设置航空标识结束【" + waybill.getAirSigns() + "】");
		}
    }

    private void setWaybillStatus(Waybill waybill) {
    	if (waybill == null || StringUtils.isBlank(waybill.getWaybillCode())) {
    		return;
    	}

    	Boolean isDelivery = waybill.isDelivery();
    	if (isDelivery) {
    		waybill.setStatusAndMessage(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    	} else {
    		waybill.setStatusAndMessage(SortingResponse.CODE_293040,
					SortingResponse.MESSAGE_293040);
    	}

		// 验证运单号，是否锁定、删除等
    	com.jd.bluedragon.distribution.fastRefund.domain.WaybillResponse cancelWaybill = null;
		try {
			cancelWaybill = WaybillCancelClient.getWaybillResponse(waybill.getWaybillCode());
		} catch (Exception e) {
			this.logger.error("WaybillResource --> setWaybillStatus get cancelWaybill Error:", e);
		}

		if (cancelWaybill != null) {
			if (SortingResponse.CODE_29300.equals(cancelWaybill.getCode())) {
				if (isDelivery) {
					waybill.setStatusAndMessage(SortingResponse.CODE_29300,
							SortingResponse.MESSAGE_29300);
				} else {
					waybill.setStatusAndMessage(SortingResponse.CODE_293000,
							SortingResponse.MESSAGE_293000);
				}
			} else if (SortingResponse.CODE_29302.equals(cancelWaybill.getCode())) {
				if (isDelivery) {
					waybill.setStatusAndMessage(SortingResponse.CODE_29302,
			                SortingResponse.MESSAGE_29302);
				} else {
					waybill.setStatusAndMessage(SortingResponse.CODE_293020,
			                SortingResponse.MESSAGE_293020);
				}
			} else if (SortingResponse.CODE_29301.equals(cancelWaybill.getCode())) {
				if (isDelivery) {
					waybill.setStatusAndMessage(SortingResponse.CODE_29301,
			                SortingResponse.MESSAGE_29301);
				} else {
					waybill.setStatusAndMessage(SortingResponse.CODE_293010,
			                SortingResponse.MESSAGE_293010);
				}
			} else if (SortingResponse.CODE_29303.equals(cancelWaybill.getCode())) {
				waybill.setStatusAndMessage(SortingResponse.CODE_29303,
			                SortingResponse.MESSAGE_29303);
			}
		}
    }

	private boolean checkAireSigns(Waybill waybill) {
		// 设置航空标识
		boolean signs = false;
		if (waybill.getBusiId() != null && !waybill.getBusiId().equals(0)) {
			this.logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId() + "-"
					+ "-" + waybill.getSiteCode()
					+ "】根据基础资料调用设置航空标识开始");
			signs = this.airTransportService.getAirSigns(
					waybill.getBusiId());
			this.logger.info("B商家ID-初始分拣中心-目的站点【" + waybill.getBusiId() + "-"
					 + "-" + waybill.getSiteCode()
					+ "】根据基础资料调用设置航空标识结束【" + waybill.getAirSigns() + "】");
		}
		return signs;
	}
	/**
	 * 获取运单信息
	 * @param waybillCode 运单号
	 * @param packOpeFlowFlg 是否获取称重信息
	 * @return
	 */
    private Waybill findWaybillMessage(String waybillCode,Integer packOpeFlowFlg) {
		Waybill waybill = this.waybillCommonService.findWaybillAndPack(waybillCode);

		if (waybill == null) {
		    return waybill;
		}

		// 获取该运单号的打印记录
		try {
			List<Pack> packs = waybill.getPackList();
			if (packs != null && !packs.isEmpty()) {
				if (BusinessHelper.checkIntNumRange(packs.size())) {
		    		List<PopPrint> popPrintList = this.popPrintService.findAllByWaybillCode(waybillCode);
		    		for (PopPrint popPrint : popPrintList) {
		    			if (Constants.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
		    				for (int i = 0; i < waybill.getPackList().size(); i++) {
		    					if (popPrint.getPackageBarcode().equals(packs.get(i).getPackCode())) {
		    						packs.get(i).setIsPrintPack(Waybill.IS_PRINT_PACK);
		    					}
		    				}
		    			} else if (Constants.PRINT_INVOICE_TYPE.equals(popPrint.getOperateType())) {
		    				waybill.setIsPrintInvoice(Waybill.IS_PRINT_INVOICE);
		    			}
		    		}
		    		/**
		    		 * 获取称重流水，并设置包裹信息pWeight
		    		 */
		    		if(Constants.INTEGER_FLG_TRUE.equals(packOpeFlowFlg)){
		    			Map<String,PackOpeFlowDto> packOpeFlows = this.waybillCommonService.getPackOpeFlowsByOpeType(waybillCode,Constants.PACK_OPE_FLOW_TYPE_PSY_REC);
		    			if(packOpeFlows!=null&&!packOpeFlows.isEmpty()){
		    				for(Pack pack:packs){
			    				PackOpeFlowDto packOpeFlow = packOpeFlows.get(pack.getPackCode());
			    				if(packOpeFlow!=null&&packOpeFlow.getpWeight()!=null){
			    					pack.setpWeight(packOpeFlow.getpWeight().toString());
			    				}
			    			}
		    			}
		    		}
		    		this.logger.info("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录，popPrintList：" + popPrintList);
				} else {
					this.logger.error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 获取该运单号的打印记录 运单包裹数大于限定值");
				}
			}
		} catch (Exception e) {
		    this.logger
		            .error("根据运单号【" + waybillCode + "】获取运单包裹信息接口 --> 调用该运单号的打印记录(数据库)异常：", e);
		}

		// 增加SOP订单EMS全国直发
		if (Constants.POP_SOP_EMS_CODE.equals(waybill.getSiteCode())) {
			waybill.setSiteName(Constants.POP_SOP_EMS_NAME);
		}

		this.setWaybillStatus(waybill);
		return waybill;
	}

	/**
	 * 根据运单号或包裹号获取运单包裹信息接口
	 * 新接口调用预分拣接口获取基础资料信息
	 * @param waybillCodeOrPackage
	 * @return
	 */
	@GET
	@Path("waybill/waybillPack/{startDmsCode}/{waybillCodeOrPackage}/{localSchedule}/{paperless}")
	public WaybillResponse<Waybill> getwaybillPack(@PathParam("startDmsCode") Integer startDmsCode,
													  @PathParam("waybillCodeOrPackage") String waybillCodeOrPackage,@PathParam("localSchedule") Integer localSchedule
			,@PathParam("paperless") Integer paperless) {
		// 判断传入参数
		if (startDmsCode == null || startDmsCode.equals(0)
				|| StringUtils.isEmpty(waybillCodeOrPackage)) {
			this.logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-"
					+ waybillCodeOrPackage + "】获取运单包裹信息接口 --> 传入参数非法");
			return new WaybillResponse<Waybill>(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		// 转换运单号
		CallerInfo info = Profiler.registerInfo("DMS.BASE.WaybillResource.getwaybillPackOld", Constants.UMP_APP_NAME_DMSWEB,false, true);
        String waybillCode = WaybillUtil.getWaybillCode(waybillCodeOrPackage);

		// 调用服务
		try {

			Waybill waybill = findWaybillMessage(waybillCode,Constants.INTEGER_FLG_FALSE);

			if (waybill == null) {
				this.logger.info("运单号【" + waybillCode
						+ "】调用根据运单号获取运单包裹信息接口成功, 无数据");
				return new WaybillResponse<Waybill>(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
			//调用分拣接口获得基础资料信息
			this.setBasicMessageByDistribution(waybill, startDmsCode, localSchedule, paperless,null);

			this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");
			return new WaybillResponse<Waybill>(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK, waybill);

		} catch (Exception e) {
			Profiler.functionError(info);
			// 调用服务异常
			this.logger
					.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
			return new WaybillResponse<Waybill>(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}finally {
			Profiler.registerInfoEnd(info);
		}

	}

	@SuppressWarnings("unused")
	private void setBasicMessageByDistribution(Waybill waybill, Integer startDmsCode ,Integer localSchedule,Integer paperless,Integer startSiteType) {
		try {
			LabelPrintingRequest request = new LabelPrintingRequest();
			BaseResponseIncidental<LabelPrintingResponse> response = new BaseResponseIncidental<LabelPrintingResponse>();
			request.setWaybillCode(waybill.getWaybillCode());
			request.setDmsCode(startDmsCode);
			request.setStartSiteType(startSiteType);
			if (localSchedule!=null && !localSchedule.equals(0))
				request.setLocalSchedule(1);
			else
				request.setLocalSchedule(0);
			request.setCky2(waybill.getCky2());
			request.setOrgCode(waybill.getOrgId());

			//是否航空
			if(checkAireSigns(waybill)){
				//request
				request.setAirTransport(true);
			}

			request.setStoreCode(waybill.getStoreId());

			// 是否调度
			// request.setPreSeparateCode(waybill.getOldCode());
			if (localSchedule!=null && !localSchedule.equals(0))
				request.setPreSeparateCode(localSchedule);// 调度站点

			// 是否DMS调用
			request.setOriginalType(OriginalType.DMS.getValue());

			//是否有纸化
			if(paperless.equals(LableType.PAPER.getLabelPaper()))
				request.setLabelType(LableType.PAPER.getLabelPaper());
			else
				request.setLabelType(LableType.PAPERLESS.getLabelPaper());

			response = labelPrinting.dmsPrint(request);

			if(response==null || response.getData()==null){
				//
				this.logger.error("根据运单号【" + waybill.getWaybillCode()
						+ "】 获取预分拣的包裹打印信息为空response对象");
				return;
			}

			LabelPrintingResponse labelPrinting = response.getData();
			if(labelPrinting==null){
				this.logger.error("根据运单号【" + waybill.getWaybillCode()
						+ "】 获取预分拣的包裹打印信息为空labelPrinting对象");
				return;
			}

			waybill.setCrossCode(String.valueOf(labelPrinting
					.getOriginalCrossCode()));
			waybill.setTrolleyCode(String.valueOf(labelPrinting
					.getOriginalTabletrolley()));
			waybill.setTargetDmsCode(labelPrinting.getPurposefulDmsCode());
			waybill.setTargetDmsName(String.valueOf(labelPrinting
					.getPurposefulDmsName()));
			waybill.setTargetDmsDkh(String.valueOf(labelPrinting
					.getPurposefulCrossCode()));
			waybill.setTargetDmsLch(String.valueOf(labelPrinting
					.getPurposefulTableTrolley()));
			waybill.setAddress(labelPrinting.getOrderAddress());
			waybill.setJsonData(response.getJsonData());
			waybill.setRoad(labelPrinting.getRoad());

			if(labelPrinting.getRoad()==null|| labelPrinting.getRoad().isEmpty()){
				this.logger.error("根据运单号【" + waybill.getWaybillCode()
						+ "】 获取预分拣的包裹打印路区信息为空");
			}
		} catch (Exception e) {
			this.logger.error("根据运单号【" + waybill.getWaybillCode()
					+ "】 获取预分拣的包裹打印信息接口 --> 异常", e);
		} catch(Throwable ee) {
			this.logger.error("根据运单号【" + waybill.getWaybillCode()
					+ "】 获取预分拣的包裹打印信息接口 --> 异常", ee);
		}
	}
	/**
	 * 根据运单号或包裹号获取运单包裹信息接口
	 * 新接口调用预分拣接口获取基础资料信息
	 * @param waybillCodeOrPackage
	 * @return
	 */
	@GET
	@Path("waybill/waybillPack/{startDmsCode}/{waybillCodeOrPackage}/{localSchedule}/{paperless}/{startSiteType}")
    @JProfiler(jKey = "DMS.BASE.WaybillResource.getWaybillPack",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public WaybillResponse<Waybill> getwaybillPack(@PathParam("startDmsCode") Integer startDmsCode,
												   @PathParam("waybillCodeOrPackage") String waybillCodeOrPackage,@PathParam("localSchedule") Integer localSchedule
			,@PathParam("paperless") Integer paperless,@PathParam("startSiteType") Integer startSiteType) {
		return this.getWaybillPack(startDmsCode, waybillCodeOrPackage, localSchedule, paperless, startSiteType, Constants.INTEGER_FLG_FALSE);
	}
	/**
	 * 根据运单号或包裹号获取运单包裹信息接口
	 * @param startDmsCode
	 * @param waybillCodeOrPackage
	 * @param localSchedule
	 * @param paperless
	 * @param startSiteType
	 * @param packOpeFlowFlg - 是否获取称重流水信息
	 * @return
	 */
	@GET
	@Path("waybill/getWaybillPack/{startDmsCode}/{waybillCodeOrPackage}/{localSchedule}/{paperless}/{startSiteType}/{packOpeFlowFlg}")
	public WaybillResponse<Waybill> getWaybillPack(@PathParam("startDmsCode") Integer startDmsCode,
												   @PathParam("waybillCodeOrPackage") String waybillCodeOrPackage, @PathParam("localSchedule") Integer localSchedule
			, @PathParam("paperless") Integer paperless, @PathParam("startSiteType") Integer startSiteType, @PathParam("packOpeFlowFlg") Integer packOpeFlowFlg) {
		// 判断传入参数
		if (startDmsCode == null || startDmsCode.equals(0) || StringUtils.isEmpty(waybillCodeOrPackage)) {
			this.logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-"
					+ waybillCodeOrPackage + "】获取运单包裹信息接口 --> 传入参数非法");
			return new WaybillResponse<Waybill>(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		if (packOpeFlowFlg == null) {
			packOpeFlowFlg = Constants.INTEGER_FLG_FALSE;
		}

		//判断返调度目的地是否为3pl站点
		boolean isThreePLSchedule = false;
		BaseStaffSiteOrgDto scheduleSiteOrgDto;

		try {
			scheduleSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(localSchedule);
			if (scheduleSiteOrgDto != null) {
				if (Constants.BASE_SITE_OPERATESTATE.equals(scheduleSiteOrgDto.getOperateState())) {
					return new WaybillResponse<Waybill>(JdResponse.CODE_SITE_OFFLINE_ERROR, JdResponse.MESSAGE_SITE_OFFLINE_ERROR);
				}
				Integer localScheduleSiteType = scheduleSiteOrgDto.getSiteType();
				if (Constants.THIRD_SITE_TYPE.equals(localScheduleSiteType)) {
					isThreePLSchedule = true;
				}
			}
		} catch (Exception e) {
			logger.error("现场预分拣获取返调度目的地信息出错：" + waybillCodeOrPackage, e);
			return new WaybillResponse<Waybill>(JdResponse.CODE_SERVICE_ERROR, "查询返调度目的地信息失败!");
		}

		// 转换运单号
		String waybillCode = WaybillUtil.getWaybillCode(waybillCodeOrPackage);
		// 调用服务
		try {
			Waybill waybill = findWaybillMessage(waybillCode, packOpeFlowFlg);
			if (waybill == null) {
				this.logger.info("运单号【" + waybillCode
						+ "】调用根据运单号获取运单包裹信息接口成功, 无数据");
				return new WaybillResponse<Waybill>(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
			// 只有输入正确预分拣
			if (scheduleSiteOrgDto != null){
				// 根据运单打标和预分拣站点判断是否需要进行黑名单校验
				WaybillResponse<Waybill> response = this.validateWaybillBlackList(waybill, scheduleSiteOrgDto.getSiteType());
				if (response != null) {
					return response;
				}
			}

			//如果是现场预分拣目的地是3pl站点，则判断商家是否支持转3方配送
			if (isThreePLSchedule) {
				BasicTraderInfoDTO traderDto = baseMinorManager.getBaseTraderById(waybill.getBusiId());
				//不支持转三方时，给前端提示
				if (traderDto != null && !BusinessHelper.canThreePLSchedule(traderDto.getTraderSign())) {
					logger.warn("商家不支持转3方配送，返调度到3方站点失败：" + waybillCodeOrPackage);
					return new WaybillResponse<Waybill>(JdResponse.CODE_THREEPL_SCHEDULE_ERROR, JdResponse.MESSAGE_THREEPL_SCHEDULE_ERROR);
				}
			}

            if(scheduleSiteOrgDto != null
                    && waybillPrintService.isCodMoneyGtZeroAndSiteThird(scheduleSiteOrgDto.getSiteType(),scheduleSiteOrgDto.getSubType(), waybill
                    .getCodMoney())){
                logger.warn("codMoney大于0不能分配三方站点waybillCode[{}]siteType[{}]subType[{}]codMoney[{}]",
                        waybillCodeOrPackage,scheduleSiteOrgDto.getSiteType(),scheduleSiteOrgDto.getSubType(),waybill.getCodMoney());
                return new WaybillResponse<>(JdResponse.CODE_CODMONAY_THIRD_SITE_ERROR, JdResponse.MESSAGE_CODMONAY_THIRD_SITE_ERROR);
            }

			//暂时下线签单返回的校验 2019年3月12日20:59:26
            /*String waybillSign = waybill.getWaybillSign();
			if(BusinessHelper.isSignatureReturnWaybill(waybillSign) && scheduleSiteOrgDto != null
                    && !Integer.valueOf(Constants.BASE_SITE_SITE).equals(scheduleSiteOrgDto.getSiteType())){
                logger.warn("此运单要求签单返回，只能分配至自营站点：" + waybillCodeOrPackage);
                return new WaybillResponse<Waybill>(JdResponse.CODE_SITE_SIGNRE_ERROR, JdResponse.MESSAGE_SITE_SIGNRE_ERROR);
            }*/

			//调用分拣接口获得基础资料信息
			this.setBasicMessageByDistribution(waybill, startDmsCode, localSchedule, paperless, startSiteType);

			this.logger.info("运单号【" + waybillCode + "】调用根据运单号获取运单包裹信息接口成功");
			return new WaybillResponse<Waybill>(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK, waybill);

		} catch (Exception e) {
            // 调用服务异常
			this.logger
					.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
			return new WaybillResponse<Waybill>(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}

	}

    /**
	 * <p>
	 *     返调度获取打印信息
	 * </p>
	 * 1.waybillCancel进行拦截的验证
	 * 2.调用waybill/waybillPack 获取包裹信息和调度站点的基础资料的信息
	 */
	@POST
	@Path("/waybill/reSchedule")
	public WaybillResponse<Waybill> PackageReschedule(WaybillRescheduleRequest request){
		WaybillResponse<Waybill> response = new WaybillResponse<>();
		if (null == request) {
			response.setCode(WaybillResponse.CODE_PARAM_ERROR);
			response.setMessage(WaybillResponse.MESSAGE_PARAM_ERROR);
			return response;
		}
        /* 校验单号是否正确 */
		String barCode = request.getBarCode();/* 调度单号 */
		if (StringHelper.isEmpty(barCode) || !WaybillUtil.isPackageCode(barCode) || !WaybillUtil.isWaybillCode(barCode)) {
			logger.warn("WaybillResource.PackageReschedule-->输入的单号不正确：{}",request.toString());
			response.setCode(WaybillResponse.CODE_PARAM_ERROR);
			response.setMessage(WaybillResponse.MESSAGE_PARAM_ERROR);
			return response;
		}

        /* 校验基本参数是否确实 */
		if (null == request.getStartDmsCode() || request.getStartDmsCode() <= 0) {
			response.setCode(WaybillResponse.CODE_PARAM_ERROR);
			response.setMessage("调度分拣中心无效");
			return response;
		}
		if (null == request.getRescheduleSiteCode() || request.getRescheduleSiteCode() <= 0) {
			response.setCode(WaybillResponse.CODE_PARAM_ERROR);
			response.setMessage("调度站点无效");
			return response;
		}
		if (null == request.getPaperless()) {
			response.setCode(WaybillResponse.CODE_PARAM_ERROR);
			response.setMessage("是否无纸化参数错误");
			return response;
		}
		if (null == request.getStartSiteType()) {
			response.setCode(WaybillResponse.CODE_PARAM_ERROR);
			response.setMessage("始发站点类型错误");
			return response;
		}

		String waybillCode = WaybillUtil.getWaybillCode(barCode);
		try {
            /* 1.调用waybillCancel检查拦截信息 */
			String waybillcancelCheckUrl =  PropertiesHelper.newInstance().getValue("DMSVER_ADDRESS")
					+ "/services/waybills/cancel" + "?packageCode=" + waybillCode;
			JdResponse waybillCancelResponse = RestHelper.jsonGetForEntity(waybillcancelCheckUrl,JdResponse.class);
			if (null != waybillCancelResponse && !JdResponse.CODE_OK.equals(waybillCancelResponse.getCode())) {
				logger.warn("WaybillResource.PackageReschedule-->此单{}属于拦截订单,无法进行返调度",waybillCode);
				response.setCode(waybillCancelResponse.getCode());
				response.setMessage(waybillCancelResponse.getMessage());
				return response;
			}

            /* 2.调用com.jd.bluedragon.distribution.rest.waybill.WaybillResource.getWaybillPack */
			response = this.getwaybillPack(request.getStartDmsCode(),barCode,
					request.getRescheduleSiteCode(),request.getPaperless(),request.getStartSiteType());
			if (null == response) {
				logger.warn("WaybillResource.PackageReschedule-->获取调度打印信息失败:{}",request.toString());
				response = new WaybillResponse<>(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
				return response;
			}
			/* 帮客户端校验返回结果 */
			Waybill entity = response.getData();
			if (null == entity) {
				response.setCode(JdResponse.CODE_RE_SCHEDULE_WAYBILL_NO_INFO);
				response.setMessage(JdResponse.MESSAGE_RE_SCHEDULE_WAYBILL_NO_INFO);
				return response;
			} else if (entity.getPackList().isEmpty()) {
				response.setCode(JdResponse.CODE_RE_SCHEDULE_WAYBILL_NO_PACKAGE);
				response.setMessage(JdResponse.MESSAGE_RE_SCHEDULE_WAYBILL_NO_PACKAGE);
				return response;
			}
//			else if (22 == entity.getType() || 10000 == entity.getType()) {
//				response.setCode(JdResponse.CODE_RE_SCHEDULE_WAYBILL_NO_THIS_PACKAGE);
//				response.setMessage(JdResponse.MESSAGE_RE_SCHEDULE_WAYBILL_NO_THIS_PACKAGE);
//				return response;
//			}
//			FIX: 2019/4/5 是否包含当前包裹
			for (Pack packageItem : entity.getPackList() ) {
				/* 设置重量 */
				if (null == packageItem.getWeight()) {
					packageItem.setWeight("0.00");
				}
				/* 设置包裹序列值 */
				packageItem.setPackageIndex(packageItem.getPackSerial() + "/" + entity.getPackageNum());
			}
			/* 设置预分拣站点的值 */
			entity.setSiteCode(request.getRescheduleSiteCode());
			entity.setSiteName(request.getRescheduleSiteName());

		} catch (Exception e) {
			logger.error("WaybillResource.PackageReschedule-->返调度操作失败，发生异常",e);
			response = new WaybillResponse<>(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
		}

		return response;
	}

	/**
	 * 根据运单打标和预分拣站点判断是否需要校验黑名单
	 *
	 * @param waybill 运单信息
	 * @return true-需要，false-不需要
	 */
	private WaybillResponse validateWaybillBlackList(Waybill waybill, Integer localScheduleSiteType) throws Exception {
		if (BusinessHelper.isReverseToStore(waybill.getWaybillSign())) {
			Integer cky2 = null;
			Integer storeId = null;
			// 是否逆向换单运单号
			if (WaybillUtil.isSwitchCode(waybill.getWaybillCode())) {
				WaybillManageDomain oldWaybillState = this.getOldWaybillStateByReturnWaybillCode(waybill.getWaybillCode());
				if (oldWaybillState != null) {
					cky2 = oldWaybillState.getCky2();
					storeId = oldWaybillState.getStoreId();
				}
			} else {
				cky2 = waybill.getCky2();
				storeId = waybill.getStoreId();
			}
			if (this.doValidateBlackList(cky2, storeId)) {
				BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(waybill.getSiteCode());
				if (siteOrgDto != null && BusinessHelper.isWms(siteOrgDto.getSiteType())) {
					return new WaybillResponse<Waybill>(JdResponse.CODE_STORE_BLACKLIST_ERROR, JdResponse.MESSAGE_STORE_BLACKLIST_ERROR);
				} else {
					// 操作现场预分拣新站点只能选择站点类型不能选仓，若选择仓则给出相应提示
					if (BusinessHelper.isWms(localScheduleSiteType)) {
						return new WaybillResponse<Waybill>(JdResponse.CODE_SITE_BLACKLIST_ERROR, JdResponse.MESSAGE_SITE_BLACKLIST_ERROR);
					}
				}
			}
		}
		return null;
	}

    /**
     * 通过返单运单号获取旧单的运单中waybillState对象
     *
     * @param waybillCode
     * @return
     */
    private WaybillManageDomain getOldWaybillStateByReturnWaybillCode(String waybillCode) {
        BaseEntity<com.jd.etms.waybill.domain.Waybill> oldBaseWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
        if (oldBaseWaybill.getResultCode() == 1 && oldBaseWaybill.getData() != null) {
            BaseEntity<BigWaybillDto> oldBaseEntity = waybillQueryManager.getDataByChoice(oldBaseWaybill.getData().getWaybillCode(), false, false, true, false);
            if (oldBaseEntity.getResultCode() == 1 && oldBaseEntity.getData() != null && oldBaseEntity.getData().getWaybillState() != null) {
                return oldBaseEntity.getData().getWaybillState();
            }
        }
        return null;
    }

	/**
	 * 调用运单接口进黑名单校验
	 *
	 * @param cky2
	 * @param storeId
	 * @return
	 */
	private boolean doValidateBlackList(Integer cky2, Integer storeId) throws Exception {
		// 调用运单接口
		if (cky2 != null && storeId != null) {
			try {
				// 如果接口调用成功且是强制换单返回true，否则返回false
				Boolean result = waybillQueryManager.ifForceCheckByWarehouse(cky2, storeId);
				if (result != null) {
					return result;
				}
			} catch (Exception e){
				logger.error("调用运单JSF接口-根据配送中心ID和仓ID查询是否强制换单-发生异常", e);
				throw e;
			}
		}
		return false;
	}

    /**
     * 根据运单号或包裹号获取运单包裹信息接口
     * 新接口调用预分拣接口获取基础资料信息
	 * @param busiId
	 * @param startDmsCode
	 * @param siteCode
     * @return
     */
    @GET
    @Path("/waybillinfo/{busiId}/{startDmsCode}/{siteCode}")
    public String getAirConfigRest(@PathParam("busiId") Integer busiId,
			@PathParam("startDmsCode") Integer startDmsCode,@PathParam("siteCode") Integer siteCode) {
    	Integer signs = this.airTransportService.getAirConfig(
				busiId, startDmsCode,
				siteCode);

    	if(signs.equals(1))
    		return "Y";

    	return "N";
    }


    /**
     * 根据运单号或包裹号获取运单包裹信息接口
     * 新接口调用预分拣接口获取基础资料信息
     * @param busiId Or package
     * @return
     */
    @GET
    @Path("/airSigns/{busiId}")
    public String getAirSigns(@PathParam("busiId") Integer busiId) {
    	boolean signs = this.airTransportService.getAirSigns(busiId);

    	if(signs)
    		return "Y";

    	return "N";
    }


    /**
     * 获取返单商家ID  作者：wangtingwei@jd.com  时间：2014-08-04
     * @param fwaybillcode F码返单号
     * @return 返回F码运单信息
     */
    @GET
    @Path("/fbackwaybill/{fbackwaybillcode}")
    public InvokeResult<Waybill> getFBackWaybill(@PathParam("fbackwaybillcode") String fwaybillcode){
        this.logger.info("获取F返单商家信息"+fwaybillcode);
        InvokeResult<Waybill> result=new InvokeResult<Waybill>();
        Waybill waybill =null;
        try{
            waybill=this.waybillCommonService.findByWaybillCode(fwaybillcode);
            result.setData(waybill);
            result.setCode(JdResponse.CODE_OK);
            result.setMessage(JdResponse.MESSAGE_OK);
        }catch (Exception e){
            this.logger.error("根据运单号【" + fwaybillcode + "】 获取运单包裹信息接口",e);
            result.setCode(JdResponse.CODE_SERVICE_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
		return result;
    }

    /**
     * 获取返单商家编号
     * @param fWaybillCode
     * @return
     */
    @GET
    @Path("/getfwaybillcustomercode/{fWaybillCode}")
    public InvokeResult<String> getFWaybillCustomerCode(@PathParam("fWaybillCode") String fWaybillCode){
        InvokeResult<String> result=new InvokeResult<String>();
        InvokeResult<Waybill> waybill=this.getFBackWaybill(fWaybillCode);
        result.setCode(waybill.getCode());
        result.setMessage(waybill.getMessage());
        if(result.getCode()==200){
            BaseStaffSiteOrgDto site= baseMajorManager.getBaseSiteBySiteId(waybill.getData().getBusiId());
            if(null!=site){
                result.setData(site.getDmsSiteCode());
            }else {
                result.setCode(500);
                result.setMessage("获取商家对应七位编号失败");
            }
        }
        return result;
    }

    /**
     * 获取订单目的分拣中心siteCode
     * @param startDmsCode 起始分拣中心
     * @param startDmsCode 运单号
     * @return
     */
    @GET
    @Path("/waybill/getTargetDmsCenter/{startDmsCode}/{siteCode}")
    public BaseResponse getTargetDmsCenter(@PathParam("startDmsCode") Integer startDmsCode, @PathParam("siteCode") Integer siteCode){
    	BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    	try{
    		BaseStaffSiteOrgDto  br = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
    		if(br!=null) response.setSiteCode(br.getDmsId());
    	}catch(Exception e){
    		this.logger.error("根据运单号【" + startDmsCode + "-" + siteCode + "】 获取目的分拣中心信息接口", e);
    	}
    	return response;
    }

    /**
     * 获取订单目的分拣中心siteCode
     * @param startDmsCode 起始分拣中心
     * @param siteCode 预分拣站点
     * @param receiveCode 目的分拣中心
     * @return
     */
    @GET
    @Path("/waybill/getBuildPackageRule/{startDmsCode}/{siteCode}/{receiveCode}")
    public BaseResponse getBuildPackageRule(@PathParam("startDmsCode") Integer startDmsCode, @PathParam("siteCode") Integer siteCode
    		, @PathParam("receiveCode") Integer receiveCode){
    	BaseResponse response = new BaseResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    	try{
    		//获取站点所在的分拣中心
    		BaseStaffSiteOrgDto  br = this.baseMajorManager.getBaseSiteBySiteId(siteCode);
    		if(br!=null){
    			List<CrossSortingDto> list =crossSortingService.getQueryByids(startDmsCode, receiveCode, br.getDmsId());
    			if(list!=null && !list.isEmpty()){
    				response.setCode(null);
    				response.setMessage("符合建包规则"+JsonHelper.toJson(list));
    			}
    		}
    	}catch(Exception e){
    		this.logger.error("getBuildPackageRule根据运单号【" + startDmsCode +"-"+ siteCode + "】 获取目的分拣中心信息接口",e);
    	}
    	return response;
    }

	@POST
	@Path("/waybill/sendModifyWaybillMQ")
	public JdResponse sendModifyWaybillMq(ModifyOrderInfo modifyOrderInfo)
	{
		JdResponse jdResponse=new JdResponse();
		try {
			String json = JsonHelper.toJson(modifyOrderInfo);
			//messageClient.sendMessage("dms_modify_order_info", json, modifyOrderInfo.getOrderId());
            dmsModifyOrderInfoMQ.send(modifyOrderInfo.getOrderId(),json);
			jdResponse.setCode(200);
			jdResponse.setMessage("成功");
			logger.info("dms_modify_order_info执行订单修改电话或者地址发MQ成功 " + modifyOrderInfo.getOrderId());
		}catch (Exception e){
			jdResponse.setCode(1000);
			jdResponse.setMessage("执行订单修改MQ消息失败");
			logger.error("dms_modify_order_info执行订单修改电话或者地址MQ失败" + modifyOrderInfo.getOrderId() + "，失败原因 " + e);
		}
		return  jdResponse;
	}

	@POST
	@Path("/waybill/sendTrace")
	public JdResponse sendBtTrace(TaskRequest request ){
		JdResponse jdResponse=new JdResponse();
		try {
			Assert.notNull(request, "request must not be null");
			logger.info(request.getSiteCode()+"站点标签打印"+request.getKeyword2());
			TaskResponse response = null;
			if (StringUtils.isBlank(request.getBody())) {
				response = new TaskResponse(JdResponse.CODE_PARAM_ERROR,
						JdResponse.MESSAGE_PARAM_ERROR);
				return response;
			}
			String eachJson = request.getBody();
			logger.warn("[" + request.getType() + "]" + eachJson);
			Task tTask = new Task();
			tTask.setKeyword1(request.getKeyword1());
			tTask.setCreateSiteCode(request.getSiteCode());
			tTask.setKeyword2(request.getKeyword2());
			tTask.setReceiveSiteCode(request.getReceiveSiteCode());
			tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
			tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
			tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
			String ownSign = BusinessHelper.getOwnSign();
			tTask.setOwnSign(ownSign);

			tTask.setBody(request.getBody());
			this.taskService.add(tTask, true);
			jdResponse.setMessage(JdResponse.MESSAGE_OK);
			jdResponse.setCode(JdResponse.CODE_OK);
		}catch (Exception ex) {
			logger.error("站点标签打印失败,参数:" + JsonHelper.toJson(request), ex);
			return new TaskResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}

		return  jdResponse;
	}


	@GET
	@GZIP
	@Path("/waybill/suk/{waybillCode}")
	public WaybillResponse<BigWaybillDto> getWaybillSku(@PathParam("waybillCode") String waybillCode){
		WaybillResponse<BigWaybillDto> waybillResponse = new WaybillResponse<BigWaybillDto>();
		try{
			WChoice wChoice = new WChoice();
			wChoice.setQueryWaybillC(true);
			wChoice.setQueryWaybillE(true);
			wChoice.setQueryWaybillM(true);
			wChoice.setQueryGoodList(true);
			com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(
					waybillCode, wChoice);
			if(null == baseEntity || baseEntity.getData() == null){
				logger.error("根据运单号获取SKU信息接口为空");
				waybillResponse.setCode(JdResponse.CODE_OK_NULL);
				waybillResponse.setMessage(JdResponse.MESSAGE_OK_NULL);
				return waybillResponse;
			}

			waybillResponse.setCode(JdResponse.CODE_OK);
			waybillResponse.setMessage(JdResponse.MESSAGE_OK);
			waybillResponse.setData(baseEntity.getData());
			return waybillResponse;
		}catch (Throwable ex){
			logger.error("根据运单号获取SKU信息接口失败",ex);
			waybillResponse.setCode(JdResponse.CODE_INTERNAL_ERROR);
			waybillResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
			return waybillResponse;
		}
	}

	/**
	 * 获取运单称重数据queryPackcode
	 *
	 *
	 * 返回示例：
		 成功{"code":200,"message":"OK","data":[
		 {
		 "waybillCode":"111111111-1-",
		 "packageBarcode":10,
		 "againWeight":100,
		 "operatorUserId":12.3,
		 "operatorUser":30.5,
		 "operatorSite":1010,
		 "operatorSiteId":"张三",
		 "operatorTime":39
		 }]}
		 失败{"code":400,"message":"ERROR"}
		 失败{"code":401,"message":"*****"}
	 */
	@GET
	@Path("/waybill/queryPackcode/{waybillCode}")
	public InvokeResult<List<PackageWeigh>> queryPackcode(@PathParam("waybillCode") String waybillCode){


		InvokeResult<List<PackageWeigh>> result = new InvokeResult<List<PackageWeigh>>();
		try{

			result = this.waybillCommonService.getPackListByCode(waybillCode);

			logger.info("/waybill/queryPackcode success waybillCode--> "+waybillCode);
		}catch (Exception e){

			logger.error("/waybill/queryPackcode  waybillCode--> "+waybillCode+" | "+e.getMessage());

			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}

	/**
	 * 提交POP打印数据至运单
	 *
	 *  只支持state = -250
	 *
	 * 改造异步模式
	 * 	预埋worker 至  task_pop 中 type 6666
	 *
	 *  原WEB 接口 入参
	 * {"packageBarcode":"VA00010628892","waybillCode":"VA00010628892","operatorUserId":"10053",
	 * "operatorUser":"邢松","operatorSite":"北京马驹桥分拣中心",
	 * "operatorSiteId":"910","state":"-250","remark":"驻厂标签打印","createTime":1516019841532}
	 * @return
	 */
	@POST
	@Path("/waybill/addPackState")
	public InvokeResult<Boolean> addPackState(PopAddPackStateRequest req){


		InvokeResult<Boolean> result = new InvokeResult<Boolean>();
		result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
		result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
		result.setData(true);
		try{
			if(!WaybillStatus.WAYBILL_TRACK_POP_PRINT_STATE.equals(req.getState()) || StringUtils.isBlank(req.getRemark())){
				logger.warn("/waybill/addPackState warn context--> " +JsonHelper.toJson(req));
				return result;
			}
			PopAddPackStateTaskBody popAddPackStateTaskBody = new PopAddPackStateTaskBody();

            popAddPackStateTaskBody.setPackageCode(req.getPackageBarcode());
            popAddPackStateTaskBody.setWaybillCode(req.getWaybillCode());
            popAddPackStateTaskBody.setCreateSiteCode(req.getOperatorSiteId());
            popAddPackStateTaskBody.setCreateSiteName(req.getOperatorSite());
            popAddPackStateTaskBody.setOperatorId(req.getOperatorUserId());
            popAddPackStateTaskBody.setOperator(req.getOperatorUser());
            popAddPackStateTaskBody.setOperateType(WaybillStatus.WAYBILL_TRACK_POP_PRINT.toString());
            popAddPackStateTaskBody.setRemark(req.getRemark());
            popAddPackStateTaskBody.setOperateTime(DateHelper.formatDateTime(new Date()));

			String body = JsonHelper.toJson(popAddPackStateTaskBody);



			Task task = new Task();
			task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
			task.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
			task.setSequenceName(Task.getSequenceName(task.getTableName()));
			task.setKeyword1(req.getRemark());
			task.setKeyword2(WaybillStatus.WAYBILL_TRACK_POP_PRINT.toString());
			task.setCreateSiteCode(new Integer(req.getOperatorSiteId()));
			task.setBody(body);
			task.setOwnSign(BusinessHelper.getOwnSign());

			taskService.add(task,true);  //直接创建task对象。因为taskService.toTask


			logger.info("/waybill/addPackState success context--> " +JsonHelper.toJson(req));
		}catch (Exception e){

			logger.error("/waybill/addPackState  context-->" +JsonHelper.toJson(req)+"  "+e.getMessage());

			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
			result.setData(false);
		}
		return result;

	}


	/**
	 *	重量上传
	 *	模拟离线称重任务
	 *
	 * 原WEB接口入参
	 * [{"packageBarcode":"VA00013129830-1-1-","againWeight":12.3,
	 * "waybillCode":"VA00013129830","operatorUserId":10053,"operatorUser":"邢松",
	 * "operatorSiteId":910,
	 * "operatorSite":"北京马驹桥分拣中心"}]
	 *
	 * @param req
	 * @return
	 *
	 * 成功{"code":200,"message":"OK","data":true}
		失败{"code":401,"message":"ERROR","data":false}或{"code":400,"message":"ERROR","data":false}
	 */
	@POST
	@Path("/waybill/editWeight")
	public InvokeResult<Boolean>editWeight(List<EditWeightRequest> req){
		InvokeResult<Boolean> result = new InvokeResult<Boolean>();

		//模拟离线称重 TASK
		try{

			if(req!=null && req.size()>0){
				for(EditWeightRequest editWeightRequest : req){

					TaskRequest taskRequest = new TaskRequest();
					taskRequest.setType(Task.TASK_TYPE_WEIGHT);
					taskRequest.setKeyword1(editWeightRequest.getWaybillCode());
					taskRequest.setKeyword2("批量分拣称重操作");
					taskRequest.setSiteCode(editWeightRequest.getOperatorSiteId());

					List<PackOpeDto> packOpeDtoList = new ArrayList<PackOpeDto>();
					PackOpeDto packOpeDto = new PackOpeDto();
					packOpeDtoList.add(packOpeDto);
					packOpeDto.setWaybillCode(editWeightRequest.getWaybillCode());
					packOpeDto.setOpeType(1);
					List<PackOpeDetail> packOpeDetailList = new ArrayList<PackOpeDetail>();
					PackOpeDetail packOpeDetail = new PackOpeDetail();
					packOpeDetail.setPackageCode(editWeightRequest.getPackageBarcode());
					packOpeDetail.setpWeight(editWeightRequest.getAgainWeight());
					packOpeDetail.setOpeUserId(editWeightRequest.getOperatorUserId());
					packOpeDetail.setOpeUserName(editWeightRequest.getOperatorUser());
					packOpeDetail.setOpeSiteId(editWeightRequest.getOperatorSiteId());
					packOpeDetail.setOpeSiteName(editWeightRequest.getOperatorSite());
					packOpeDetail.setOpeTime(DateHelper.formatDateTime(new Date(Long.valueOf(editWeightRequest.getOpeTime()))));

					packOpeDetailList.add(packOpeDetail);
					packOpeDto.setOpeDetails(packOpeDetailList);

					//转换JSON 存入body
					String body = JsonHelper.toJson(packOpeDtoList);
					taskRequest.setBody(body);

					taskService.add(this.taskService.toTask(taskRequest, body),true);

				}

				result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
				result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
				result.setData(true);
			}else{

				result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
				result.setMessage(InvokeResult.PARAM_ERROR);
				result.setData(false);
			}

			logger.info("/waybill/editWeight success context--> " +JsonHelper.toJson(req));
		}catch (Exception e){

			logger.error("/waybill/editWeight  context-->" +JsonHelper.toJson(req)+"  "+e.getMessage());

			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
			result.setData(false);
		}

		return result;
	}
	/**
	 * 运单称重对外接口
	 * @param req 入参
	 * @return
	 */
	@POST
	@Path("/waybill/weight")
	@BusinessLog(sourceSys = 1,bizType = 1902,operateType = 1902001)
	public InvokeResult<Boolean> saveWaybillWeight(WaybillWeightVO req){

		InvokeResult<Boolean> checkResult = weighByWaybillController.verifyWaybillReality(req.getCodeStr());

		if(checkResult.getData()){
			//通过
			req.setStatus(10); //存在运单
			InvokeResult<Boolean> insertResult = weighByWaybillController.insertWaybillWeight(req);
			return insertResult;
		}else{
			//校验没通过
			return checkResult;
		}
	}

    /**
     * B网运单称重 校验接口
     * @param codeStr 入参
     * @return
     */
    @GET
    @Path("/waybill/checkWeight/{codeStr}")
    public InvokeResult<Boolean> checkWeight(@PathParam("codeStr") String codeStr){

       return weighByWaybillController.verifyWaybillReality(codeStr);

    }

	/**
	 * 获得预分拣站点和路由下一节点
	 * @param siteCode 当前站点
	 * @param packageCode 包裹号
	 * @return
	 */
	@GET
	@Path("/preSortingSiteCodeAndNextRouter/{siteCode}/{packageCode}")
	public InvokeResult<String> getPreSortingSiteCodeAndNextRouter(@PathParam("siteCode") Integer siteCode,
																   @PathParam("packageCode") String packageCode) {
		InvokeResult invokeResult =new InvokeResult();
		String result = "";
		Integer preSortingSiteCode = null;
		Integer nextRouterSiteCode = null;
		try{
			if(StringHelper.isNotEmpty(packageCode) && siteCode != null){
				//获得运单的预分拣站点
				Waybill waybill = waybillCommonService.findWaybillAndPack(WaybillUtil.getWaybillCode(packageCode), true, false, false, false);
				if(waybill != null && StringHelper.isNotEmpty(waybill.getWaybillCode())){
					preSortingSiteCode = waybill.getSiteCode();
					//获得路由中的下一节点
					String routerStr = jsfSortingResourceService.getRouterByWaybillCode(waybill.getWaybillCode());
					if(StringUtils.isNotBlank(routerStr)){
						String[] routers = routerStr.split(WAYBILL_ROUTER_SPLITER);
						if(routers != null && routers.length > 0) {
							for (int i = 0; i < routers.length - 1; i++) {
								if(siteCode.equals(Integer.valueOf(routers[i]))){
									nextRouterSiteCode = Integer.valueOf(routers[i+1]);
									break;
								}
							}
						}
					}
				}
			}else {
				invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
				invokeResult.setMessage("传入的参数不能为空");
				return invokeResult;
			}
		}catch (Exception e){
			this.logger.error("批量一车一单发货获取预分拣站点或路由信息异常" + packageCode,e);
		}
		if(preSortingSiteCode == null){
			invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			invokeResult.setMessage("根据包裹号获取预分拣站点失败");
			return invokeResult;
		}else if(nextRouterSiteCode == null){
			result = result + preSortingSiteCode ;
		}else {
			result = result + preSortingSiteCode + "," + nextRouterSiteCode ;
		}
		invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
		invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
		invokeResult.setData(result);
		return invokeResult;
	}

	/**
	 * 获取目的站点和路由的下一跳站点（新接口
	 * 旧：{@link WaybillResource#getPreSortingSiteCodeAndNextRouter(java.lang.Integer, java.lang.String)}
	 * <p>
	 *     获取路由关系可以从路由jsfSortingResourceService.getRouterByWaybillCode(waybill.getWaybillCode())
	 *     或者从自动化的发货关系配置中AreaDestJsfService.findAreaDest(AreaDestJsfRequest request)获取结果
	 *     根据operateType判断哪个源获取。
	 *     <doc>
	 *         operateType:1 路由
	 *         operateType:2 配置
	 *     </doc>
	 * </p>
	 *
	 * @param request createSiteCode 当前站点
	 * @param request packageCode 包裹号
	 * @param request operateType 操作类型，1走路由的查询，2走配置的查询
	 * @param request operateTime 操作时间 形如：2018-12-20 16:27:17
	 * @return
	 */
	@POST
	@Path("/getBarCodeAllRouters")
	@JProfiler(jKey = "DMS.WEB.WaybillResource.getBarCodeAllRouters")
	public InvokeResult<List<Integer>> getBarCodeAllRouters(PdaOperateRequest request) {
		InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();
		/* 检查参数的有效性 */
		if (null == request || StringHelper.isEmpty(request.getPackageCode())
				|| StringHelper.isEmpty(request.getOperateTime()) || null == request.getCreateSiteCode()) {
			logger.error("WaybillResource.getBarCodeAllRouters-->参数错误：{" + JsonHelper.toJson(request) + "}");
			result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			result.setMessage(InvokeResult.PARAM_ERROR);
			return result;
		}
		/* 校验单号是否是运单号或者是包裹号 */
		if (!WaybillUtil.isWaybillCode(request.getPackageCode()) && !WaybillUtil.isPackageCode(request.getPackageCode())) {
			logger.error("WaybillResource.getBarCodeAllRouters-->参数错误：{" + JsonHelper.toJson(request) + "}，单号不是京东单号");
			result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			result.setMessage("单号不正确，未通过京东单号规则校验");
			return result;
		}

		String barCode = request.getPackageCode();
		String waybillCode = WaybillUtil.getWaybillCode(barCode);
		Integer operateSiteCode = request.getCreateSiteCode();
		Integer operateType = request.getOperateType();
		long operateTime = DateHelper.parseAllFormatDateTime(request.getOperateTime()).getTime();
		/* 预分拣站点 */
		Integer siteCode = null;
		try {
			/* 获取包裹的运单数据，如果单号正确的话 */
			Waybill waybill = waybillCommonService.findWaybillAndPack(waybillCode,true,
					false,false,false);
			/* 判断运单信息准确性 */
			if (null == waybill || null == waybill.getSiteCode() || waybill.getSiteCode() < 0) {
				logger.error("WaybillResource.getBarCodeAllRouters-->运单:{" + barCode + "}信息为空或者预分拣站点信息为空");
				result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
				result.setMessage("运单信息为空");
				return result;
			}
			/* 预分拣站点 */
			siteCode = waybill.getSiteCode();
		} catch (Exception e) {
			logger.error("WaybillResource.getBarCodeAllRouters-->运单接口调用异常,单号为：" + waybillCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage("调用运单接口异常");
			return result;
		}

		/* 路由站点关系 index:0 表示预分拣站点；index:>0 表示可能的下一跳的路由 */
		List<Integer> siteRouters = new ArrayList<Integer>();
		Set<Integer> nextRouters = new HashSet<Integer>();
		siteRouters.add(siteCode);

		/* 根据operateType判断是否走路由，还是走分拣的配置 */
		if (1 == request.getOperateType()) {
			/* 通过路由调用 */
			result = getSiteRoutersFromRouterJsf(operateSiteCode,waybillCode,nextRouters);
		} else if (2 == request.getOperateType()) {
			/* 通过发货配置jsf接口调用 */
			result = getSiteRoutersFromDMSAutoJsf(operateSiteCode,siteCode,operateTime,waybillCode,nextRouters);
		}
		siteRouters.addAll(nextRouters);
		result.setData(siteRouters);
		return result;
	}

	private InvokeResult<List<Integer>> getSiteRoutersFromRouterJsf
			(Integer operateSiteCode, String waybillCode,Set<Integer> nextRouters) {

		InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();

		try {
			String routerStr = jsfSortingResourceService.getRouterByWaybillCode(waybillCode);
			if(StringHelper.isNotEmpty(routerStr)){
				String[] routers = routerStr.split(WAYBILL_ROUTER_SPLITER);
				if(routers.length > 0) {
					for (int i = 0; i < routers.length - 1; i++) {
						/* 将当前分拣中心的下一跳路由站点设置进返回值，并跳出循环 */
						if(operateSiteCode.equals(Integer.valueOf(routers[i]))){
							nextRouters.add(Integer.valueOf(routers[i+1]));
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("WaybillResource.getBarCodeAllRouters-->路由接口调用异常,单号为：" + waybillCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage("调用路由接口异常");
			return result;
		}

		return  result;
	}

	private InvokeResult<List<Integer>> getSiteRoutersFromDMSAutoJsf
			(Integer operateSiteCode, Integer destinationSiteCode,Long operateTime,String waybillCode,Set<Integer> nextRouters) {

		InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();

		AreaDestJsfRequest jsfRequest = new AreaDestJsfRequest();
		jsfRequest.setOriginalSiteCode(operateSiteCode);
		jsfRequest.setDestinationSiteCode(destinationSiteCode);
		jsfRequest.setOperateTime(operateTime);
		BaseDmsAutoJsfResponse<List<AreaDestJsfVo>> jsfResponse;

		CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.areaDestJsfService.findAreaDest", Constants.UMP_APP_NAME_DMSWEB,false, true);
		try {
				/* 调用发货配置jsf接口 */
			jsfResponse = areaDestJsfService.findAreaDest(jsfRequest);
		} catch (Exception e) {
			Profiler.functionError(info);
			logger.error("WaybillResource.getBarCodeAllRouters-->配置接口调用异常,单号为：" + waybillCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage("发货配置接口异常");
			return result;
		}finally {
			Profiler.registerInfoEnd(info);
		}

		if (null == jsfResponse || jsfResponse.getStatusCode() != BaseDmsAutoJsfResponse.SUCCESS_CODE
				|| jsfResponse.getData() == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("WaybillResource.getBarCodeAllRouters-->从分拣的发货配置关系中没有获取到有效的路由配置，返回值为:"
						+ JsonHelper.toJson(jsfResponse));
			}
			result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			result.setMessage("未配置发货关系");
			return result;
		}
		for (AreaDestJsfVo areaDestJsfVo : jsfResponse.getData()) {
			if (!operateSiteCode.equals(areaDestJsfVo.getCreateSiteCode())) {
				logger.error("WaybillResource.getBarCodeAllRouters-->areaDestJsfService接口数据获取错误");
				continue;
			}
				/*
				 * 如果中转场编号为空，或者为0，或者为当前分拣中心，则选receiveSiteCode作为下一跳
				 * 否则直接将中转场编号作为下一跳
				 * 如果返回的记录中有多条符合则拼接到siteRouters的后面
				 */
			if (areaDestJsfVo.getTransferSiteCode() == null
					|| operateSiteCode.equals(areaDestJsfVo.getTransferSiteCode())
					|| areaDestJsfVo.getTransferSiteCode() <= 0) {
				if (null != areaDestJsfVo.getReceiveSiteCode() && areaDestJsfVo.getReceiveSiteCode() > 0) {
					nextRouters.add(areaDestJsfVo.getReceiveSiteCode());
				}
			} else {
				nextRouters.add(areaDestJsfVo.getTransferSiteCode());
			}
		}
		return result;
	}

	@POST
	@Path("/waybill/post/cancel")
	public WaybillSafResponse isCancel(PdaOperateRequest pdaOperateRequest) {
		return waybillSafService.isCancelPost(pdaOperateRequest);
	}

	/**
	 * 检查运单是否为理赔完成拦截
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/waybill/checkIsLPWaybill/{waybillCode}")
	public InvokeResult<Boolean> checkIsLPWaybill(@PathParam("waybillCode") String waybillCode){

		InvokeResult invokeResult =new InvokeResult();
		invokeResult.setData(false);
		try{
			Integer featureType = jsfSortingResourceService.getWaybillCancelByWaybillCode(waybillCode);
			if(featureType!= null && Constants.FEATURE_TYPCANCEE_LP.equals(featureType)){
				invokeResult.setData(true);
			}
			invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
			invokeResult.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
		}catch (Exception e){
			logger.error("判断理赔完成拦截运单异常",e);
			invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
			invokeResult.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}

		return invokeResult;
	}
	/**
	 * 外单新换单接口
	 * @param waybillCode  老运单号
	 * @param operatorId  操作人ID
	 * @param operatorName 操作人姓名
	 * @param operateTime  操作时间
	 * @param packageCount  包裹数（整单换单可不输入）
	 * @param orgId   操作机构
	 * @param createSiteCode 操作站
	 * @param isTotal  是否为整单换单
	 * @return
	 */
	@GET
	@Path("/dy/createReturnsWaybill/{waybillCode}/{operatorId}/{operatorName}/{operateTime}/{packageCount}/{orgId}/{createSiteCode}/{isTotal}")
	@BusinessLog(sourceSys = 1,bizType = 1900,operateType = 1900002)
	public InvokeResult<WaybillReverseResult> createReturnsWaybill(@PathParam("waybillCode")String waybillCode, @PathParam("operatorId")Integer operatorId, @PathParam("operatorName")String operatorName,
													  @PathParam("operateTime")String operateTime , @PathParam("packageCount")Integer packageCount, @PathParam("orgId")Integer orgId, @PathParam("createSiteCode")Integer createSiteCode, @PathParam("isTotal")boolean isTotal) {
		InvokeResult invokeResult =new InvokeResult();

		logger.debug("外单新换单接口入参：waybillCode:"+waybillCode+" operatorId:"+operatorId+" operatorName:"+operatorName+" operateTime:"+operateTime+" packageCount:"+packageCount+" orgId:"+orgId+" createSiteCode:"
				+createSiteCode+" isTotal:"+isTotal);

		try {
			WaybillReverseDTO waybillReverseDTO = ldopManager.makeWaybillReverseDTO( waybillCode,  operatorId,  operatorName,  DateHelper.parseDateTime(operateTime) ,  packageCount,  orgId,  createSiteCode,  isTotal);
			StringBuilder errorMessage = new StringBuilder();
			WaybillReverseResult waybillReverseResult = ldopManager.waybillReverse(waybillReverseDTO,errorMessage);
			if(waybillReverseResult == null){
				//失败
				invokeResult.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
				invokeResult.setMessage(errorMessage.toString());
			}else{
				invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
				invokeResult.setData(waybillReverseResult);
			}

		}catch (Exception e){
			logger.error("外单逆向换单接口异常,接口入参：waybillCode:"+waybillCode+" operatorId:"+operatorId+" operatorName:"+operatorName+" operateTime:"+operateTime+" packageCount:"+packageCount+" orgId:"+orgId+" createSiteCode:"
					+createSiteCode+" isTotal:"+isTotal,e);
			invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
			invokeResult.setMessage("系统异常");
		}
        return invokeResult;
	}


	/**
	 * 换单前获取信息接口
	 * @param waybillCode  老运单号
	 * @param operatorId  操作人ID
	 * @param operatorName 操作人姓名
	 * @param operateTime  操作时间
	 * @param packageCount  包裹数（整单换单可不输入）
	 * @param orgId   操作机构
	 * @param createSiteCode 操作站
	 * @param isTotal  是否为整单换单
	 * @return
	 */
	@GET
	@Path("/dy/getOldOrderMessage/{waybillCode}/{operatorId}/{operatorName}/{operateTime}/{packageCount}/{orgId}/{createSiteCode}/{isTotal}")
	@BusinessLog(sourceSys = 1,bizType = 1900,operateType = 1900001)
	public InvokeResult<WaybillReverseResponseDTO> getOldOrderMessage(@PathParam("waybillCode")String waybillCode, @PathParam("operatorId")Integer operatorId, @PathParam("operatorName")String operatorName,
																   @PathParam("operateTime")String operateTime , @PathParam("packageCount")Integer packageCount, @PathParam("orgId")Integer orgId, @PathParam("createSiteCode")Integer createSiteCode, @PathParam("isTotal")boolean isTotal) {
		InvokeResult invokeResult =new InvokeResult();

		logger.debug("换单前获取信息接口入参：waybillCode:"+waybillCode+" operatorId:"+operatorId+" operatorName:"+operatorName+" operateTime:"+operateTime+" packageCount:"+packageCount+" orgId:"+orgId+" createSiteCode:"
				+createSiteCode+" isTotal:"+isTotal);

		try {
			WaybillReverseDTO waybillReverseDTO = ldopManager.makeWaybillReverseDTO( waybillCode,  operatorId,  operatorName,  DateHelper.parseDateTime(operateTime) ,  packageCount,  orgId,  createSiteCode,  isTotal);
			StringBuilder errorMessage = new StringBuilder();
			WaybillReverseResponseDTO waybillReverseResponseDTO = ldopManager.queryReverseWaybill(waybillReverseDTO,errorMessage);
			if(waybillReverseResponseDTO == null){
				//失败
				invokeResult.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
				invokeResult.setMessage(errorMessage.toString());
			}else{
				invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
				invokeResult.setData(waybillReverseResponseDTO);
			}

		}catch (Exception e){
			logger.error("换单前获取信息接口异常,接口入参：waybillCode:"+waybillCode+" operatorId:"+operatorId+" operatorName:"+operatorName+" operateTime:"+operateTime+" packageCount:"+packageCount+" orgId:"+orgId+" createSiteCode:"
					+createSiteCode+" isTotal:"+isTotal,e);
			invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
			invokeResult.setMessage("系统异常");
		}
        return invokeResult;
	}

	/**
	 * 外单新换单接口 POST接口
	 * @return
	 */
	@POST
	@Path("/dy/createReturnsWaybill")
	@BusinessLog(sourceSys = 1,bizType = 1900,operateType = 1900002)
	public InvokeResult<WaybillReverseResult> createReturnsWaybillNew(ExchangeWaybillDto request) {
		InvokeResult invokeResult =new InvokeResult();

		logger.debug("换单前获取信息接口入参："+JsonHelper.toJson(request));

		try {
			WaybillReverseDTO waybillReverseDTO = ldopManager.makeWaybillReverseDTOCanTwiceExchange(request);
			StringBuilder errorMessage = new StringBuilder();
			WaybillReverseResult waybillReverseResult = ldopManager.waybillReverse(waybillReverseDTO,errorMessage);
			if(waybillReverseResult == null){
				//失败
				invokeResult.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
				invokeResult.setMessage(errorMessage.toString());
			}else{
				invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
				invokeResult.setData(waybillReverseResult);
			}

		}catch (Exception e){
			logger.error("换单前获取信息接口入参："+JsonHelper.toJson(request),e);
			invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
			invokeResult.setMessage("系统异常");
		}
        return invokeResult;
	}


	/**
	 * 换单前获取信息接口 POST接口

	 * @return
	 */
	@POST
	@Path("/dy/getOldOrderMessage")
	@BusinessLog(sourceSys = 1,bizType = 1900,operateType = 1900001)
	public InvokeResult<WaybillReverseResponseDTO> getOldOrderMessageNew(ExchangeWaybillDto request) {
		InvokeResult invokeResult =new InvokeResult();

		logger.debug("换单前获取信息接口入参："+JsonHelper.toJson(request));

		try {
			WaybillReverseDTO waybillReverseDTO = ldopManager.makeWaybillReverseDTOCanTwiceExchange(request);
			StringBuilder errorMessage = new StringBuilder();
			WaybillReverseResponseDTO waybillReverseResponseDTO = ldopManager.queryReverseWaybill(waybillReverseDTO,errorMessage);
			if(waybillReverseResponseDTO == null){
				//失败
				invokeResult.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
				invokeResult.setMessage(errorMessage.toString());
			}else{
				invokeResult.setCode(InvokeResult.RESULT_SUCCESS_CODE);
				invokeResult.setData(waybillReverseResponseDTO);
			}

		}catch (Exception e){
			logger.error("换单前获取信息接口入参："+JsonHelper.toJson(request),e);
			invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
			invokeResult.setMessage("系统异常");
		}
        return invokeResult;
	}

	/**
	 * 二次换单检查
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/waybill/twiceExchange/check/{waybillCode}")
	public InvokeResult<TwiceExchangeCheckDto> twiceExchangeCheck(@PathParam("waybillCode") String waybillCode){
		InvokeResult<TwiceExchangeCheckDto> invokeResult = new InvokeResult<TwiceExchangeCheckDto>();
		TwiceExchangeCheckDto twiceExchangeCheckDto = new TwiceExchangeCheckDto();
		//获取老单号
		BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
		if(oldWaybill.getResultCode()==1 && oldWaybill.getData()!=null && StringUtils.isNotBlank(oldWaybill.getData().getWaybillCode())){
			String oldWaybillCode = oldWaybill.getData().getWaybillCode();
			twiceExchangeCheckDto.setOldWaybillCode(oldWaybillCode);
			//获取理赔状态及物权归属
			LocalClaimInfoRespDTO claimInfoRespDTO =  obcsManager.getClaimListByClueInfo(1,oldWaybillCode);
			if(claimInfoRespDTO == null){
				invokeResult.setCode(invokeResult.RESULT_THIRD_ERROR_CODE);
				invokeResult.setMessage("理赔接口失败，请稍后再试");
				return invokeResult;
			}
			twiceExchangeCheckDto.setGoodOwner(claimInfoRespDTO.getGoodOwnerName());
			//划分理赔状态 以及 物权归属
			twiceExchangeCheckDto.setStatusOfLP(claimInfoRespDTO.getStatusDesc());

			if(LocalClaimInfoRespDTO.LP_STATUS_DOING.equals(twiceExchangeCheckDto.getStatusOfLP()) && claimInfoRespDTO.getGoodOwner() == 0){
				//twiceExchangeCheckDto.setReturnDestinationTypes("000");
				invokeResult.setCode(invokeResult.RESULT_THIRD_ERROR_CODE);
				invokeResult.setMessage("理赔中运单禁止换单，请稍后再试");
				return invokeResult;
			}else if(LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(twiceExchangeCheckDto.getStatusOfLP()) && claimInfoRespDTO.getGoodOwner() == LocalClaimInfoRespDTO.GOOD_OWNER_JD){
				twiceExchangeCheckDto.setReturnDestinationTypes("100");
			}else if(LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(twiceExchangeCheckDto.getStatusOfLP()) && claimInfoRespDTO.getGoodOwner() == LocalClaimInfoRespDTO.GOOD_OWNER_BUSI){
				twiceExchangeCheckDto.setReturnDestinationTypes("011");
			}else if(LocalClaimInfoRespDTO.LP_STATUS_NONE.equals(twiceExchangeCheckDto.getStatusOfLP())){
				twiceExchangeCheckDto.setReturnDestinationTypes("011");
			}


		}
		invokeResult.setData(twiceExchangeCheckDto);
		return invokeResult;
	}

	/**
	 * ECLP备件库分拣 不允许按商品分拣
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/waybill/eclpSpareSortingCheck/{waybillCode}")
	public InvokeResult<Boolean> eclpSpareSortingCheck(@PathParam("waybillCode") String waybillCode){
		InvokeResult<Boolean> invokeResult = new InvokeResult<Boolean>();
		invokeResult.setData(true);
		try{
			if(WaybillUtil.isPackageCode(waybillCode)){
				waybillCode = WaybillUtil.getWaybillCode(waybillCode);
			}
			Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
			if(waybill!=null){
				BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
				if(oldWaybill!=null && oldWaybill.getData()!=null
						&& StringUtils.isNotBlank(oldWaybill.getData().getBusiOrderCode())){
					invokeResult.setData(!WaybillUtil.isECLPByBusiOrderCode(oldWaybill.getData().getBusiOrderCode()));
				}

			}else{
				invokeResult.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
				invokeResult.setData(false);
				invokeResult.setMessage("运单不存在");
			}
		}catch (Exception e){
			invokeResult.setCode(InvokeResult.SERVER_ERROR_CODE);
			invokeResult.setData(false);
			invokeResult.setMessage("系统异常");
			logger.error("备件库分拣检查异常"+waybillCode,e);
		}

		return invokeResult;
	}

	/**
	 * 根据商家ID和商家单号 获取运单号
	 * @param busiId 商家ID
	 * @param busiCode 商家单号
	 *
	 * @return 运单号
	 */
	@GET
	@Path("/waybill/findByBusiCode/{busiId}/{busiCode}")
	@Deprecated
	public InvokeResult<String> findWaybillByBusiIdAndBusiCode(@PathParam("busiId") String busiId,@PathParam("busiCode") String busiCode){
		InvokeResult<String> result = new InvokeResult<String>();
		try{
			result = ldopManager.findWaybillCodeByBusiIdAndBusiCode(busiId,busiCode);
		}catch (Exception e){
			logger.error("根据商家ID和商家单号获取运单号异常"+busiId+" "+busiCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}

	/**
	 * 根据商家ID和商家单号 获取包裹号
	 * @param busiId 商家ID
	 * @param busiCode 商家单号
	 *
	 * @return 运单号
	 */
	@GET
	@Path("/waybill/findPackByBusiCode/{busiId}/{busiCode}")
	public InvokeResult<String> findPackByBusiIdAndBusiCode(@PathParam("busiId") Integer busiId,@PathParam("busiCode") String busiCode){
		InvokeResult<String> result = new InvokeResult<String>();
		try{
			result.setData(eclpPackageApiService.queryPackage(busiId,busiCode));
		}catch (Exception e){
			logger.error(MessageFormat.format("根据商家ID和商家单号获取包裹号异常{0}|{1}",busiId,busiCode),e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}


	/**
	 * 根据包裹号获取重量信息
	 *
	 * @param packageCode 包裹号
	 * @param type 称重类型
	 *              为以后方便接口扩展使用 ，如果有需求请扩展该字段
	 *             	1 - 揽收称重信息  (揽收包括站点称重 车队称重 驻厂)
	 *
	 * @return 运单号
	 */
	@GET
	@Path("/package/weight/{type}/{packageCode}")
	public InvokeResult<PackWeightVO> findPackageWeight(@PathParam("type") String type,@PathParam("packageCode") String packageCode){
		InvokeResult<PackWeightVO> result = new InvokeResult<PackWeightVO>();
		try{
			Integer[] opeTypes = {Constants.PACK_OPE_FLOW_TYPE_PSY_REC,Constants.PACK_OPE_FLOW_TYPE_CD_REC,Constants.PACK_OPE_FLOW_TYPE_ZC_REC};
			//称重信息优先级 站点 > 车队 > 驻厂
			for(Integer opeType : opeTypes){

				Map<String,PackOpeFlowDto> packOpeFlows = this.waybillCommonService.getPackOpeFlowsByOpeType(WaybillUtil.getWaybillCode(packageCode),opeType);

				if(packOpeFlows!= null && packOpeFlows.size() != 0 && packOpeFlows.get(packageCode)!=null){
					PackOpeFlowDto packOpeFlowDto = packOpeFlows.get(packageCode);

				PackWeightVO packWeightVO = new PackWeightVO();
				packWeightVO.setHigh(packOpeFlowDto.getpHigh());
				packWeightVO.setLength(packOpeFlowDto.getpLength());
				packWeightVO.setWidth(packOpeFlowDto.getpWidth());
				if(packOpeFlowDto.getpLength() != null && packOpeFlowDto.getpHigh() !=null && packOpeFlowDto.getpWidth() !=null){
					packWeightVO.setVolume(packOpeFlowDto.getpLength() * packOpeFlowDto.getpHigh() * packOpeFlowDto.getpWidth());
				}
				packWeightVO.setWeight(packOpeFlowDto.getpWeight());
				result.setData(packWeightVO);
				return result;

				}
			}

			result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
			result.setMessage("未获取到揽收重量信息");
			return result;

		}catch (Exception e){
			logger.error("根据包裹号获取揽收重量信息异常"+packageCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}


	/**
	 * 上传包裹称重信息
	 * @param packWeightVO
	 *
	 * @return 运单号
	 */
	@POST
	@Path("/package/weight")
	@BusinessLog(sourceSys = 1,bizType = 1903,operateType = 1903001)
	public InvokeResult<Boolean> savePackageWeight(PackWeightVO packWeightVO){
		InvokeResult<Boolean> result = new InvokeResult<Boolean>();
		try{
			StringBuilder message = new StringBuilder();
			if(packWeightVO.checkParam(message)){
				//验证是否妥投
				String waybillCode = WaybillUtil.getWaybillCode(packWeightVO.getCodeStr());
				if(waybillTraceManager.isWaybillFinished(waybillCode)){
					result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
					result.setMessage("此运单为妥投状态，禁止操作此功能，请检查单号是否正确");
					result.setData(false);
					return result;
				}

				this.taskService.add(packWeightVO.convertToTask("package/weight上传"), false);
				result.setData(true);
				return result;
			}
			result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
			result.setMessage(message.toString());
			result.setData(false);
		}catch (Exception e){
			logger.error("上传包裹称重信息异常"+JsonHelper.toJson(packWeightVO),e);
			result.setData(false);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}


    /**
     * 包裹称重 提示警告信息
     *
     *
     * @param packWeightVO
     *
     * @return
     */
    @POST
    @Path("/package/weight/warn/check")
    public InvokeResult<Boolean> packageWeightCheck(PackWeightVO packWeightVO){
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();
		WeightVolumeCollectDto weightVolumeCollectDto = new WeightVolumeCollectDto();
        //1.只针对一单一件
        if(WaybillUtil.isWaybillCode(packWeightVO.getCodeStr()) || WaybillUtil.isPackageCode(packWeightVO.getCodeStr())){
			weightVolumeCollectDto.setWaybillCode(WaybillUtil.getWaybillCode(packWeightVO.getCodeStr()));
			weightVolumeCollectDto.setPackageCode(packWeightVO.getCodeStr());
        }else {
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("运单号/包裹号不符合规则!");
            return result;
        }

		String waybillCode = WaybillUtil.getWaybillCode(packWeightVO.getCodeStr());
        InvokeResult<Integer> packNumResult = getPackNum(waybillCode);
        if(packNumResult != null && packNumResult.getData() > 1){
            result.setCode(InvokeResult.RESULT_THIRD_ERROR_CODE);
            result.setMessage("重量体积抽查只支持一单一件!");
            return result;
        }
		result = weightAndVolumeCheckService.insertAndSendMq(packWeightVO,weightVolumeCollectDto,result);
        return result;

    }


    /**
	 * 判断运单是否存在是否妥投
	 * @param barCode
	 * @return
	 */
	@GET
	@Path("/waybill/isWaybillExistAndNotFinished/{barCode}")
	public InvokeResult<Boolean> isWaybillExistAndNotFinished(@PathParam("barCode") String barCode){
		InvokeResult<Boolean> result = new InvokeResult<Boolean>();
		try{
			String waybillCode = barCode;
			if(WaybillUtil.isPackageCode(barCode) || WaybillUtil.isWaybillCode(barCode)){
				waybillCode = WaybillUtil.getWaybillCode(barCode);
			}
			WChoice choice = new WChoice();
			choice.setQueryWaybillM(true);
			choice.setQueryWaybillC(true);
			choice.setQueryWaybillE(true);

			//判断运单是否存在
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,choice);
			if(baseEntity == null){
				logger.error("调用运单获取运单数据失败，waybillCode:"+waybillCode);
				result.setData(false);
				result.setCode(InvokeResult.RESULT_NULL_CODE);
				result.setMessage("调用运单接口获取运单信息异常");
				return result;
			}
			if(baseEntity.getData() == null || baseEntity.getData().getWaybill() == null){
				logger.info("调用运单获取运单信息为空，waybillCode:"+waybillCode);
				result.setData(false);
				result.setCode(InvokeResult.RESULT_NULL_CODE);
				result.setMessage("运单信息为空，请联系IT人员处理");
				return result;
			}
			//判断运单是否妥投
			if(waybillTraceManager.isWaybillFinished(waybillCode)){
				logger.warn("运单已经妥投，waybillCode:"+waybillCode);
				result.setData(false);
				result.setCode(InvokeResult.RESULT_NULL_CODE);
				result.setMessage("此运单为妥投状态，禁止操作此功能，请检查单号是否正确");
				return result;
			}
			result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
			result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
			return result;

		}catch (Exception e){
			logger.error("判断运单是否存在是否妥投异常，barCode"+barCode,e);
			result.setCode(InvokeResult.SERVER_ERROR_CODE);
			result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
		}
		return result;
	}

	/**
	 * 判断换单新单运单是否存
	 * @param oldWaybillCode 老运单号
	 * @param newWaybillCode 新运单号
	 * @return
	 */
	@GET
	@Path("/waybill/exchange/weightAndVolume/limit/{oldWaybillCode}/{newWaybillCode}")
	public InvokeResult<Integer> waybillExchangeCheckWeightAndVolume(@PathParam("oldWaybillCode") String oldWaybillCode, @PathParam("newWaybillCode") String newWaybillCode ){
		return receiveWeightCheckService.waybillExchangeCheckWeightAndVolume(oldWaybillCode,newWaybillCode);
	}

	/**
	 * 判断仓配/纯配订单
     * 0：运单号为空 1：纯配运单 2：仓配运单 3：既不是纯配也不是仓配
	 * 4：运单数据为空 5.服务异常
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/waybill/checkIsPureMatchOrWarehouse/{waybillCode}")
	public InvokeResult<Integer> checkIsPureMatchOrWarehouse(@PathParam("waybillCode") String waybillCode){
		InvokeResult<Integer> result = new InvokeResult<Integer>();
		result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
		if(StringUtils.isBlank(waybillCode)){
			this.logger.error("运单号不能为空!");
			result.setData(0);
			result.setMessage("运单号不能为空!");
			return result;
		}
		try{
            String oldWaybillCode1 = "";
            String oldWaybillCode2 = "";
            String busiOrderCode = "";
            BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill1 = waybillQueryManager.getWaybillByReturnWaybillCode(waybillCode);
            if(oldWaybill1 != null && oldWaybill1.getData() != null){
                oldWaybillCode1 = oldWaybill1.getData().getWaybillCode();
                busiOrderCode = oldWaybill1.getData().getBusiOrderCode();
                BaseEntity<com.jd.etms.waybill.domain.Waybill> oldWaybill2 = waybillQueryManager.getWaybillByReturnWaybillCode(oldWaybillCode1);
                if(oldWaybill2 != null && oldWaybill2.getData() != null){
                    //二次换单
                    oldWaybillCode2 = oldWaybill2.getData().getWaybillCode();
                    isPurematchOrWarehouse(result, oldWaybillCode2,busiOrderCode);
                }else if(oldWaybill2 != null && oldWaybill2.getData() == null){
                    //一次换单
                    isPurematchOrWarehouse(result, oldWaybillCode1,busiOrderCode);
                }
            }else if(oldWaybill1 != null && oldWaybill1.getData() == null){
                //原单
                isPurematchOrWarehouse(result, waybillCode,busiOrderCode);
            }
		}catch (Exception e){
			this.logger.error("通过运单号："+waybillCode+"查询运单信息失败!");
			result.setMessage("服务异常!");
			result.setData(5);
		}
        return result;
    }


    /**
     * 仓配纯配判断
     * @param result
     * @param waybillCode
     * @param busiOrderCode
     */
    private void isPurematchOrWarehouse(InvokeResult<Integer> result, String waybillCode,String busiOrderCode) {
        BigWaybillDto bigWaybillDto = waybillService.getWaybillProduct(waybillCode);
        if(bigWaybillDto != null && bigWaybillDto.getWaybill() != null
                && StringUtils.isNotBlank(bigWaybillDto.getWaybill().getWaybillSign())){
            if(StringUtils.isEmpty(busiOrderCode)){
                busiOrderCode = bigWaybillDto.getWaybill().getBusiOrderCode();
            }
            if(BusinessUtil.isPurematch(bigWaybillDto.getWaybill().getWaybillSign())){
                //纯配外单
                result.setData(1);
            }else if(WaybillUtil.isECLPByBusiOrderCode(busiOrderCode)){
                //仓配订单
                result.setData(2);
            }else{
                result.setData(3);//既不是纯配也不是仓配
            }
        }else{
            this.logger.error("通过运单号查询运单为空!");
            result.setMessage("运单号："+waybillCode+"数据为空!");
            result.setData(4);
        }
    }

	/**
	 * 获取运单中包裹数量
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/waybill/getPackNum/{waybillCode}")
	public InvokeResult<Integer> getPackNum(@PathParam("waybillCode") String waybillCode){

		return waybillCommonService.getPackNum(waybillCode);
	}

	/**
	 * 一车一单发货、组板、建箱差异查询
	 * @param
	 * @return
	 */
	@POST
	@Path("/waybill/collection/uneven")
	public InvokeResult<WaybillNoCollectionResult> getWaybillNoCollectionInfo(WaybillNoCollectionRequest waybillNoCollectionRequest){

		InvokeResult<WaybillNoCollectionResult> result = new InvokeResult<>();
		result.success();
		WaybillNoCollectionResult waybillNoCollectionResult = null;
		if (waybillNoCollectionRequest == null) {
			result.parameterError("请求内容为空，请检查请求体！");
			return result;
		}
		String queryCode = waybillNoCollectionRequest.getQueryCode();
		int queryType = waybillNoCollectionRequest.getQueryType();
		Integer createSiteCode = waybillNoCollectionRequest.getSiteCode();
		Integer receiveSiteCode = waybillNoCollectionRequest.getReceiveSiteCode();

		if (StringHelper.isEmpty(queryCode) || ! WaybillNoCollectionQueryTypeEnum.isCorrectType(queryType) || createSiteCode == null || receiveSiteCode == null) {
			logger.error("请求差异查询信息参数有误，参数："+ JsonHelper.toJson(waybillNoCollectionRequest));
			result.parameterError("请求参数有误，请检查参数！");
			return result;
		}

		BaseStaffSiteOrgDto createSiteOrgDto = null;
		BaseStaffSiteOrgDto receiveSiteOrgDto = null;

		try {
			createSiteOrgDto = siteService.getSite(createSiteCode);
			receiveSiteOrgDto = siteService.getSite(receiveSiteCode);
		} catch (Exception e) {
			logger.error("请求差异查询，获取站点信息失败，参数："+ JsonHelper.toJson(waybillNoCollectionRequest), e);
		}

		//默认只看B网
		int queryRange = WaybillNoCollectionRangeEnum.B_RANGE.getType();
		if (createSiteOrgDto == null || receiveSiteOrgDto == null) {
			result.setCode(JdResponse.CODE_NO_SITE);
			result.setMessage("获取始发或目的站点信息失败！");
			logger.error("请求差异查询，获取站点信息失败，参数："+ JsonHelper.toJson(waybillNoCollectionRequest));
			return result;
		} else {
			//始发和目的有一个是车队
			if (Constants.BASE_SITE_MOTORCADE.equals(createSiteOrgDto.getSiteType()) ||
					Constants.BASE_SITE_MOTORCADE.equals(receiveSiteOrgDto.getSiteType())) {
				//全部都看
				queryRange = WaybillNoCollectionRangeEnum.ALL_RANGE.getType();
			}
			//始发和目的有一个是快运中心
			else if ((createSiteOrgDto.getSubType() != null && createSiteOrgDto.getSubType() == Constants.B2B_SITE_TYPE) ||
					(receiveSiteOrgDto.getSubType() != null && receiveSiteOrgDto.getSubType() == Constants.B2B_SITE_TYPE)) {
				//全部都看
				queryRange = WaybillNoCollectionRangeEnum.ALL_RANGE.getType();
			}
		}

		WaybillNoCollectionCondition waybillNoCollectionCondition = new WaybillNoCollectionCondition();
		waybillNoCollectionCondition.setCreateSiteCode(createSiteCode);
		waybillNoCollectionCondition.setQueryRange(queryRange);

		try {
			if (queryType == WaybillNoCollectionQueryTypeEnum.BOARD_CODE_QUERY_TYPE.getType()) {
				waybillNoCollectionCondition.setBoardCode(queryCode);
				waybillNoCollectionResult = waybillNoCollectionInfoService.getWaybillNoCollectionInfoByBoardCode(waybillNoCollectionCondition);
			} else {
				if (queryType == WaybillNoCollectionQueryTypeEnum.SEND_CODE_QUERY_TYPE.getType()) {
					waybillNoCollectionCondition.setSendCode(queryCode);
				} else if (queryType == WaybillNoCollectionQueryTypeEnum.BOX_CODE_QUERY_TYPE.getType()) {
					waybillNoCollectionCondition.setBoxCode(queryCode);
				}
				waybillNoCollectionResult = waybillNoCollectionInfoService.getWaybillNoCollectionInfo(waybillNoCollectionCondition);
			}
		} catch (WaybillNoCollectionException waybillEx) {
			result.setCode(JdResponse.CODE_SERVICE_ERROR);
			result.setMessage(waybillEx.getMessage());
			logger.error(waybillEx.getMessage());
		} catch (DataAccessException e) {
			result.setCode(JdResponse.CODE_SERVICE_ERROR);
			result.setMessage("服务端数据库查询异常，请稍后再试！");
			logger.error("获取差异查询信息失败，参数："+ JsonHelper.toJson(waybillNoCollectionRequest) + e);
		}

		result.setData(waybillNoCollectionResult);
		return result;
	}

    @POST
    @Path("/waybill/cancelFeatherLetter")
	public InvokeResult<String> cancelFeatherLetter(CancelFeatherLetterRequest request){
        InvokeResult<String> result = new InvokeResult<>();
        result.success();
        if(StringUtils.isEmpty(request.getWaybillCode()) || request.getCancelFeatherLetter() == null){
            result.setCode(JdResponse.CODE_PARAM_ERROR);
            result.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return result;
        }
        com.jd.etms.waybill.domain.Waybill  waybill = waybillService.getWaybillByWayCode(request.getWaybillCode());
        if(waybill == null){
            logger.error("鸡毛信取消接口-查询运单信息为空waybillCode[{}]",request.getWaybillCode());
            result.setCode(JdResponse.CODE_SEE_OTHER);
            result.setMessage(JdResponse.MESSAGE_NOT_EXIST_WAYBILL);
            return result;
        }
        if(!BusinessUtil.isFeatherLetter(waybill.getWaybillSign())){
            logger.error("鸡毛信取消接口-非鸡毛信运单waybillCode[{}]waybillSign[{}]",request.getWaybillCode(),waybill.getWaybillSign());
            result.setCode(JdResponse.CODE_SEE_OTHER);
            result.setMessage(JdResponse.MESSAGE_NO_FEATHER_LETTER);
            return result;
        }
        if(!Objects.equals(request.getCancelFeatherLetter(),Boolean.TRUE)){
            logger.error("鸡毛信取消接口-cancelFeatherLetter不为true不用请求取消接口，waybillCode[{}]waybillSign[{}]",request.getWaybillCode(),
                    waybill.getWaybillSign());
            result.success();
            result.setMessage("不用取消鸡毛信属性");
            return result;
        }
        InvokeResult<String> ldopInvokeResult = ldopWaybillUpdateManager.cancelFeatherLetterByWaybillCode(request.getWaybillCode());
        if(ldopInvokeResult.getCode() == InvokeResult.RESULT_SUCCESS_CODE){
            result.success();
            result.setMessage("取消鸡毛信成功！");
            return result;
        }
        result.setCode(JdResponse.CODE_SEE_OTHER);
        result.setMessage("取消鸡毛信失败【"+ldopInvokeResult.getMessage()+"】");
        return result;
    }

}
