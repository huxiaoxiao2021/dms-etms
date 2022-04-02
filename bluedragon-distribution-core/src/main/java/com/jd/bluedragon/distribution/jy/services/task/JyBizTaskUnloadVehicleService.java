package com.jd.bluedragon.distribution.jy.services.task;

import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;

import java.util.List;
import java.util.Map;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/1
 * @Description:  到车卸车任务服务类
 */
public interface JyBizTaskUnloadVehicleService {


    /**
     * 按状态分组返回 统计 总数
     *
     * @param condition 条件 车牌后四位 封车编码  目的场地（必填）状态集合
     * @return
     */
    Map<JyBizTaskUnloadStatusEnum, Integer> findStatusCountByCondition4Status(JyBizTaskUnloadVehicleEntity condition,JyBizTaskUnloadStatusEnum... enums);


    /**
     * 按状态和线路 分组返回 统计 总数
     *
     * @param condition 条件 车牌后四位 封车编码  目的场地（必填）状态集合
     * @return
     */
    Map<JyBizTaskUnloadStatusEnum, Map<JyLineTypeEnum, Integer>> findStatusCountByCondition4StatusAndLine(JyBizTaskUnloadVehicleEntity condition,JyBizTaskUnloadStatusEnum... enums);


    /**
     * 分页返回数据 集合（最大支持滚动到200条数据）
     *
     * @param condition 车牌后四位 封车编码  目的场地（必填）状态 线路
     * @param typeEnum  排序类型
     * @param pageNum   页码  不小于0
     * @param pageSize  每页数量
     * @return
     */
    List<JyBizTaskUnloadVehicleEntity> findByConditionOfPage(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadOrderTypeEnum typeEnum, Integer pageNum, Integer pageSize);

    /**
     * 改变状态 只支持完成
     * @param entity 业务主键 必填 状态 必填 修改人必填 修改时间必填
     * @return
     */
    boolean changeStatus(JyBizTaskUnloadVehicleEntity entity);

    /**
     * 创建业务任务
     *
     * 无任务 和 任务 模式 通用服务
     *
     * 必填字段以下：
     * 封车编码
     * 车牌号
     * 始发场地ID
     * 始发场地名称
     * 目的场地ID
     * 目的场地名称
     * 是否无任务卸车；1：是：0：否
     *
     * @param entity
     * @return
     */
    boolean createUnloadTask(JyBizTaskUnloadVehicleEntity entity);

    /**
     * 保存或更新基础信息 注:字段未NULL时不更新此字段
     * 包含以下业务字段
     * 业务主键
     * 封车编码
     * 车牌号
     * 派车明细编码
     * 模糊查询车牌号（自动从车牌号截取）
     * 任务状态
     * 是否无任务卸车
     * 始发场地ID
     * 始发场地名称
     * 目的场地ID
     * 目的场地名称
     *
     * @param entity
     * @return
     */
    boolean saveOrUpdateOfBaseInfo(JyBizTaskUnloadVehicleEntity entity);

    /**
     * 保存或更新其他业务信息
     * 包含以下业务字段
     * 业务主键
     * <p>
     * 排除saveOrUpdateOfBaseInfo字段
     *
     * @param entity
     * @return
     */
    boolean saveOrUpdateOfOtherBusinessInfo(JyBizTaskUnloadVehicleEntity entity);

    /**
     * 检查当前卸车任务是否被锁定
     * 支持自旋，操作锁异常抛出异常调用者自行处理
     *
     * @param bizId 业务主键 本任务等于封车编码
     * @return
     */
    boolean isLocked(String bizId);


    /**
     * 锁定本任务
     * 支持自旋，操作锁异常抛出异常调用者自行处理
     *
     * @param bizId 业务主键 本任务等于封车编码
     * @return
     */
    boolean locked(String bizId);

    /**
     * 解除锁定本任务
     * 支持自旋，操作锁异常抛出异常调用者自行处理
     *
     * @param bizId 业务主键 本任务等于封车编码
     * @return
     */
    boolean unLocked(String bizId);
}
