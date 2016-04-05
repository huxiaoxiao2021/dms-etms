package com.jd.bluedragon.distribution.admin.service;

import com.jd.bluedragon.distribution.api.domain.YtWaybillSync;
import com.jd.bluedragon.distribution.api.request.RemoteAccessRequest;
import com.jd.bluedragon.distribution.api.response.RemoteAccessResponse;

import java.util.List;

/**
 * Created by yangbo7 on 2016/3/28.
 */
public interface RemoteAccessService {


    RemoteAccessResponse<List<YtWaybillSync>> findListByWaybillCode(RemoteAccessRequest request);

}
