package com.jd.ql.dms.common.web.mvc.api;


/**
 * 
 * @ClassName: PagerCondition
 * @Description: 分页查询条件
 * @author: wuyoude
 * @date: 2017年12月19日 下午6:55:48
 * 
 * @param <E>
 */
public class BasePagerCondition implements PagerCondition{
	private static final long serialVersionUID = 1L;
	/**
	 * 总页数-分页插件回传数据总条数
	 */
	private int total = 0;
	/**
	 * 分页参数-开始值
	 */
	private int offset = 0;
	/**
	 * 分页参数-数据条数
	 */
	private int limit = 10;

    /**
     * 页码
     */
    private int pageNumber;

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
		this.setOffset();
	}

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
        this.setOffset();
    }

    /**
     * 设置分页起始值，自动设置
     */
    public void setOffset() {
        if (this.pageNumber > 0) {
            this.offset = this.limit * (this.pageNumber - 1);
        }
    }

}
