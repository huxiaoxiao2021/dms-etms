package com.jd.bluedragon.distribution.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperateUser;
import com.jd.bluedragon.common.dto.base.request.Pager;
import com.jd.bluedragon.common.dto.basedata.request.StreamlinedBasicSiteQuery;
import com.jd.bluedragon.common.dto.sysConfig.request.FuncUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.*;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.redis.TaskMode;
import com.jd.bluedragon.distribution.base.dao.SysConfigDao;
import com.jd.bluedragon.distribution.base.domain.BasePdaUserDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.*;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.bluedragon.distribution.product.service.ProductService;
import com.jd.bluedragon.distribution.reverse.domain.Product;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.distribution.sysloginlog.domain.ClientInfo;
import com.jd.bluedragon.utils.*;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Goods;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ql.basic.domain.Assort;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.domain.BaseOrg;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.dto.*;
import com.jd.ql.basic.proxy.BasicPrimaryWSProxy;
import com.jd.ql.basic.proxy.BasicSecondaryWSProxy;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.basic.ws.BasicMixedWS;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import com.jd.ql.dms.report.domain.StreamlinedBasicSite;
import com.jd.ssa.domain.UserInfo;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.CarrierDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("baseService")
public class BaseServiceImpl extends AbstractClient implements BaseService, ErpValidateService {

	/** 日志 */
	private Logger log = LoggerFactory.getLogger(BaseServiceImpl.class);

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	WaybillQueryManager waybillQueryManager;

	@Autowired
	SysConfigDao sysConfigDao;

    @Autowired
    SysConfigService sysConfigService;
	
	@Autowired
	@Qualifier("basicMixedWS")
	BasicMixedWS basicMixedWS;

	@Autowired
	BaseMinorManager baseMinorManager;

	@Autowired
	private ProductService productService;
	
	@Autowired
	@Qualifier("basicPrimaryWS")
	private BasicPrimaryWS basicPrimaryWS;

	@Autowired
	@Qualifier("basicPrimaryWSProxy")
	private BasicPrimaryWSProxy basicPrimaryWSProxy;

	@Autowired
	@Qualifier("basicSecondaryWS")
	private BasicSecondaryWS basicSecondaryWS;

	@Autowired
	@Qualifier("basicSecondaryWSProxy")
	private BasicSecondaryWSProxy basicSecondaryWSProxy;

    @Autowired
	private UserVerifyManager userVerifyManager;

    @Autowired
	private BasicQueryWSManager basicQueryWSManager;

    @Autowired
	private BasicSelectWsManager basicSelectWsManager;

	@Autowired
	@Qualifier("thirdValidateService")
	private ErpValidateService thirdValidateService;

