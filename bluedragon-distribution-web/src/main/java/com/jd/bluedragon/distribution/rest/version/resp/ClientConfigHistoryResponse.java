package com.jd.bluedragon.distribution.rest.version.resp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;

public class ClientConfigHistoryResponse extends JdResponse {

    private static final long serialVersionUID = 1676139518411817836L;

    private List<ClientConfigHistory> datas = new ArrayList<ClientConfigHistory>();

    public ClientConfigHistoryResponse() {
    }

    public ClientConfigHistoryResponse(Integer code, String message) {
        super(code, message);
    }

    public List<ClientConfigHistory> getDatas() {
        return datas;
    }

    public void setDatas(List<ClientConfigHistory> datas) {
        Collections.sort(datas, new Comparator<ClientConfigHistory>() {
            public int compare(ClientConfigHistory o1, ClientConfigHistory o2) {
                return (o1.getSiteCode()+o1.getProgramType()).compareTo((o2.getSiteCode()+o2.getProgramType()));
            }
        });
        this.datas = datas;
    }

}
