package com.jd.bluedragon.distribution.newseal.controller;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleCondition;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.domain.VehicleMeasureInfo;
import com.jd.bluedragon.distribution.newseal.service.PreSealBatchService;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 *
 * @ClassName: PreSealVehicleController
 * @Description: 预封车数据表--Controller实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Controller
@RequestMapping("newseal/preSealVehicle")
public class PreSealVehicleController extends DmsBaseController{

	private static final Logger log = LoggerFactory.getLogger(PreSealVehicleController.class);

	@Autowired
	private PreSealVehicleService preSealVehicleService;

	@Autowired
	private SealVehiclesService sealVehiclesService;

    @Autowired
    private SendMService sendMService;

	@Autowired
	private SiteService siteService;

    @Autowired
    @Qualifier("newSealVehicleService")
    private NewSealVehicleService newSealVehicleService;
    
	@Autowired
	private PreSealBatchService preSealBatchService;
	
    private static final Integer SEAL_LIMIT = 5;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

	/**
	 * 返回主页面
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex(Model model) {
        model.addAttribute("quickSealTips", uccPropertyConfiguration.getQuickSealTips());
		return "/newseal/preSealVehicle";
	}
	/**
	 * 跳转到未使用运力页面
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
	@RequestMapping(value = "/unusedtransport")
	public String unusedtransport() {
		return "/newseal/unusedtransport";
	}

	/**
	 * 根据条件查询预封车信息（包含对应未封车的批次信息）
	 * @param condition
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
	@RequestMapping(value = "/queryPreSeals")
    public @ResponseBody JdResponse<List<PreSealVehicle>>  queryPreSeals(@RequestBody PreSealVehicleCondition condition) {
        JdResponse<List<PreSealVehicle>> rest = new JdResponse<List<PreSealVehicle>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if(uccPropertyConfiguration.getOfflineQuickSeal()){
            rest.toFail("一键封车已下线，请使用PDA进行封车！");
            return rest;
        }
        if( condition.getHourRange() == null){
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("参数错误，时间范围不能为空!");
            return rest;
        }
        LoginUser user = getLoginUser();
        if(user == null || StringUtils.isEmpty(user.getUserErp()) || StringUtils.isEmpty(user.getUserName())){
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("登录已过期，请重新登录!");
            return rest;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.PreSealVehicleController.queryPreSeals", Constants.UMP_APP_NAME_DMSWEB,false, true);
        Integer createSiteCode = user.getSiteCode();
        try{
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.queryBySiteCode(createSiteCode);
            // 目的地对应的预封车数据
            Map<Integer, PreSealVehicle> preMap = convertMap(preSealVehicleList);
            if(preMap == null || preMap.isEmpty()){
                rest.setData(new ArrayList<PreSealVehicle>());
                return rest;
            }
            // 目的地对应的已验未发批次
            Map<Integer, Set<String>> sendUnSealMap = getSendUnSealMap(createSiteCode, new ArrayList<Integer>(preMap.keySet()), condition.getHourRange());
            // 构建最终预封车数据
            rest.setData(buildSendUnSealSealVehicles(preMap, sendUnSealMap));
        }catch (Exception e){
            Profiler.functionError(info);
            log.error("查询预封车数据信息失败，查询条件：{}", JsonHelper.toJson(condition), e);
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("服务异常，查询预封车数据信息失败!");
        }finally {
            Profiler.registerInfoEnd(info);
        }
		return rest;
	}

    /**
     * 构建预封车数据
     *  > 将 目的地存在已发未封的批次 添加到 对应的预封车数据中
     *      > 已发未封的批次存在 需要封车（页面选中） 和 无需封车（页面不选中）
     * @param preMap 目的地对应的预封车数据
     * @param sendUnSealMap 目的地对应的已发未封批次
     * @return
     */
    private List<PreSealVehicle> buildSendUnSealSealVehicles(Map<Integer, PreSealVehicle> preMap, Map<Integer, Set<String>> sendUnSealMap) {
        List<PreSealVehicle> finalResult = new ArrayList<>();

        for (Integer preReceiveSiteCode : preMap.keySet()) {

            if(sendUnSealMap.containsKey(preReceiveSiteCode)){

                Set<String> sendUnSealSendCodeList = sendUnSealMap.get(preReceiveSiteCode);
                // 预封车无需封车批次
                PreSealVehicle item = preMap.get(preReceiveSiteCode);
                List<String> selectSendCodeList = preSealBatchService.querySendCodesByUuids(item.getPreSealUuids());
                // 已发未封中无需封车批次
                List<String> selectSendUnSealSendCodeList = CollectionHelper.retainAll(new ArrayList<String>(sendUnSealSendCodeList), selectSendCodeList);
                // 预封车中需封车批次
                List<SealVehicles> needSealList = item.getSendCodes();
                for(String sendCode : sendUnSealSendCodeList){
                    SealVehicles vo = new SealVehicles();
                    vo.setSealDataCode(sendCode);
                    vo.setReceiveSiteCode(preReceiveSiteCode);
                    if(CollectionUtils.isNotEmpty(selectSendUnSealSendCodeList) && !selectSendUnSealSendCodeList.contains(sendCode)) {
                        vo.setSelectedFalg(Boolean.FALSE);
                    }else {
                        vo.setSelectedFalg(Boolean.TRUE);
                    }
                    if(item.getVehicleNumbers().size() == Constants.CONSTANT_NUMBER_ONE){
                        vo.setVehicleNumber(item.getVehicleNumbers().get(Constants.NUMBER_ZERO));
                        vo.setSealCodes(item.getVehicleSealCodeMap().get(vo.getVehicleNumber()));
                    }
                    needSealList.add(vo);
                }
            }
            finalResult.add(preMap.get(preReceiveSiteCode));
        }
        //按发车时间排序
        Collections.sort(finalResult, new Comparator<PreSealVehicle>() {
            @Override
            public int compare(PreSealVehicle dto1, PreSealVehicle dto2) {
                String sendCarTime1 = dto1.getSendCarTime();
                String sendCarTime2 = dto2.getSendCarTime();
                if(StringUtils.isEmpty(sendCarTime1) || StringUtils.isEmpty(sendCarTime2)){
                    return 0;
                }
                String hhmm1 = sendCarTime1.substring(sendCarTime1.length()-5);
                String hhmm2 = sendCarTime2.substring(sendCarTime2.length()-5);
                return hhmm1.compareTo(hhmm2);
            }
        });
        return finalResult;
    }

