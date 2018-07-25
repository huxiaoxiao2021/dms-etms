package com.jd.bluedragon.distribution.rest.waybill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
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
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingDto;
import com.jd.bluedragon.distribution.cross.service.CrossSortingService;
import com.jd.bluedragon.distribution.external.service.DmsWaybillService;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.popPrint.domain.PopAddPackStateTaskBody;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.saf.WaybillSafResponse;
import com.jd.bluedragon.distribution.saf.WaybillSafService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.LabelPrinting;
import com.jd.bluedragon.distribution.web.kuaiyun.weight.WeighByWaybillController;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDetail;
import com.jd.bluedragon.distribution.weight.domain.PackOpeDto;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.LableType;
import com.jd.bluedragon.utils.OriginalType;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.PackageWeigh;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class WaybillResource implements DmsWaybillService {

    private final Log logger = LogFactory.getLog(this.getClass());

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

	/**
	 * 运单路由字段使用的分隔符
	 */
	private static final  String WAYBILL_ROUTER_SPLITER = "\\|";



	/* 运单查询 */
	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	private WaybillSafService waybillSafService;

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
     * @param waybillCode Or package
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
        String waybillCode = BusinessHelper.getWaybillCode(waybillCodeOrPackage);

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
		String waybillCode = BusinessHelper
				.getWaybillCode(waybillCodeOrPackage);
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
			// 调用服务异常
			this.logger
					.error("根据运单号【" + waybillCode + "】 获取运单包裹信息接口 --> 异常", e);
			return new WaybillResponse<Waybill>(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
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

            //如果是一号店,那么需要在标签上打出其标志,这里将标志图片名称发到打印端，打印端自行处理图片路径加载
            if(BusinessHelper.isYHD(waybill.getSendPay())){
            	request.setBrandImageKey(Constants.BRAND_IMAGE_KEY_YHD);
            }
            
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

			if (response != null) {
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
			} else {
				this.logger.error("根据运单号【" + waybill.getWaybillCode()
						+ "】 获取预分拣的包裹打印信息为空");
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
												   @PathParam("waybillCodeOrPackage") String waybillCodeOrPackage,@PathParam("localSchedule") Integer localSchedule
			,@PathParam("paperless") Integer paperless,@PathParam("startSiteType") Integer startSiteType,@PathParam("packOpeFlowFlg") Integer packOpeFlowFlg) {
		// 判断传入参数
		if (startDmsCode == null || startDmsCode.equals(0)
				|| StringUtils.isEmpty(waybillCodeOrPackage)) {
			this.logger.error("根据初始分拣中心-运单号/包裹号【" + startDmsCode + "-"
					+ waybillCodeOrPackage + "】获取运单包裹信息接口 --> 传入参数非法");
			return new WaybillResponse<Waybill>(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		if(packOpeFlowFlg==null){
			packOpeFlowFlg = Constants.INTEGER_FLG_FALSE;
		}
		// 转换运单号
		String waybillCode = BusinessHelper
				.getWaybillCode(waybillCodeOrPackage);
		// 调用服务
		try {
			Waybill waybill = findWaybillMessage(waybillCode,packOpeFlowFlg);
			if (waybill == null) {
				this.logger.info("运单号【" + waybillCode
						+ "】调用根据运单号获取运单包裹信息接口成功, 无数据");
				return new WaybillResponse<Waybill>(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
			
			//调用分拣接口获得基础资料信息
			this.setBasicMessageByDistribution(waybill, startDmsCode, localSchedule, paperless,startSiteType);

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
     * @param waybillCode Or package
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
    public InvokeResult<Waybill> getFBackWaybill(
            @PathParam("fbackwaybillcode") String fwaybillcode
    ){
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
        finally {
            return result;
        }
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
     * @param waybillCode 运单号
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
     * @param receiveCode目的分拣中心
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
			logger.error(request.getSiteCode() + "站点标签打印" + request.getKeyword2(), ex);
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
		try{

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


			result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
			result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
			result.setData(true);
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
				Waybill waybill = waybillCommonService.findWaybillAndPack(BusinessHelper.getWaybillCode(packageCode), true, false, false, false);
				if(waybill != null && StringHelper.isNotEmpty(waybill.getWaybillCode())){
					preSortingSiteCode = waybill.getSiteCode();
					//获得路由中的下一节点
					String routerStr = jsfSortingResourceService.getRouterByWaybillCode(waybill.getWaybillCode());
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

	@POST
	@Path("/waybill/post/cancel")
	@Override
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


}