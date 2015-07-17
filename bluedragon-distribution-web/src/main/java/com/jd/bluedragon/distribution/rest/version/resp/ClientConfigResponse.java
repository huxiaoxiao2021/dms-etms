package com.jd.bluedragon.distribution.rest.version.resp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.version.domain.ClientConfig;

public class ClientConfigResponse extends JdResponse {

    private static final long serialVersionUID = -8310677209957145264L;

    private List<ClientConfig> datas = new ArrayList<ClientConfig>();

    public ClientConfigResponse() {
    }

    public ClientConfigResponse(Integer code, String message) {
        super(code, message);
    }

    public List<ClientConfig> getDatas() {
        return datas;
    }

    public void setDatas(List<ClientConfig> datas) {
        Collections.sort(datas, new Comparator<ClientConfig>() {
            public int compare(ClientConfig o1, ClientConfig o2) {
                return (o1.getSiteCode()+o1.getProgramType()).compareTo((o2.getSiteCode()+o2.getProgramType()));
            }
        });
        this.datas = datas;
    }
}