	/**
	 * @description: erp登录校验处理逻辑
	 * @return: com.jd.bluedragon.distribution.base.domain.BasePdaUserDto
	 * @author: lql
	 * @date: 2020/8/20 13:23
	 **/
    @Override
    public BasePdaUserDto pdaUserLogin(String userid, String password, ClientInfo clientInfo, Byte loginVersion) {
        BasePdaUserDto basePdaUserDto = new BasePdaUserDto();
        try {
			// 调用人事接口验证用户
			//User user = userVerifyManager.baseVerify(userid, password);
			//调用sso的SsoService验证用户
			InvokeResult<UserInfo> result = userVerifyManager.baseVerify(userid, password, loginVersion);
			log.info("userVerifyManager.baseVerify-userid[{}]password[{}]loginVersion[{}]result[{}]",userid, password, loginVersion,JsonHelper.toJson(result));
			if (result != null && result.getCode() != InvokeResult.RESULT_SUCCESS_CODE) {
				basePdaUserDto.setErrorCode(Constants.PDA_USER_LOGIN_FAILUE);
				basePdaUserDto.setMessage(result.getMessage());
			// 人事接口验证通过，获取基础资料信息
			} else {
//                    BaseStaffSiteOrgDto basestaffDto = baseMajorManager.getBaseStaffByStaffIdNoCache(Integer.parseInt(String.valueOf(user.getUserId())));
				BaseStaffSiteOrgDto basestaffDto = baseMajorManager.getBaseStaffByErpNoCache(userid);
				if (null == basestaffDto) {
					basePdaUserDto.setErrorCode(Constants.PDA_USER_GETINFO_FAILUE );
					basePdaUserDto.setMessage(Constants.PDA_USER_GETINFO_FAILUE_MSG);
				} else {
					fillPdaUserDto(basePdaUserDto, basestaffDto,password);
				}
			}
        } catch (Exception e) {
            log.error("user login error {}" , userid, e);
			basePdaUserDto.setErrorCode(Constants.PDA_USER_ABNORMAL);
			basePdaUserDto.setMessage(Constants.PDA_USER_ABNORMAL_MSG);
        }
        return basePdaUserDto;
    }
	protected void fillPdaUserDto(BasePdaUserDto basePdaUserDto, BaseStaffSiteOrgDto baseStaffDto, String password) {
		basePdaUserDto.setSiteId(baseStaffDto.getSiteCode());
		basePdaUserDto.setSiteName(baseStaffDto.getSiteName());
		basePdaUserDto.setDmsCode(baseStaffDto.getDmsSiteCode());
		basePdaUserDto.setLoginTime(new Date());
		basePdaUserDto.setStaffId(baseStaffDto.getStaffNo());
		basePdaUserDto.setPassword(password);
		basePdaUserDto.setStaffName(baseStaffDto.getStaffName());
		basePdaUserDto.setErrorCode(Constants.PDA_USER_GETINFO_SUCCESS);
		basePdaUserDto.setMessage(Constants.PDA_USER_GETINFO_SUCCESS_MSG);
		basePdaUserDto.setOrganizationId(baseStaffDto.getOrgId());
		basePdaUserDto.setOrganizationName(baseStaffDto.getOrgName());
		basePdaUserDto.setSiteType(baseStaffDto.getSiteType());
		basePdaUserDto.setSubType(baseStaffDto.getSubType());
	}

	public List<SysConfig> queryConfigByKey(Map<String, Object> params) {
		return sysConfigDao.queryByKey(params);
	}
	
	public List<SysConfig> queryConfigByKey(String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("configName", key);
		return sysConfigDao.queryByKey(params);
	}

    @Cache(key = "sysconfig.cache.@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    public List<SysConfig> queryConfigByKeyWithCache(String key){
        List<SysConfig> lst=  queryConfigByKey(key);
        return lst;
    }

