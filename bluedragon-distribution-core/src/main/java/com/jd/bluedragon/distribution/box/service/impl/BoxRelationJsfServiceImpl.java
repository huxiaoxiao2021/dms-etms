package com.jd.bluedragon.distribution.box.service.impl;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.dao.BoxRelationDao;
import com.jd.bluedragon.distribution.box.domain.BoxBindingDto;
import com.jd.bluedragon.distribution.box.domain.BoxRelation;
import com.jd.bluedragon.distribution.box.domain.BoxRelationQ;
import com.jd.bluedragon.distribution.box.jsf.BoxRelationJsfService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName BoxRelationJsfServiceImpl
 * @Description
 * @Author wyh
 * @Date 2020/12/23 19:25
 **/
@Service("boxRelationJsfService")
public class BoxRelationJsfServiceImpl implements BoxRelationJsfService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoxRelationJsfServiceImpl.class);

    @Autowired
    private BoxRelationDao boxRelationDao;

    @Override
    public InvokeResult<PagerResult<BoxBindingDto>> queryBindingData(BoxRelationQ query) {
        InvokeResult<PagerResult<BoxBindingDto>> invokeResult = new InvokeResult<>();
        try {
            PagerResult<BoxBindingDto> result = new PagerResult<>();
            List<BoxBindingDto> rows = new ArrayList<>();
            result.setRows(rows);
            invokeResult.setData(result);

            PagerResult<BoxRelation> pagerResult = boxRelationDao.queryByPagerCondition(query);
            if (null != pagerResult && CollectionUtils.isNotEmpty(pagerResult.getRows())) {
                for (BoxRelation resultRow : pagerResult.getRows()) {
                    BoxBindingDto dto = new BoxBindingDto();
                    dto.setBoxCode(resultRow.getBoxCode());
                    dto.setRelationBoxCode(resultRow.getRelationBoxCode());
                    dto.setSiteCode(resultRow.getCreateSiteCode());
                    dto.setUserErp(resultRow.getCreateUserErp());
                    dto.setCreateTime(resultRow.getCreateTime());
                    rows.add(dto);
                }

                result.setTotal(pagerResult.getTotal());
            }
        }
        catch (Exception ex) {
            LOGGER.error("查询箱号关联关系失败. query:{}", JsonHelper.toJson(query), ex);
            invokeResult.error(ex.getMessage());
        }

        return invokeResult;
    }

    @Override
    public InvokeResult<Integer> queryCount(BoxRelationQ query) {
        InvokeResult<Integer> invokeResult = new InvokeResult<>();
        invokeResult.setData(boxRelationDao.countByQuery(query));
        return invokeResult;
    }
}
