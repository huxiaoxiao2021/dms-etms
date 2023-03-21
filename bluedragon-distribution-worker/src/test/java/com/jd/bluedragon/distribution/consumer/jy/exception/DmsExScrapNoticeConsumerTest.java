package com.jd.bluedragon.distribution.consumer.jy.exception;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpScrappedDetailReq;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.exception.JyExScrapNoticeMQ;
import com.jd.bluedragon.distribution.jy.exception.JyExceptionAgg;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.jy.service.exception.JyScrappedExceptionService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.lsb.flow.domain.HistoryApprove;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/3/13 5:41 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-worker-context-test.xml")
public class DmsExScrapNoticeConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(DmsExScrapNoticeConsumerTest.class);
    
    @Autowired
    private DmsExScrapNoticeConsumer dmsExScrapNoticeConsumer;
    
    @Autowired
    private JyExceptionService jyExceptionService;

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;

    @Autowired
    private JyScrappedExceptionService jyScrappedExceptionService;
    
    @Test
    public void consume() {
        try {
            String content = "{\n" +
                    "  \"id\" : 1082373,\n" +
                    "  \"businessCode\" : \"dms.etms\",\n" +
                    "  \"flowId\" : 2721,\n" +
                    "  \"processInstanceNo\" : \"fresh_scrap_approve-1-20230321103905-1638007288868376576\",\n" +
                    "  \"nodeName\" : \"thirdApprove\",\n" +
                    "  \"nodeDisplayName\" : \"三级审批\",\n" +
                    "  \"performType\" : \"ANY\",\n" +
                    "  \"applicant\" : \"hujiping1\",\n" +
                    "  \"createTime\" : 1679366699000,\n" +
                    "  \"system\" : \"QLFJZXJT\",\n" +
                    "  \"appName\" : \"dms.etms\",\n" +
                    "  \"flowName\" : \"fresh_scrap_approve\",\n" +
                    "  \"flowVersion\" : 1,\n" +
                    "  \"state\" : 1,\n" +
                    "  \"approver\" : \"tim.he\",\n" +
                    "  \"approverName\" : \"何田\",\n" +
                    "  \"comment\" : \"\",\n" +
                    "  \"finishTime\" : 1679366751449,\n" +
                    "  \"dataMap\" : {\n" +
                    "    \"oa\" : {\n" +
                    "      \"jmeMainColList\" : [ \"生鲜报废单号:JDVA19408919512\", \"生鲜报废场地:北京马驹桥分拣中心最多20个字试试行不行\", \"生鲜报废提交人:hujiping1\", \"生鲜报废网格:CLLQ-07\" ],\n" +
                    "      \"jmeFiles\" : [ {\n" +
                    "        \"fileName\" : \"物品照片1\",\n" +
                    "        \"fileUrl\" : \"http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D\"\n" +
                    "      }, {\n" +
                    "        \"fileName\" : \"物品照片2\",\n" +
                    "        \"fileUrl\" : \"http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D\"\n" +
                    "      }, {\n" +
                    "        \"fileName\" : \"物品照片3\",\n" +
                    "        \"fileUrl\" : \"http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D\"\n" +
                    "      }, {\n" +
                    "        \"fileName\" : \"证明照片1\",\n" +
                    "        \"fileUrl\" : \"http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D\"\n" +
                    "      }, {\n" +
                    "        \"fileName\" : \"证明照片2\",\n" +
                    "        \"fileUrl\" : \"http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D\"\n" +
                    "      }, {\n" +
                    "        \"fileName\" : \"证明照片3\",\n" +
                    "        \"fileUrl\" : \"http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D\"\n" +
                    "      } ],\n" +
                    "      \"jmeReqComments\" : \"生鲜报废申请\",\n" +
                    "      \"jmeReqName\" : \"生鲜报废申请单\"\n" +
                    "    },\n" +
                    "    \"bussinessData\" : {\n" +
                    "      \"businessNoKey\" : \"SCRAPPED_JDVA19408919512\"\n" +
                    "    },\n" +
                    "    \"flowControl\" : {\n" +
                    "      \"secondTriggerErp\" : \"bjgyzhi\",\n" +
                    "      \"firstTriggerErp\" : \"hujiping1\",\n" +
                    "      \"approveCount\" : 3,\n" +
                    "      \"preApprover\" : \"tim.he\",\n" +
                    "      \"preApproveNodeState\" : 1,\n" +
                    "      \"thirdTriggerErp\" : \"zhangjunjun19\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"historyApprove\" : true\n" +
                    "}";
            HistoryApprove historyApprove = JsonHelper.fromJson(content, HistoryApprove.class);
            historyApprove.setNodeName("firstApprove");
            historyApprove.setApprover("bjgyzhi");
            jyScrappedExceptionService.dealApproveResult(historyApprove);
            historyApprove.setNodeName("secondApprove");
            historyApprove.setApprover("zhangjunjun19");
            jyScrappedExceptionService.dealApproveResult(historyApprove);
            historyApprove.setNodeName("thirdApprove");
            historyApprove.setApprover("tim.he");
            jyScrappedExceptionService.dealApproveResult(historyApprove);
            
            ExpScrappedDetailReq req = new ExpScrappedDetailReq();
            req.setBizId("SCRAPPED_JDVA19408919512");
            req.setGoodsImageUrl("http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D");
            req.setCertifyImageUrl("http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D,http://storage.jd.local/volumepicture/JDVA15097578817-1-1-_695114_20220724235616.jpg?Expires=3806162170&AccessKey=6KBoeA1WKY6qcw10&Signature=bKn98ICaVsgqR67tRAh2Gxofulo%3D");
            jyScrappedExceptionService.dealApprove(req);
            Assert.assertTrue(true);
            
            String handlerErp = "wuyoude";
            Date queryStartTime = DateHelper.parseDateTime("2022-08-01 00:00:00");
            Date queryEndTime = DateHelper.parseDateTime("2022-09-01 00:00:00");
            List<JyBizTaskExceptionEntity> list = jyExceptionService.queryScrapDetailByCondition(handlerErp, queryStartTime, queryEndTime);
            Assert.assertTrue(true);

            List<String> handlerErpList = jyExceptionService.queryScrapHandlerErp(queryStartTime, queryEndTime);
            Assert.assertTrue(true);
            
            JyBizTaskExceptionEntity entity = new JyBizTaskExceptionEntity();
            entity.setSiteCode(910L);
            entity.setProcessBeginTime(DateHelper.parseDateTime("2022-08-01 00:00:00"));
            Integer count = jyBizTaskExceptionDao.queryScrapCountByCondition(entity);
            Assert.assertTrue(true);

            Map<String, Object> params = Maps.newHashMap();
            params.put("queryStartTime", queryStartTime);
            params.put("queryEndTime", queryEndTime);
            List<JyExceptionAgg> jyExceptionAggs = jyBizTaskExceptionDao.queryUnCollectAndOverTimeAgg(params);
            Assert.assertTrue(true);
            
            long time = DateHelper.getFirstDateOfMonth().getTime();
            Message message = new Message();
            JyExScrapNoticeMQ jyExScrapNoticeMQ = new JyExScrapNoticeMQ();
            jyExScrapNoticeMQ.setHandlerErp("bjxings");
            jyExScrapNoticeMQ.setQueryStartTime(new Date(1678701600000L));
            jyExScrapNoticeMQ.setQueryEndTime(new Date(1678636800000L));
            message.setText(JsonHelper.toJson(jyExScrapNoticeMQ));
            dmsExScrapNoticeConsumer.consume(message);
        }catch (Exception e){
            logger.error("服务异常!", e);
            Assert.fail();
        }
    }
}
