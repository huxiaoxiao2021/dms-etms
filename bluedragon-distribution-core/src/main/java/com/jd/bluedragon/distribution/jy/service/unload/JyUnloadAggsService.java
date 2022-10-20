package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2022/10/9 6:35 PM
 */
public interface JyUnloadAggsService {

    int insert(JyUnloadAggsEntity entity);

    List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity);

}