	public BaseStaffSiteOrgDto[] queryDriverByOrgId(Integer orgid) {
		/** 查询司机信息 */
		/** 获取某个机构下的角色为staffRole员工信息 staff：2 司机 */
		try {
			List<BaseStaffSiteOrgDto> resultal = baseMajorManager.getBaseStaffListByOrgId(orgid,
			        2);
			if (resultal != null) {
				return resultal.toArray(new BaseStaffSiteOrgDto[0]);
			}
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseStaffByOrgId(id, 2)异常", e);
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
	@Cache(key = "basicMajorServiceProxy.getDmsBaseSiteAllByOrgId@args0", memoryEnable = false, memoryExpiredTime = 5 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	public BaseStaffSiteOrgDto[] querySiteByOrgID(Integer orgid) {
		/** 查询所有站点的信息 */
		log.info("调用basicMajorServiceProxy.getBaseSiteAllByOrgId({})接口",orgid);
		try {
			/** 不包括库房的信息 */
			// return (List<T>)basicMajorServiceProxy.getBaseSiteAllByOrgId(id);
			/** 包括库房信息 */
			List<BaseStaffSiteOrgDto> resultal = new ArrayList<BaseStaffSiteOrgDto>();
			
			List<BaseStaffSiteOrgDto> staff = basicPrimaryWSProxy
			        .getBaseSiteAllByOrgIdAndTime(orgid ,null,null);
			
			resultal.addAll(staff);
			
			List<BaseStoreInfoDto> store = basicPrimaryWSProxy
			        .getBaseStoreInfoByOrgIdAndTime(orgid ,null,null);
			
			for(BaseStoreInfoDto dto : store){
				resultal.add(getBaseStaffSiteOrgDtoFromStore(dto));
			}
			
			if (resultal != null) {
				return resultal.toArray(new BaseStaffSiteOrgDto[0]);
			}
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseSiteAllByOrgId({})异常",orgid, e);
		}
		return null;
	}

	@Override
	public BaseStaffSiteOrgDto getSiteBySiteID(Integer siteid) {
		/** 根据站点ID查询站点信息，因为返回的数据是 单一数据为了结构统一，统一使用List */
		log.info("调用basicMajorServiceProxy.getBaseSiteBySiteId({})接口",siteid);
		try {
			BaseStaffSiteOrgDto sitedto = baseMajorManager.getBaseSiteBySiteId(siteid);
			return sitedto;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseSiteBySiteId({})异常",siteid, e);
		}
		return null;
	}

	@Override
	public BaseDataDict[] getBaseDataDictListByDate(Integer typeGroup) {
		/** 查询错误信息列表 */
		log.info("调用basicMajorServiceProxy.getBaseDataDictList({},2, {})接口",typeGroup,typeGroup);
		try {
			List<BaseDataDict> resultal = baseMajorManager
			        .getBaseDataDictList(typeGroup, 2, typeGroup);
			if (resultal != null) {
				return resultal.toArray(new BaseDataDict[0]);
			}
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseDataDictList({},2, {})接口",typeGroup,typeGroup, e);
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
	@JProfiler(jKey = "DMSWEB.BaseServiceImpl.getBaseDataDictListByDate", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseDataDict[] getBaseDataDictListByDate(List<Integer> typeGroups) {
		/** 查询错误信息列表 */
		try {
			ArrayList<BaseDataDict> resultal = new ArrayList<BaseDataDict>();
			for (Integer typeGroup : typeGroups) {
				log.info("调用basicMajorServiceProxy.getBaseDataDictList({},2, {})接口",typeGroup,typeGroup);
				List<BaseDataDict> tmpal = baseMajorManager.getBaseDataDictList(
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
			log.error("调用basicMajorServiceProxy.getBaseDataDictList()异常，待查询类型为：{}" , stypeGroup, e);
		}
		return null;
	}
	
	/**
	 * basicQueryWSManager.getDictList()接口，获取字典数据
	 * add by lhc
	 * 2016.9.1
	 */
	@Override
	public List<BasicDictDto> getDictListByGroupType(List<Integer> typeGroups) {
		/** 查询错误信息列表 */
		try {
			List<BasicDictDto> resultal = new ArrayList<BasicDictDto>();
			for (Integer typeGroup : typeGroups) {
				log.info("调用basicQueryWS.getDictList({},2, {})接口",typeGroup,typeGroup);
				String typeGroupStr = String.valueOf(typeGroup);
				List<BasicDictDto>  commonDtoList = basicQueryWSManager.getDictList(typeGroupStr, 2, typeGroupStr);
				if (!CollectionUtils.isEmpty(commonDtoList)) {
					resultal.addAll(commonDtoList);
				}else{
					if(log.isInfoEnabled()){
						log.info("请求basicQueryWS.getDictList()接口服务成功，获取字典数据为空！");
					}
				}
			}
			return resultal;
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder();
			for (Integer typeGroup : typeGroups) {
				sb.append(typeGroup);
				sb.append(",");
			}
			String stypeGroup = sb.toString();
			stypeGroup = typeGroups.size() > 0 ? stypeGroup.substring(0, stypeGroup.length() - 1)
			        : stypeGroup;
			log.error("调用basicQueryWSManager.getDictList()异常，待查询类型为：{}" , stypeGroup, e);
		}
		return null;
	}
	
	/**
	 * 承运商列表
	 * add by lhc
	 * 2016.9.1
	 */
	public List<CarrierDto> getCarrierInfoList(CarrierDto carrierDto){
		try{
			List<CarrierDto> commonDtoList = basicSelectWsManager.getCarrierInfoList(carrierDto);
			if (!CollectionUtils.isEmpty(commonDtoList)) {
				return commonDtoList;
			}else{
				log.warn("请求basicSelectWsManager.getCarrierInfoList()接口服务成功，获取承运商列表为空！carrierParamDto={}", JsonHelper.toJson(carrierDto));
			}
		}catch(Exception e){
			log.error("调用basicSelectWsManager.getCarrierInfoList()承运商列表异常，carrierParamDto={}", JsonHelper.toJson(carrierDto), e);
		}
		return null;
	}

	@Override
	public List<BaseDataDict> getBaseDataDictList(Integer parentId, Integer nodeLevel,
	        Integer typeGroup) {
		return baseMajorManager.getBaseDataDictList(parentId, nodeLevel, typeGroup);
	}


	@Override
	@Cache(key = "BaseService.getAllOrg", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public List<BaseOrg> getAllOrg() {
		CallerInfo info = Profiler.registerInfo("dmsWeb.jsf.basicPrimaryWS.getBaseOrgAll", Constants.UMP_APP_NAME_DMSWEB,false, true);
		try {
			List<BaseOrg> orgal = basicPrimaryWS.getBaseOrgAll();
			return orgal;
		} catch (Exception e) {
			Profiler.functionError(info);
			log.error("调用basicMajorServiceProxy.getBaseOrgAll()异常", e);
			return null;
		}finally {
			Profiler.registerInfoEnd(info);
		}
	}

	@Override
	public BaseStaffSiteOrgDto queryDriverByDriverCode(Integer drivercode) {
		try {
			BaseStaffSiteOrgDto staffdto = baseMajorManager.getBaseStaffByStaffId(drivercode);
			return staffdto;
		} catch (Exception e) {
			log.error("调用basicMajorServiceProxy.getBaseStaffByStaffId(drivercode)异常,drivercode={}",drivercode, e);
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
		CallerInfo info = Profiler.registerInfo("DMS.BaseServiceImpl.getSiteInfoByBaseStaffId", false, true);
		BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager
		        .getBaseStaffByStaffId(baseStaffId);

		List<BaseStaffSiteOrgDto> baseStaffSiteOrgList = new ArrayList<BaseStaffSiteOrgDto>();
		if (null == baseStaffSiteOrgDto) {
			//orgId=null标识所有分拣中心
			baseStaffSiteOrgList = basicPrimaryWSProxy.getBaseSiteByOrgIdSiteType(null,64);
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
			Profiler.registerInfoEnd(info);
			return map;
		}
		for (BaseStaffSiteOrgDto dto : baseStaffSiteOrgList) {
			map.put(dto.getSiteCode(), dto.getSiteName());
		}
		Profiler.registerInfoEnd(info);
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
			log.info("调用运单接口, 订单号为： {}" , orderCode);
			WChoice wChoice = new WChoice();
			wChoice.setQueryWaybillC(true);
			wChoice.setQueryWaybillE(true);
			wChoice.setQueryWaybillM(true);
			wChoice.setQueryGoodList(true);
			wChoice.setQueryPackList(true);
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(orderCode,wChoice);
			if (baseEntity != null && baseEntity.getData() != null) {
				reverseSendWms = convWaybill(baseEntity.getData());
				if (reverseSendWms == null) {
					// 无数据
					log.info("BaseServiceImpl 调用运单接口, 订单号为： {} 调用运单WSS数据为空", orderCode);
				}else if (reverseSendWms.getProList()==null||reverseSendWms.getProList().size()==0) {
					log.info("BaseServiceImpl 调用运单接口, 订单号为： {} 调用运单WSS商品数据为空", orderCode);
				}
			}
		} catch (Exception e) {
			log.error("BaseServiceImpl --> getWaybillByOrderCode, 调用运单接口异常：{}",orderCode, e);
			return null;
		}
		return reverseSendWms;
	}
	
	/**
	 * 转换运单基本信息
	 *
	 * @param
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
		if(bigWaybillDto.getPackageList() != null){
			for (DeliveryPackageD packageD : bigWaybillDto.getPackageList()) {
				packageCodeBuffer.append(packageD.getPackageBarcode());
				packageCodeBuffer.append(",");
			}
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
				//自营的订单才可以调用此接口
				if(SerialRuleUtil.isMatchNumeric(waybillWS.getWaybillCode())){
					List<com.jd.bluedragon.distribution.product.domain.Product> productList = this.productService.getOrderProducts(Long.valueOf(waybillWS.getVendorId()));
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
			} catch (Exception e) {
				log.error("BaseServiceImpl --> convWaybill, 调用订单接口获得商品明细异常：{}",waybillWS.getVendorId(), e);
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
        reverseSendWms.setBusiOrderCode(bigWaybillDto.getWaybill().getBusiOrderCode());
		reverseSendWms.setOrderId(bigWaybillDto.getWaybill().getVendorId());
		reverseSendWms.setXniType(bigWaybillDto.getWaybill().getWaybillType());
		reverseSendWms.setVenderId(bigWaybillDto.getWaybill().getShopCode());
		reverseSendWms.setSpareColumn3(bigWaybillDto.getWaybill().getSpareColumn3());
		reverseSendWms.setSendPay(bigWaybillDto.getWaybill().getSendPay());
        /*
		 * WaybillManageDomain manageDomain = bigWaybillDto.getWaybillState();
		 * if (manageDomain == null) {
		 * this.log.info("转换运单基本信息 --> 原始运单数据集manageDomain为空"); return null;
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
			BasicTraderInfoDTO baseTradeInfoDto = baseMinorManager.getTraderInfoByPopCode(popCode);
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
			log.error("调用basicMajorServiceProxy.getBaseStaffByStaffId()异常,staffId={}",staffId, e);
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
		List<BaseStaffSiteOrgDto> dataList = this.basicPrimaryWSProxy
		        .getBaseSiteAllByOrgId(paramInteger);
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
	 * @param
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
			List<BaseDataDict> list = basicPrimaryWS
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
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.basicMixedWS.getSiteSelfDBySiteCode",mState={JProEnum.TP,JProEnum.FunctionError})
	public Integer getSiteSelfDBySiteCode(Integer sitecode){
		BaseResult<BaseSelfDDto>  result = basicMixedWS.getSiteSelfDBySiteCode(sitecode);
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
        List<Assort> list= basicSecondaryWSProxy.getBaseAssortList();
        if(null!=list&&list.size()>0){
            for (Assort a:list){
                if(a.getAssId().equals(assortId))
                    return a;
            }
        }
        return null;
    }

	@Override
	@Cache(memoryEnable = false,key = "getOneAssortById@args0")
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.basicSecondaryWS.getAssortById",mState={JProEnum.TP,JProEnum.FunctionError})
	public Assort getOneAssortById(int assId) {
		return basicSecondaryWS.getAssortById(assId);
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

	@Override
	public BaseStaffSiteOrgDto queryDmsBaseSiteByCode(String siteCode) {
		// TODO Auto-generated method stub
		if(NumberHelper.isNumber(siteCode))
			return baseMajorManager.getBaseSiteBySiteId(Integer.parseInt(siteCode));
		else
			return baseMajorManager.getBaseSiteByDmsCode(siteCode);
	}


	@Override
	@Cache(key = "baseMajorManagerImpl.getDmsShortNameByCode@args0", memoryEnable = false,
			redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMajorManagerImpl.getDmsShortNameByCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public String getDmsShortNameByCode(Integer dmsCode){
		BaseStaffSiteOrgDto dto = queryDmsBaseSiteByCode(dmsCode+"");
		if(dto == null){
			return null;
		}

		//读取站点名称
		String siteName = dto.getSiteName();

		if (StringHelper.isEmpty(siteName)){
			return "";
		}
		//截取分拣中心、分拨中心、中转场
		return siteName.replace(Constants.SUFFIX_DMS_ONE,"")
				.replace(Constants.SUFFIX_DMS_TWO,"")
				.replace(Constants.SUFFIX_TRANSIT,"");
	}

	/**
	 * 获取运单大小站逻辑
	 * <p>
	 * 返回所属自营站点
	 *
	 * @param smallSiteCode
	 * @return
	 */
	@Override
	public Integer getMappingSite(Integer smallSiteCode) {
		if (null == smallSiteCode)return null;
		//自提柜所属站点
		Integer resultSiteCode = baseMajorManager.getPartnerSiteBySiteId(smallSiteCode);
		if (-1 != resultSiteCode){
			return resultSiteCode;
		}
		return null;
	}


	@Override
	protected ErpValidateService setOwnErpValidateService() {
		return this;
	}

	@Override
	protected ErpValidateService setThirdErpValidateService() {
		return thirdValidateService;
	}

	@Override
	protected LoginClientService selectLoginClient() {
		return this;
	}

    /**
     * 安卓根据菜单编码获取菜单可用性结果
     * @param menuUsageConfigRequestDto 请求参数
     * @return 菜单可用性结果
     * @author fanggang7
     * @time 2022-04-11 16:47:33 周一
     */
    @Override
    public MenuUsageProcessDto getClientMenuUsageConfig(MenuUsageConfigRequestDto menuUsageConfigRequestDto) {
        log.info("BaseServiceImpl.getClientMenuUsageConfig param {}", JsonHelper.toJson(menuUsageConfigRequestDto));

        MenuUsageProcessDto menuUsageProcessDto = null;
        final CurrentOperate currentOperate = menuUsageConfigRequestDto.getCurrentOperate();
        final int siteCode = currentOperate.getSiteCode();
        final BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
        if(siteInfo == null){
            return null;
        }

        final MenuUsageProcessDto clientMenuUsageByCodeConfig = getClientMenuUsageByCodeConfig(menuUsageConfigRequestDto);
        if (clientMenuUsageByCodeConfig != null) {
            return clientMenuUsageByCodeConfig;
        }

        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_ANDROID_MENU_USAGE + menuUsageConfigRequestDto.getMenuCode());
        if (sysConfig == null) {
            return null;
        }

        final MenuUsageConfigDto menuUsageConfigDto = JSON.parseObject(sysConfig.getConfigContent(), MenuUsageConfigDto.class);
        if (menuUsageConfigDto == null) {
            return null;
        }
        final MenuUsageConditionConfigDto conditionConfig = menuUsageConfigDto.getCondition();
        if (conditionConfig == null) {
            return menuUsageConfigDto.getProcess();
        }

        if(CollectionUtils.isEmpty(conditionConfig.getSiteType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSubType())
                && CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType())){
            return menuUsageConfigDto.getProcess();
        }
        if((CollectionUtils.isEmpty(conditionConfig.getSiteType()) || (siteInfo.getSiteType() != null && conditionConfig.getSiteType().contains(siteInfo.getSiteType())))
                && (CollectionUtils.isEmpty(conditionConfig.getSiteSubType()) || (siteInfo.getSubType() != null && conditionConfig.getSiteSubType().contains(siteInfo.getSubType())))
                && (CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) || (siteInfo.getSortType() != null && conditionConfig.getSiteSortType().contains(siteInfo.getSortType())))
                && (CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) || (siteInfo.getSortSubType() != null && conditionConfig.getSiteSortSubType().contains(siteInfo.getSortSubType())))
                && (CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType()) || (siteInfo.getSortThirdType() != null && conditionConfig.getSiteSortThirdType().contains(siteInfo.getSortThirdType())))){
            return menuUsageConfigDto.getProcess();
        }

        return menuUsageProcessDto;

    }

    @Override
	public ImmutablePair<Boolean, String> checkMenuIsAvailable(String menuCode, Integer siteCode) {
		final MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
		menuUsageConfigRequestDto.setMenuCode(menuCode);
		final CurrentOperate currentOperate = new CurrentOperate();
		currentOperate.setSiteCode(siteCode);
		menuUsageConfigRequestDto.setCurrentOperate(currentOperate);
		final MenuUsageProcessDto clientMenuUsageConfig = getClientMenuUsageConfig(menuUsageConfigRequestDto);
		return ImmutablePair.of(clientMenuUsageConfig == null || Objects.equals(clientMenuUsageConfig.getCanUse(), Constants.YN_YES),
				clientMenuUsageConfig == null ? null : clientMenuUsageConfig.getMsg());
	}

	/**
     * 获取指定具体场地或区域的配置
     */
    public MenuUsageProcessDto getClientMenuUsageByCodeConfig(MenuUsageConfigRequestDto menuUsageConfigRequestDto) {
        final SysConfig sysConfigByCode = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_ANDROID_MENU_USAGE_BY_SITE_CODE + menuUsageConfigRequestDto.getMenuCode());
        // 如果配置都为空，则直接返回空
        if (sysConfigByCode == null) {
            return null;
        }

        final MenuUsageConfigDto menuUsageConfigDtoByCode = JSON.parseObject(sysConfigByCode.getConfigContent(), MenuUsageConfigDto.class);
        if (menuUsageConfigDtoByCode == null) {
            return null;
        }

        final CurrentOperate currentOperate = menuUsageConfigRequestDto.getCurrentOperate();

        final int siteCode = currentOperate.getSiteCode();

        final MenuUsageConditionConfigDto conditionConfig = menuUsageConfigDtoByCode.getCondition();
        if (conditionConfig == null) {
            return menuUsageConfigDtoByCode.getProcess();
        }

        if(CollectionUtils.isEmpty(conditionConfig.getSiteCodes()) || conditionConfig.getSiteCodes().contains(siteCode)){
            return menuUsageConfigDtoByCode.getProcess();
        }

        return null;
    }

    /**
     * 获取全局功能管控配置
     *
     * @param funcUsageConfigRequestDto 请求参数
     * @return 功能可用性结果
     * @author fanggang7
     * @time 2023-03-22 19:59:20 周三
     */
    @Override
    public GlobalFuncUsageControlDto getGlobalFuncUsageControlConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        log.info("BaseServiceImpl.getFuncUsageConfig param {}", JsonHelper.toJson(funcUsageConfigRequestDto));
        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_GLOBAL_FUNC_USAGE_CONTROL + funcUsageConfigRequestDto.getSystemCode());
        // 如果配置都为空，则直接返回空
        if (sysConfig == null) {
            return null;
        }

        GlobalFuncUsageControlDto globalFuncUsageControlDto = new GlobalFuncUsageControlDto();
        final List<String> funcCodes = JSON.parseArray(sysConfig.getConfigContent(), String.class);
        globalFuncUsageControlDto.setFuncCodes(funcCodes);

        return globalFuncUsageControlDto;
    }

    /**
     * 根据功能编码获取功能可用性配置结果
     *
     * @param funcUsageConfigRequestDto 请求参数
     * @return 菜单可用性结果
     * @author fanggang7
     * @time 2022-04-11 16:47:33 周一
     */
    @Override
    public FuncUsageProcessDto getFuncUsageConfig(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {
        log.info("BaseServiceImpl.getFuncUsageConfig param {}", JsonHelper.toJson(funcUsageConfigRequestDto));

        FuncUsageProcessDto funcUsageProcessDto = null;

        final OperateUser operateUser = funcUsageConfigRequestDto.getOperateUser();
        final int siteCode = operateUser.getSiteCode();

        final BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
        if(siteInfo == null){
            return null;
        }

        final FuncUsageProcessDto clientMenuUsageByCodeConfig = getFuncUsageByCodeConfig4SpecificList(funcUsageConfigRequestDto);
        if (clientMenuUsageByCodeConfig != null) {
            return clientMenuUsageByCodeConfig;
        }

        final FuncUsageProcessDto clientMenuUsageByCodeConfigBySiteType = getFuncUsageByCodeConfig4SiteType(funcUsageConfigRequestDto, siteInfo);
        if (clientMenuUsageByCodeConfigBySiteType != null) {
            return clientMenuUsageByCodeConfigBySiteType;
        }


        return funcUsageProcessDto;
    }

    /**
     * 获取指定具体场地或区域的配置
     */
    public FuncUsageProcessDto getFuncUsageByCodeConfig4SpecificList(FuncUsageConfigRequestDto funcUsageConfigRequestDto) {

        final SysConfig sysConfigByCode = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE_BY_SITE_CODE + funcUsageConfigRequestDto.getFuncCode());
        // 如果配置都为空，则直接返回空
        if (sysConfigByCode == null) {
            return null;
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfigByCode.getConfigContent(), FuncUsageConfigDto.class);
        if (CollectionUtils.isEmpty(funcUsageConfigDtos)) {
            return null;
        }
        final OperateUser operateUser = funcUsageConfigRequestDto.getOperateUser();
        final int siteCode = operateUser.getSiteCode();

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                return funcUsageConfigDto.getProcess();
            }

            if((CollectionUtils.isEmpty(conditionConfig.getSiteCodes()) || conditionConfig.getSiteCodes().contains(siteCode)
            && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis))){
                return funcUsageConfigDto.getProcess();
            }
        }


        return null;
    }

    /**
     * 获取指定具体场地类型的配置
     */
    public FuncUsageProcessDto getFuncUsageByCodeConfig4SiteType(FuncUsageConfigRequestDto funcUsageConfigRequestDto, BaseSiteInfoDto siteInfo) {
        final SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(Constants.SYS_CONFIG_FUNC_USAGE + funcUsageConfigRequestDto.getFuncCode());
        if (sysConfig == null) {
            return null;
        }

        final List<FuncUsageConfigDto> funcUsageConfigDtos = JSON.parseArray(sysConfig.getConfigContent(), FuncUsageConfigDto.class);

        final long currentTimeMillis = System.currentTimeMillis();
        for (FuncUsageConfigDto funcUsageConfigDto : funcUsageConfigDtos) {
            final FuncUsageConditionConfigDto conditionConfig = funcUsageConfigDto.getCondition();
            if (conditionConfig == null) {
                return funcUsageConfigDto.getProcess();
            }

            if(CollectionUtils.isEmpty(conditionConfig.getSiteType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSubType())
                    && CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) && CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType())
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)){
                return funcUsageConfigDto.getProcess();
            }
            if((CollectionUtils.isEmpty(conditionConfig.getSiteType()) || (siteInfo.getSiteType() != null && conditionConfig.getSiteType().contains(siteInfo.getSiteType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSubType()) || (siteInfo.getSubType() != null && conditionConfig.getSiteSubType().contains(siteInfo.getSubType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortType()) || (siteInfo.getSortType() != null && conditionConfig.getSiteSortType().contains(siteInfo.getSortType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortSubType()) || (siteInfo.getSortSubType() != null && conditionConfig.getSiteSortSubType().contains(siteInfo.getSortSubType())))
                    && (CollectionUtils.isEmpty(conditionConfig.getSiteSortThirdType()) || (siteInfo.getSortThirdType() != null && conditionConfig.getSiteSortThirdType().contains(siteInfo.getSortThirdType())))
                    && this.checkEffectiveTimeIsEffective(funcUsageConfigDto, currentTimeMillis)){
                return funcUsageConfigDto.getProcess();
            }
        }
        return null;
    }

    /**
     * 比较生效时间
     * @param funcUsageConfigDto 配置
     * @param compareTimeMillis 比较时间
     * @return 是否生效
     */
    private boolean checkEffectiveTimeIsEffective(FuncUsageConfigDto funcUsageConfigDto, long compareTimeMillis){
        if (funcUsageConfigDto.getEffectiveTime() == null) {
            return true;
        }
        // 计算生效时间
        if(funcUsageConfigDto.getEffectiveTimeFormatStr() == null){
            funcUsageConfigDto.setEffectiveTimeFormatStr(DateUtil.FORMAT_DATE_TIME);
        }
        final Date effectiveDate = DateUtil.parse(funcUsageConfigDto.getEffectiveTime(), funcUsageConfigDto.getEffectiveTimeFormatStr());
        if(effectiveDate.getTime() < compareTimeMillis){
            return true;
        }
        return false;
    }

    /**
     * 场地列表
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-11 14:59:04 周二
     */
    @Override
    public Result<Pager<StreamlinedBasicSite>> selectSiteList(Pager<StreamlinedBasicSiteQuery> request) {
        log.info("BaseServiceImpl.selectSiteList param {}", JsonHelper.toJson(request));
        Result<Pager<StreamlinedBasicSite>> result = Result.success();
        Pager<StreamlinedBasicSite> pagerData = new Pager<>(request.getPageNo(), request.getPageSize(), 0L);
        result.setData(pagerData);

        try {
            final com.jd.ql.dms.report.domain.BaseEntity<Pager<StreamlinedBasicSite>> queryPageResult = baseMajorManager.querySiteByConditionFromStreamlinedSite(request);
            if(!queryPageResult.isSuccess()){
                log.warn("BaseMajorManager.querySiteByConditionFromStreamlinedSite error {}", JsonHelper.toJson(queryPageResult));
                return result.toFail("查询站点异常");
            }
            final Pager<StreamlinedBasicSite> queryPagerData = queryPageResult.getData();
            pagerData.setData(queryPagerData.getData());
            pagerData.setPageNo(queryPagerData.getPageNo());
            pagerData.setPageSize(queryPagerData.getPageSize());
            pagerData.setTotal(queryPagerData.getTotal());
        } catch (Exception e) {
            log.error("BaseServiceImpl.selectSiteList error ", e);
            return result.toFail("接口异常");
        }
        return result;
    }
}
