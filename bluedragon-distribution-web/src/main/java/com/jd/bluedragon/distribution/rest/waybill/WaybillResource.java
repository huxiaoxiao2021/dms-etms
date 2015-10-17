package com.jd.bluedragon.distribution.rest.waybill;

import java.util.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.distribution.api.request.ModifyOrderInfo;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.message.produce.client.MessageClient;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.etms.waybill.wss.WaybillQueryWS;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.AirTransportService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.cross.domain.CrossSortingDto;
import com.jd.bluedragon.distribution.cross.service.CrossSortingService;
import com.jd.bluedragon.distribution.fastRefund.service.WaybillCancelClient;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.preseparate.saf.LabelPrintingWS;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.basic.dto.BaseCrossDto;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.preseparate.util.LableType;
import com.jd.preseparate.util.OriginalType;
import com.jd.preseparate.vo.BaseResponseIncidental;
import com.jd.preseparate.vo.lablePrinting.LabelPrintingRequest;
import com.jd.preseparate.vo.lablePrinting.LabelPrintingResponse;
import org.springframework.util.Assert;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class WaybillResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
	private WaybillCommonService waybillCommonService;

    @Autowired
	private PopPrintService popPrintService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private AirTransportService airTransportService;

    @Autowired
    private LabelPrintingWS labelPrintingWS;

    @Autowired
    private BaseService baseService;

    @Autowired
    private BaseMajorManager baseMajorManager;


	@Autowired
	private MessageClient messageClient;

	@Autowired
	private TaskService taskService;

	@Autowired
	private CrossSortingService crossSortingService;

	public static final Integer DMSTYPE = 10; // 建包


	/* 运单查询 */
	@Autowired
	@Qualifier("waybillQueryWSProxy")
	private WaybillQueryWS waybillQueryWSProxy;

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

            this.setWaybillCrossCode(waybill, startDmsCode);

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

            this.setWaybillCrossCode(waybill, startDmsCode);

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

    private void setWaybillCrossCode(Waybill waybill, Integer startDmsCode) {
    	if (waybill == null || StringUtils.isBlank(waybill.getWaybillCode())) {
    		return;
    	}
    	String waybillCode = waybill.getWaybillCode();
    	try {
        	if (waybill.getSiteCode() != null && !waybill.getSiteCode().equals(0)) {
        		this.logger.warn("运单号-初始分拣中心-目的站点【" + waybillCode + "-" + startDmsCode  + "-" + waybill.getSiteCode() + "】根据基础资料调用滑到信息开始");
        		BaseCrossDto baseCrossDto = this.baseMinorManager.getComplexCrossDetail(String.valueOf(startDmsCode), String.valueOf(waybill.getSiteCode()));
        		if (baseCrossDto != null) {
        			waybill.setCrossCode(baseCrossDto.getStartDmsDkh());
        			waybill.setTrolleyCode(baseCrossDto.getStartDmsLch());
        			if (StringUtils.isNotBlank(baseCrossDto.getTargetDmsCode())) {
        				waybill.setTargetDmsCode(Integer.parseInt(baseCrossDto.getTargetDmsCode()));
        			}
        			waybill.setTargetDmsName(baseCrossDto.getTargetDmsName());
        			waybill.setTargetDmsDkh(baseCrossDto.getTargetDmsDkh());
        			waybill.setTargetDmsLch(baseCrossDto.getTargetDmsLch());
        			this.logger.warn("运单号-初始分拣中心-目的站点【" + waybillCode + "-" + startDmsCode  + "-" + waybill.getSiteCode() + "】根据基础资料调用滑到信息结束 返回有内容");
        		} else {
        			this.logger.warn("运单号-初始分拣中心-目的站点【" + waybillCode + "-" + startDmsCode  + "-" + waybill.getSiteCode() + "】根据基础资料调用滑到信息结束 返回对象为空");
        		}
        	} else {
        		this.logger.warn("运单号-初始分拣中心-目的站点【" + waybillCode + "-" + startDmsCode  + "-" + waybill.getSiteCode() + "】根据基础资料调用滑到信息 站点信息为空或0");
        	}

        } catch (Throwable e) {
        	this.logger.error("运单号-初始分拣中心-目的站点【" + waybillCode + "-" + startDmsCode  + "-" + waybill.getSiteCode() + "】根据基础资料调用滑到信息异常");
        }
    }

    /**
     * 根据运单号或包裹号获取运单包裹信息接口
     * 新接口调用预分拣接口获取基础资料信息
     * @param waybillCode Or package
     * @return
     */
    @GET
    @Path("/waybillPrePack/{startDmsCode}/{waybillCodeOrPackage}/{localSchedule}/{paperless}")
    public WaybillResponse<Waybill> getwaybillPrePack(@PathParam("startDmsCode") Integer startDmsCode,
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
			Waybill waybill = findWaybillMessage(waybillCode);
			if (waybill == null) {
				this.logger.info("运单号【" + waybillCode
						+ "】调用根据运单号获取运单包裹信息接口成功, 无数据");
				return new WaybillResponse<Waybill>(JdResponse.CODE_OK_NULL,
						JdResponse.MESSAGE_OK_NULL);
			}
			//调用预分拣接口获得基础资料信息
			this.setBasicMessage(waybill, startDmsCode ,localSchedule, paperless);

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
	private void setBasicMessage(Waybill waybill, Integer startDmsCode ,Integer localSchedule,Integer paperless) {
    	try {
			LabelPrintingRequest request = new LabelPrintingRequest();
			BaseResponseIncidental<LabelPrintingResponse> response = new BaseResponseIncidental<LabelPrintingResponse>();
			request.setWaybillCode(waybill.getWaybillCode());
			request.setDmsCode(startDmsCode);
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

			response = labelPrintingWS.dmsPrint(request);

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

    private Waybill findWaybillMessage(String waybillCode) {
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
     * @param waybillCode Or package
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
            BaseStaffSiteOrgDto site= baseService.queryDmsBaseSiteByCode(waybill.getData().getBusiId().toString());
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
    		this.logger.error("根据运单号【" + startDmsCode +"-"+ siteCode + "】 获取目的分拣中心信息接口",e);
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
			messageClient.sendMessage("dms_modify_order_info", json, modifyOrderInfo.getOrderId());
			jdResponse.setCode(200);
			jdResponse.setMessage("成功");
			logger.info("dms_modify_order_info执行订单修改电话或者地址发MQ成功 " + modifyOrderInfo.getOrderId());
		}catch (Exception e){
			jdResponse.setCode(1000);
			jdResponse.setMessage("执行订单修改MQ消息失败");
			logger.error("dms_modify_order_info执行订单修改电话或者地址MQ失败"+modifyOrderInfo.getOrderId()+"，失败原因 " + e);
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
			com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryWSProxy.getDataByChoice(
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

}
