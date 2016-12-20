package com.jd.bluedragon.distribution.gantry.service;

import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.send.domain.SendDetail;

import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/10
 */
public interface GantryDeviceService {
    /**
     * 根据分拣中心编号获取该分拣下面的所有龙门架
     * @param dmsCode 分拣中心ID
     * */
    public List<GantryDevice> getGantryByDmsCode(Integer dmsCode);

    /***
     * 根据条件查询符合条件的龙门架数量
     * @param param 条件
     * @return
     */
    public Integer getGantryCount(Map<String, Object> param);

    /***
     * 根据条件查询符合条件的龙门架
     * @param param 条件
     * @return
     */
    public List<GantryDevice> getGantry(Map<String, Object> param);

    /***
     * 根据条件查询符合条件的龙门架(分页,Create_time倒序)
     * @param param 条件
     * @return
     */
    public List<GantryDevice> getGantryPage(Map<String, Object> param);

    /***
     * 增加龙门架设备
     * @param device
     * @return
     */
    public int addGantry(GantryDevice device);

    /***
     * 根据ID删除龙门架
     * @param id
     * @return
     */
    public int delGantryById(Integer id);

    /***
     * 根据ID更新龙门架
     * @param device
     * @return
     */
    public int updateGantryById(GantryDevice device);

    /**
     * 通过批次号 获取sendD的列表
     */
    public List<SendDetail> queryWaybillsBySendCode(String sendCode);
}
