package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 待提明细初始化-取运输封车数据
 * @Author zhengchengfa
 * @Date 2023/12/7 21:40
 * @Description
 */
@Service("PickingGoodDetailInitByTmsSealInfoServiceImpl")
public class PickingGoodDetailInitByTmsSealInfoServiceImpl implements PickingGoodDetailInitService , InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        PickingGoodDetailInitServiceFactory.registerPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.OTHER_SEND_DMS_PICKING.getTargetCode(), this);
    }

    @Override
    public boolean pickingGoodDetailInit(PickingGoodTaskDetailInitDto detailInitDto) {
        return false;
    }
}
