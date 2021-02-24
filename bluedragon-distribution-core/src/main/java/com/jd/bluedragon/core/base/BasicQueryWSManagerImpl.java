package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.tms.basic.dto.*;
import com.jd.tms.basic.ws.BasicQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wss.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author : xumigen
 * @date : 2019/9/16
 */
@Service("basicQueryWSManager")
public class BasicQueryWSManagerImpl implements BasicQueryWSManager {
    private final static Logger log = LoggerFactory.getLogger(BasicQueryWSManagerImpl.class);

    @Autowired
    private BasicQueryWS basicQueryWS;

    @Override
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getCarrierByNodeCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ConfNodeCarrierDto getCarrierByNodeCode(String nodeCode) {
        try {
            CommonDto<ConfNodeCarrierDto> commonDto = basicQueryWS.getCarrierByNodeCode(nodeCode);
            if (commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
                log.warn("查询承运商编码为空nodeCode[{}]", nodeCode);
                return null;
            }
            return commonDto.getData();
        } catch (Exception e) {
            log.error("查询承运商编码报错nodeCode[{}]", nodeCode, e);
        }
        return null;
    }

    @Override
    @Cache(key = "BasicQueryWSManagerImpl.getDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getDictList", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BasicDictDto> getDictList(String parentCode, int dictLevel, String dictGroup) {
        try {
            CommonDto<List<BasicDictDto>> commonDto = basicQueryWS.getDictList(parentCode, dictLevel, dictGroup);
            if (commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
                log.warn("查询BASIC数据字典查询接口失败parentCode[{}]dictLevel[{}]dictGroup[{}]",
                        parentCode, dictLevel, dictGroup, JsonHelper.toJson(commonDto));
                return null;
            }
            return commonDto.getData();
        } catch (Exception e) {
            log.error("查询BASIC数据字典查询接口报错parentCode[{}]dictLevel[{}]dictGroup[{}][{}]", parentCode, dictLevel, dictGroup, e);
        }
        return null;
    }

    /**
     * 获取车型查询接口
     *
     * @param owner
     * @param type
     * @return
     * @throws Exception
     */
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getVehicleTypeByType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public List<BasicDictDto> getVehicleTypeByType(String owner, int type) {
        CommonDto<List<BasicDictDto>> commonDto = basicQueryWS.getVehicleTypeByType(owner, type);
        if (commonDto.getCode() == CommonDto.CODE_SUCCESS) {
            return commonDto.getData();
        } else {
            log.warn("[BASIC车辆/车型信息查询]获取车型查询接口，owner[{}]，type[{}]，code[{}]，message[{}]",
                    owner, type, commonDto.getCode(), commonDto.getMessage());
        }
        return null;
    }

    /**
     * 根据车牌号获取车型配置信息
     *
     * @param vehicleNum
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getVehicleTypeByVehicleNum", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BasicVehicleTypeDto getVehicleTypeByVehicleNum(String vehicleNum) {
        try {
            CommonDto<BasicVehicleTypeDto> commonDto = basicQueryWS.getVehicleTypeByVehicleNum(vehicleNum);
            if (commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
                log.warn("根据车牌号获取车型配置信息为空,vehicleNum={}", vehicleNum);
                return null;
            }
            return commonDto.getData();
        } catch (Exception e) {
            log.error("根据车牌号获取车型配置信息异常,vehicleNum={}", vehicleNum, e);
        }
        return null;
    }

    /**
     * 根据车型获取车型配置信息
     *
     * @param vehicleType
     * @return
     */
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getVehicleTypeByVehicleType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public BasicVehicleTypeDto getVehicleTypeByVehicleType(Integer vehicleType) {
        if (null == vehicleType) {
            return null;
        }
        try {
            CommonDto<BasicVehicleTypeDto> commonDto = basicQueryWS.getVehicleTypeByVehicleType(vehicleType);
            if (commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS) {
                log.warn("根据车型获取车型配置信息返回结果为空");
                return null;
            }
            return commonDto.getData();
        } catch (Exception e) {
            log.warn("根据车型获取车型配置信息异常:", e);
        }
        return null;
    }


}
