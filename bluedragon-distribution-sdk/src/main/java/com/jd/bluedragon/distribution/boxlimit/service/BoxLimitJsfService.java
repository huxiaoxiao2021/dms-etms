package com.jd.bluedragon.distribution.boxlimit.service;

import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 箱号集包数量限制JSF 服务
 *
 * @author chenyaguo
 * @date 2022/3/31 19:44
 */
public interface BoxLimitJsfService {

    /**
     * 获取集箱包裹限制配置列表
     * @param dto
     * @return
     */
    JdResponse<PagerResult<BoxLimitVO>> listData(BoxLimitQueryDTO dto);

    /**
     * 根据站点获取站点名称
     * @param siteId
     * @return
     */
    JdResponse getSiteNameById(Integer siteId);

    /**
     * 新增或修改集箱包裹限制配置
     * @param dto
     * @return
     */
    JdResponse save( BoxLimitDTO dto);

    /**
     * 根据id删除集箱包裹限制配置信息
     * @param ids
     * @return
     */
    JdResponse delete(ArrayList<Long> ids);

    /**
     * 按站点集箱包裹限制配置导入
     * @param dataList
     * @return
     */
    JdResponse toImport(List<BoxLimitTemplateVO> dataList);

    /**
     * 查询符合条件的数据条数
     * @param dto
     * @return
     */
    JdResponse<Integer> countByCondition(BoxLimitQueryDTO dto);

}
