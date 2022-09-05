package com.jd.bluedragon.core.base;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDetailMQ;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.report.domain.spotcheck.WeightVolumeSpotCheckDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 抽检实现代理
 *
 * @author hujiping
 * @date 2022/2/10 2:25 PM
 */
@Service
public class SpotCheckServiceProxy {

    @Autowired
    private SpotCheckQueryManager spotCheckQueryManager;

    @Autowired
    @Qualifier("spotCheckDetailProducer")
    private DefaultJMQProducer spotCheckDetailProducer;

    /**
     * 新增或修改代理
     *  1、新增或修改 2、对外发抽检明细MQ
     * @param dto
     */
    public void insertOrUpdateProxyReform(WeightVolumeSpotCheckDto dto){
        Boolean isSuccess = spotCheckQueryManager.insertOrUpdateSpotCheck(dto);
        if(isSuccess){
            SpotCheckDetailMQ spotCheckDetailMQ = new SpotCheckDetailMQ();
            BeanUtils.copyProperties(dto, spotCheckDetailMQ);
            spotCheckDetailProducer.sendOnFailPersistent(spotCheckDetailMQ.getPackageCode(), JsonHelper.toJson(spotCheckDetailMQ));
        }
    }
}
