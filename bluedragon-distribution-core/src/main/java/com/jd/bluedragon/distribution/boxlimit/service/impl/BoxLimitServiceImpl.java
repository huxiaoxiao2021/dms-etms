package com.jd.bluedragon.distribution.boxlimit.service.impl;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.dao.BoxLimitDao;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimit;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BoxLimitServiceImpl implements BoxLimitService {
    @Autowired
    private BoxLimitDao boxLimitDao;

    @Override
    public PagerResult<BoxLimitVO> listData(BoxLimitQueryDTO dto) {
        PagerResult<BoxLimitVO> result = new PagerResult<>();
        Integer count = boxLimitDao.countByCondition(dto);
        result.setTotal(count);
        if (count == 0) {
            result.setRows(new ArrayList<BoxLimitVO>());
            return result;
        }
        List<BoxLimit> boxLimits = boxLimitDao.queryByCondition(dto);
        List<BoxLimitVO> list = new ArrayList<>();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (BoxLimit b : boxLimits) {
            BoxLimitVO vo = new BoxLimitVO();
            vo.setSiteName(b.getSiteName());
            vo.setSiteId(b.getSiteId());
            vo.setLimitNum(b.getLimitNum());
            vo.setOperatorErp(b.getOperatorErp());
            vo.setOperatorSiteName(b.getOperatorSiteName());
            vo.setOperatingTime(format.format(b.getOperatingTime()));
            list.add(vo);
        }
        result.setRows(list);
        return result;
    }

    @Override
    public JdResponse importData(List<BoxLimitTemplateVO> data, LoginUser operator) {
        List<BoxLimit> list = new ArrayList<>();
        Date now = new Date();
        String operatorErp = operator.getUserErp();
        Integer operatorSiteId = operator.getSiteCode();
        String operatorName = operator.getSiteName();

        // TODO 校验格式及站点信息
        for (BoxLimitTemplateVO vo : data) {
            BoxLimit b = new BoxLimit();
            b.setSiteName(vo.getSiteName());
            b.setSiteId(vo.getSiteId());
            b.setLimitNum(vo.getLimitNum());
            b.setOperatorErp(operatorErp);
            b.setOperatorSiteId(operatorSiteId);
            b.setOperatorSiteName(operatorName);
            b.setOperatingTime(now);
            b.setCreateTime(now);
            b.setUpdateTime(now);
            b.setYn(true);
            list.add(b);
        }
        boxLimitDao.batchInsert(list);
        return new JdResponse();
    }

    @Override
    public JdResponse create(BoxLimitDTO dto, LoginUser operator) {
        Date now = new Date();
        BoxLimit boxLimit = new BoxLimit();
        boxLimit.setSiteName(dto.getSiteName());
        boxLimit.setSiteId(dto.getSiteId());
        boxLimit.setLimitNum(dto.getLimitNum());
        boxLimit.setOperatorErp(operator.getUserErp());
        boxLimit.setOperatorSiteId(operator.getSiteCode());
        boxLimit.setOperatorSiteName(operator.getSiteName());
        boxLimit.setOperatingTime(now);

        boxLimit.setCreateTime(now);
        boxLimit.setUpdateTime(now);
        boxLimit.setYn(true);

        return new JdResponse();
    }

    @Override
    public JdResponse update(BoxLimitDTO dto, LoginUser operator) {
        JdResponse response = new JdResponse();
        if (dto.getId() == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("ID不能为空!");
            return response;
        }
        BoxLimit boxLimit = boxLimitDao.queryById(dto.getId());
        if (boxLimit == null) {
            response.setCode(JdResponse.CODE_FAIL);
            response.setMessage("数据不存在!");
            return response;
        }

        Date now = new Date();
        boxLimit.setUpdateTime(now);
        boxLimit.setOperatingTime(now);
        boxLimit.setOperatorErp(operator.getUserErp());
        boxLimit.setOperatorSiteId(operator.getSiteCode());
        boxLimit.setOperatorSiteName(operator.getSiteName());

        boxLimitDao.updateByIdSelective(boxLimit);
        return response;
    }

    @Override
    public JdResponse delete(List<Integer> ids) {
        boxLimitDao.batchDelete(ids);
        return new JdResponse();
    }

}
