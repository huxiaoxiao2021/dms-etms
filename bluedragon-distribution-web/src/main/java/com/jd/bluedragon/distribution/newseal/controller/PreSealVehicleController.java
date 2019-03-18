package com.jd.bluedragon.distribution.newseal.controller;

import java.util.*;

import com.jd.bluedragon.distribution.api.request.CapacityCodeRequest;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.departure.domain.CapacityCodeResponse;
import com.jd.bluedragon.distribution.departure.domain.CapacityDomain;
import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.utils.JsonHelper;
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
public class PreSealVehicleController {

	private static final Log logger = LogFactory.getLog(PreSealVehicleController.class);

	@Autowired
	private PreSealVehicleService preSealVehicleService;

	@Autowired
	private SealVehiclesService sealVehiclesService;

    @Autowired
    private SendMService sendMService;

	@Autowired
	private SiteService siteService;

    private static final Integer SQL_IN_EXPRESS_LIMIT = 999;
	/**
	 * 返回主页面
	 * @return
	 */
	@RequestMapping(value = "/toIndex")
	public String toIndex() {
		return "/newseal/preSealVehicle";
	}
	/**
	 * 根据id获取基本信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/detail/{id}")
	public @ResponseBody JdResponse<PreSealVehicle> detail(@PathVariable("id") Long id) {
		JdResponse<PreSealVehicle> rest = new JdResponse<PreSealVehicle>();
		rest.setData(preSealVehicleService.findById(id));
		return rest;
	}
	/**
	 * 保存数据
	 * @param preSealVehicle
	 * @return
	 */
	@RequestMapping(value = "/save")
	public @ResponseBody JdResponse<Boolean> save(@RequestBody PreSealVehicle preSealVehicle) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		try {
			rest.setData(preSealVehicleService.saveOrUpdate(preSealVehicle));
	    } catch (Exception e) {
			logger.error("fail to save！"+e.getMessage(),e);
			rest.toError("保存失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据id删除多条数据
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteByIds")
	public @ResponseBody JdResponse<Integer> deleteByIds(@RequestBody List<Long> ids) {
		JdResponse<Integer> rest = new JdResponse<Integer>();
		try {
			rest.setData(preSealVehicleService.deleteByIds(ids));
		} catch (Exception e) {
			logger.error("fail to delete！"+e.getMessage(),e);
			rest.toError("删除失败，服务异常！");
		}
		return rest;
	}
	/**
	 * 根据条件分页查询数据信息
	 * @param preSealVehicleCondition
	 * @return
	 */
	@RequestMapping(value = "/listData")
	public @ResponseBody PagerResult<PreSealVehicle> listData(@RequestBody PreSealVehicleCondition preSealVehicleCondition) {
        JdResponse<PagerResult<PreSealVehicle>> rest = new JdResponse<PagerResult<PreSealVehicle>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);;
        try{
            rest.setData(preSealVehicleService.queryByPagerCondition(preSealVehicleCondition));
        }catch (Exception e){
            logger.error("查询预封车数据信息失败，查询条件：" + JsonHelper.toJson(preSealVehicleCondition), e);
            rest.setCode(JdResponse.CODE_ERROR);
            rest.setMessage("服务异常，查询预封车数据信息失败!");
        }
		return rest.getData();
	}
	/**
	 * 根据条件查询预封车信息（包含对应未封车的批次信息）
	 * @param condition
	 * @return
	 */
	@RequestMapping(value = "/queryPreSeals")
	public @ResponseBody JdResponse<List<PreSealVehicle>>  queryPreSeals(@RequestBody PreSealVehicleCondition condition) {
        JdResponse<List<PreSealVehicle>> rest = new JdResponse<List<PreSealVehicle>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if(condition.getCreateSiteCode() == null || condition.getHourRange() == null){
            rest.setCode(JdResponse.CODE_FAIL);
            rest.setMessage("参数错误，始发地和时间范围不能为空!");
            return rest;
        }
        Integer createSiteCode = condition.getCreateSiteCode();
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

        List<String> sealedSendCodes = getSealedSendCodes(sendCodeSet);
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
     * 查询已封车批次号
     * @param sendCodeSet
     * @return
     */
    private List<String> getSealedSendCodes(Set<String> sendCodeSet){
        List<String> sendCodes = new ArrayList<>(sendCodeSet);
        List<String> result = new ArrayList<>();
        int total = sendCodes.size();
        for(int index = 0; index < total; index += SQL_IN_EXPRESS_LIMIT){
            List<String> temp;
            int end = index + SQL_IN_EXPRESS_LIMIT;
            if(end > total ){
                temp = sendCodes.subList(index, total);
            }else{
                temp = sendCodes.subList(index, end);
            }
            if (temp != null && !temp.isEmpty()) {
                List<String> sealedList = sealVehiclesService.findBySealDataCodes(temp);
                if(sealedList != null && !sealedList.isEmpty()){
                    result.addAll(sealedList);
                }
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
	 * 查询当前场地未使用的运力编码
	 * @param createSiteCode
	 * @return
	 */
	@RequestMapping(value = "/getRemainTransportCode/{createSiteCode}")
	public @ResponseBody JdResponse<List<CapacityDomain>> getRemainTransportCode(@PathVariable("createSiteCode") Integer createSiteCode) {
		JdResponse<List<CapacityDomain>> rest = new JdResponse<List<CapacityDomain>>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

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
