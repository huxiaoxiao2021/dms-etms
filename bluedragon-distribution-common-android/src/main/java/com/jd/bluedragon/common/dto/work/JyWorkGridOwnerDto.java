package com.jd.bluedragon.common.dto.work;

import java.io.Serializable;

public class JyWorkGridOwnerDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 网格组长erp
     * @return
     */
    private String erp;
    /**
     * 网格组长name
     * @return
     */
    private String name;


    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
