package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.erp.ws.SupportServiceInterface;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: TerminalManagerImpl
 * @Description: 终端包装类
 * @author: hujiping
 * @date: 2019/4/2 15:15
 */
@Service("terminalManager")
public class TerminalManagerImpl implements TerminalManager {

    @Autowired
    private SupportServiceInterface supportProxy;

    /**
     * 获取终端箱号中所有运单信息
     * @param boxCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWEB.TerminalManagerImpl.getSendDetails", jAppName= Constants.UMP_APP_NAME_DMSWEB, mState={JProEnum.TP, JProEnum.FunctionError})
    public com.jd.etms.erp.service.domain.BaseEntity<List<SendInfoDto>> getSendDetails(String boxCode){
        SendInfoDto sendInfoDto = new SendInfoDto();
        sendInfoDto.setBoxCode(boxCode);
        return supportProxy.getSendDetails(sendInfoDto);
    }
}
