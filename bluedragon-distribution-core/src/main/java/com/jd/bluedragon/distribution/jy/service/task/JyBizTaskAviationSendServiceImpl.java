package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskAviationSendDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author zhengchengfa
 * @Date 2023/8/2 15:08
 * @Description  空铁库表查询服务
 */
public class JyBizTaskAviationSendServiceImpl implements JyBizTaskAviationSendService{

    @Autowired
    private JyBizTaskAviationSendDao jyBizTaskAviationSendDao;
}
