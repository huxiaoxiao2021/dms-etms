package com.jd.bluedragon.common.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.order.ws.OrderWebService;
import com.jd.bluedragon.distribution.product.domain.Product;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;


@Service("waybillCommonService")
public class WaybillCommonServiceImpl implements WaybillCommonService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ProductService productService;

    /* 运单查询 */
    @Autowired
    private WaybillQueryApi waybillQueryApi;

    @Autowired
    private BaseService baseService;

    @Autowired
    private OrderWebService orderWebService;

    @Profiled(tag = "WaybillCommonServiceImpl.findByWaybillCode")
    public Waybill findByWaybillCode(String waybillCode) {
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), false, false);
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单WSS数据成功，运单【" + waybill + "】");
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
        }
        if (waybill == null) {
            // 无数据
            this.logger.info("运单号【 " + waybillCode + "】的调用运单WSS数据为空，调用订单中间件开始");
            waybill = this.getWaybillFromOrderService(waybillCode);
            this.logger
                    .info("运单号【 " + waybillCode + "】的调用运单WSS数据为空，调用订单中间件结束，返回值【" + waybill + "】");
        }
        return waybill;
    }
    

	@Override
	public Waybill findWaybillAndPack(String waybillCode) {
		Waybill waybill = null;
		
		try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                if (Waybill.isInvalidWaybill(waybill)) {
                    this.logger.error("运单号【 " + waybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                    return null;
                }
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单WSS数据成功");
            
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
        }
        
		return waybill;
	}

	@Override
	public Waybill findWaybillAndPack(String waybillCode, boolean isQueryWaybillC, boolean QueryWaybillE, boolean QueryWaybillM, boolean isQueryPackList) {
		Waybill waybill = null;
		
		try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(isQueryWaybillC);
            wChoice.setQueryWaybillE(QueryWaybillE);
            wChoice.setQueryWaybillM(QueryWaybillM);
            wChoice.setQueryPackList(isQueryPackList);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getDataByChoice(
                    waybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                if (Waybill.isInvalidWaybill(waybill)) {
                	this.logger.error("运单号【 " + waybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                	return null;
                }
            }
            this.logger.info("运单号【 " + waybillCode + "】调用运单WSS数据成功");
            
        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
        }
        
		return waybill;
	}

    @Override
    public InvokeResult<Waybill> getReverseWaybill(String oldWaybillCode) {
        InvokeResult<Waybill> result=new InvokeResult<Waybill>();
        Waybill waybill = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
            wChoice.setQueryWaybillE(true);
            wChoice.setQueryWaybillM(true);
            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryApi.getReturnWaybillByOldWaybillCode(
                    oldWaybillCode, wChoice);
            if (baseEntity != null && baseEntity.getData() != null) {
                waybill = this.convWaybillWS(baseEntity.getData(), true, true);
                if (Waybill.isInvalidWaybill(waybill)) {
                    this.logger.error("运单号【 " + oldWaybillCode + "】验证运单数据缺少必要字段，运单【" + waybill + "】");
                    result.customMessage(-1,"运单缺少必要数据");
                    return result;
                }
            }
            if(logger.isInfoEnabled()) {
                this.logger.info("运单号【 " + oldWaybillCode + "】调用运单JSF数据成功");
            }
        } catch (Exception e) {
            this.logger.error("运单号【 " + oldWaybillCode + "】调用运单JSF异常：", e);
            result.error(e);
        }
        result.setData(waybill);
        return result;
    }

    /**
     * 获取订单信息通过订单中间件
     * @param waybillCode 运单号
     * @return
     */
    @Profiled(tag = "WaybillCommonServiceImpl.getWaybillFromOrderService")
    public Waybill getWaybillFromOrderService(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            this.logger.error("通过运单号调用非运单接口获取运单数据，传入参数为空");
            return null;
        }
        if(!StringUtils.isNumeric(waybillCode.trim())){
            this.logger.error("通过运单号调用非运单接口获取运单数据，传入参数为非数字,立即返回NULL");
            return null;
        }
        this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件开始");
        Waybill waybill = this.orderWebService.getWaybillByOrderId(Long.valueOf(waybillCode));
        List<Product> products = this.productService.getOrderProducts(Long.parseLong(waybillCode));

        if (waybill != null) {
            this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单【" + waybill + "】，非POP，运单类型【"
                    + waybill.getType() + "】");
            waybill.setProList(products);
        } else {
            this.logger.error("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单为空");
        }
        return waybill;
    }

    @Profiled(tag = "WaybillCommonServiceImpl.getHisWaybillFromOrderService")
    public Waybill getHisWaybillFromOrderService(String waybillCode) {
        if (StringUtils.isBlank(waybillCode)) {
            this.logger.error("通过运单号调用非运单接口获取运单数据，传入参数为空");
            return null;
        }

        this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件开始");
		
		Waybill waybill = this.findByWaybillCode(waybillCode);
		
		if (waybill == null) {
			waybill = this.orderWebService.getHisWaybillByOrderId(Long.valueOf(waybillCode));
		}

        List<Product> products = this.productService.getOrderProducts(Long.parseLong(waybillCode));
        if (waybill != null) {
            this.logger.info("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单【" + waybill + "】，非POP，运单类型【"
                    + waybill.getType() + "】");
            waybill.setProList(products);
        } else {
            this.logger.error("通过运单号调用非运单接口获取运单数据，调用运单中间件结束，运单为空");
        }
        return waybill;
    }

    /**
     * 转换运单基本信息
     *
     * @param waybillWS
     * @return
     */
    public Waybill convWaybillWS(BigWaybillDto bigWaybillDto, boolean isSetName, boolean isSetPack) {
        if (bigWaybillDto == null) {
            this.logger.debug("转换运单基本信息 --> 原始运单数据集bigWaybillDto为空");
            return null;
        }
        com.jd.etms.waybill.domain.Waybill waybillWS = bigWaybillDto.getWaybill();
        if (waybillWS == null) {
            this.logger.debug("转换运单基本信息 --> 原始运单数据集waybillWS为空");
            return null;
        }
        WaybillManageDomain manageDomain = bigWaybillDto.getWaybillState();
        if (manageDomain == null) {
            this.logger.debug("转换运单基本信息 --> 原始运单数据集manageDomain为空");
            return null;
        }
        Waybill waybill = new Waybill();
        waybill.setWaybillCode(waybillWS.getWaybillCode());
        waybill.setPopSupId(waybillWS.getConsignerId());
        waybill.setPopSupName(waybillWS.getConsigner());
        waybill.setBusiId(waybillWS.getBusiId());
        waybill.setBusiName(waybillWS.getBusiName());

        // 设置站点
        waybill.setSiteCode(waybillWS.getOldSiteId());
        if (isSetName) {
        	dealWaybillSiteName(waybill);
        }

        waybill.setPaymentType(waybillWS.getPayment());
		waybill.setQuantity(waybillWS.getGoodNumber());
		waybill.setWeight(waybillWS.getGoodWeight());
		waybill.setAddress(waybillWS.getReceiverAddress());
		waybill.setOrgId(waybillWS.getArriveAreaId());
		waybill.setSendPay(waybillWS.getSendPay());
		waybill.setType(waybillWS.getWaybillType());
		waybill.setTransferStationId(waybillWS.getTransferStationId());
		waybill.setCrossCode(waybillWS.getSlideCode());
		waybill.setDistributeStoreId(waybillWS.getDistributeStoreId());
        waybill.setWaybillSign(waybillWS.getWaybillSign());

//        PickupTask pick = bigWaybillDto.getPickupTask();
//        if (pick != null) {
//            waybill.setServiceCode(pick.getServiceCode());
//        }

		
		if (isSetPack) {
			List<DeliveryPackageD> ds = bigWaybillDto.getPackageList();
			if (ds == null || ds.size() <= 0) {
				this.logger.error("转换包裹信息 --> 运单号【" + waybill.getWaybillCode() + "】,原始运单数据集bigWaybillDto为空或size为空");
			} else {
				// 转换包裹信息
				this.logger.debug("转换包裹信息 --> 运单号：" + waybill.getWaybillCode()
						+ "转换包裹信息, 包裹数量为:" + ds.size());
				if (BusinessHelper.checkIntNumRange(ds.size())) {
					List<Pack> packList = new ArrayList<Pack>();
					for (DeliveryPackageD d : ds) {
						Pack pack = new Pack();
						pack.setWaybillCode(d.getWaybillCode());
						pack.setPackCode(d.getPackageBarcode());
						if (d.getGoodWeight() != null) {
							pack.setWeight(String.valueOf(d.getGoodWeight()));
						}
						if (StringUtils.isNotEmpty(d.getPackageBarcode())) {
							String[] pcs = d.getPackageBarcode().split("[-NS]");
							pack.setPackSerial(Integer.valueOf(pcs[1]));
						} else {
							this.logger.info("转换包裹信息 --> 运单号："
									+ waybill.getWaybillCode() + "生成包裹号,包裹号为空");
						}
						pack.setPackId(d.getPackageId());
						packList.add(pack);
					}
					waybill.setPackList(packList);
				} else {
					this.logger.error("转换包裹信息【运单返回】 --> 运单号："
							+ waybill.getWaybillCode() + ", 包裹数量为:" + ds.size());
				}
			}
		}
		
		return waybill;
    }
    
    private void dealWaybillSiteName(Waybill waybill) {
    	if (waybill == null) {
    		return;
    	}
        // 设置站点
        Integer siteCode = waybill.getSiteCode();
        String waybillCode = waybill.getWaybillCode();
        // 调用获取站点接口
        this.logger.info("运单号为【 " + waybillCode + "】 调用接口设置站点名称开始");
        if (siteCode != null) {
            waybill.setSiteCode(siteCode);
            // 根据站点ID获取站点Name
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseService
                    .queryDmsBaseSiteByCode(String.valueOf(siteCode));
            if (baseStaffSiteOrgDto != null) {
                waybill.setSiteName(baseStaffSiteOrgDto.getSiteName());
                this.logger.info("运单号为【 " + waybillCode + "】  调用接口设置站点名称成功【 "
                        + siteCode + "-" + waybill.getSiteName() + "】");
            } else {
                this.logger.info("运单号为【 " + waybillCode + "】  查找不到站点【" + siteCode
                        + "】相关信息");
            }
        }
        
        this.logger.info("运单号为【 " + waybillCode + "】 调用接口设置中转站站点名称开始");
		// 设置中转站站点名称
		Integer transferStationId = waybill.getTransferStationId();
		if (transferStationId != null) {
			waybill.setTransferStationId(transferStationId);
			// 根据中转站站点ID获取中转站站点Name
			BaseStaffSiteOrgDto baseStaffSiteOrgDto = this.baseService
            		.queryDmsBaseSiteByCode(String.valueOf(transferStationId));
			if (baseStaffSiteOrgDto != null) {
				waybill.setTransferStationName(baseStaffSiteOrgDto.getSiteName());
				this.logger.info("运单号为【 " + waybillCode + "】 调用接口设置中转站站点名称成功【 "
						+ transferStationId + "-"
						+ waybill.getTransferStationName() + "】");
			} else {
				this.logger
						.info("运单号为【 " + waybillCode + "】  查找不到中转站站点【" + transferStationId + "】 相关信息");
			}
		}
	}
}
