package com.jd.bluedragon.distribution.sealVehicle.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.TextConstants;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleSourceEnum;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelationCondition;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteCondition;
import com.jd.bluedragon.distribution.newseal.service.DmsSendRelationService;
import com.jd.bluedragon.distribution.newseal.service.PreSealBatchService;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.DmsSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.domain.*;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("dmsSealVehicleService")
public class DmsSealVehicleServiceImpl implements DmsSealVehicleService {

    private static final Logger log = LoggerFactory.getLogger(DmsSealVehicleServiceImpl.class);

    @Autowired
    private PreSealVehicleService preSealVehicleService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;
    
    @Autowired
    private DmsSendRelationService dmsSendRelationService;
    
    @Autowired
    private TmsVehicleRouteService tmsVehicleRouteService;
    @Autowired
    private PreSealBatchService preSealBatchService;
    
    /**
     * 发货关系数据有效天数，默认7天
     */
    @Value("${beans.DmsSealVehicleServiceImpl.effectDays:7}")
    private Integer effectDays;
    /**
     * 默认-查询发车时间6小时内
     */
    private static final Integer DEFAULT_RECENT_HOURS = 6;
    /**
     * 传摆线路类型列表
     */
    private static List<Integer> SITE_LINE_TYPES = new ArrayList<Integer>();
    
