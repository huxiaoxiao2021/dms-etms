package com.jd.bluedragon.distribution.rest.version.resp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.version.domain.ClientVersion;

public class ClientVersionResponse extends JdResponse {

    private static final long serialVersionUID = -5705658289592703176L;

    private List<ClientVersion> datas = new ArrayList<ClientVersion>();

    public ClientVersionResponse() {
    }

    public ClientVersionResponse(Integer code, String message) {
        super(code, message);
    }

    public List<ClientVersion> getDatas() {
        return datas;
    }

    public void setDatas(List<ClientVersion> datas) {
        Collections.sort(datas, new Comparator<ClientVersion>() {
            public int compare(ClientVersion o1, ClientVersion o2) {
                return (o1.getVersionCode()+o1.getVersionType()).compareTo((o2.getVersionCode()+o2.getVersionType()));
            }
        });
        this.datas = datas;
    }

}
