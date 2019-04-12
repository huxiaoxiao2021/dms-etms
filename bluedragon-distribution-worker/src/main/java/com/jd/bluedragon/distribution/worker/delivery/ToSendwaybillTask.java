package com.jd.bluedragon.distribution.worker.delivery;

import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.worker.AbstractScheduler;
import com.jd.bluedragon.utils.LongHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Deprecated
public class ToSendwaybillTask extends AbstractScheduler<SendDetail> {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private DeliveryService deliveryService;

    public boolean execute(Object[] taskArray, String ownSign) throws Exception {
        List<SendDetail> sendDetails = new ArrayList<SendDetail>();
        for (Object sortingReturn : taskArray) {
            if (sortingReturn != null && sortingReturn instanceof SendDetail) {
                sendDetails.add((SendDetail) sortingReturn);
            }
        }

        try {
            this.deliveryService.updateWaybillStatus(sendDetails);
        } catch (Exception e) {
            this.logger.error("异常信息为：" + e.getMessage(), e);
        }

        return Boolean.TRUE;
    }

    @Override
    public List<SendDetail> selectTasks(String ownSign, int queueNum, List<String> queryCondition,
            int fetchNum) throws Exception {
        List<SendDetail> sendDetails = new ArrayList<SendDetail>();
        if(CollectionUtils.isEmpty(queryCondition)){
            return null;
        }
        List<Long> ids = new ArrayList<>();
        for(String item : queryCondition){
            ids.add(LongHelper.strToLongOrNull(item));
        }
        try {
            List<SendDetail> unhandles = deliveryService.findWaybillStatus(ids);
            for (SendDetail sendDetail : unhandles) {
                if (this.isMyTask(queueNum, sendDetail.getSendDId(), queryCondition)) {
                    sendDetails.add(sendDetail);
                }
            }
        } catch (Exception e) {
            this.logger.error("查询未处理的分拣退货信息出现异常， 异常信息为：" + e.getMessage(), e);
        }
        return sendDetails;
    }

    @Override
    public Comparator<SendDetail> getComparator() {
        return new Comparator<SendDetail>() {
            public int compare(SendDetail o1, SendDetail o2) {
                if (null != o1 && null != o2 && o1.getSendDId()!=null&&o1.getSendDId().equals(o2.getSendDId())) {
                    return 0;
                }
                return 1;
            }
        };
    }

    public boolean execute(List<SendDetail> sendDatailArray) throws Exception {
        if (sendDatailArray == null) {
            return false;
        }

        return true;
    }

}