    static{
    	SITE_LINE_TYPES.add(30);
    	SITE_LINE_TYPES.add(31);
    	SITE_LINE_TYPES.add(40);
    }
    /*
     * 获取未封车信息列表
     *
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getUnSealVehicleInfo", mState = JProEnum.TP)
    public JdResponse<List<UnSealVehicleInfo>> getUnSealVehicleInfo(Integer createSiteCode, Integer hourRange, String createUserErp) {
    	QuickSealQueryRequest queryRequest = new QuickSealQueryRequest();
    	queryRequest.setCreateSiteCode(createSiteCode);
    	queryRequest.setCreateUserErp(createUserErp);
    	queryRequest.setHourRange(hourRange);
    	return this.getUnSealVehicleInfos(queryRequest);
    }
    /*
     * 获取未封车信息列表
     *
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getUnSealVehicleInfoNew", mState = JProEnum.TP)
	public JdResponse<List<UnSealVehicleInfo>> getUnSealVehicleInfos(QuickSealQueryRequest queryRequest) {
    	JdResponse<List<UnSealVehicleInfo>> jdResponse = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
    	Integer createSiteCode = queryRequest.getCreateSiteCode();
        try {
            //获取该场地所有预封车信息
            PreSealVehicle param = new PreSealVehicle();
            param.setCreateSiteCode(createSiteCode);
            if (StringUtils.isNotBlank(queryRequest.getCreateUserErp())) {
                param.setCreateUserErp(queryRequest.getCreateUserErp());
            }
            if (StringUtils.isNotBlank(queryRequest.getVehicleNumber())) {
                param.setVehicleNumber(queryRequest.getVehicleNumber());
            }
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.queryByParam(param);
            Map<String, UnSealVehicleInfo> unSealVehicleInfoMap = new HashMap<>();
            /**
             * 存放uuid
             */
            Map<String, List<String>> unSealVehicleUuidMap = new HashMap<>();
            if (preSealVehicleList != null) {
                //遍历所有预封车记录
                for (PreSealVehicle preSealVehicle : preSealVehicleList) {
                    //运力编码维度记录信息
                    String transportCode = preSealVehicle.getTransportCode();
                    if (unSealVehicleInfoMap.containsKey(transportCode)) {
                        UnSealVehicleInfo unSealVehicleInfo = unSealVehicleInfoMap.get(transportCode);
                        //存在多条运力编码信息，说明该运力下有多个车牌，存在多个车牌时设置未就绪
                        unSealVehicleInfo.setReady(false);
                        unSealVehicleUuidMap.get(transportCode).add(preSealVehicle.getPreSealUuid());
                    } else {
                        //不存在运力编码，进行信息初始化
                        UnSealVehicleInfo unSealVehicleInfo = this.convert2UnSealVehicleInfo(preSealVehicle);
                        List<String> uuids = new ArrayList<String>();
                        uuids.add(preSealVehicle.getPreSealUuid());
                        //设置批次数量
                        int sendCodeCount = this.getUnSealSendCodeCount(createSiteCode, preSealVehicle.getReceiveSiteCode(), queryRequest.getHourRange());
                        unSealVehicleInfo.setSendCodeCount(sendCodeCount);
                        
                        if (sendCodeCount > 0) {
                            //判断是否需要进行体积判断
                            if (this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay())) {
                                unSealVehicleInfo.setReady(NumberHelper.gt0(preSealVehicle.getVolume()));
                            } else {
                                unSealVehicleInfo.setReady(true);
                            }
                        } else {
                            unSealVehicleInfo.setReady(false);
                        }
                        unSealVehicleUuidMap.put(transportCode, uuids);
                        unSealVehicleInfoMap.put(transportCode, unSealVehicleInfo);
                    }
                }
            }
            /**
             * 设置选中的批次信息
             */
            for(String transCode : unSealVehicleInfoMap.keySet()) {
            	List<String> selectedSendCodes= this.preSealBatchService.querySendCodesByUuids(unSealVehicleUuidMap.get(transCode));
            	unSealVehicleInfoMap.get(transCode).setSelectedSendCodes(selectedSendCodes);
            	unSealVehicleInfoMap.get(transCode).setSelectedSendCodeCount(selectedSendCodes.size());
            }
            jdResponse.setData(new ArrayList<>(unSealVehicleInfoMap.values()));
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("获取未封车信息列表失败，操作单位：{}", createSiteCode, e);
        }
        return jdResponse;
	}
    /*
     * 获取未封车信息明细
     *
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getUnSealSendCodeDetail", mState = JProEnum.TP)
    public JdResponse<UnSealVehicleDetail> getUnSealSendCodeDetail(Integer createSiteCode, String transportCode, Integer hourRange) {
        JdResponse<UnSealVehicleDetail> jdResponse = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        try {
            //按运力编码获取预封车记录
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode);

            if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                jdResponse.setCode(JdResponse.CODE_FAIL);
                jdResponse.setMessage("运力编码【" + transportCode + "】没有预封车信息，无法获取一键封车明细！");
                return jdResponse;
            }

            Integer receiveSiteCode = preSealVehicleList.get(0).getReceiveSiteCode();
            List<String> unSealSendCodeList = newSealVehicleService.getUnSealSendCodeList(createSiteCode, receiveSiteCode, hourRange);

            if (unSealSendCodeList == null || unSealSendCodeList.isEmpty()) {
                jdResponse.setCode(JdResponse.CODE_FAIL);
                jdResponse.setMessage("运力编码【" + transportCode + "】没有需要封车的批次信息！");
                return jdResponse;
            }
            boolean isAllReady = false;
            String vehicleNumber = null;
            String sealCode = null;
            //如果只有一个预封车记录，说明只有一个车牌，进一步判断体积是否大于0
            if (preSealVehicleList.size() == 1) {
                PreSealVehicle preSealVehicle = preSealVehicleList.get(0);
                //进行体积判断
                if (! this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay()) || NumberHelper.gt0(preSealVehicle.getVolume())) {
                    vehicleNumber = preSealVehicle.getVehicleNumber();
                    sealCode = preSealVehicle.getSealCodes();
                    isAllReady = true;
                }
            }
            //PDA未选择批次时，默认全选
            boolean selectAllSendCodes = false;
            //查询满足预封车条件下PDA已选择的所有的批次号
            List<String> selectedSendCodes = querySelectedSendCodes(preSealVehicleList);
            if(CollectionUtils.isEmpty(selectedSendCodes)) {
            	selectAllSendCodes = true;
            }
            UnSealVehicleDetail unSealVehicleDetail = new UnSealVehicleDetail();
            unSealVehicleDetail.setCreateSiteCode(createSiteCode);
            unSealVehicleDetail.setTransportCode(transportCode);
            List<SealVehicleSendCodeInfo> sealVehicleSendCodeInfoList = new ArrayList<>();
            //遍历批次号，组装批次明细
            for (String unSealSendCode : unSealSendCodeList) {
                SealVehicleSendCodeInfo sealVehicleSendCodeInfo = new SealVehicleSendCodeInfo();
                sealVehicleSendCodeInfo.setSendCode(unSealSendCode);
                sealVehicleSendCodeInfo.setReady(isAllReady);
                sealVehicleSendCodeInfo.setVehicleNumber(vehicleNumber);
                sealVehicleSendCodeInfo.setSealCode(sealCode);
                //设置选中状态
                if(isAllReady && (selectAllSendCodes || selectedSendCodes.contains(unSealSendCode))) {
                	sealVehicleSendCodeInfo.setSelectedFlag(Boolean.TRUE);
                }else {
                	sealVehicleSendCodeInfo.setSelectedFlag(Boolean.FALSE);
                }
                sealVehicleSendCodeInfoList.add(sealVehicleSendCodeInfo);
            }
            unSealVehicleDetail.setSendCodeInfoList(sealVehicleSendCodeInfoList);
            jdResponse.setData(unSealVehicleDetail);
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("获取未封车批次信息失败，运力编码：{}", transportCode, e);
        }

        return jdResponse;
    }

    /*
     * 修改运力编码中的车辆基础信息
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getVehicleBaseInfoList", mState = JProEnum.TP)
    public JdResponse<List<VehicleBaseInfo>> getVehicleBaseInfoList(String transportCode) {

        JdResponse<List<VehicleBaseInfo>> jdResponse = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        try {
            //按运力编码获取预封车记录
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode);

            if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                jdResponse.setCode(JdResponse.CODE_FAIL);
                jdResponse.setMessage("运力编码【" + transportCode + "】没有预封车信息，无法车牌相关信息！");
                return jdResponse;
            }

            List<VehicleBaseInfo> vehicleNumberInfoList = new ArrayList<>();
            //遍历预封车记录，组装车牌信息列表并判断车牌是够处于就绪状态
            for (PreSealVehicle preSealVehicle : preSealVehicleList) {
                if (StringHelper.isNotEmpty(preSealVehicle.getVehicleNumber())) {
                    VehicleBaseInfo vehicleBaseInfo = new VehicleBaseInfo();
                    vehicleBaseInfo.setVehicleNumber(preSealVehicle.getVehicleNumber());
                    vehicleBaseInfo.setSealCode(preSealVehicle.getSealCodes());
                    vehicleBaseInfo.setVolume(preSealVehicle.getVolume());
                    vehicleBaseInfo.setWeight(preSealVehicle.getWeight());
                    //预封车来源为传摆，且运力类型为零担，需要判断体积是否大于0，大于0则就绪
                    if (this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay())) {
                        vehicleBaseInfo.setReady(NumberHelper.gt0(preSealVehicle.getVolume()));
                    } else {
                        vehicleBaseInfo.setReady(true);
                    }
                    vehicleNumberInfoList.add(vehicleBaseInfo);
                } else {
                    log.warn("预封车信息中车牌号为空：{}", JsonHelper.toJson(preSealVehicle));
                }
            }

            jdResponse.setData(vehicleNumberInfoList);
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("获取车牌信息失败！，运力编码：{}", transportCode, e);
    }
        return jdResponse;
    }

    /*
     * 校验数据提交
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.modifyVehicleBaseInfo", mState = JProEnum.TP)
    public JdResponse modifyVehicleBaseInfo(String transportCode, VehicleBaseInfo vehicleBaseInfo) {
        JdResponse jdResponse = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if (transportCode == null || vehicleBaseInfo == null || vehicleBaseInfo.getVehicleNumber() == null) {
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("运力编码和车牌号不能为空！");
            return jdResponse;
        }

        if (vehicleBaseInfo.getVolume() == null || ! NumberHelper.gt0(vehicleBaseInfo.getVolume())) {
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("体积不能为空且必须大于0！");
            return jdResponse;
        }
        try {
            PreSealVehicle preSealVehicle = new PreSealVehicle();
            preSealVehicle.setTransportCode(transportCode);
            preSealVehicle.setVehicleNumber(vehicleBaseInfo.getVehicleNumber());
            preSealVehicle.setVolume(vehicleBaseInfo.getVolume());
            if (NumberHelper.gt0(vehicleBaseInfo.getWeight())) {
                preSealVehicle.setWeight(vehicleBaseInfo.getWeight());
            }
            preSealVehicleService.updatePreSealVehicleMeasureInfo(preSealVehicle);

        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("更新预封车重量体积信息失败，运力编码：{}，车牌信息：{}", transportCode, JsonHelper.toJson(vehicleBaseInfo), e);
        }
        return jdResponse;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.checkTransportVehicleSubmit", mState = JProEnum.TP)
    public JdResponse checkTransportVehicleSubmit(String transportCode, List<String> vehicleNumberList, Boolean hasBatchInfo) {
        JdResponse jdResponse = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        List<String> vehicleNumberListTemp = new ArrayList<>();

        if (StringHelper.isEmpty(transportCode)) {
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("运力编码不能为空！");
            return jdResponse;
        }
        try {
            //如果没有车牌信息
            if (hasBatchInfo) {
                if (vehicleNumberList != null && ! vehicleNumberList.isEmpty()) {
                    vehicleNumberListTemp.addAll(vehicleNumberList);
                } else {
                    jdResponse.setCode(JdResponse.CODE_FAIL);
                    jdResponse.setMessage("由于运力编码【"+ transportCode +"】没有车牌信息，请录入车牌后提交！");
                    return jdResponse;
                }
            } else {
                //根据运力编码查询预封车信息
                List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode);
                if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                    jdResponse.setCode(JdResponse.CODE_FAIL);
                    jdResponse.setMessage("由于运力编码【"+ transportCode +"】没有车牌信息，请录入车牌后提交！");
                    return jdResponse;
                }
                if (preSealVehicleList.size() > 1) {
                    jdResponse.setCode(JdResponse.CODE_FAIL);
                    jdResponse.setMessage("由于运力编码【"+ transportCode +"】有多个车牌信息，请选择后提交！");
                    return jdResponse;
                }

                vehicleNumberListTemp.add(preSealVehicleList.get(0).getVehicleNumber());
            }

            for (String vehicleNumber : vehicleNumberListTemp) {
                List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode, vehicleNumber);
                if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                    jdResponse.setCode(JdResponse.CODE_FAIL);
                    jdResponse.setMessage("不存在车牌号【" + vehicleNumber + "】的预封车信息！");
                    return jdResponse;
                } else {
                    PreSealVehicle preSealVehicle = preSealVehicleList.get(0);

                    //预封车来源为传摆，且运力类型为零担，需要判断体积是否大于0
                    if (this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay())) {
                        //不大于0，返回有误
                        if (! NumberHelper.gt0(preSealVehicle.getVolume())) {
                            jdResponse.setCode(JdResponse.CODE_FAIL);
                            jdResponse.setMessage("由于运力编码【"+ transportCode +"】为【" + preSealVehicle.getTransWayName()+"】，车牌号【" + vehicleNumber + "】的体积不能为0，请重新录入！");
                        }
                    }
                }

            }
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("校验提交运力车牌信息失败，运力编码：{}，车牌信息：{}", transportCode, JsonHelper.toJson(vehicleNumberList), e);
        }
        return jdResponse;
    }

    /**
     * 查询全部的未封车批次号数量
     */
    private Integer getUnSealSendCodeCount(Integer createSiteCode, Integer receiveSiteCode, Integer hourRange) {
        List<String> result = newSealVehicleService.getUnSealSendCodeList(createSiteCode, receiveSiteCode, hourRange);
        return result == null ? 0 : result.size();
    }

    /**
     * 查询全部的未封车批次号
     */
    private boolean isNeedCheckVolume(Integer preSealSource, Integer transWay) {
        return preSealSource == PreSealVehicleSourceEnum.FERRY_PRE_SEAL.getCode() && transWay == TransTypeEnum.ROAD_LINGDAN.getType();
    }

    /*
    * 对象转换
    * */
    private UnSealVehicleInfo convert2UnSealVehicleInfo(PreSealVehicle preSealVehicle) {
        UnSealVehicleInfo unSealVehicleInfo = new UnSealVehicleInfo();
        unSealVehicleInfo.setTransportCode(preSealVehicle.getTransportCode());
        unSealVehicleInfo.setCreateSiteCode(preSealVehicle.getCreateSiteCode());
        unSealVehicleInfo.setCreateSiteName(preSealVehicle.getCreateSiteName());
        unSealVehicleInfo.setReceiveSiteCode(preSealVehicle.getReceiveSiteCode());
        unSealVehicleInfo.setReceiveSiteName(preSealVehicle.getReceiveSiteName());
        unSealVehicleInfo.setTransWay(preSealVehicle.getTransWay());
        unSealVehicleInfo.setTransWayName(preSealVehicle.getTransWayName());
        unSealVehicleInfo.setSendCarTime(preSealVehicle.getSendCarTime());
        unSealVehicleInfo.setPreSealSource(preSealVehicle.getPreSealSource());
        unSealVehicleInfo.setSource(preSealVehicle.getSource());
        unSealVehicleInfo.setCreateUserErp(preSealVehicle.getCreateUserErp());
        return unSealVehicleInfo;
    }

	@Override
	public JdResponse<List<PassPreSealRecord>> queryPassPreSealData(PassPreSealQueryRequest queryCondition) {
		JdResponse<List<PassPreSealRecord>> result = new JdResponse<List<PassPreSealRecord>>();
		result.toSucceed();
		
		//根据始发、滑道号、目的查询流向信息
		DmsSendRelationCondition dmsSendRelationCondition = new DmsSendRelationCondition();
		dmsSendRelationCondition.setOriginalSiteCode(queryCondition.getOriginalSiteCode());
		dmsSendRelationCondition.setDestinationCrossCode(queryCondition.getDestinationCrossCode());
		//数字按站点编码查询，否则按名称处理
		String destinationSiteCodeOrName = queryCondition.getDestinationSiteCodeOrName();
		if(NumberHelper.isNumber(destinationSiteCodeOrName)) {
			dmsSendRelationCondition.setDestinationSiteCode(NumberHelper.convertToInteger(destinationSiteCodeOrName));
		}else {
			dmsSendRelationCondition.setDestinationSiteName(destinationSiteCodeOrName);
		}
		dmsSendRelationCondition.setLimitNum(queryCondition.getLimitNum());
		dmsSendRelationCondition.setLineTypes(SITE_LINE_TYPES);
		//查询7天内有发货的线路
		dmsSendRelationCondition.setStartTime(DateHelper.addDate(new Date(), -effectDays));
		List<DmsSendRelation> sendList = dmsSendRelationService.queryByCondition(dmsSendRelationCondition);
		if(CollectionUtils.isNotEmpty(sendList)) {
			List<PassPreSealRecord> dataList = new ArrayList<PassPreSealRecord>();
			for(DmsSendRelation sendRelation : sendList) {
				loadCarAndPreSealInfo(sendRelation,queryCondition,dataList);
			}
			result.setData(dataList);
		}
		return result;
		
	}
	/**
	 * 加载发车及预封车信息
	 * @param sendRelation
	 * @param requestCondition
	 * @return
	 */
	private void loadCarAndPreSealInfo(DmsSendRelation sendRelation,PassPreSealQueryRequest requestCondition,List<PassPreSealRecord> resultList) {
		//筛选满足始发、目的、车牌、发车时间的数据
		TmsVehicleRouteCondition routeCondition = new TmsVehicleRouteCondition();
		routeCondition.setVehicleNumber(routeCondition.getVehicleNumber());
		//时间格式yyyy-mm-dd hh:mm:ss
		Integer recentHours = requestCondition.getRecentHours();
		if(!NumberHelper.gt0(recentHours)) {
			recentHours = DEFAULT_RECENT_HOURS;
		}
		Date now = new Date();
		routeCondition.setDepartStartTime(DateHelper.add(now, Calendar.HOUR_OF_DAY, -recentHours));
		routeCondition.setDepartEndTime(DateHelper.add(now, Calendar.HOUR_OF_DAY, recentHours));
		
		routeCondition.setOriginalSiteCode(sendRelation.getOriginalSiteCode());
		routeCondition.setDestinationSiteCode(sendRelation.getDestinationSiteCode());
		List<TmsVehicleRoute> routeList = tmsVehicleRouteService.queryByCondition(routeCondition);
		if(CollectionUtils.isNotEmpty(routeList)) {
			for(TmsVehicleRoute route:routeList) {
				resultList.add(initAndLoadPreSealInfo(sendRelation,route));
			}
		}else {
			resultList.add(initAndLoadPreSealInfo(sendRelation,null));
		}
	}
	/**
	 * 初始化看板信息，加载预封车状态
	 * @param sendRelation
	 * @param routeInfo
	 * @return
	 */
	private PassPreSealRecord initAndLoadPreSealInfo(DmsSendRelation sendRelation,TmsVehicleRoute routeInfo) {
		PassPreSealRecord record = new PassPreSealRecord();
		record.setOriginalSiteName(sendRelation.getOriginalSiteName());
		record.setDestinationSiteName(sendRelation.getDestinationSiteName());
		record.setDestinationCrossCode(sendRelation.getDestinationCrossCode());
		record.setPreSealStatus(TextConstants.TEXT_FLAG_NO);
		if(routeInfo != null) {
			//存在车次信息，按运力编码+车牌查询预封车信息
			record.setCarrierTeamName(routeInfo.getCarrierTeamName());
			record.setJobCreateTime(routeInfo.getJobCreateTime());
			record.setVehicleNumber(routeInfo.getVehicleNumber());
			record.setDepartTime(routeInfo.getDepartTime());
			Integer preNum = preSealVehicleService.countPreSealNumByTransportInfo(routeInfo.getTransportCode(), routeInfo.getVehicleNumber());
			if(NumberHelper.gt0(preNum)) {
				record.setPreSealStatus(TextConstants.TEXT_FLAG_YES);
			}
		}else {
			//不存在车次信息，按始发和目的查询预封车信息
			Integer preNum = preSealVehicleService.countPreSealNumBySendRelation(sendRelation.getOriginalSiteCode(), sendRelation.getDestinationSiteCode());
			if(NumberHelper.gt0(preNum)) {
				record.setPreSealStatus(TextConstants.TEXT_FLAG_YES);
			}
		}
		return record;
	}
	/**
	 * 根据预封车列表，查询预封车的批次信息
	 * @param preSealVehicleList
	 * @return
	 */
	private List<String> querySelectedSendCodes(List<PreSealVehicle> preSealVehicleList){
		if(CollectionUtils.isEmpty(preSealVehicleList)) {
			return Collections.EMPTY_LIST;
		}
	    //查询满足预封车条件下PDA已选择的所有的批次号
	    List<String> preSealUuids = new ArrayList<String>();
	    for(PreSealVehicle preSealVehicle:preSealVehicleList) {
	    	preSealUuids.add(preSealVehicle.getPreSealUuid());
	    }
	    return preSealBatchService.querySendCodesByUuids(preSealUuids);
	}
}
