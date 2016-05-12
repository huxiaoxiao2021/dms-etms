package com.jd.bluedragon.distribution.asynBuffer.service;

import IceInternal.Ex;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendService;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/5/12
 */
@Service("asynBufferSendProcessor")
public class AsynBufferSendProcessor implements AsynBufferProcessor {
    @Autowired
    private ReverseSendService reverseSendService;
    @Override
    public Boolean doTaskProcess(Task task) throws Exception{
        Boolean processResult = Boolean.FALSE;
        switch(Integer.valueOf(task.getKeyword1()).intValue()){
            case 1 : break;
            case 2 : break;
            case 3 : break;
            case 4 : processResult = reverseSendService.findSendwaybillMessage(task);
            case 5 : break;
            default: break;
        }
        return processResult;
    }
}