    /**
     * 分批查询时间跨度小时数
     */
    private int timeRangeOneBatch = 12;

    /**
     * 获取已发未封批次
     *  key：目的地 value：已验未发批次集合
     *  校验两次是否封车：因封车后未成功写入redis
     *      1、查redis
     *      2、查封车表seal_vehicles
     *
     * @param createSiteCode
     * @param receiveSiteCodes
     * @param hourRange
     * @return
     */
    private Map<Integer, Set<String>> getSendUnSealMap(Integer createSiteCode, ArrayList<Integer> receiveSiteCodes, Integer hourRange) {
        List<List<Integer>> batch = Lists.partition(receiveSiteCodes, 50);
        List<SendM> sendMList = new ArrayList<>();
        int timeRangeOneBatchTemp = timeRangeOneBatch;
        if(hourRange < timeRangeOneBatch){
            timeRangeOneBatchTemp = hourRange;
        }
        int timeRangeBatchTotal = hourRange / timeRangeOneBatchTemp;
        if(hourRange % timeRangeOneBatchTemp != 0){
            timeRangeBatchTotal++;
        }
        final Date dateCurrent = new Date();
        for(int i = 1; i <= timeRangeBatchTotal; i++){
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(dateCurrent);
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(dateCurrent);
            calendarEnd.add(Calendar.HOUR_OF_DAY, timeRangeOneBatchTemp * (i - 1) * -1);
            if(i < timeRangeBatchTotal){
                calendarStart.add(Calendar.HOUR_OF_DAY, timeRangeOneBatchTemp * i * -1);
            } else {
                calendarStart.add(Calendar.HOUR_OF_DAY, hourRange * -1);
            }
            Date startDate = calendarStart.getTime();
            Date endDate = calendarEnd.getTime();
            for (List<Integer> singList : batch) {
                sendMList.addAll(sendMService.batchSearchBySiteCodeAndStartTime(createSiteCode, singList, startDate, endDate));
            }
        }
        Set<String> allSendCodeList = new HashSet<>();
        for (SendM sendM : sendMList) {
            allSendCodeList.add(sendM.getSendCode());
        }
        List<String> sealedSendCodeList = sealVehiclesService.findBySealDataCodes(allSendCodeList);
        Map<Integer, Set<String>> sendUnSealMap = new HashMap<>(10);
        for (SendM sendM : sendMList) {
            if(!newSealVehicleService.checkBatchCodeIsSendPreSealVehicle(sendM.getSendCode())){
                continue;
            }
            if(newSealVehicleService.checkSendCodeIsSealed(sendM.getSendCode())){
                continue;
            }
            if(sealedSendCodeList.contains(sendM.getSendCode())){
                continue;
            }
            if(sendUnSealMap.containsKey(sendM.getReceiveSiteCode())){
                sendUnSealMap.get(sendM.getReceiveSiteCode()).add(sendM.getSendCode());
            }else {
                Set<String> sendCodeSet = new HashSet<>();
                sendCodeSet.add(sendM.getSendCode());
                sendUnSealMap.put(sendM.getReceiveSiteCode(), sendCodeSet);
            }
        }
        return sendUnSealMap;
    }

