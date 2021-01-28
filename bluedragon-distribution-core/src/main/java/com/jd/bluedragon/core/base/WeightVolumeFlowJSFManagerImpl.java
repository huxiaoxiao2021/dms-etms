package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.weightVolumeFlow.WeightVolumeFlowJSFService;
import com.jd.ql.dms.report.weightVolumeFlow.domain.WeightVolumeFlowEntity;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2021/1/21 09:03
 * @Description:
 */
@Service("weightVolumeFlowJSFManager")
public class WeightVolumeFlowJSFManagerImpl implements WeightVolumeFlowJSFManager{

    private Logger logger = LoggerFactory.getLogger(WeightVolumeFlowJSFManagerImpl.class);


    @Autowired
    protected WeightVolumeFlowJSFService weightVolumeFlowJSFService;

    /**
     * 获取已称重数据
     *
     * @param barCodes
     * @param siteCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WeightVolumeFlowJSFManager.findExistFlowNotSplit",mState={JProEnum.TP,JProEnum.FunctionError})
    public Set<String> findExistFlow(List<String> barCodes, Integer siteCode) {
        BaseEntity<Set<String>> result = weightVolumeFlowJSFService.findExistFlowNotSplit(barCodes,siteCode);
        if(result.isSuccess()){
            return result.getData();
        }else{
            logger.error("WeightVolumeFlowJSFManager.findExistFlow fail! {},{}  result:{}", JsonHelper.toJson(barCodes),siteCode,JsonHelper.toJson(result));
        }
        return new HashSet<>();
    }

    /**
     * 检查箱号是否称重
     *
     * @param boxCode
     * @param siteCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "WeightVolumeFlowJSFManager.findExistFlowOfBox",mState={JProEnum.TP,JProEnum.FunctionError})
    public WeightVolumeFlowEntity findExistFlowOfBox(String boxCode, Integer siteCode) {
        BaseEntity<WeightVolumeFlowEntity> result = weightVolumeFlowJSFService.findFlowOfBox(boxCode,siteCode);
        if(result.isSuccess()){
            return result.getData();
        }else{
            logger.error("WeightVolumeFlowJSFManager.findExistFlowOfBox fail! {},{}  result:{}", boxCode,siteCode,JsonHelper.toJson(result));
        }
        return null;
    }

}
