package com.jd.bluedragon.core.base;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.etms.erp.service.domain.BaseEntity;
import com.jd.etms.erp.service.dto.SendInfoDto;
import com.jd.etms.erp.ws.SupportServiceInterface;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(TerminalManagerImpl.class);

    @Autowired
    private SupportServiceInterface supportProxy;

    @Override
    public List<SendInfoDto> getSendDetailsFromZD(String boxCode) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.TerminalManager.getSendDetailsFromZD",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            SendInfoDto sendInfoDto = new SendInfoDto();
            sendInfoDto.setBoxCode(boxCode);
            BaseEntity<List<SendInfoDto>> baseEntity = supportProxy.getSendDetails(sendInfoDto);
            if (baseEntity != null && baseEntity.getResultCode() > 0 && CollectionUtils.isNotEmpty(baseEntity.getData())) {
                return baseEntity.getData();
            }
        }catch (Exception e){
            logger.error("根据箱号:{}查询终端发货明细异常!", boxCode, e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return Lists.newArrayList();
    }
}
