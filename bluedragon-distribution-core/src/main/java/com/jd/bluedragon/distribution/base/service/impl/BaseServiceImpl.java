package com.jd.bluedragon.distribution.base.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.jd.etms.basic.cache.proxy.BasicMinorWSProxy;
import com.jd.etms.basic.domain.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.redis.TaskMode;
import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.distribution.base.domain.PdaStaff;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.Product;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.basic.cache.proxy.BasicMajorWSProxy;
import com.jd.etms.basic.dto.BaseGoodsPositionDto;
import com.jd.etms.basic.dto.BasePdaUserDto;
import com.jd.etms.basic.dto.BaseSelfDDto;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;
import com.jd.etms.basic.dto.BaseStoreInfoDto;
import com.jd.etms.basic.dto.BaseTradeInfoDto;
import com.jd.etms.basic.saf.BasicSafInterface;
import com.jd.etms.basic.wss.BasicMajorWS;
import com.jd.etms.basic.wss.BasicMinorWS;
import com.jd.etms.utils.cache.annotation.Cache;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;

@Service("baseService")
public class BaseServiceImpl implements BaseService {

	/** 日志 */
	private Logger log = Logger.getLogger(BaseServiceImpl.class);

	@Autowired
	@Qualifier("basicMinorWSSaf")
	private BasicMinorWS basicMinorWSSaf;

	@Autowired
	@Qualifier("basicMajorWSSaf")
	private BasicMajorWS basicMajorWSSaf;
	
	@Autowired
	@Qualifier("basicMajorWSProxy")
	private BasicMajorWSProxy basicMajorWSProxy;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	WaybillQueryApi waybillQueryApi;
	
	@Autowired
	WaybillQueryManager waybillQueryManager;

	@Autowired
	SysConfigDao sysConfigDao;
	
	@Autowired
	@Qualifier("basicSafInterface")
	BasicSafInterface basicSafInterface;

	@Autowired
	BaseMinorManager baseMinorManager;

    @Autowired
    private BasicMinorWSProxy basicMinorWSProxy;
	
	@Autowired
	private ProductService productService;
	
	@Override
	public PdaStaff login(String erpcode, String password) {
		// TODO Auto-generated method stub
		/** 验证结果 */
		PdaStaff result = new PdaStaff();
		/** 调用基础信息接口查询PDA用户 */
		// 测试接口代码 baseMinorServiceProxy.getServerDate() 取服务器时间
		BasePdaUserDto pdadata = null;
		try {
			pdadata = basicMinorWSSaf.pdaUserLogin(erpcode, password);
		} catch (Exception e) {
			log.error("调用baseMinorServiceProxy.pdaUserLogin接口出现异常！", e);
		}
		/** 错误标示 */
		Integer errorcode = pdadata != null ? pdadata.getErrorCode() : 99999;
		/** 根据返回的错误标示进行处理 */
		switch (errorcode) {
			case 99999:
				/** 返回空数据 */
				// 设置错误标示和错误信息
				result.setError(true);
				result.setErrormsg("基础信息服务异常");
				// 返回结果
				return result;
			case 0:/** errorcode=0表示获取用户信息失败 */
			case -1:
				/** errorcode=-1表示验证失败 */
				// 设置错误标示和错误信息
				result.setError(true);
				result.setErrormsg(pdadata.getMessage());
				// 返回结果
				return result;
			case 1:
				/** errorcode=1表示验证成功，返回用户ID和名称，分拣中心ID和名称 */
				result.setError(false);
				// 用户ID
				result.setStaffId(pdadata.getStaffId());
				// 用户名称
				result.setStaffName(pdadata.getStaffName());
				// 分拣中心ID
				result.setSiteId(pdadata.getSiteId());
				// 分拣中心名称
				result.setSiteName(pdadata.getSiteName());
				// 机构ID
				result.setOrganizationId(pdadata.getOrganizationId());
				// 机构名称
				result.setOrganizationName(pdadata.getOrganizationName());
				// DMS编码
				result.setDmsCod(pdadata.getDmsCode());
				// 返回结果
				return result;
			default:
				/** 未知状态 */
				// 设置错误标示和错误信息
				result.setError(true);
				result.setErrormsg("调用服务出现未知状态.错误信息为[" + pdadata.getMessage() + "]");
				// 返回结果集
				return result;
		}
	}

	public List<SysConfig> queryConfigByKey(Map<String, Object> params) {
		return sysConfigDao.queryByKey(params);
	}
	
