package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineModalDTO;
import com.jd.bluedragon.utils.JsonHelper;
import com.jdwl.bcoi.entry.api.dto.ActionSlimDtoRe;
import com.jdwl.bcoi.entry.api.dto.ActionSlimDtoRs;
import com.jdwl.bcoi.entry.api.dto.PrintJobDTO;
import com.jdwl.bcoi.entry.api.dto.SystemInfo;
import com.jdwl.bcoi.entry.api.service.JdwlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("jdwlSignManager")
public class JdwlSignManagerImpl implements JdwlSignManager {

    private Logger logger = LoggerFactory.getLogger(JdwlSignManagerImpl.class);

    @Autowired
    @Qualifier("jdwlSignServiceJsf")
    private JdwlService jdwlSignServiceJsf;

    @Override
    public boolean aciton(PrintOnlineModalDTO printOnlineModalDTO) {
        try{
            ActionSlimDtoRe actionSlimDtoRe = new ActionSlimDtoRe();

            actionSlimDtoRe.setNs("ERP");
            actionSlimDtoRe.setAppcode("NXJJ");
            actionSlimDtoRe.setTemplateid("NXJJD");
            actionSlimDtoRe.setOper_type("Gen");
            actionSlimDtoRe.setPin_code("liuduo8");

            //系统标识
            SystemInfo systemInfo = new SystemInfo();
            systemInfo.setToken("XXXX");
            systemInfo.setSystemflg("JDWL_DMS");
            systemInfo.setOut_seqno("JDWL_DMS_" + System.currentTimeMillis());
            actionSlimDtoRe.setSystemInfo(systemInfo);

            //云打印结构
            PrintJobDTO printJobDTO = new PrintJobDTO();
            JSONObject model = (JSONObject)JSONObject.toJSON(printOnlineModalDTO);
            printJobDTO.setModel(model);
            printJobDTO.setOrderNum(printOnlineModalDTO.getSendCode()); //实际业务的订单号
            printJobDTO.setTime(System.currentTimeMillis());
            printJobDTO.setSys("JDWL_DMS");
            actionSlimDtoRe.setPrintJobDTO(printJobDTO);
            ActionSlimDtoRs actionSlimDtoRs =jdwlSignServiceJsf.action(actionSlimDtoRe);
            if(actionSlimDtoRs.getResult()){
                return true;
            }else{
                logger.error("调用线上签传输数据接口失败"+ JsonHelper.toJson(printOnlineModalDTO)+" 返回结果："+JsonHelper.toJson(actionSlimDtoRs));
                return false;
            }

        }catch (Exception e){
            logger.error("调用线上签传输数据接口异常"+ JsonHelper.toJson(printOnlineModalDTO),e);
            return false;
        }

    }
}
