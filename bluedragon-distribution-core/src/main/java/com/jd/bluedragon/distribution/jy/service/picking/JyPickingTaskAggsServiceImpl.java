package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskAggsDao;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingTaskSendAggsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 空铁提货统计层服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:16
 * @Description
 */
@Service
public class JyPickingTaskAggsServiceImpl implements JyPickingTaskAggsService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingTaskAggsServiceImpl.class);

    @Autowired
    private JyPickingTaskAggsDao jyPickingTaskAggsDao;
    @Autowired
    private JyPickingTaskSendAggsDao jyPickingTaskSendAggsDao;
}