	public List<SysConfig> queryConfigByKey(String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("configName", key);
		return sysConfigDao.queryByKey(params);
	}

    @Cache(key = "sysconfig.cache.@args0", memoryEnable = false, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public List<SysConfig> queryConfigByKeyWithCache(String key){
        List<SysConfig> lst=  queryConfigByKey(key);
        log.warn("sysconfig.cache."+key);
        return lst;
    }

	@Cache(key = "basicMajorServiceProxy.getBaseStaffByOrgId@args02", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public BaseStaffSiteOrgDto[] queryDriverByOrgId(Integer orgid) {
		/** 查询司机信息 */
		/** 获取某个机构下的角色为staffRole员工信息 staff：2 司机 */
		log.info("调用basicMajorServiceProxy.getBaseStaffByOrgId(id, 2)接口");
		try {
			List<BaseStaffSiteOrgDto> resultal = basicMajorWSProxy.getBaseStaffListByOrgId(orgid,
			        2);
			if (resultal != null) {
				return resultal.toArray(new BaseStaffSiteOrgDto[0]);
			}
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseStaffByOrgId(id, 2)异常", e);
		}
		return null;
	}

	@Override
	@Cache(key = "baseMinorServiceProxy.getBaseVehicleByOrg@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public BaseVehicle[] queryVehicleByOrgId(Integer orgid) {
		/** 查询车辆信息 */
		log.info("调用baseMinorServiceProxy.getBaseVehicleByOrg(id)接口");
		try {
			List<BaseVehicle> resultal = basicMinorWSSaf.getBaseVehicleByOrg(orgid);
			if (resultal != null) {
				return resultal.toArray(new BaseVehicle[0]);
			}
		} catch (Exception e) {
			log.error("调用baseMinorServiceProxy.getBaseVehicleByOrg(id)异常", e);
		}
		return null;
	}
	
	public BaseStaffSiteOrgDto getBaseStaffSiteOrgDtoFromStore(
			BaseStoreInfoDto store) {
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = new BaseStaffSiteOrgDto();
		baseStaffSiteOrgDto.setDmsSiteCode(store.getDmsSiteCode());
		baseStaffSiteOrgDto.setStoreCode(store.getDmsStoreId());
		baseStaffSiteOrgDto.setSiteCode(store.getStoreSiteCode());
		baseStaffSiteOrgDto.setSiteName(store.getDmsStoreName());
		baseStaffSiteOrgDto.setCustomCode(store.getCustomCode());
		baseStaffSiteOrgDto.setSiteType(store.getDmsType());
		baseStaffSiteOrgDto.setOrgId(store.getParentId());
		baseStaffSiteOrgDto.setOrgName(store.getOrgName());
		return baseStaffSiteOrgDto;
	}

	@Override
	@Cache(key = "basicMajorServiceProxy.getDmsBaseSiteAllByOrgId@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	public BaseStaffSiteOrgDto[] querySiteByOrgID(Integer orgid) {
		/** 查询所有站点的信息 */
		log.info("调用basicMajorServiceProxy.getBaseSiteAllByOrgId(id)接口");
		try {
			/** 不包括库房的信息 */
			// return (List<T>)basicMajorServiceProxy.getBaseSiteAllByOrgId(id);
			/** 包括库房信息 */
			List<BaseStaffSiteOrgDto> resultal = new ArrayList<BaseStaffSiteOrgDto>();
			
			List<BaseStaffSiteOrgDto> staff = basicMajorWSProxy
			        .getBaseSiteAllByOrgIdAndTime(orgid ,null,null);
			
			resultal.addAll(staff);
			
			List<BaseStoreInfoDto> store = basicMajorWSProxy
			        .getBaseStoreInfoByOrgIdAndTime(orgid ,null,null);
			
			for(BaseStoreInfoDto dto : store){
				resultal.add(getBaseStaffSiteOrgDtoFromStore(dto));
			}
			
			if (resultal != null) {
				return resultal.toArray(new BaseStaffSiteOrgDto[0]);
			}
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseSiteAllByOrgId(id)异常", e);
		}
		return null;
	}

	@Override
	public BaseStaffSiteOrgDto getSiteBySiteID(Integer siteid) {
		/** 根据站点ID查询站点信息，因为返回的数据是 单一数据为了结构统一，统一使用List */
		log.info("调用basicMajorServiceProxy.getBaseSiteBySiteId(id)接口");
		try {
			BaseStaffSiteOrgDto sitedto = baseMajorManager.getBaseSiteBySiteId(siteid);
			return sitedto;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseSiteBySiteId(id)异常", e);
		}
		return null;
	}

	@Override
	public BaseDataDict[] getBaseDataDictListByDate(Integer typeGroup) {
		/** 查询错误信息列表 */
		log.info("调用basicMajorServiceProxy.getBaseDataDictList(" + typeGroup + ",2, " + typeGroup
		        + ")接口");
		try {
			List<com.jd.etms.basic.domain.BaseDataDict> resultal = baseMajorManager
			        .getBaseDataDictList(typeGroup, 2, typeGroup);
			if (resultal != null) {
				return resultal.toArray(new BaseDataDict[0]);
			}
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseDataDictList(" + typeGroup + ",2, "
			        + typeGroup + ")异常", e);
		}
		return null;
	}

    /**
     * 非递归获取字典树
     * @param typeGroup 树类别标识
     * @return
     */
    public List<BaseDataDict> getBaseDictionaryTree(int typeGroup){
        List<Integer> parentIds=new ArrayList<Integer>();
        parentIds.add(0);
        int layer=1;
        List<BaseDataDict> list=new ArrayList<BaseDataDict>();
        List<BaseDataDict> items=new ArrayList<BaseDataDict>();
        do{
            items.clear();
            for(Integer parentId:parentIds){
                items.addAll(baseMajorManager.getBaseDataDictList(parentId,layer,typeGroup));
            }
            parentIds.clear();
            if(null!=items&&items.size()>0){
                list.addAll(items);
                for(BaseDataDict dic:items){
                    parentIds.add(dic.getTypeCode());
                }
            }
            ++layer;
        }while(items.size()>0);
        return list;
    }


	@Override
	public BaseDataDict[] getBaseDataDictListByDate(List<Integer> typeGroups) {
		/** 查询错误信息列表 */
		try {
			ArrayList<BaseDataDict> resultal = new ArrayList<BaseDataDict>();
			for (Integer typeGroup : typeGroups) {
				log.info("调用basicMajorServiceProxy.getBaseDataDictList(" + typeGroup + ",2, "
				        + typeGroup + ")接口");
				List<com.jd.etms.basic.domain.BaseDataDict> tmpal = baseMajorManager.getBaseDataDictList(
						typeGroup, 2, typeGroup);
				if (tmpal != null && tmpal.size() > 0) {
					resultal.addAll(tmpal);
				}
			}

			return resultal.toArray(new BaseDataDict[0]);
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			for (Integer typeGroup : typeGroups) {
				sb.append(typeGroup);
				sb.append(",");
			}
			String stypeGroup = sb.toString();
			stypeGroup = typeGroups.size() > 0 ? stypeGroup.substring(0, stypeGroup.length() - 1)
			        : stypeGroup;
			log.error("调用basicMajorServiceProxy.getBaseDataDictList()异常，待查询类型为：" + stypeGroup, e);
		}
		return null;
	}

	@Override
	public List<BaseDataDict> getBaseDataDictList(Integer parentId, Integer nodeLevel,
	        Integer typeGroup) {
		return baseMajorManager.getBaseDataDictList(parentId, nodeLevel, typeGroup);
	}

	@Override
	@Profiled
	@Cache(key = "basicMajorServiceProxy.getDmsBaseSiteByCode@args0", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000, 
		redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	public BaseStaffSiteOrgDto queryDmsBaseSiteByCode(String code) {
		try {
			BaseStaffSiteOrgDto result = basicMajorWSSaf.getDmsBaseSiteByCode(code);
			return result;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getDmsBaseSiteByCode(code)异常", e);
			return null;
		}
	}

	@Override
	@Cache(key = "BaseService.getAllOrg", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseOrg> getAllOrg() {
		try {
			List<BaseOrg> orgal = basicMajorWSSaf.getBaseOrgAll();
			return orgal;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseOrgAll()异常", e);
			return null;
		}
	}

	@Override
	public BaseStaffSiteOrgDto queryDriverByDriverCode(Integer drivercode) {
		try {
			BaseStaffSiteOrgDto staffdto = baseMajorManager.getBaseStaffByStaffId(drivercode);
			return staffdto;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseStaffByStaffId(drivercode)异常", e);
			return null;
		}
	}

	@Override
	@Cache(key = "baseMinorServiceProxy.queryVehicleByVehicleCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public BaseVehicle queryVehicleByVehicleCode(String vehiclecode) {
		try {
			BaseVehicle vehicle = basicMinorWSSaf.baseVehicleByLicense(vehiclecode);
			return vehicle;
		} catch (Exception e) {
			log.error("调用baseMinorServiceProxy.baseVehicleByLicense(vehiclecode)异常", e);
			return null;
		}
	}

	@Override
	@Cache(key = "baseMinorServiceProxy.getVehicleByBarCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public BaseVehicle queryVehicleByBarCode(String barcode) {
		try {
			BaseVehicle vehicle = basicMinorWSSaf.getVehicleByBarCode(barcode);
			return vehicle;
		} catch (Exception e) {
			log.error("调用baseMinorServiceProxy.getVehicleByBarCode(barcode)异常", e);
			return null;
		}
	}

	@Override
	public List<com.jd.etms.basic.domain.BaseDataDict> getBaseDataDictListByDate(String date) {
		try {
			BaseDataDict baseDataDict = new BaseDataDict();
			baseDataDict.setParentId(110);
			baseDataDict.setNodeLevel(2);
			baseDataDict.setTypeGroup(110);
			List<com.jd.etms.basic.domain.BaseDataDict> results = basicMajorWSSaf
			        .getBaseDataDictByTime(date, baseDataDict);
			return results;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseDataDictByTime(date, baseDataDict)异常", e);
			return null;
		}
	}
	
	@Profiled
	public List<BaseStaffSiteOrgDto> getDmsSiteAll() {
		try {
			List<BaseStaffSiteOrgDto> results = baseMajorManager.getDmsSiteAll();
			return results;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getDmsSiteAll()异常", e);
			return null;
		}
	}

	@Cache(key = "basicMajorServiceProxy.getBaseSiteByAssort@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseSite> getDmsSitBySiteType(Integer siteType) {
		BaseSite baseSite = new BaseSite();
		baseSite.setSiteType(siteType);
		try {
			List<BaseSite> results = basicMajorWSSaf.getBaseSiteByAssort(baseSite);
			return results;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseSiteByAssort()异常", e);
			return null;
		}
	}

	/**
	 * 通过员工id查询分拣中心信息
	 *
	 * @param baseStaffId
	 * @return
	 */
	public Map<Integer, String> getSiteInfoByBaseStaffId(Integer baseStaffId) {
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager
		        .getBaseStaffByStaffId(baseStaffId);

		List<BaseStaffSiteOrgDto> baseStaffSiteOrgList = new ArrayList<BaseStaffSiteOrgDto>();
		if (null == baseStaffSiteOrgDto) {
			baseStaffSiteOrgList = baseMajorManager.getDmsSiteAll();
		}

		if (null != baseStaffSiteOrgDto) {
			Integer defaultSiteType = baseStaffSiteOrgDto.getSiteType();

			if (null != defaultSiteType
			        && (Constants.DMS_SITE_TYPE.equals(defaultSiteType) || Constants.TRANS_SORTING_SITE_TYPE_SECOND == defaultSiteType)) {
				log.debug("初始化查询条件-->员工信息 属于分拣中心");
				// 分拣中心
				baseStaffSiteOrgList.add(baseStaffSiteOrgDto);
			}
		}

		Map<Integer, String> map = new HashMap<Integer, String>();
		if (baseStaffSiteOrgList.isEmpty()) {
			return map;
		}
		for (BaseStaffSiteOrgDto dto : baseStaffSiteOrgList) {
			map.put(dto.getSiteCode(), dto.getSiteName());
		}
		return map;
	}

	/**
	 * 普通仓使用
	 */
	@Override
	public ReverseSendWms getWaybillByOrderCode(String orderCode) {
		ReverseSendWms reverseSendWms = null;
		try {
			// 调用运单接口
			this.log.info("调用运单接口, 订单号为： " + orderCode);
			WChoice wChoice = new WChoice();
			wChoice.setQueryWaybillC(true);
			wChoice.setQueryWaybillE(true);
			wChoice.setQueryWaybillM(true);
			wChoice.setQueryGoodList(true);
			wChoice.setQueryPackList(true);
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryApi.getDataByChoice(orderCode,
			        wChoice);
			if (baseEntity != null && baseEntity.getData() != null) {
				reverseSendWms = convWaybill(baseEntity.getData());
				if (reverseSendWms == null) {
					// 无数据
					log.info("BaseServiceImpl 调用运单接口, 订单号为： " + orderCode + " 调用运单WSS数据为空");
				}else if (reverseSendWms.getProList()==null||reverseSendWms.getProList().size()==0) {
					log.info("BaseServiceImpl 调用运单接口, 订单号为： " + orderCode + " 调用运单WSS商品数据为空");
				}
			}
		} catch (Exception e) {
			log.error("BaseServiceImpl --> getWaybillByOrderCode, 调用运单接口异常：", e);
			return null;
		}
		return reverseSendWms;
	}
	
	/**
	 * 转换运单基本信息
	 *
	 * @param waybillWS
	 * @return
	 */
	private ReverseSendWms convWaybill(BigWaybillDto bigWaybillDto) {
		if (bigWaybillDto == null) {
			log.info("BaseServiceImpl 转换运单基本信息 --> 原始运单数据bigWaybillDto为空");
			return null;
		}
		com.jd.etms.waybill.domain.Waybill waybillWS = bigWaybillDto.getWaybill();
		if (waybillWS == null) {
			log.info("BaseServiceImpl 转换运单基本信息 --> 原始运单数据集waybillWS为空");
			return null;
		}
		ReverseSendWms reverseSendWms = new ReverseSendWms();
		/****************************** 包裹号处理 *********************************************/
		StringBuffer packageCodeBuffer = new StringBuffer();
		String packageCode = "";
		for (DeliveryPackageD packageD : bigWaybillDto.getPackageList()) {
			packageCodeBuffer.append(packageD.getPackageBarcode());
			packageCodeBuffer.append(",");
		}
		if (packageCodeBuffer.length() > 0) {
			packageCode = packageCodeBuffer.toString();
			packageCode = packageCode.substring(0, packageCode.length() - 1);
		}
		/*************************************************************************************/
		List<Product> proList = new ArrayList<Product>();
		//首先检查运单中的商品信息是否为空,如为空则从订单中间件取得商品信息
		if(bigWaybillDto.getGoodsList()!=null&&bigWaybillDto.getGoodsList().size()>0){
			for (Goods good : bigWaybillDto.getGoodsList()) {
				Product product = new Product();
				product.setProductId(good.getSku());
				product.setProductName(good.getGoodName());
				product.setProductNum(good.getGoodCount());
				product.setProductPrice(good.getGoodPrice());
				product.setProductLoss("0");
				proList.add(product);
			}
		}else{
			//快生项目：从订单中间件获得商品明细
			try{
				log.info("运单商品明细为空, 改调用订单接口");
				List<com.jd.bluedragon.distribution.product.domain.Product> productList = this.productService.getOrderProducts(Long.valueOf(waybillWS.getWaybillCode()));
				for(com.jd.bluedragon.distribution.product.domain.Product prod: productList){
					Product product = new Product();
					product.setProductId(prod.getProductId());
					product.setProductName(prod.getName());
					product.setProductNum(prod.getQuantity());
					product.setProductPrice(String.valueOf(prod.getPrice()));
					product.setProductLoss("0");
					proList.add(product);
				}
			} catch (Exception e) {
				log.error("BaseServiceImpl --> convWaybill, 调用订单接口获得商品明细异常：", e);
			}
			
		}
		

		/*************************************************************************************/
		reverseSendWms.setCky2(bigWaybillDto.getWaybillState().getCky2());
		reverseSendWms.setLossQuantity(0);
		reverseSendWms.setOrgId(bigWaybillDto.getWaybill().getArriveAreaId());
		reverseSendWms.setPackageCodes(packageCode);
		reverseSendWms.setProList(proList);
		reverseSendWms.setStoreId(bigWaybillDto.getWaybillState().getStoreId());
		reverseSendWms.setType(bigWaybillDto.getWaybillState().getWaybillType());
        reverseSendWms.setWaybillSign(bigWaybillDto.getWaybill().getWaybillSign());
        reverseSendWms.setSourceCode(bigWaybillDto.getWaybill().getSourceCode());

        /*
		 * WaybillManageDomain manageDomain = bigWaybillDto.getWaybillState();
		 * if (manageDomain == null) {
		 * this.logger.info("转换运单基本信息 --> 原始运单数据集manageDomain为空"); return null;
		 * }
		 */
		return reverseSendWms;
	}

	/**
	 * 根据pop商家编号获取名称
	 *
	 * @param popCode
	 */
	@Override
	@Cache(key = "baseMinorServiceProxy.getTraderInfoByPopCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public String getPopBusinessNameByCode(String popCode) {
		try {
			BaseTradeInfoDto baseTradeInfoDto = basicMinorWSSaf
			        .getTraderInfoByPopCode(popCode);
			if (null == baseTradeInfoDto || StringUtils.isBlank(baseTradeInfoDto.getTraderName())) {
				return null;
			} else {
				return baseTradeInfoDto.getTraderName();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}

	}

	public void updateSysConfig(SysConfig sysconfig) {
		sysConfigDao.update(SysConfigDao.namespace, sysconfig);
	}

	public void insertSysConfig(SysConfig sysconfig) {
		sysConfigDao.add(SysConfigDao.namespace, sysconfig);
	}

	public SysConfig getSysConfig(Long sysconfigId) {
		return sysConfigDao.get(SysConfigDao.namespace, sysconfigId);
	}

	public BaseStaffSiteOrgDto getBaseStaffByStaffId(Integer staffId) {
		try {
			BaseStaffSiteOrgDto results = baseMajorManager.getBaseStaffByStaffId(staffId);
			return results;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseStaffByStaffId()异常", e);
			return null;
		}
	}


    @Override
    @Cache(key = "baseServiceImpl.getCachedStaffByStaffId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
    redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public BaseStaffSiteOrgDto getCachedStaffByStaffId(Integer staffId) {
        return getBaseStaffByStaffId(staffId);
    }

    @Cache(key = "basicMajorServiceProxy.getPopBaseSiteByOrgId@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseStaffSiteOrgDto> getPopBaseSiteByOrgId(Integer paramInteger) {
		List<BaseStaffSiteOrgDto> dataList = this.basicMajorWSProxy
		        .getPopBaseSiteByOrgId(paramInteger);
		if (dataList != null && !dataList.isEmpty()) {
			BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
			dto.setSiteCode(-1);
			dto.setSiteName("其他");
			dataList.add(dto);
		}

		return dataList;
	}

	/**
	 * 根据参数查询 基础参数设置的总数据量
	 * @param map
	 * @return INTEGET
	 */
	public Integer totalSysconfigSizeByParams(String key){
		return sysConfigDao.totalSysconfigSizeByParams(key+"%");
	}
	
	/**
	 * 初始化任务写入的源类型 1-redis 2-db
	 */
	public TaskMode getTaskMode() {
		int mode = 0;
		try {
			String taskMode = null;//本地配置的基础资料系统中的标识ID
			Integer taskModeNum = null;
			
			//1.获取基础资料中源类型的标识ID
			try {
				taskMode = PropertiesHelper.newInstance().getValue(Constants.TASK_MODE_KEY);
				taskModeNum = Integer.parseInt(taskMode);
			} catch (Exception e) {
				log.error("getWorkerTaskMode 获得key值失败 ", e);
			}
			if (StringHelper.isEmpty(taskMode) || (taskModeNum == null)) {
				return TaskMode.DB;
			}

			//2.根据标识ID从基础资料中查询源类型
			List<BaseDataDict> list = basicMajorWSSaf
					.getBaseDataDictList(taskModeNum, 2, taskModeNum);
			for (BaseDataDict item : list) {
				if (item.getTypeName().equalsIgnoreCase("redis")) {
					mode = item.getTypeCode();
					break;
				}
			}

		} catch (Exception e) {
			log.error("getWorkerTaskMode 失败 ", e);
		}

		//3.根据取回的基础资料标识值，返回源类型
		if (mode == 1) {
			return TaskMode.REDIS;
		} else {
			return TaskMode.DB;
		}
	}
	
	
	@Override
	public String getSiteNameBySiteID(Integer siteid) {
		BaseStaffSiteOrgDto site =this.getSiteBySiteID(siteid);
		if(site==null){
			return null;
		}else{
			return site.getSiteName();
		}
		
	}
	

	@Override
	@Cache(key = "basicSafInterface.getSiteSelfDBySiteCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000, 
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public Integer getSiteSelfDBySiteCode(Integer sitecode){
		BaseResult<BaseSelfDDto>  result = basicSafInterface.getSiteSelfDBySiteCode(sitecode);
		if(result==null){
			return -1;
		}
		
		if(result.getData() == null){
			return -1;
		}
		
		return result.getData().getBelongCode();
	}

	/**
	 * 
	 * 分拣细分，分拣中心ID与目的地 获取货柜信息
	 * */
	@Override
	public ElectronSite getBaseGoodsPositionDmsCodeSiteCode(Integer createCode,
			Integer receiveCode) {
		List<BaseGoodsPositionDto> baseGoodsPositionDto= baseMinorManager.getBaseGoodsPositionDmsCodeSiteCode( createCode,Constants.SEPARATOR_HYPHEN, receiveCode);
		if (baseGoodsPositionDto != null && !baseGoodsPositionDto.isEmpty()) {
			return toElectronSite(baseGoodsPositionDto.get(0));
		}
		return null;
	}

    @Override
    @Cache(memoryEnable = false,key = "getAssortById@args0")
    public Assort getAssortById(Integer assortId) {
        List<Assort> list= basicMinorWSProxy.getBaseAssortList();
        if(null!=list&&list.size()>0){
            for (Assort a:list){
                if(a.getAssId().equals(assortId))
                    return a;
            }
        }
        return null;
    }


    @SuppressWarnings("static-access")
	private ElectronSite toElectronSite(BaseGoodsPositionDto baseGoodsPositionDto){
		ElectronSite electronSite = new ElectronSite();
		if(baseGoodsPositionDto!=null){
			electronSite.setCode(electronSite.CODE_OK);
			electronSite.setTaskAreaNo(baseGoodsPositionDto.getTaskAreaNo());
			electronSite.setElectronNo(baseGoodsPositionDto.getElectronNo());
			electronSite.setIp(baseGoodsPositionDto.getIpAddress());
		}else{
			electronSite.setCode(electronSite.CODE_OK_NULL);
			electronSite.setMessage(electronSite.MESSAGE_OK_NULL);
			
		}
		return electronSite;
	}
	
	public static void main(String[] args){
		
		BigWaybillDto bigWaybillDto = new BigWaybillDto();
		List<Goods> goodsList = new ArrayList<Goods>();
		Goods goodx = new Goods();
		goodx.setGoodPrice("12");
		goodsList.add(goodx);
		bigWaybillDto.setGoodsList(goodsList);
		
		
		List<Product> proList = new ArrayList<Product>();
		//首先检查运单中的商品信息是否为空,如为空则从订单中间件取得商品信息
		if(bigWaybillDto.getGoodsList()!=null&&bigWaybillDto.getGoodsList().size()>0){
			for (Goods good : bigWaybillDto.getGoodsList()) {
				Product product = new Product();
				product.setProductId(good.getSku());
				product.setProductName(good.getGoodName());
				product.setProductNum(good.getGoodCount());
				product.setProductPrice(good.getGoodPrice());
				product.setProductLoss("0");
				proList.add(product);
			}
		}else{
			//从订单中间件获得商品明细
			List<com.jd.bluedragon.distribution.product.domain.Product> productList = new ArrayList<com.jd.bluedragon.distribution.product.domain.Product>();
			com.jd.bluedragon.distribution.product.domain.Product temp1 = new com.jd.bluedragon.distribution.product.domain.Product();
			com.jd.bluedragon.distribution.product.domain.Product temp2 = new com.jd.bluedragon.distribution.product.domain.Product();
			com.jd.bluedragon.distribution.product.domain.Product temp3 = new com.jd.bluedragon.distribution.product.domain.Product();
			temp1.setName("手机1");
			temp1.setPrice(BigDecimal.valueOf(1100.00));
			temp1.setProductId("5");
			temp1.setQuantity(1);
			
			temp2.setName("手机2");
			temp2.setPrice(BigDecimal.valueOf(1200.00));
			temp2.setProductId("2");
			temp2.setQuantity(2);
			
			temp3.setName("手机3");
			temp3.setPrice(BigDecimal.valueOf(1300.01));
			temp3.setProductId("3");
			temp3.setQuantity(3);
			productList.add(temp1);
			productList.add(temp2);
			productList.add(temp3);
			
			for(com.jd.bluedragon.distribution.product.domain.Product prod: productList){
				Product product = new Product();
				product.setProductId(prod.getProductId());
				product.setProductName(prod.getName());
				product.setProductNum(prod.getQuantity());
				product.setProductPrice(String.valueOf(prod.getPrice()));
				product.setProductLoss("0");
				proList.add(product);
			}
		}		
	}
}
