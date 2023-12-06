package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendRecordDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 空铁提发记录服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:29
 * @Description
 */
@Service
public class JyPickingSendRecordServiceImpl implements JyPickingSendRecordService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendRecordServiceImpl.class);

    @Autowired
    private JyPickingSendRecordDao jyPickingSendRecordDao;
}
