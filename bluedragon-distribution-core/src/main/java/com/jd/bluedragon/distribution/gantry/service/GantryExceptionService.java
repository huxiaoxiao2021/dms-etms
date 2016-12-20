package com.jd.bluedragon.distribution.gantry.service;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hanjiaxing1 on 2016/12/8.
 */
public interface GantryExceptionService {
    /***
     * 根据条件查询符合条件的异常信息
     * @param param 条件
     * @return
     */
    public List<GantryException> getGantryException(Map<String, Object> param);

    /***
     * 根据条件查询符合条件的异常信息(分页,Create_time倒序)
     * @param param 条件
     * @return
     */
    public List<GantryException> getGantryExceptionPage(Map<String, Object> param);
    /***
     * 增加异常信息
     * @param gantryException
     * @return
     */
    public int addGantryException(GantryException gantryException);

    /***
     * 根据条件查询符合条件的异常数量
     * @param param 条件
     * @return
     */
    public Integer getGantryExceptionCount(Map<String, Object> param);

    /***
     * 根据条件查询符合条件的异常数量
     * @param machineId 龙门架ID
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public Integer getGantryExceptionCount(Long machineId, Date beginTime, Date endTime);

    /***
     * 更新发货状态
     * @param barCode 条码
     * @return
     */
    public int updateSendStatus(String barCode);


}
