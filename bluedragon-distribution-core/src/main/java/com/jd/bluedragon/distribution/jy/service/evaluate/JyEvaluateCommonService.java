package com.jd.bluedragon.distribution.jy.service.evaluate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateRecordDao;
import com.jd.bluedragon.distribution.jy.dao.evaluate.JyEvaluateTargetInfoDao;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateRecordEntity;
import com.jd.bluedragon.distribution.jy.evaluate.JyEvaluateTargetInfoEntity;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JyEvaluateCommonService {

    @Autowired
    private JyEvaluateTargetInfoDao jyEvaluateTargetInfoDao;
    @Autowired
    private JyEvaluateRecordDao jyEvaluateRecordDao;

    @Transactional
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "JyEvaluateCommonService.saveEvaluateInfo", mState = {JProEnum.TP, JProEnum.FunctionError})
    public void saveEvaluateInfo(JyEvaluateTargetInfoEntity evaluateTargetInfo, List<JyEvaluateRecordEntity> recordList) {
        jyEvaluateTargetInfoDao.insertSelective(evaluateTargetInfo);
        jyEvaluateRecordDao.batchInsert(recordList);
    }


}
