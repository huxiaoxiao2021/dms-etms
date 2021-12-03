package com.jd.bluedragon.distribution.record.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;
import com.jd.bluedragon.distribution.record.vo.WaybillHasnoPresiteRecordVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 */
public interface WaybillHasnoPresiteRecordService{

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     */
    Response<Long> selectCount(WaybillHasnoPresiteRecordQo query);

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     */
    Response<List<WaybillHasnoPresiteRecord>> selectList(WaybillHasnoPresiteRecordQo query);

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     */
    Response<PageDto<WaybillHasnoPresiteRecordVo>> selectPageList(WaybillHasnoPresiteRecordQo query);
    /**
     * 按运单查询
     * @param waybillCode
     * @return
     */
    WaybillHasnoPresiteRecordVo selectByWaybillCode(String waybillCode);
    
    /**
     * 同步mq数据到数据库
     * @param dmsHasnoPresiteWaybillMq
     * @return
     */
	boolean syncMqDataToDb(DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq);
    /**
     * 发送mq消息
     * @param dmsHasnoPresiteWaybillMq
     * @return
     */
	boolean sendDataChangeMq(DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq);
	/**
	 * 定时扫描未处理的数据
	 * @return
	 */
	boolean doScan();
}