    /**
     * 预封车数据list转Map
     * @param preSealVehicleList
     * @return
     */
	private Map<Integer, PreSealVehicle> convertMap(List<PreSealVehicle> preSealVehicleList){
        Map<Integer, PreSealVehicle> preMap = null;
        if(preSealVehicleList != null && !preSealVehicleList.isEmpty()){
            preMap = new HashMap<>(preSealVehicleList.size());
            //组装车牌信息
            StringBuilder sealCode = new StringBuilder();
            for(PreSealVehicle vo : preSealVehicleList){
                //同一目的地，将车牌组装到车牌list中
                sealCode.append("车辆-").append(vo.getVehicleNumber()).append(Constants.SEPARATOR_COLON).append("封签-").append(vo.getSealCodes());
                if(preMap.containsKey(vo.getReceiveSiteCode())){
                    PreSealVehicle temp = preMap.get(vo.getReceiveSiteCode());
					temp.getPreSealUuids().add(vo.getPreSealUuid());
                    temp.getVehicleNumbers().add(vo.getVehicleNumber());
                    temp.appendSealCodeStr(sealCode.toString());
                    temp.putVehicleSealCode(vo.getVehicleNumber(), vo.getSealCodes());
                    temp.getVehicleMeasureMap().put(vo.getVehicleNumber(), new VehicleMeasureInfo(vo.getVehicleNumber(), vo.getVolume(), vo.getWeight()));
                }else{
                	vo.setPreSealUuids(new ArrayList<String>());
                    vo.getVehicleNumbers().add(vo.getVehicleNumber());
                    vo.appendSealCodeStr(sealCode.toString());
                    vo.putVehicleSealCode(vo.getVehicleNumber(), vo.getSealCodes());
                    vo.getVehicleMeasureMap().put(vo.getVehicleNumber(), new VehicleMeasureInfo(vo.getVehicleNumber(), vo.getVolume(), vo.getWeight()));
                    vo.getPreSealUuids().add(vo.getPreSealUuid());
                    preMap.put(vo.getReceiveSiteCode(), vo);
                }
                sealCode.delete(Constants.NUMBER_ZERO, sealCode.length());
            }
        }
        return preMap;
    }

