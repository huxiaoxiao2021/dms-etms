package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.ConfNodeCarrierDto;
import com.jd.tms.basic.ws.BasicQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/9/16
 */
@Service("basicQueryWSManager")
public class BasicQueryWSManagerImpl implements BasicQueryWSManager {
    private final static Logger logger = LoggerFactory.getLogger(BasicQueryWSManagerImpl.class);

    @Autowired
    private BasicQueryWS basicQueryWS;

    @Override
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getCarrierByNodeCode",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ConfNodeCarrierDto getCarrierByNodeCode(String nodeCode){
        try {
            CommonDto<ConfNodeCarrierDto> commonDto = basicQueryWS.getCarrierByNodeCode(nodeCode);
            if(commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS){
                logger.warn("查询承运商编码为空nodeCode[{}]",nodeCode);
                return null;
            }
            return commonDto.getData();
        } catch (Exception e) {
            logger.error("查询承运商编码报错nodeCode[{}]",nodeCode,e);
        }
        return null;
    }

    @Override
    @Cache(key = "BasicQueryWSManagerImpl.getDictList@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
    @JProfiler(jKey = "DMS.BASE.BasicQueryWSManagerImpl.getDictList",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BasicDictDto> getDictList(String parentCode, int dictLevel, String dictGroup){
        try {
            CommonDto<List<BasicDictDto>> commonDto = basicQueryWS.getDictList(parentCode,dictLevel,dictGroup);
            if(commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS){
                logger.warn("查询BASIC数据字典查询接口失败parentCode[{}]dictLevel[{}]dictGroup[{}]",
                        parentCode,dictLevel,dictGroup, JsonHelper.toJson(commonDto));
                return null;
            }
            return commonDto.getData();
        } catch (Exception e) {
            logger.error("查询BASIC数据字典查询接口报错parentCode[{}]dictLevel[{}]dictGroup[{}][{}]",parentCode,dictLevel,dictGroup,e);
        }
        return null;
    }
}
