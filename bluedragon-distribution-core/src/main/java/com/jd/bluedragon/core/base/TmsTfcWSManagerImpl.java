package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.tfc.dto.CommonDto;
import com.jd.tms.tfc.dto.ScheduleCargoSimpleDto;
import com.jd.tms.tfc.dto.TransPlanScheduleCargoDto;
import com.jd.tms.tfc.dto.TransWorkItemDto;
import com.jd.tms.tfc.ws.TfcQueryWS;
import com.jd.tms.tfc.ws.TfcSelectWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName TmsTfcWSManagerImpl
 * @date 2019/4/8
 */
@Service("tmsTfcWSManager")
public class TmsTfcWSManagerImpl implements TmsTfcWSManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TfcQueryWS tfcQueryWS;

    @Autowired
    private TfcSelectWS tfcSelectWS;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.tfcSelectWS.getTransPlanScheduleCargoByCondition",mState={JProEnum.TP,JProEnum.FunctionError})
    public List<TransPlanScheduleCargoDto> getTransPlanScheduleCargoByCondition(ScheduleCargoSimpleDto cargoSimpleDto) {
        if (cargoSimpleDto != null) {
            CommonDto<List<TransPlanScheduleCargoDto>> commonDto = tfcSelectWS.getTransPlanScheduleCargoByCondition(cargoSimpleDto);
            if (commonDto.getCode() == 1) {
                return commonDto.getData();
            } else if (commonDto.getCode() == 2) {
                // 查询失败 状态码2 表示查询数据为空
                return Collections.EMPTY_LIST;
            } else {
                log.warn("[调用TMS-TFC-JSF接口]根据条件查询运输计划信息接口返回状态失败，调用参数: {}, 接口返回code:{}, message:{}"
                        ,JsonHelper.toJson(cargoSimpleDto),commonDto.getCode(),commonDto.getMessage());
            }
        }
        return null;
    }

    @Override
    public TransWorkItemDto queryTransWorkItemBySimpleCode(String simpleCode) throws Exception {
        if (StringUtils.isNotEmpty(simpleCode)) {
            CommonDto<TransWorkItemDto> commonDto = tfcQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
            if (commonDto.getCode() == 1) {
                return commonDto.getData();
            } else {
                log.warn("[调用TMS-TFC-JSF接口]根据派车任务明细简码获取派车任务明细接口返回状态失败，调用参数: {}, 接口返回code:{}, message:{}"
                        ,simpleCode, commonDto.getCode(), commonDto.getMessage());
            }
        }
        return null;
    }
}
