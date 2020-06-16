package com.jd.bluedragon.distribution.financialForKA.service.thread;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.financialForKA.dao.WaybillCodeCheckDao;
import com.jd.bluedragon.distribution.financialForKA.domain.KaCodeCheckCondition;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.bluedragon.utils.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * @author: 刘春和
 * @date: 2020/6/11 16:58
 * @description:
 */
public class ExportWaybillCodeCheckCallable  implements Callable {
    /** 单号校验结果 */
    private static Integer SUCCESS_RESULT_NUM = 1;
    private static Integer FAIL_RESULT_NUM = 0;
    private static String SUCCESS_RESULT = "通过";
    private static String FAIL_RESULT = "失败";
    KaCodeCheckCondition condition;
    List<List<Object>> resList;
    private WaybillCodeCheckDao waybillCodeCheckDao;
    public ExportWaybillCodeCheckCallable(KaCodeCheckCondition condition,WaybillCodeCheckDao waybillCodeCheckDao){
        this.condition=condition;
        this.waybillCodeCheckDao=waybillCodeCheckDao;
    }


    @Override
    public  List<List<Object>> call() throws Exception {
        List<List<Object>> resList=new ArrayList<>();
        List<WaybillCodeCheckDto> dataList = waybillCodeCheckDao.exportByCondition(condition);
        if(dataList != null && dataList.size() > 0) {
            //表格信息
            for(WaybillCodeCheckDto detail : dataList) {
                List<Object> body = Lists.newArrayList();
                body.add(detail.getWaybillCode());
                body.add(detail.getCompareCode());
                body.add(detail.getBusiCode());
                body.add(detail.getBusiName());
                body.add(detail.getOperateSiteCode());
                body.add(detail.getOperateSiteName());
                body.add(detail.getCheckResult() == null ? FAIL_RESULT : (detail.getCheckResult() == SUCCESS_RESULT_NUM ? SUCCESS_RESULT : FAIL_RESULT));
                body.add(detail.getOperateErp());
                body.add(detail.getOperateTime() == null ? null : DateHelper.formatDate(detail.getCreateTime(), Constants.DATE_TIME_FORMAT));
                resList.add(body);
            }
        }
        return resList;
    }
}
