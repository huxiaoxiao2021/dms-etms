package com.jd.bluedragon.distribution.jy.pickinggood;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/12/22 13:50
 * @Description
 */
public class JyPickingTaskSendAggsCondition extends JyPickingTaskSendAggsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> bizIdList;

    public List<String> getBizIdList() {
        return bizIdList;
    }

    public void setBizIdList(List<String> bizIdList) {
        this.bizIdList = bizIdList;
    }
}
