package com.jd.bluedragon.distribution.receive.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.receive.dao.ReceiveWeightCheckDao;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ReceiveWeightCheckServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/2/28 18:01
 */
@Service
public class ReceiveWeightCheckServiceImpl implements ReceiveWeightCheckService {

    private Logger log = Logger.getLogger(ReceiveWeightCheckServiceImpl.class);

    @Autowired
    private ReceiveWeightCheckDao receiveWeightCheckDao;

    /**
     * 数据不存在就插入，存在就更新
     * @param weightAndVolumeCheck
     * @return
     */
    @Override
    public int insert(WeightAndVolumeCheck weightAndVolumeCheck) {
        WeightAndVolumeCheck weightAndVolumeCheck1 = receiveWeightCheckDao.queryByPackageCode(weightAndVolumeCheck.getPackageCode());
        if(weightAndVolumeCheck1 != null){
            //更新
            return receiveWeightCheckDao.updateByPackageCode(weightAndVolumeCheck);
        }else{
            //插入
            return receiveWeightCheckDao.insert(weightAndVolumeCheck);
        }
    }

    @Override
    public PagerResult<WeightAndVolumeCheck> queryByCondition(WeightAndVolumeCheckCondition condition) {
        PagerResult<WeightAndVolumeCheck>  result = new PagerResult<WeightAndVolumeCheck>();
        List<WeightAndVolumeCheck> list = receiveWeightCheckDao.queryByCondition(condition);
        Integer num = queryNumByCondition(condition);
        result.setRows(list);
        result.setTotal(num);
        return result;
    }

    @Override
    public Integer queryNumByCondition(WeightAndVolumeCheckCondition condition) {
        return receiveWeightCheckDao.queryNumByCondition(condition);
    }

    /**
     * 整理导出数据
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("复核日期");
        heads.add("运单号");
        heads.add("包裹号");
        heads.add("商家名称");
        heads.add("信任商家");
        heads.add("复核区域");
        heads.add("复核分拣");
        heads.add("机构类型");
        heads.add("复核人erp");
        heads.add("分拣复重kg");
        heads.add("复核长宽高cm");
        heads.add("复核体积cm³");
        heads.add("计费操作区域");
        heads.add("计费操作机构");
        heads.add("计费操作人ERP");
        heads.add("计费重量kg");
        heads.add("计费体积cm³");
        heads.add("重量差异");
        heads.add("体积重量差异");
        heads.add("误差标准值");
        heads.add("是否超标(1:超标)");
        resList.add(heads);
        condition.setLimit(-1);
        PagerResult<WeightAndVolumeCheck> result = queryByCondition(condition);
        if(result != null && result.getRows() != null && result.getRows().size() > 0){
            List<WeightAndVolumeCheck> list = result.getRows();
            //表格信息
            for(WeightAndVolumeCheck weightAndVolumeCheck : list){
                List<Object> body = Lists.newArrayList();
                body.add(weightAndVolumeCheck.getReviewDate() == null ? null : DateHelper.formatDate(weightAndVolumeCheck.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(weightAndVolumeCheck.getWaybillCode());
                body.add(weightAndVolumeCheck.getPackageCode());
                body.add(weightAndVolumeCheck.getBusiName());
                body.add(weightAndVolumeCheck.getIsTrustBusiName()==1?"是":"否");
                body.add(weightAndVolumeCheck.getReviewOrg());
                body.add(weightAndVolumeCheck.getReviewCreateSiteName());
                body.add(weightAndVolumeCheck.getMechanismType()==1?"分拣中心":"转运中心");
                body.add(weightAndVolumeCheck.getReviewErp());
                body.add(weightAndVolumeCheck.getReviewWeight());
                body.add(weightAndVolumeCheck.getReviewLwh());
                body.add(weightAndVolumeCheck.getReviewVolume());
                body.add(weightAndVolumeCheck.getBillingOperateOrg());
                body.add(weightAndVolumeCheck.getBillingOperateDepartment());
                body.add(weightAndVolumeCheck.getBillingOperateErp());
                body.add(weightAndVolumeCheck.getBillingWeight());
                body.add(weightAndVolumeCheck.getBillingVolume());
                body.add(weightAndVolumeCheck.getWeightDiff());
                body.add(weightAndVolumeCheck.getVolumeWeightDiff());
                body.add(weightAndVolumeCheck.getDiffStandard());
                body.add(weightAndVolumeCheck.getIsExcess());
                resList.add(body);
            }
        }
        return  resList;
    }
}
