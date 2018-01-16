package com.jd.bluedragon.distribution.sysloginlog.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.sysloginlog.domain.SysLoginLog;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;

import java.util.List;
import java.util.Map;

/**
 * 登录日志dao
 * Created by shipeilin on 2018/1/16.
 */
public class SysLoginLogDao extends BaseDao<SysLoginLog> {
    public static final String namespace = SysLoginLogDao.class.getName();

    /**
     * 新增异常操作记录
     * @param sysLoginLog
     * @return
     */
    public int insert(SysLoginLog sysLoginLog){
        return super.getSqlSession().insert(namespace + ".insert" , sysLoginLog);
    }

}
