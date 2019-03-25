package com.jd.bluedragon.distribution.newseal.controller;

import java.util.*;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleCondition;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

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

	private static final Log logger = LogFactory.getLog(PreSealVehicleController.class);

	@Autowired
	private PreSealVehicleService preSealVehicleService;

	@Autowired
	private SealVehiclesService sealVehiclesService;

    @Autowired
    private SendMService sendMService;

	@Autowired
	private SiteService siteService;

    private static final Integer SEAL_LIMIT = 5;
	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/newseal/preSealVehicle";
	}
	/**
	 * 跳转到未使用运力页面
	 * @return
	 */
	@RequestMapping(value = "/unusedtransport")
	public String unusedtransport() {
		return "/newseal/unusedtransport";
	}

	/**
	 * 根据条件查询预封车信息（包含对应未封车的批次信息）
	 * @param condition
	 * @return
	 */
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
        Integer createSiteCode = user.getSiteCode();
        try{
            Map<Integer, PreSealVehicle> preMap = preSealVehicleService.queryBySiteCode(createSiteCode);
            logger.debug("查询预封车信息为：" + JsonHelper.toJson(preMap));
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
                if(unSealSendCodes == null || unSealSendCodes.isEmpty()){
                    rest.setCode(JdResponse.CODE_FAIL);
                    rest.setMessage("未查询到待封车批次，请确认时间范围和预封车数据信息是否准确!");
                }else{
                    rest.setData(buildPreSealVehicle(preMap, unSealSendCodes));
                }
            }
        }catch (Exception e){
            logger.error("查询预封车数据信息失败，查询条件：" + JsonHelper.toJson(condition), e);
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("服务异常，查询预封车数据信息失败!");
        }
		return rest;
	}

    /**
     * 组装未封车批次号到预封车集合中
     * @param preMap
     * @param unSealSendCodes
     */
	private List<PreSealVehicle> buildPreSealVehicle(Map<Integer, PreSealVehicle> preMap, List<SealVehicles> unSealSendCodes){

        //组装批次信息
        for(SealVehicles vo : unSealSendCodes){
	        Integer key = vo.getReceiveSiteCode();
	        if(preMap.containsKey(key)){
                List<SealVehicles> temp = preMap.get(key).getSendCodes();
                boolean exist = false;
                for(SealVehicles seal : temp){
                    if(seal.getSealDataCode().equals(vo.getSealDataCode())){
                        exist = true;
                        break;
                    }
                }
                if(!exist){
                    //该目的地只有一个车牌号时默认设置为改车牌号
                    List<String> vehicleNumbers = preMap.get(key).getVehicleNumbers();
                    if(vehicleNumbers.size() == 1){
                        vo.setVehicleNumber(vehicleNumbers.get(0));
                    }
                    temp.add(vo);
                }
            }

        }
        List<PreSealVehicle> result = new ArrayList<>(preMap.size());
        result.addAll(preMap.values());
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
     * @param startDate
     * @return
     */
    private List<SealVehicles> getAllSendCodes(Integer createSiteCode, Integer receiveSiteCode, Date startDate){
        List<SealVehicles> result = null;
        List<SendM> sendMS = sendMService.findAllSendCodesWithStartTime(createSiteCode, receiveSiteCode, startDate);
        if(sendMS != null && !sendMS.isEmpty()){
            result = new ArrayList<>(sendMS.size());
            for(SendM sendM : sendMS){
                SealVehicles vehicles = new SealVehicles();
                vehicles.setSealDataCode(sendM.getSendCode());
                vehicles.setReceiveSiteCode(sendM.getReceiveSiteCode());
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
    @RequestMapping(value = "/batchSeal")
    public @ResponseBody JdResponse<List<PreSealVehicle>>  batchSeal(@RequestBody List<PreSealVehicle> data) {
        JdResponse<List<PreSealVehicle>> rest = new JdResponse<List<PreSealVehicle>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        logger.debug("一键封车请求参数：" + JsonHelper.toJson(data));
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
        String userErp = user.getUserErp();
        String usetName = user.getUserName();
        Integer userCode = user.getStaffNo();
        //小批量执行封车
        List<PreSealVehicle> failedList = new ArrayList<>();
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
                preSealVehicleService.batchSeal(partList, userCode, userErp, usetName, operateTime);
            }catch (Exception e){
                failedList.addAll(partList);
                logger.error("批量封车异常：" + JsonHelper.toJson(partList), e);
            }
        }
        if(!failedList.isEmpty()){
            rest.setData(failedList);
            rest.setCode(JdResponse.CODE_PARTIAL_SUCCESS);
            rest.setMessage("以下数据一键封车失败!");
        }

        return rest;
    }

	/**
	 * 查询当前场地未使用的运力编码
	 * @return
	 */
	@RequestMapping(value = "/getRemainTransportCode")
	public @ResponseBody JdResponse<List<CapacityDomain>> getRemainTransportCode() {
		JdResponse<List<CapacityDomain>> rest = new JdResponse<List<CapacityDomain>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        LoginUser user = getLoginUser();
        if(user == null || StringUtils.isEmpty(user.getUserErp()) || StringUtils.isEmpty(user.getUserName())){
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("登录已过期，请重新登录!");
            return rest;
        }
        Integer createSiteCode = user.getSiteCode();
		try{
            rest.setData(getUnusedTransports(createSiteCode));
        }catch (Exception e){
		    logger.error("查询当前场地未使用的运力编码失败，场地ID：" + createSiteCode, e);
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("服务异常，查询当前场地未使用的运力编码失败!");
        }

        return rest;
	}

    /**
     * 查询当前场地当天未使用运力资源信息
     * @param createSiteCode
     * @return
     */
	private List<CapacityDomain> getUnusedTransports(Integer createSiteCode){
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
