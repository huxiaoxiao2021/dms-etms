package com.jd.bluedragon.distribution.sorting.service;

import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import java.util.List;
import java.util.Map;

public interface SortingService {
	
	public static final Integer SORTING_CANCEL = 1;
	public static final Integer SORTING_CANCEL_NORMAL = 0;

    Integer add(Sorting sorting);

    Integer update(Sorting sorting);

    /** 任务转分拣记录 
     * @return */
    boolean doSorting(Task task);

    /** 通过操作站点编号、包裹号码，查询对应分拣信息 */
//    List<Sorting> findSortingPackages(Sorting sorting);

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
     * 根据包裹号或者运单号查询箱子、create_site_code、receive_site_code
     * added by zhanglei 重新加入一个新方法 取消分拣这里需要特殊处理一下
     * @param sorting
     * @return
     */
    List<Sorting> queryByCode2(Sorting sorting);

    /** 执行取消文件操作  这里不再判断业务类型是正向还是逆向等  added by zhanglei */
    Boolean canCancel2(Sorting sorting);
    
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

    /**
     * 添加回传分拣的运单状态
     * @param sorting
     */
    public void addSortingAdditionalTask(Sorting sorting);

    /**
     * 根据包裹号，当前站点查询所有分拣记录
     * @param createSiteCode
     * @param packageCode
     * @return
     */
    public List<Sorting>  findByPackageCode(Integer createSiteCode, String packageCode);
    /**
     * 根据箱号，当前站点查询有限的分拣记录
     * @param boxCode
     * @param createSiteCode
     * @param fetchNum
     * @return
     */
    public List<Sorting>  findByBoxCodeAndFetchNum(String boxCode, int createSiteCode, int fetchNum);

    /**
     * 根据运单号或者包裹号，当前站点查询所有分拣记录
     * @param createSiteCode
     * @param waybillCode
     * @param packageCode
     * @return
     */
    public List<Sorting> findByWaybillCodeOrPackageCode(Integer createSiteCode,String waybillCode, String packageCode);

    /**分页查询分拣任务*/
    public List<Sorting> findPageSorting(Map<String,Object> params);

    /**
     * 处理任务数据
     * @param task
     * @return 成功与否
     */
    boolean processTaskData(Task task);

    SendDetail addSendDetail(Sorting sorting);

    void fixSendDAndSendTrack(Sorting sorting, List<SendDetail> sendDs);

    void fillSortingIfPickup(Sorting sorting);

    boolean useNewSorting(Integer siteCode);

    /**
     * 根据箱号获取运单号列表
     *
     * @param boxCode
     * @return
     */
    List<String> getWaybillCodeListByBoxCode(String boxCode);

}
