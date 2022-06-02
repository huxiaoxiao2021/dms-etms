package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName ToSealDestAgg
 * @Description
 * @Author wyh
 * @Date 2022/5/19 18:25
 **/
public class ToSealDestAgg implements Serializable {
    private static final long serialVersionUID = -8374301449951442972L;

    /**
     * 封车的流向数量
     */
    private Integer sealedTotal;

    /**
     * 流向总数
     */
    private Integer destTotal;

    /**
     * 发货流向明细
     */
    private List<ToSealDestDetail> destList;
}
