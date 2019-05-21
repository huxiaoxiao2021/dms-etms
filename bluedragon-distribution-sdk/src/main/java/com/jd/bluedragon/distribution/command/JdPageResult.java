package com.jd.bluedragon.distribution.command;
/**
 * 
 * @ClassName: JdPagerResult
 * @Description: 分页结果集
 * @author: wuyoude
 * @date: 2019年3月20日 上午11:10:43
 * 
 * @param <T> 结果集类型
 */
public class JdPageResult<T> extends JdResult<T>{
	private static final long serialVersionUID = 1L;
	/**
	 * 使用这个构造器需要自行设置total、pageSize、totalPages
	 */
	public JdPageResult() {
		super();
	}
	/**
	 * 数据总数
	 */
	private int total;
	/**
	 * 总页数
	 */
	private int totalPages;
	/**
	 * 分页大小
	 */
	private int pageSize;
	/**
	 * 传入数据总条数、分页大小会自动计算totalPages的值
	 * @param total
	 * @param pageSize
	 */
	public void initPage(int total, int pageSize) {
		this.total = total;
		this.pageSize = pageSize;
		if(total > 0 && pageSize > 0){
			this.totalPages = total/pageSize;
			if(total%pageSize > 0){
				this.totalPages ++;
			}
		}
	}
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
	 * @return the totalPages
	 */
	public int getTotalPages() {
		return totalPages;
	}
	/**
	 * @param totalPages the totalPages to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
