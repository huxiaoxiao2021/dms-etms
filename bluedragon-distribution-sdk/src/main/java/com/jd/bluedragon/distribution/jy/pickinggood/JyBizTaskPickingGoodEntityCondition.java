package com.jd.bluedragon.distribution.jy.pickinggood;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2023/12/7 15:19
 * @Description
 */
public class JyBizTaskPickingGoodEntityCondition extends JyBizTaskPickingGoodEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date createTimeStart;


    public Date getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(Date createTimeStart) {
        this.createTimeStart = createTimeStart;
    }
}
