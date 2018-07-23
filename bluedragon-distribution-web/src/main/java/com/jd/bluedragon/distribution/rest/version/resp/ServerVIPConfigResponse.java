package com.jd.bluedragon.distribution.rest.version.resp;

import com.jd.bd.dms.automatic.sdk.modules.dmslocalserverinfo.entity.VipInfoJsfEntity;
import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lixin39 on 2018/7/17.
 */
public class ServerVIPConfigResponse extends JdResponse {

    private static final long serialVersionUID = 1L;

    private List<VipInfoJsfEntity> datas = new ArrayList<VipInfoJsfEntity>();

    public ServerVIPConfigResponse() {
    }

    public ServerVIPConfigResponse(Integer code, String message) {
        super(code, message);
    }

    public List<VipInfoJsfEntity> getDatas() {
        return datas;
    }

    public void setDatas(List<VipInfoJsfEntity> datas) {
        this.datas = datas;
    }
}