    /**
     * 一键封车（封车小批量异步化）
     * @param data
     * @return
     */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
    @RequestMapping(value = "/batchSeal")
    @JProfiler(jKey = "DMSWEB.PreSealVehicleController.batchSeal", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    public @ResponseBody JdResponse<List<PreSealVehicle>> batchSeal(@RequestBody List<PreSealVehicle> data) {
        JdResponse<List<PreSealVehicle>> rest = new JdResponse<List<PreSealVehicle>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if(log.isDebugEnabled()){
            log.debug("一键封车请求参数：{}", JsonHelper.toJson(data));
        }
        if(uccPropertyConfiguration.getOfflineQuickSeal()){
            rest.toFail("一键封车已下线，请使用PDA进行封车！");
            return rest;
        }
        if(data == null || data.isEmpty()){
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("请选择封车数据!");
            return rest;
        }

        LoginUser user = getLoginUser();
        if(user == null || StringUtils.isEmpty(user.getUserErp()) || StringUtils.isEmpty(user.getUserName())){
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("登录已过期，请重新登录!");
            return rest;
        }

        //去除的空批次封车数据或者失败的数据
        List<PreSealVehicle> removeOrFailedList = new ArrayList<>();
        String userErp = user.getUserErp();
        String usetName = user.getUserName();
        Integer userCode = user.getStaffNo();
        //实操时间取服务器时间
        Date operateTime = new Date();
        int total = data.size();
        for(int index = 0; index < total; index += SEAL_LIMIT ){
            List<PreSealVehicle> partList;

            int end = index + SEAL_LIMIT;
            if(end > total){
                partList = data.subList(index, total);
            }else{
                partList = data.subList(index, end);
            }

            if(partList == null || partList.isEmpty()){
                continue;
            }

            try{
                removeOrFailedList.addAll(preSealVehicleService.batchSeal(partList, userCode, userErp, usetName, operateTime));
            }catch (Exception e){
                log.error("批量封车异常：{}", JsonHelper.toJson(partList), e);
            }
        }

        if(!removeOrFailedList.isEmpty()){
            rest.setData(removeOrFailedList);
            rest.setCode(JdResponse.CODE_PARTIAL_SUCCESS);
            rest.setMessage("以下数据一键封车失败,请确认封车批次内是否有发货数据后，重新查询待封车数据再封车。");
        }

        return rest;
    }

    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
    @RequestMapping("/getModifyVolumePage")
    public String getModifyVolumePage() {
        return "/newseal/preSealVolumeModify";
    }

	/**
	 * 查询当前场地未使用的运力编码
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
	@RequestMapping(value = "/getRemainTransportCode")
	public @ResponseBody JdResponse<List<CapacityDomain>> getRemainTransportCode() {
		JdResponse<List<CapacityDomain>> rest = new JdResponse<List<CapacityDomain>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        LoginUser user = getLoginUser();
        if(user == null || StringUtils.isEmpty(user.getUserErp()) || StringUtils.isEmpty(user.getUserName())){
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("登录已过期，请重新登录!");
            return rest;
        }
        CallerInfo info = Profiler.registerInfo("DMSWEB.PreSealVehicleController.getRemainTransportCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
        Integer createSiteCode = user.getSiteCode();
		try{
            rest.setData(getRemainTransportCodes(createSiteCode));
        }catch (Exception e){
            Profiler.functionError(info);
		    log.error("查询当前场地未使用的运力编码失败，场地ID：{}", createSiteCode, e);
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("服务异常，查询当前场地未使用的运力编码失败!");
        }finally {
            Profiler.registerInfoEnd(info);
        }

        return rest;
	}

    /**
     * 查询当前场地当天未使用运力资源信息
     * @param createSiteCode
     * @return
     */
	private List<CapacityDomain> getRemainTransportCodes(Integer createSiteCode){
        List<CapacityDomain> result = new ArrayList<>();

        List<String> usedList = getUsedTransports(createSiteCode);
        Map<String, CapacityDomain> allMap = getAllTransports(createSiteCode);
        if(allMap == null || allMap.isEmpty()){
            return result;
        }
        if(usedList.isEmpty()){
            result.addAll(allMap.values());
        }else {
            for(String key : allMap.keySet()){
                //只保留未使用的运力
                if(!usedList.contains(key)){
                    result.add(allMap.get(key));
                }
            }
        }
        //按发车时间排序
        Collections.sort(result, new Comparator<CapacityDomain>() {
            @Override
            public int compare(CapacityDomain dto1, CapacityDomain dto2) {
                return Long.valueOf(dto1.getSendTime()).compareTo(Long.valueOf(dto2.getSendTime()));
            }
        });
        return result;
    }

    /**
     * 查询当前场地全量运力资源
     * @param createSiteCode
     * @return
     */
	private Map<String, CapacityDomain> getAllTransports(Integer createSiteCode){
        //接口实现
        CapacityCodeRequest queryParameter = new CapacityCodeRequest();
        queryParameter.setScode(createSiteCode);
        CapacityCodeResponse response = siteService.queryCapacityCodeInfo(queryParameter);
        if(JdResponse.CODE_SUCCESS.equals(response.getCode()) && response.getData() != null){
            List<CapacityDomain> domainList = response.getData();
            Map<String, CapacityDomain> domainMap = new HashMap<>(domainList.size());
            for (CapacityDomain domain : domainList){
                domainMap.put(domain.getTranCode(), domain);
            }
            return domainMap;
        }
        return null;
    }

    /**
     * 查询当前场地当天到此刻的已使用运力资源
     * @param createSiteCode
     * @return
     */
    private List<String> getUsedTransports(Integer createSiteCode){
        List<String> usedTransports = new ArrayList<>();
        //接口实现
        List<String> preUsedTransports = preSealVehicleService.findTodayUsedTransports(createSiteCode);
        if(preUsedTransports != null && !preUsedTransports.isEmpty()){
            usedTransports.addAll(preUsedTransports);
        }
        List<String> sealUsedTransports = sealVehiclesService.findTodayUsedTransports(createSiteCode);
        if(sealUsedTransports != null && !sealUsedTransports.isEmpty()){
            usedTransports.addAll(sealUsedTransports);
        }

        return usedTransports;
    }

}
