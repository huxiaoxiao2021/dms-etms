package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.pickinggood.JyPickingSendRecordDao;
import com.jd.bluedragon.distribution.jy.pickinggood.JyPickingSendRecordEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 空铁提发记录服务
 * @Author zhengchengfa
 * @Date 2023/12/6 20:29
 * @Description
 */
@Service
public class JyPickingSendRecordServiceImpl implements JyPickingSendRecordService{

    private static final Logger log = LoggerFactory.getLogger(JyPickingSendRecordServiceImpl.class);

    @Autowired
    private JyPickingSendRecordDao jyPickingSendRecordDao;


    public InvokeResult<String> fetchPickingBizIdByBarCode(Long siteCode, String barCode) {
        InvokeResult<String> res = new InvokeResult<>();
        if(Objects.isNull(siteCode) || StringUtils.isBlank(barCode)) {
            res.parameterError("查询待提任务参数不合法");
            return res;
        }
        JyPickingSendRecordEntity recordEntity = new JyPickingSendRecordEntity(siteCode);
        recordEntity.setWaitScanCode(barCode);

        String bizId = jyPickingSendRecordDao.fetchPickingBizIdByBarCode(recordEntity);
        res.setData(bizId);
        if(StringUtils.isBlank(bizId)) {
            res.setMessage(String.format("根据%s没有查到待提货任务BizId", barCode));
        }
        return res;
    }
}
