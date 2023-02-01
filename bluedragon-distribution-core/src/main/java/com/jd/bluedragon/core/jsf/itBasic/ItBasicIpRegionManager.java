package com.jd.bluedragon.core.jsf.itBasic;

import com.jd.dms.java.utils.sdk.base.PageData;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jdl.basic.api.domain.itBasic.ItBasicStorageIpRegion;
import com.jdl.basic.api.dto.itBasic.dto.MatchIpRegionDto;
import com.jdl.basic.api.dto.itBasic.po.ItBasicIpRegionPo;
import com.jdl.basic.api.dto.itBasic.qo.ItBasicStorageIpRegionQo;
import com.jdl.basic.api.dto.itBasic.vo.ItBasicStorageIpRegionVo;

/**
 * IT设备IP对应区域信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-12-05 00:53:05 周一
 */
public interface ItBasicIpRegionManager {

    /**
     * 统计个数
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    Result<Long> queryCount(ItBasicStorageIpRegionQo itBasicStorageIpRegionQo);

    /**
     * 查询IP地址区域信息分页列表
     * @return 分页数据
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    Result<PageData<ItBasicStorageIpRegionVo>> queryPageList(ItBasicStorageIpRegionQo itBasicStorageIpRegionQo);

    /**
     * 添加一条设备记录
     * @param itBasicStorageIpRegion 设备记录
     * @return 处理结果
     */
    Result<Long> add(ItBasicStorageIpRegion itBasicStorageIpRegion);

    /**
     * 根据ID更新
     * @param itBasicStorageIpRegion 设备记录
     * @return 处理结果
     */
    Result<Boolean> updateById(ItBasicStorageIpRegion itBasicStorageIpRegion);

    /**
     * 根据ID逻辑删除
     * @param itBasicStorageIpRegion 设备记录
     * @return 处理结果
     */
    Result<Boolean> logicDeleteById(ItBasicStorageIpRegion itBasicStorageIpRegion);

    /**
     * 根据IP查询匹配的IP区段对应园区信息
     * @return 匹配的IP对应园区信息
     * @author fanggang7
     * @time 2022-12-04 10:40:36 周日
     */
    Result<MatchIpRegionDto> selectMatchRegionByIp(ItBasicIpRegionPo itBasicIpRegionPo);
}
