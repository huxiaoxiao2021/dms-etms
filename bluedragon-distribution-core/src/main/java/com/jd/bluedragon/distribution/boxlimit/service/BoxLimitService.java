package com.jd.bluedragon.distribution.boxlimit.service;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

public interface BoxLimitService {

    /**
     *  PDA建箱包裹数 列表查询
     */
    PagerResult<BoxLimitVO> listData(BoxLimitQueryDTO dto);

    /**
     * 导入模板数据
     * @param data
     * @param operator
     * @return
     */
    JdResponse importData(List<BoxLimitTemplateVO> data, LoginUser operator);

    JdResponse create(BoxLimitDTO dto, LoginUser operator);

    JdResponse update(BoxLimitDTO dto, LoginUser operator);

    JdResponse delete(List<Integer> ids);

}
