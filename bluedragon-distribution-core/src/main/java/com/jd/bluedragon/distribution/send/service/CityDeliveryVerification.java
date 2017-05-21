package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.transBillSchedule.service.TransBillScheduleService;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.*;

/**
 * 城配订单发货验证
 * Created by wangtingwei on 2017/4/26.
 * 验证处理：
 *        原则：针对城配箱子及原包，其它类型一概通过。
 *        流程：
 *             1：原包发货，只能发往分拣中心(即站点类型为64)
 *             2：箱子发货，同一派车单下的订单需要装入同一箱子中，才能发货
 */
@Service("cityDeliveryVerification")
public class CityDeliveryVerification implements DeliveryVerification{

    private static final Log LOG= LogFactory.getLog(CityDeliveryVerification.class);

    public static final Integer SITE_TYPE_FOR_SORTING_CENTER=Integer.valueOf(64);
    public static final int CITY_DISTRIBUTION_WAYBILL_INDEX = 145;
    public static final char CITY_DISTRIBUTION_WAYBILL_INDEX_CHAR_VALUE = '1';
    public static final String CITY_CAN_NOT_TO_NONE_SORTING_CENTER = "城配运单原包发货只能发到分拣中心";
    public static final String NONE_SCHEDULER_BOX = "-1";

    @Resource(name = "baseService")
    private BaseService                     baseService;


    @Resource(name = "transbillMService")
    private TransbillMService transbillMService;

    @Resource(name = "transBillScheduleService")
    private TransBillScheduleService        transBillScheduleService;

    @Resource(name = "sortingService")
    private SortingService                  sortingService;

    @Resource(name = "boxService")
    private BoxService                      boxService;

    @Resource(name = "waybillCommonService")
    private WaybillCommonService            waybillCommonService;

    /**
     * 城配验证
     * @param       boxCode                 箱号
     * @param       receiveSiteCode         收货站点
     * @return
     */
    @Override
    public VerificationResult verification(String boxCode, Integer receiveSiteCode,boolean checkPackage) {
        VerificationResult result=new VerificationResult();
        try {
            boxCode=boxCode.trim();
            if (checkPackage && SerialRuleUtil.isMatchAllPackageNo(boxCode)
                    && isCityDistributionWaybill(SerialRuleUtil.getWaybillCode(boxCode))) {
                BaseStaffSiteOrgDto site = baseService.getSiteBySiteID(receiveSiteCode);
                if (null != site && !SITE_TYPE_FOR_SORTING_CENTER.equals(site.getSiteType())) {
                    result.setCode(false);
                    result.setMessage(CITY_CAN_NOT_TO_NONE_SORTING_CENTER);
                }
            }
            if (SerialRuleUtil.isMatchBoxCode(boxCode)) {
                /****send_d与派车单下运单进行对比，当一致时，才能通过*/
                String scheduleBillCode = transBillScheduleService.getKey(boxCode);
                if (StringUtils.isBlank(scheduleBillCode) || scheduleBillCode.equals(NONE_SCHEDULER_BOX)) {
                    return result;
                }
                List<String> list = transbillMService.getEffectWaybillCodesByScheduleBillCode(scheduleBillCode);
                Box box = this.boxService.findBoxByCode(boxCode);
                Sorting queryArgument = new Sorting();
                queryArgument.setBoxCode(boxCode);
                queryArgument.setCreateSiteCode(box.getCreateSiteCode());
                queryArgument.setReceiveSiteCode(box.getReceiveSiteCode());
                List<Sorting> sortingList = sortingService.findByBoxCode(queryArgument);
                Map<String, Set<String>> map = new HashMap<String, Set<String>>();
                for (Sorting item : sortingList) {
                    if(!SerialRuleUtil.isMatchAllPackageNo(item.getPackageCode())){
                        continue;
                    }
                    if (map.containsKey(item.getWaybillCode())) {
                        map.get(item.getWaybillCode()).add(item.getPackageCode());
                    } else {
                        Set<String> temp = new HashSet<String>();
                        temp.add(item.getPackageCode());
                        map.put(item.getWaybillCode(), temp);
                    }
                }
                for (String itemString : list) {
                    if (map.containsKey(itemString)) {
                        /**一单多件不全校验**/
                        Set<String> packageSets=  map.get(itemString);
                        List<String> packageCodeList=new ArrayList<String>(packageSets);
                        if (packageCodeList.size() != SerialRuleUtil.getPackageCounter(packageCodeList.get(0))) {
                            List<String> targetList = SerialRuleUtil.generateAllPackageCodes(packageCodeList.get(0));
                            for (String packageCode : packageCodeList) {
                                targetList.remove(packageCode);
                            }
                            result.setCode(false);
                            result.setMessage(MessageFormat.format("运单【{0}】一单多件不齐，缺少包裹【{1}】", itemString, targetList.get(0)));
                            break;
                        }
                    } else {
                        result.setCode(false);
                        result.setMessage(MessageFormat.format("派车单【{0}】不齐，缺少运单【{1}】", scheduleBillCode, itemString));
                        break;
                    }
                }
            }
        }catch (Throwable throwable){
            LOG.error(throwable.getMessage(),throwable);

        }
        return result;
    }


    private boolean isCityDistributionWaybill(String waybillCode){
        try {
            Waybill waybillDto = waybillCommonService.findWaybillAndPack(waybillCode, true, false, false, false);
            return  waybillDto!=null
                    &&waybillDto.getSendPay()!=null
                    &&waybillDto.getSendPay().length()>CITY_DISTRIBUTION_WAYBILL_INDEX
                    &&waybillDto.getSendPay().charAt(CITY_DISTRIBUTION_WAYBILL_INDEX)== CITY_DISTRIBUTION_WAYBILL_INDEX_CHAR_VALUE;
        }catch (Throwable throwable){
            LOG.error(throwable.getMessage(),throwable);
            return false;
        }

    }


}
