package com.jd.bluedragon.distribution.consumer.gantry;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.gantry.domain.GantryResidentDto;
import com.jd.bluedragon.distribution.gantry.exception.GantryResidentException;
import com.jd.bluedragon.distribution.gantry.service.GantryResidentScanService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 龙门架驻厂扫描消费
 *
 * @author: hujiping
 * @date: 2020/10/22 16:54
 */
@Service("gantryResidentScanConsumer")
public class GantryResidentScanConsumer extends MessageBaseConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GantryResidentScanService gantryResidentScanService;

    @Override
    public void consume(Message message) throws Exception {

        logger.info("GantryResidentScanConsumer consume --> 消息Body为【{}】", message.getText());
        try {
            //反序列化
            GantryResidentDto gantryResidentDto = JsonHelper.fromJson(message.getText(), GantryResidentDto.class);
            // 参数校验
            if(checkParam(gantryResidentDto)){
                return;
            }
            // 龙门架驻厂扫描逻辑处理
            gantryResidentScanService.dealLogic(gantryResidentDto);
        }catch (GantryResidentException e){
            throw new GantryResidentException(e.getMessage());
        }catch (Exception e){
            logger.error("龙门架驻厂扫描处理异常!", e);
        }

    }

    /**
     * 必要参数校验
     * @param dto
     * @return
     */
    private boolean checkParam(GantryResidentDto dto) {
        if(dto == null){
            logger.warn("龙门架驻厂扫描消息转换对象为空!");
            return true;
        }
        if(StringUtils.isEmpty(dto.getBoxCode())){
            logger.warn("龙门架驻厂扫描消息【扫描箱号为空】");
            return true;
        }
        if(!WaybillUtil.isPackageCode(dto.getBarCode())){
            logger.warn("龙门架驻厂扫描消息【扫描条码非包裹号】");
            return true;
        }
        if(dto.getWeight() == null || dto.getVolume() == null
                || dto.getHeight() == null || dto.getWidth() == null || dto.getLength() == null){
            logger.warn("龙门架驻厂扫描消息【称重量方数据为空】");
            return true;
        }
        if(dto.getOperateSiteCode() == null || dto.getOperatorErp() == null){
            logger.warn("龙门架驻厂扫描消息【操作人信息为空】");
            return true;
        }
        if(StringUtils.isEmpty(dto.getOperateTime()) || DateHelper.parseDateTime(dto.getOperateTime()) == null){
            logger.warn("龙门架驻厂扫描消息【操作人时间不正确】");
            return true;
        }
        return false;
    }

}
