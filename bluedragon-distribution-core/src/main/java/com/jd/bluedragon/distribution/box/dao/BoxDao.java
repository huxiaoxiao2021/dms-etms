package com.jd.bluedragon.distribution.box.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.box.domain.Box;

import java.util.List;

public class BoxDao extends BaseDao<Box> {

    public static final String namespace = BoxDao.class.getName();

    public Integer reprint(Box box) {
        return super.getSqlSession().update(BoxDao.namespace + ".reprint", box);
    } 
    
    public Box findBoxByCode(String code) {
        return (Box) super.getSqlSession().selectOne(BoxDao.namespace + ".findBoxByCode", code);
    }

    public Box findBoxByBoxCode(Box box) {
        return (Box) super.getSqlSession().selectOne(BoxDao.namespace + ".findBoxByBoxCode", box);
    }

    public Integer updateVolumeByCode(Box box) {
        return super.getSqlSession().update(BoxDao.namespace + ".updateVolumeByCode", box);
    }

    /*根据boxCode 更新create_site_name，receive_site_name 用于还原因jproxy故障造成的箱号站点乱码 add by luyue*/
    public int updateMessySiteNameByBoxCode(Box box){
        return super.getSqlSession().update(BoxDao.namespace + ".updateMessySiteNameByBoxCode", box);
    }

}
