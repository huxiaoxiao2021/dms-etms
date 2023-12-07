package com.jd.bluedragon.distribution.jy.service.picking.bridge;

import com.jd.bluedragon.distribution.jy.constants.PickingGoodTaskDetailInitServiceEnum;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskDetailInitDto;
import com.jd.bluedragon.distribution.jy.service.picking.factory.PickingGoodDetailInitServiceFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 待提明细初始化-取分拣发货数据
 * @Author zhengchengfa
 * @Date 2023/12/7 21:42
 * @Description
 */
@Service("PickingGoodDetailInitByDmsSendInfoServiceImpl")
public class PickingGoodDetailInitByDmsSendInfoServiceImpl implements PickingGoodDetailInitService, InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        PickingGoodDetailInitServiceFactory.registerPickingGoodDetailInitService(PickingGoodTaskDetailInitServiceEnum.DMS_SEND_DMS_PICKING.getTargetCode(), this);
    }

    @Override
    public boolean pickingGoodDetailInit(PickingGoodTaskDetailInitDto detailInitDto) {
        return false;
    }


}
