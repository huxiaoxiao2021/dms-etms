package com.jd.bluedragon.distribution.newseal.controller;

import com.jd.bluedragon.Constants;
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
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.utils.DateHelper;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    private static final Integer SEAL_LIMIT = 5;
	/**
	 * 返回主页面
	 * @return
	 */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
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
            Map<Integer, PreSealVehicle> preMap = convertMap(preSealVehicleList);
            if(log.isDebugEnabled()){
                log.debug("查询预封车信息为：{}", JsonHelper.toJson(preMap));
            }
            if(preMap == null || preMap.isEmpty()){
                rest.setData(new ArrayList<PreSealVehicle>());
            }else{
                List<SealVehicles> unSealSendCodes = new ArrayList<>();
                for(Integer receiveSiteCode : preMap.keySet()){
                    List<SealVehicles> temp = getUnSealSendCodes(createSiteCode, receiveSiteCode, condition.getHourRange());
                    if(temp != null && !temp.isEmpty()){
                        unSealSendCodes.addAll(temp);
                    }
                }
                rest.setData(buildPreSealVehicle(preMap, unSealSendCodes));
            }
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
     * 预封车数据list转Map
     * @param preSealVehicleList
     * @return
     */
	private Map<Integer, PreSealVehicle> convertMap(List<PreSealVehicle> preSealVehicleList){
        Map<Integer, PreSealVehicle> preMap = null;
        if(preSealVehicleList != null && !preSealVehicleList.isEmpty()){
            preMap = new HashMap<>(preSealVehicleList.size());
            //组装车牌信息
            for(PreSealVehicle vo : preSealVehicleList){
                //同一目的地，将车牌组装到车牌list中
                if(preMap.containsKey(vo.getReceiveSiteCode())){
                    PreSealVehicle temp = preMap.get(vo.getReceiveSiteCode());
                    temp.getVehicleNumbers().add(vo.getVehicleNumber());
                    temp.appendSealCodeStr("车辆-" + vo.getVehicleNumber() + Constants.SEPARATOR_COLON + "封签-" + vo.getSealCodes());
                    temp.putVehicleSealCode(vo.getVehicleNumber(), vo.getSealCodes());
                    temp.getVehicleMeasureMap().put(vo.getVehicleNumber(), new VehicleMeasureInfo(vo.getVehicleNumber(), vo.getVolume(), vo.getWeight()));
                }else{
                    vo.getVehicleNumbers().add(vo.getVehicleNumber());
                    vo.appendSealCodeStr("车辆-" + vo.getVehicleNumber() + Constants.SEPARATOR_COLON + "封签-" + vo.getSealCodes());
                    vo.putVehicleSealCode(vo.getVehicleNumber(), vo.getSealCodes());
                    vo.getVehicleMeasureMap().put(vo.getVehicleNumber(), new VehicleMeasureInfo(vo.getVehicleNumber(), vo.getVolume(), vo.getWeight()));
                    preMap.put(vo.getReceiveSiteCode(), vo);
                }
            }
        }
        return preMap;
    }

    /**
     * 组装未封车批次号到预封车集合中
     * @param preMap
     * @param unSealSendCodes
     */
	private List<PreSealVehicle> buildPreSealVehicle(Map<Integer, PreSealVehicle> preMap, List<SealVehicles> unSealSendCodes){

        //组装批次信息
        if(CollectionUtils.isNotEmpty(unSealSendCodes)){
            for(SealVehicles unSealVehiclesWithSendCodeTemp : unSealSendCodes){
                Integer receiveSiteCode = unSealVehiclesWithSendCodeTemp.getReceiveSiteCode();
                if(preMap.containsKey(receiveSiteCode)){
                    PreSealVehicle preSV = preMap.get(receiveSiteCode);
                    List<SealVehicles> preSealVehiclesWithSendCodeList = preSV.getSendCodes();
                    //批次去重
                    boolean isSealed = false;
                    for(SealVehicles sealVehicleTemp : preSealVehiclesWithSendCodeList){
                        if(sealVehicleTemp.getSealDataCode().equals(unSealVehiclesWithSendCodeTemp.getSealDataCode())){
                            isSealed = true;
                            break;
                        }
                    }
                    if(!isSealed){
                        //该目的地只有一个车牌号时默认设置为改车牌号
                        List<String> vehicleNumbers = preSV.getVehicleNumbers();
                        if(vehicleNumbers.size() == 1){
                            unSealVehiclesWithSendCodeTemp.setVehicleNumber(vehicleNumbers.get(0));
                            unSealVehiclesWithSendCodeTemp.setSealCodes(preSV.getVehicleSealCodeMap().get(unSealVehiclesWithSendCodeTemp.getVehicleNumber()));
                        }
                        preSealVehiclesWithSendCodeList.add(unSealVehiclesWithSendCodeTemp);
                    }
                }

            }
        }
        List<PreSealVehicle> result = new ArrayList<>(preMap.size());
        result.addAll(preMap.values());
        //按发车时间排序
        Collections.sort(result, new Comparator<PreSealVehicle>() {
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
        return result;
    }

    /**
     * 查询未封车批次信息
     * @param createSiteCode
     * @param receiveSiteCode
     * @param hourRange
     * @return
     */
    private List<SealVehicles> getUnSealSendCodes(Integer createSiteCode, Integer receiveSiteCode, Integer hourRange){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, hourRange * -1);
        Date startDate = calendar.getTime();

        List<SealVehicles> allSendCodes = getAllSendCodes(createSiteCode, receiveSiteCode, startDate);
        if(allSendCodes == null || allSendCodes.isEmpty()){
            return allSendCodes;
        }

        Set<String> sendCodeSet = new HashSet<>(allSendCodes.size());
        for(SealVehicles vo : allSendCodes){
            sendCodeSet.add(vo.getSealDataCode());
        }

        List<String> sealedSendCodes = sealVehiclesService.findBySealDataCodes(sendCodeSet);
        if(sealedSendCodes == null || sealedSendCodes.isEmpty()){
            return allSendCodes;
        }

        List<SealVehicles> result = new ArrayList<>();
        for(SealVehicles vo : allSendCodes){
            if(!sealedSendCodes.contains(vo.getSealDataCode())){
                result.add(vo);
            }
        }

        return result;
    }

    /**
     * 查询全部的批次号
     * @param createSiteCode
     * @param date
     * @return
     */
    @RequestMapping(value = "/getAllSendCodesStr")
    @ResponseBody
    public String getAllSendCodesStr(@RequestParam("createSiteCode") Integer createSiteCode,
                                     @RequestParam("receiveSiteCode") Integer receiveSiteCode,
                                     @RequestParam("date") String date){
        List<SealVehicles> sealVehicles = getAllSendCodes( createSiteCode,  receiveSiteCode, DateHelper.parseDateTime(date));
        return JsonHelper.toJson(sealVehicles);
    }

    private List<SealVehicles> getAllSendCodes(Integer createSiteCode, Integer receiveSiteCode, Date startDate){
        List<SealVehicles> result = null;

        List<SealVehicles> sourceList= convertSealVehiclesBySendM(sendMService.findAllSendCodesWithStartTime(createSiteCode, receiveSiteCode, startDate),receiveSiteCode);

        if(sourceList != null && !sourceList.isEmpty()){
            result = new ArrayList<>(sourceList.size());
            for(SealVehicles itme : sourceList){
                if(!newSealVehicleService.checkBatchCodeIsSendPreSealVehicle(itme.getSealDataCode())){
                    continue;
                }

                if(newSealVehicleService.checkSendCodeIsSealed(itme.getSealDataCode())){
                    continue;
                }

                result.add(itme);
            }
        }
        return result;
    }

    private List<SealVehicles> convertSealVehiclesBySendM(List<SendM> sendMList,Integer receiveSiteCode){
        List<SealVehicles> result = null;
        if(sendMList != null && !sendMList.isEmpty()){
            result = new ArrayList<>();
            for(SendM sendM : sendMList){
                SealVehicles vehicles = new SealVehicles();
                vehicles.setSealDataCode(sendM.getSendCode());
                vehicles.setReceiveSiteCode(receiveSiteCode);
                result.add(vehicles);
            }
        }

        return result;
    }

    /**
     * 一键封车（封车小批量异步化）
     * @param data
     * @return
     */
    @Authorization(Constants.DMS_WEB_PRE_SEALVEHICLE_R)
    @RequestMapping(value = "/batchSeal")
    @JProfiler(jKey = "DMSWEB.PreSealVehicleController.batchSeal", jAppName=Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP})
    public @ResponseBody JdResponse<List<PreSealVehicle>>  batchSeal(@RequestBody List<PreSealVehicle> data) {
        JdResponse<List<PreSealVehicle>> rest = new JdResponse<List<PreSealVehicle>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if(log.isDebugEnabled()){
            log.debug("一键封车请求参数：{}", JsonHelper.toJson(data));
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
