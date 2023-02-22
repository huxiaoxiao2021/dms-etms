package com.jd.bluedragon.openplateform;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.openplateform
 * @ClassName: IJYOpenCargoOperate
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/7 01:00
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface IJYOpenCargoOperate {

    InvokeResult<Boolean> inspection(JYCargoOperateEntity entity);
    InvokeResult<Boolean> sorting(JYCargoOperateEntity entity);
    InvokeResult<Boolean> send(JYCargoOperateEntity entity);
}
