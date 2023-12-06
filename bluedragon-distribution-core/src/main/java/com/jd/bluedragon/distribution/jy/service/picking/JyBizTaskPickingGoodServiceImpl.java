package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyBizTaskPickingGoodExtendDao;
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
}
