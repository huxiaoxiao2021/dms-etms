package com.jd.bluedragon.external.crossbow.pdd.domain;

/**
 * <p>
 *     拼多多的运单商品简要信息
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
public class PDDGoodsItem {

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品数量
     */
    private Long count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
