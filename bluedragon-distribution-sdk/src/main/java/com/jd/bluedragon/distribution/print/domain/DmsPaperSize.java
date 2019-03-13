package com.jd.bluedragon.distribution.print.domain;

/**
 * 
 * @ClassName: DmsPaperSize
 * @Description: 分拣纸张大小定义
 * @author: wuyoude
 * @date: 2019年3月11日 下午6:09:54
 *
 */
public enum DmsPaperSize {
	/**
	 * 标签尺寸10*5
	 */
	PAPER_SIZE_1005("1005", "标签尺寸10*5"),
	/**
	 * 标签尺寸10*10
	 */
	PAPER_SIZE_1010("1010", "标签尺寸10*10"),
	/**
	 * 标签尺寸10*11
	 */
	PAPER_SIZE_1011("1011", "标签尺寸10*11");
    /**
     * 编码
     */
    private String paperSizeCode;
    /**
     * 名称
     */
    private String paperSizeName;

    private DmsPaperSize(String paperSizeCode, String paperSizeName) {
		this.paperSizeCode = paperSizeCode;
		this.paperSizeName = paperSizeName;
	}
    /**
     * 标签尺寸编码-1005
     */
    public static String PAPER_SIZE_CODE_1005 = PAPER_SIZE_1005.paperSizeCode;
    /**
     * 标签尺寸编码-1010
     */
    public static String PAPER_SIZE_CODE_1010 = PAPER_SIZE_1010.paperSizeCode;
    /**
     * 标签尺寸编码-1011
     */
    public static String PAPER_SIZE_CODE_1011 = PAPER_SIZE_1011.paperSizeCode;

	/**
	 * @return the paperSizeCode
	 */
	public String getPaperSizeCode() {
		return paperSizeCode;
	}
	/**
	 * @return the paperSizeName
	 */
	public String getPaperSizeName() {
		return paperSizeName;
	}
}
