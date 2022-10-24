package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/10/9 6:35 PM
 */
@Service("jyUnloadAggsService")
public class JyUnloadAggsServiceImpl implements JyUnloadAggsService {

    @Autowired
    private JyUnloadAggsDao jyUnloadAggsDao;

    @Override
    public int insert(JyUnloadAggsEntity entity) {
        return jyUnloadAggsDao.insert(entity);
    }

    @Override
    public List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity) {
        return jyUnloadAggsDao.queryByBizId(entity);
    }
}
