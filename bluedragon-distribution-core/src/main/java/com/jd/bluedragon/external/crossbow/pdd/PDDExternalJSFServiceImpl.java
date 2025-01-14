package com.jd.bluedragon.external.crossbow.pdd;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.external.pdd.DMSExternalInPDDService;
import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoDto;
import com.jd.bluedragon.distribution.external.pdd.domain.PDDWaybillPrintInfoRequest;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.service.PDDService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     拼多多运单面单信息的获取实现类 jsf接口实现类，工程内的调用请使用 PDDService
 *
 * @link com.jd.bluedragon.external.crossbow.pdd.service.PDDService 工程内的拼多多业务的接口调用请调用该类方法
 * @author wuzuxiang
 * @since 2019/10/14
 **/
@Service("dmsExternalInPDDService")
public class PDDExternalJSFServiceImpl implements DMSExternalInPDDService {

    private static final Logger log = LoggerFactory.getLogger(PDDExternalJSFServiceImpl.class);

    @Autowired
    private PDDService pddService;

    @Override
    public BaseEntity<PDDWaybillPrintInfoDto> queryWaybillByWaybillCode(PDDWaybillPrintInfoRequest request) {
        if (null == request || StringHelper.isEmpty(request.getWaybillCode()) || StringHelper.isEmpty(request.getSystemFlag())) {
            return new BaseEntity<>(BaseEntity.CODE_PARAM_ERROR, BaseEntity.MESSAGE_PARAM_ERROR);
        }
        CallerInfo callerInfo = Profiler.registerInfo("dms.web." + request.getSystemFlag() + ".PDDExternalJSFServiceImpl.queryWaybillByWaybillCode",
                Constants.UMP_APP_NAME_DMSWEB, false, true);

        PDDResponse<PDDWaybillDetailDto> response = pddService.queryPDDWaybillInfoByWaybillCodeWithCacheAndSource
                (request.getWaybillCode(), request.getSystemFlag(), true, true);
        Profiler.registerInfoEnd(callerInfo);

        if (null == response) {
            return new BaseEntity<>(BaseEntity.CODE_SUCCESS_NO, BaseEntity.MESSAGE_SUCCESS_NO);
        }
        /* 接口调用失败，则返回具体信息 */
        if (!response.getSuccess() || response.getResult() == null) {
            log.warn("拼多多接口调用失败，返回值：{}",JsonHelper.toJson(response));
            return new BaseEntity<>(BaseEntity.CODE_SUCCESS_NO, response.getErrorMsg());
        }
        BaseEntity<PDDWaybillPrintInfoDto> baseEntity = new BaseEntity<>(BaseEntity.CODE_SUCCESS, BaseEntity.MESSAGE_SUCCESS);
        /* 对拼多多接口返回内容进行重新组装返回 */
        PDDWaybillDetailDto pddWaybillDetailDto = response.getResult();
        PDDWaybillPrintInfoDto pddWaybillPrintInfoDto = new PDDWaybillPrintInfoDto();
        pddWaybillPrintInfoDto.setWaybillCode(request.getWaybillCode());
        pddWaybillPrintInfoDto.setSenderName(pddWaybillDetailDto.getSenderName());
        pddWaybillPrintInfoDto.setSenderMobile(pddWaybillDetailDto.getSenderMobile());
        pddWaybillPrintInfoDto.setSenderPhone(pddWaybillDetailDto.getSenderPhone());
        pddWaybillPrintInfoDto.setConsigneeName(pddWaybillDetailDto.getConsigneeName());
        pddWaybillPrintInfoDto.setConsigneeMobile(pddWaybillDetailDto.getConsigneeMobile());
        pddWaybillPrintInfoDto.setConsigneePhone(pddWaybillDetailDto.getConsigneePhone());
        baseEntity.setData(pddWaybillPrintInfoDto);

        if(log.isInfoEnabled()){
            log.info("JsfService.pdd.queryWaybillByWaybillCode,req:{},resp:{}", JsonHelper.toJson(request),JsonHelper.toJson(baseEntity));
        }

        return baseEntity;
    }
}
