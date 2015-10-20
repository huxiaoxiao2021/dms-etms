package com.jd.bluedragon;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-31 下午05:55:10
 *
 *             分页对象
 */
public class Pager<T> {
	/**
	 * 默认初始页
	 */
	public final static int DEFAULT_PAGE_NO = 1;
	/**
	 * 默认每页显示数
	 */
	public final static int DEFAULT_PAGE_SIZE = 20;
	public static final int DEFAULT_POP_PAGE_SIZE = 1000;
	/**
	 * 分页-开始索引标识
	 */
	public static final String STRING_START_INDEX = "startIndex";
	/**
	 * 分页-结束索引标识
	 */
	public static final String STRING_END_INDEX = "endIndex";
	/**
	 * 开始索引
	 */
	private Integer startIndex;
	/**
	 * 结束索引
	 */
	private Integer endIndex;
	/**
	 * 当前页面
	 */
	private Integer pageNo = DEFAULT_PAGE_NO;
	/**
	 * 每页最大显示条数
	 */
	private Integer pageSize = DEFAULT_PAGE_SIZE;
	/**
	 * 总共页数
	 */
	private Integer totalNo;
	/**
	 * 总共条数
	 */
	private Integer totalSize;

	/**
	 * 数据集合
	 */
	private T data;

	private String tableName;

	public Pager() {

	}
    public void init(){
        this.startIndex = (this.pageNo - 1) * this.pageSize;
        this.endIndex = this.pageNo * this.pageSize;
    }

	public Pager(Integer pageNo) {
		this(pageNo, DEFAULT_PAGE_SIZE);
	}

    public Pager(Pager copyFrom) {
       this(copyFrom.getPageNo(),copyFrom.getPageSize());
    }
	public Pager(Integer pageNo, Integer pageSize) {
		this.pageNo = (pageNo != null && pageNo > 1) ? pageNo : DEFAULT_PAGE_NO;
		this.pageSize = (pageSize != null && pageSize > 0) ? pageSize : DEFAULT_PAGE_SIZE;
		this.startIndex = (this.pageNo - 1) * this.pageSize;
		this.endIndex = this.pageNo * this.pageSize;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTotalNo() {
		return totalNo;
	}

	public void setTotalNo(Integer totalNo) {
		this.totalNo = totalNo;
	}

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
		if (totalSize != null && totalSize > 0) {
			if (this.totalSize % this.pageSize == 0) {
				this.totalNo = this.totalSize / this.pageSize;
			} else {
				this.totalNo = this.totalSize / this.pageSize + 1;
			}
		}
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
