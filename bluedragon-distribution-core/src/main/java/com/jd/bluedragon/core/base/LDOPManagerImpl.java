package com.jd.bluedragon.core.base;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.reverse.WaybillReverseApi;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 外单 jsf接口 包装类
 */
@Service("ldopManager")
public class LDOPManagerImpl implements LDOPManager {

    @Autowired
    private WaybillReverseApi waybillReverseApi;

    private final Logger logger = Logger.getLogger(LDOPManagerImpl.class);
    /**
     * 触发外单逆向换单接口
     * @param waybillReverseDTO
     * @return
     */
    public boolean waybillReverse(WaybillReverseDTO waybillReverseDTO){

        //body中是批次号,号分割
        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.LDOPManagerImpl.waybillReverse",false, true);

            ResponseDTO responseDTO = waybillReverseApi.waybillReverse(waybillReverseDTO);
            if(!responseDTO.getStatusCode().equals(ResponseDTO.SUCCESS_CODE)){
                //失败
                logger.error("触发逆向换单失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+responseDTO.getStatusMessage());
                return false;
            }

            return true;
        }catch (Exception e){
            logger.error("触发逆向换单失败,入参："+ JsonHelper.toJson(waybillReverseDTO)+"  失败原因："+e.getMessage());
            Profiler.functionError(info);
            return false;
        }finally{
            Profiler.registerInfoEnd(info);
        }

    }
}
