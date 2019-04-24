package com.jd.bluedragon.distribution.receive.service.impl;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.receive.dao.ReceiveWeightCheckDao;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.bluedragon.distribution.receive.service.ReceiveWeightCheckService;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheck;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
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
     * @param receiveWeightCheckResult
     * @return
     */
    @Override
    public int insert(ReceiveWeightCheckResult receiveWeightCheckResult) {
        ReceiveWeightCheckResult receiveWeightCheckResult1 = receiveWeightCheckDao.queryByPackageCode(receiveWeightCheckResult.getPackageCode());
        if(receiveWeightCheckResult1 != null){
            //更新
            return receiveWeightCheckDao.updateByPackageCode(receiveWeightCheckResult);
        }else{
            //插入
            return receiveWeightCheckDao.insert(receiveWeightCheckResult);
        }
    }

    @Override
    public PagerResult<ReceiveWeightCheckResult> queryByCondition(ReceiveWeightCheckCondition condition) {
        PagerResult<ReceiveWeightCheckResult>  result = new PagerResult<ReceiveWeightCheckResult>();
        List<ReceiveWeightCheckResult> list = receiveWeightCheckDao.queryByCondition(condition);
        Integer num = queryNumByCondition(condition);
        result.setRows(list);
        result.setTotal(num);
        return result;
    }

    @Override
    public PagerResult<WeightAndVolumeCheck> queryByCondition1(WeightAndVolumeCheckCondition condition) {
        PagerResult<WeightAndVolumeCheck>  result = new PagerResult<WeightAndVolumeCheck>();
        List<WeightAndVolumeCheck> list = receiveWeightCheckDao.queryByCondition1(condition);
        Integer num = queryNumByCondition1(condition);
        result.setRows(list);
        result.setTotal(num);
        return result;
    }

    @Override
    public Integer queryNumByCondition(ReceiveWeightCheckCondition condition) {
        return receiveWeightCheckDao.queryNumByCondition(condition);
    }

    @Override
    public Integer queryNumByCondition1(WeightAndVolumeCheckCondition condition) {
        return receiveWeightCheckDao.queryNumByCondition1(condition);
    }

    /**
     * 整理导出数据
     * @param condition
     * @return
     */
    @Override
    public List<List<Object>> getExportData(ReceiveWeightCheckCondition condition) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();
        //添加表头
        heads.add("复核日期");
        heads.add("包裹号");
        heads.add("商家名称");
        heads.add("复核区域");
        heads.add("复核分拣");
        heads.add("复核人erp");
        heads.add("分拣复重kg");
        heads.add("复核长宽高cm");
        heads.add("复核体积cm³");
        heads.add("揽收区域");
        heads.add("揽收营业部");
        heads.add("揽收人erp");
        heads.add("揽收重量kg");
        heads.add("揽收长宽高cm");
        heads.add("揽收体积cm³");
        heads.add("重量差异");
        heads.add("体积重量差异");
        heads.add("误差标准值");
        heads.add("是否超标(1:超标)");
        resList.add(heads);
        condition.setLimit(-1);
        PagerResult<ReceiveWeightCheckResult> result = queryByCondition(condition);
        if(result != null && result.getRows() != null && result.getRows().size() > 0){
            List<ReceiveWeightCheckResult> list = result.getRows();
            //表格信息
            for(ReceiveWeightCheckResult receiveWeightCheckResult : list){
                List<Object> body = Lists.newArrayList();
                body.add(receiveWeightCheckResult.getReviewDate() == null ? null : DateHelper.formatDate(receiveWeightCheckResult.getReviewDate(), Constants.DATE_TIME_FORMAT));
                body.add(receiveWeightCheckResult.getPackageCode());
                body.add(receiveWeightCheckResult.getBusiName());
                body.add(receiveWeightCheckResult.getReviewOrg());
                body.add(receiveWeightCheckResult.getReviewCreateSiteName());
                body.add(receiveWeightCheckResult.getReviewErp());
                body.add(receiveWeightCheckResult.getReviewWeight());
                body.add(receiveWeightCheckResult.getReviewLwh());
                body.add(receiveWeightCheckResult.getReviewVolume());
                body.add(receiveWeightCheckResult.getReceiveOrg());
                body.add(receiveWeightCheckResult.getReceiveDepartment());
                body.add(receiveWeightCheckResult.getReceiveErp());
                body.add(receiveWeightCheckResult.getReceiveWeight());
                body.add(receiveWeightCheckResult.getReceiveLwh());
                body.add(receiveWeightCheckResult.getReceiveVolume());
                body.add(receiveWeightCheckResult.getWeightDiff());
                body.add(receiveWeightCheckResult.getVolumeWeightDiff());
                body.add(receiveWeightCheckResult.getDiffStandard());
                body.add(receiveWeightCheckResult.getIsExcess());
                resList.add(body);
            }
        }
        return  resList;
    }
}
