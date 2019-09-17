package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.ConfNodeCarrierDto;
import com.jd.tms.basic.ws.BasicQueryWS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
