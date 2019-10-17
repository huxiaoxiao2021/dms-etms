package com.jd.bluedragon.external.crossbow.pdd.service;

import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.bluedragon.external.crossbow.pdd.manager.PDDManager;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *     拼多多电子面单处理类接口
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
@Service
public class PDDServiceImpl implements PDDService {

    private static final Logger logger = LoggerFactory.getLogger(PDDServiceImpl.class);

    @Autowired
    private PDDManager pddManager;

    @Override
    public PDDWaybillDetailDto queryWaybillDetailByWaybillCode(String waybillCode) {
        if (!WaybillUtil.isPDDWaybillCode(waybillCode)) {
            return null;
        }
        PDDWaybillQueryDto pddWaybillQueryDto = new PDDWaybillQueryDto();
        pddWaybillQueryDto.setWaybillCode(waybillCode);

        PDDResponse<PDDWaybillDetailDto> response = pddManager.queryWaybillDetailByWaybillCode(pddWaybillQueryDto);
        logger.debug("获取拼多多的电子面单处理信息，参数为：{}，返回结果为：{}",waybillCode, JsonHelper.toJson(response));
        if (response == null) {
            logger.error("获取拼多多电子面信息失败，信息获取为空,{}",waybillCode);
            return null;
        }
        if (!response.getSuccess()) {
            logger.warn("获取拼多多电子面单信息失败，单号为：{}, 原因：{}",waybillCode,response.getErrorMsg());
            return null;
        }
        return response.getResult();
    }
}
