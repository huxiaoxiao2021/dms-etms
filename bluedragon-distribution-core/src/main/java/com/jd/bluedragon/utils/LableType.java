package com.jd.bluedragon.utils;

/**
 * 打印标签按纸化
 * @author yanghongqiang
 *
 */
public enum LableType {
    /**有纸化标签*/
    PAPER(0),

    /**无纸化标签*/
    PAPERLESS(1);

    private final Integer labelPaper;

    public Integer getLabelPaper() {
        return labelPaper;
    }

    private LableType(Integer labelPaper){
        this.labelPaper = labelPaper;
    }
}