package com.jd.bluedragon.distribution.test;

import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;

public class KuGuanDataUtil {

    //先款支付类型
    public static KuGuanDomain XKZF_TYPE = new KuGuanDomain();

    static{
        XKZF_TYPE.setLblWay("出库");
        XKZF_TYPE.setLblType("放货");
        XKZF_TYPE.setLblOther("0");

    }

}
