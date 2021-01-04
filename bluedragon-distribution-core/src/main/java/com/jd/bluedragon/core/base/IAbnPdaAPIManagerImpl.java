package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.BaseResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ExceptionReason;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ReportRecord;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ResultCodeEnum;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.service.ExceptionReasonService;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.service.ReportService;
import com.jd.wms.packExchange.domains.packExchange.result.ReportResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("iAbnPdaAPIManager")
public class IAbnPdaAPIManagerImpl implements IAbnPdaAPIManager {

    private static final Integer SUCCESS_CODE= 1;
    private static final Integer FAIL=-1;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ExceptionReasonService exceptionReasonService;

    @Autowired
    private ReportService reportService;

    @JProfiler(jKey = "DMSWEB.IAbnPdaAPIManagerImpl.selectAbnReasonByErp", mState = {JProEnum.TP})
    @Cache(key = "IAbnPdaAPIManager.selectAbnReasonByErp@args0", memoryEnable = true, memoryExpiredTime = 3 * 60 * 1000, redisEnable = true, redisExpiredTime = 5 * 60 * 1000)
    @Override
    public Map<String, ExceptionReason> selectAbnReasonByErp(String userErp) {
        List<ExceptionReason> abnormalReasonDtoList = null;

        BaseResult<List<ExceptionReason>> res=getExceptionReasons();
        if(res!=null && res.getResultCode().equals(SUCCESS_CODE)){
            abnormalReasonDtoList=res.getData();
        }

        if (abnormalReasonDtoList == null || abnormalReasonDtoList.size() == 0) {
            logger.warn("getExceptionReasons JSF接口返回原因列表为null！ERP：{}", userErp);
            return null;
        }

        logger.info("调用质控系统JSF接口获取质控侧异常原因列表：{}", JsonHelper.toJson(abnormalReasonDtoList));

        Map<String, ExceptionReason> abnormalReasonDtoMap = new HashMap<>(abnormalReasonDtoList.size());

        for (ExceptionReason abnormalReasonDto : abnormalReasonDtoList) {
            Long id = abnormalReasonDto.getId();
            String level = abnormalReasonDto.getAbnormalLevel();
            //层级+编号作为key
            String key = level + "-" + id.toString();
            abnormalReasonDtoMap.put(key, abnormalReasonDto);
        }
        return abnormalReasonDtoMap;
    }

    @JProfiler(jKey = "DMSWEB.IAbnPdaAPIManagerImpl.report", mState = {JProEnum.TP})
    @Override
    public JdCResponse report(List<ReportRecord> wpAbnormalRecordPda) {
        JdCResponse res = new JdCResponse<>(0, JdCResponse.MESSAGE_FAIL);

        List<String> reportFails=new ArrayList<>();
        for (ReportRecord item : wpAbnormalRecordPda) {
            try {
                BaseResult result= reportService.report(item);
                if(!result.getResultCode().equals(ResultCodeEnum.SUCCESS.getCode())){
                    reportFails.add(item.getCode());
                }
            } catch (Exception e) {
                logger.error("调用质控系统report JSF接口异常！入参：[{}]",JsonHelper.toJson(item), e);
                reportFails.add(item.getCode());
            }
        }

        if(reportFails.size()<=0){
            res.setCode(5);
            res.setMessage("全部成功");
            return res;
        }

        if(reportFails.size()>0 && reportFails.size()<wpAbnormalRecordPda.size()){
            res.setCode(3);
            res.setMessage(StringUtils.join(reportFails.toArray(), ','));
            return res;
        }

        return res;
    }

    @Override
    public JdCResponse<ExceptionReason> getAbnormalFirst(Long abnormalId){
        JdCResponse<ExceptionReason> res = new JdCResponse<>(JdCResponse.CODE_FAIL, JdCResponse.MESSAGE_FAIL);
        BaseResult<List<ExceptionReason>> reasons=getExceptionReasons();

        if(reasons==null || !reasons.getResultCode().equals(SUCCESS_CODE)){
            return res;
        }

        List<ExceptionReason> reasonList=reasons.getData();
        //按照AbnormalLevel降序排列
        Collections.sort(reasonList, new Comparator<ExceptionReason>() {
            @Override
            public int compare(ExceptionReason re0, ExceptionReason re1) {
                return re1.getAbnormalLevel().compareTo(re0.getAbnormalLevel());
            }
        });

        for (ExceptionReason item : reasonList) {
            if(item.getId().equals(abnormalId)){
                if(item.getAbnormalLevel().equals("1")){
                    res.setCode(JdCResponse.CODE_SUCCESS);
                    res.setMessage(JdCResponse.MESSAGE_SUCCESS);
                    res.setData(item);
                    return res;
                }else {
                    abnormalId=item.getFid();
                }
            }
        }

        return res;
    }

    private BaseResult<List<ExceptionReason>> getExceptionReasons(){
        BaseResult<List<ExceptionReason>> res=new BaseResult<>(FAIL);

        try {
            res = exceptionReasonService.getExceptionReasons();
        } catch (Exception e) {
            logger.error("getExceptionReasons JSF接口异常！",e);
        }

        return res;
    }
}
