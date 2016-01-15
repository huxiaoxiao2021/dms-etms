package com.jd.bluedragon.distribution.sorting.service;

import java.util.List;

import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;

public interface SortingService {
	
	public static final Integer SORTING_CANCEL = 1;
	public static final Integer SORTING_CANCEL_NORMAL = 0;

    Integer add(Sorting sorting);

    Integer update(Sorting sorting);

    void addSortingAndSendDetail(Sorting sorting);

    /** 任务转分拣记录 
     * @return */
    boolean doSorting(Task task);

    /** 通过操作站点编号、包裹号码，查询对应分拣信息 */
    List<Sorting> findSortingPackages(Sorting sorting);

    /** 通过操作站点编号、箱号，查询对应分拣信息 */
    List<Sorting> findByBoxCode(Sorting sorting);

    /** 通过创建站点、包裹号码或运单号码、业务类型，判断其分拣记录及验货异常能否被取消 */
    Boolean canCancel(Sorting sorting);
    
    /** 通过创建站点、包裹号码或运单号码、业务类型，判断其分拣记录能否被取消 */
    Boolean canCancelSorting(Sorting sorting);

    /** 通过创建站点、业务类型，模糊匹配的包裹号码，判断其分拣记录能否被取消 */
	Boolean canCancelSortingFuzzy(Sorting sorting);
    
    /** 通过操作站点编号、箱号，查询对应分拣信息dms报表 */
    List<Sorting> findOrderDetail(Sorting sorting);
    
    /** 通过操作站点编号、箱号，查询对应分拣信息dms报表 */
    List<Sorting> findOrder(Sorting sorting);

    /**
     * 根据箱号获取包裹信息
     * @param boxCode
     * @return
     */
    public int findBoxPack(Integer createSiteCode, String boxCode);
    
    /**
     * 根据箱号获取包裹分拣目的站点信息
     * @param boxCode
     * @return
     */
    public Sorting findBoxDescSite(Integer createSiteCode, String boxCode);
    
    /**
     * 根据订单号或包裹号查询箱号包裹信息
     * @param sorting
     * @return
     */
    public List<Sorting> findBoxPackList(Sorting sorting);
    
    /**
     * 根据包裹号查询是否存在数据
     * @param sorting
     * @return
     */
    public boolean existSortingByPackageCode(Sorting sorting);
    
    /**
     * 根据包裹号或者运单号查询箱子、create_site_code、receive_site_code
     * @param sorting
     * @return
     */
	List<Sorting> queryByCode(Sorting sorting);
    
	/**
	 * 加入日志
	 * @param sorting
	 * @param logType
	 */
	public void addOpetationLog(Sorting sorting, Integer logType);

	public void addOpetationLog(Sorting sorting, Integer logType, String remark);

	public boolean taskToSorting(List<Sorting> sortings);
	
	 /**
     * 根据批量批次号查询所有箱号
     * @param sorting
     * @return
     */
    public List<Sorting> findByBsendCode(Sorting sorting);
}
