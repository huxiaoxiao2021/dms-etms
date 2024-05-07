package com.jd.bluedragon.core.jsf.jyJobType;

import com.jdl.basic.api.domain.jyJobType.JyJobType;

import java.util.List;

/**
 * @author pengchong28
 * @description 拣运工种服务接口
 * @date 2024/2/25
 */
public interface JyJobTypeManager {
    List<JyJobType> getAllAvailable();

    List<JyJobType> getListByCondition(JyJobType query);

}
