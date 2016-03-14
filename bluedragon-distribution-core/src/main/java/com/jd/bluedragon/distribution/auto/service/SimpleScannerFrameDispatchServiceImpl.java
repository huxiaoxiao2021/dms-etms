package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceConfigService;
import com.jd.bluedragon.utils.JsonHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangtingwei on 2016/3/10.
 */
@Service("scannerFrameDispatchService")
public class SimpleScannerFrameDispatchServiceImpl implements ScannerFrameDispatchService {

    private static final Log logger= LogFactory.getLog(SimpleScannerFrameDispatchServiceImpl.class);

    @Autowired
    private GantryDeviceConfigService gantryDeviceConfigService;

    @Qualifier("scannerFrameConsumeMap")
    @Autowired
    private Map<Integer,ScannerFrameConsume> scannerFrameConsumeMap;

    @Override
    public boolean dispatch(UploadData domain) {
        GantryDeviceConfig config= gantryDeviceConfigService.getGantryDeviceOperateType(domain.getRegisterNo(), domain.getScannerTime());
        if(logger.isInfoEnabled()){
            logger.info(MessageFormat.format("获取龙门架操作方式registerNo={0},operateTime={1}|结果{2}",domain.getRegisterNo(),domain.getScannerTime(), JsonHelper.toJson(config)));
        }
        if(null==config){
            if(logger.isWarnEnabled()){
                logger.warn(MessageFormat.format("获取龙门架操作方式registerNo={0},operateTime={1}|结果为NULL",domain.getRegisterNo(),domain.getScannerTime()));
            }
            return true;
        }
        boolean result=false;
        Iterator<Map.Entry<Integer,ScannerFrameConsume>> item= scannerFrameConsumeMap.entrySet().iterator();
        while (item.hasNext()){
            Map.Entry<Integer,ScannerFrameConsume> comsume=item.next();
            if(comsume.getKey()==(config.getOperateType()&comsume.getKey())){
                if(logger.isInfoEnabled()){
                    logger.info(MessageFormat.format("龙门架分发消息registerNo={0},operateTime={1},comsume={2},barcode={3}",domain.getRegisterNo(),domain.getScannerTime(),comsume.getKey(),domain.getBarCode()));
                }
                result= comsume.getValue().onMessage(domain,config);
            }

        }
        return result;
    }


}
