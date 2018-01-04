package com.jd.bluedragon.distribution.receive.service;

import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.receive.domain.Receive;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.List;
import java.util.Map;

public interface ReceiveService {
	/**
	 * 新增一条收货记录
	 * @param receive
	 * @return
	 */
	public boolean add(Receive receive);
	/**
	 * 收货
	 * */
	public void doReceiveing(Receive receive);

	/**
	 * task转化receive
	 * 
	 * @param task
	 * @return
	 */
	public Receive taskToRecieve(Task task);
	/**
	 * 收/发/取消[空周转箱]
	 * @param turnoverBox
	 */
	public void turnoverBoxAdd(TurnoverBox turnoverBox);
	
	/**
	 * 按条件查询[收箱打印]清单集合
	 * @param paramMap
	 * @return
	 */
	public List<Receive> findReceiveJoinList(Map<String, Object> paramMap);
	
	/**
	 * 按条件查询[收箱打印]清单总数
	 * @param paramMap
	 * @return
	 */
	public int findReceiveJoinTotalCount(Map<String, Object> paramMap);

    /**
     *  收货扫描车次号或者扫5个箱号的时候向计费系统发送MQ
     *  @param key 是根据车次号还是箱号查询
     *  @param code 车次号或者箱号
     * */
    public DeparturePrintResponse dellSendMq2ArteryBillingSys(String key, String code ,String message);
}
