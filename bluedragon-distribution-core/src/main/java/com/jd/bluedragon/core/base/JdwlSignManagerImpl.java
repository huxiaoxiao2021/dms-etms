package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineModalDTO;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jdwl.bcoi.entry.api.dto.ActionSlimDtoRe;
import com.jdwl.bcoi.entry.api.dto.ActionSlimDtoRs;
import com.jdwl.bcoi.entry.api.dto.PrintJobDTO;
import com.jdwl.bcoi.entry.api.dto.SystemInfo;
import com.jdwl.bcoi.entry.api.service.JdwlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("jdwlSignManager")
public class JdwlSignManagerImpl implements JdwlSignManager {

    private static Long SYSTEM_LOG_TYPE = 12004L;

    private Logger log = LoggerFactory.getLogger(JdwlSignManagerImpl.class);

    @Autowired
    @Qualifier("jdwlSignServiceJsf")
    private JdwlService jdwlSignServiceJsf;

    @Value("${print.online.ns:ERP}")
    private String ns;
    @Value("${print.online.app.code:NXJJ}")
    private String appCode;
    @Value("${print.online.template.id:NXJJD}")
    private String templateId;
    @Value("${print.online.oper.type:Gen}")
    private String operType;
    @Value("${print.online.pin.code:liuduo8}")
    private String pinCode;
    @Value("${print.online.token:XXXX}")
    private String token;
    @Value("${print.online.system.flg:JDWL_DMS}")
    private String systemFlg;
    @Value("${print.online.out.seqNo.begin:JDWL_DMS}")
    private String outSeqNoBegin;

    @Override
    public boolean aciton(PrintOnlineModalDTO printOnlineModalDTO) {

        ActionSlimDtoRs actionSlimDtoRs = new ActionSlimDtoRs();
        try{
            ActionSlimDtoRe actionSlimDtoRe = new ActionSlimDtoRe();

            actionSlimDtoRe.setNs(ns);
            actionSlimDtoRe.setAppcode(appCode);
            actionSlimDtoRe.setTemplateid(templateId);
            actionSlimDtoRe.setOper_type(operType);
            actionSlimDtoRe.setPin_code(pinCode);

            //系统标识
            SystemInfo systemInfo = new SystemInfo();
            systemInfo.setToken(token);
            systemInfo.setSystemflg(systemFlg);
            systemInfo.setOut_seqno(outSeqNoBegin + printOnlineModalDTO.getSendCode());
            actionSlimDtoRe.setSystemInfo(systemInfo);

            //云打印结构
            PrintJobDTO printJobDTO = new PrintJobDTO();
            JSONObject model = (JSONObject)JSONObject.toJSON(printOnlineModalDTO);
            printJobDTO.setModel(model);
            printJobDTO.setOrderNum(printOnlineModalDTO.getSendCode()); //实际业务的订单号
            printJobDTO.setTime(System.currentTimeMillis());
            printJobDTO.setSys(systemFlg);
            actionSlimDtoRe.setPrintJobDTO(printJobDTO);
            actionSlimDtoRs =jdwlSignServiceJsf.action(actionSlimDtoRe);
            if(actionSlimDtoRs.getResult()){
                return true;
            }else{
                log.error("调用线上签传输数据接口失败{} 返回结果：{}",JsonHelper.toJson(printOnlineModalDTO),JsonHelper.toJson(actionSlimDtoRs));
                return false;
            }

        }catch (Exception e){
            log.error("调用线上签传输数据接口异常:{}", JsonHelper.toJson(printOnlineModalDTO),e);
            return false;
        }finally {
            pushSystemLog(printOnlineModalDTO.getSendCode(),actionSlimDtoRs);
        }

    }

    /**
     * 记录日志
     * @param sendCode
     * @param actionSlimDtoRs
     */
    private void pushSystemLog(String sendCode, ActionSlimDtoRs actionSlimDtoRs){
        try{
            //增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(sendCode);
            sLogDetail.setKeyword2(sendCode);
            sLogDetail.setKeyword3("PrintOnline");
            if(actionSlimDtoRs == null){
                sLogDetail.setKeyword4(Long.valueOf(Constants.RESULT_ERROR));
            }else{
                sLogDetail.setKeyword4(Long.valueOf(actionSlimDtoRs.getCode()));
            }
            sLogDetail.setType(SYSTEM_LOG_TYPE);
            sLogDetail.setContent(actionSlimDtoRs.getMsg());
            SystemLogUtil.log(sLogDetail);
        }catch (Exception e){
            log.error("JdwlSignManagerImpl.pushSystemLog",e);
        }
    }
}
