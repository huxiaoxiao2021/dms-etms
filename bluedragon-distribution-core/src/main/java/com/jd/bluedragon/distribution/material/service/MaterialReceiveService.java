package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialReceive;

import java.util.List;

/**
 * @ClassName MaterialReceiveService
 * @Description
 * @Author wyh
 * @Date 2020/3/13 17:33
 **/
public interface MaterialReceiveService {

    JdResult<Boolean> saveMaterialReceive(List<DmsMaterialReceive> materialReceives);
}
