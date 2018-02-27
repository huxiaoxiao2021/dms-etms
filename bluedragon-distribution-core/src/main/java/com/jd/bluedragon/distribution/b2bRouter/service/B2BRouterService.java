package com.jd.bluedragon.distribution.b2bRouter.service;

import com.jd.bluedragon.distribution.b2bRouter.domain.B2BRouter;

/**
 * Created by xumei3 on 2018/2/26.
 */
public interface B2BRouterService {
    public boolean isHasRouter(B2BRouter router);

    public Integer addRouter(B2BRouter router);
}
