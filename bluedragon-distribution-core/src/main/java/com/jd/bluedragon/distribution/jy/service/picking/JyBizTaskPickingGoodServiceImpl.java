package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.picking.req.PickingGoodsReq;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodExtendDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntityCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 空铁提货任务服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:13
 * @Description
 */
@Service
public class JyBizTaskPickingGoodServiceImpl implements JyBizTaskPickingGoodService{

    private static final Logger log = LoggerFactory.getLogger(JyBizTaskPickingGoodServiceImpl.class);

    @Autowired
    private JyBizTaskPickingGoodDao jyBizTaskPickingGoodDao;
    @Autowired
    private JyBizTaskPickingGoodExtendDao jyBizTaskPickingGoodExtendDao;

    @Override
    public JyBizTaskPickingGoodEntity findByBizIdWithYn(String bizId, boolean ignoreYn) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBizId(bizId);
        if(!ignoreYn) {
            entity.setYn(Constants.YN_YES);
        }
        return jyBizTaskPickingGoodDao.findByBizIdWithYn(entity);
    }

    @Override
    public JyBizTaskPickingGoodEntity generateManualCreateTask(PickingGoodsReq request) {
        //todo zcf
        return null;
    }

    @Override
    public boolean updateStatusByBizId(String bizId, Integer status) {
        JyBizTaskPickingGoodEntity entity = new JyBizTaskPickingGoodEntity();
        entity.setBizId(bizId);
        entity.setStatus(status);
        return jyBizTaskPickingGoodDao.updateStatusByBizId(entity);
    }
}
