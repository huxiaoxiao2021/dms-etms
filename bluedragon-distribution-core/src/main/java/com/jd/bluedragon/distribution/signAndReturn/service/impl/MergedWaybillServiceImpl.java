package com.jd.bluedragon.distribution.signAndReturn.service.impl;

import com.jd.bluedragon.distribution.signAndReturn.dao.MergedWaybillDao;
import com.jd.bluedragon.distribution.signAndReturn.dao.SignReturnDao;
import com.jd.bluedragon.distribution.signAndReturn.domain.MergedWaybill;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signAndReturn.service.MergedWaybillService;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: MergedWaybillServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/29 18:19
 */
@Service("mergedWaybillService")
public class MergedWaybillServiceImpl implements MergedWaybillService {

    @Autowired
    private MergedWaybillDao mergedWaybillDao;
    @Autowired
    private SignReturnDao signReturnDao;

    /**
     * 批量增加
     * @param mergedWaybillList
     * @return
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int batchAdd(List<MergedWaybill> mergedWaybillList){
        return mergedWaybillDao.batchAdd(mergedWaybillList);
    }

    /**
     * 通过主键id获取旧运单号集合
     * @param id
     * @return
     */
    @Override
    public List<MergedWaybill> getListBySignReturnPrintMId(Long id){
        return mergedWaybillDao.getListBySignReturnPrintMId(id);
    }

    /**
     * 通过运单号获取旧运单号集合
     * */
    @Override
    public List<MergedWaybill> getListByWaybillCode(SignReturnCondition condition) {

        List<MergedWaybill> mergedWaybillList = mergedWaybillDao.getListByWaybillCode(condition);
        List<MergedWaybill> list = new ArrayList<MergedWaybill>();
        if(mergedWaybillList != null && mergedWaybillList.size() > 0){
            list = mergedWaybillDao.getListBySignReturnPrintMId(mergedWaybillList.get(0).getSignReturnPrintMId());
        }
        return list;

    }


    /**
     * 通过旧单号获得返单信息
     * @param condition
     * @return
     */
    @Override
    public PagerResult<SignReturnPrintM> getSignReturnByConditon(SignReturnCondition condition){

        PagerResult<SignReturnPrintM> result = new PagerResult<SignReturnPrintM>();
        List<MergedWaybill> mergedWaybillList = getListByWaybillCode(condition);
        List<SignReturnPrintM> signReturnPrintMList = new ArrayList<SignReturnPrintM>();
        if(mergedWaybillList != null && mergedWaybillList.size()>0){
            //存在数据则继续
            Long id = mergedWaybillList.get(0).getSignReturnPrintMId();
            //永远不会是空
            signReturnPrintMList = signReturnDao.getSignReturnById(id);
            for(SignReturnPrintM signReturnPrintM : signReturnPrintMList){
                signReturnPrintM.setMergedWaybillList(mergedWaybillList);
            }

        }
        result.setRows(signReturnPrintMList );
        result.setTotal(signReturnPrintMList.size());
        return result;
    }
}
