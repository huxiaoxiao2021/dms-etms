package com.jd.bluedragon.distribution.jy.strand;

import com.google.common.collect.Lists;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.*;
import com.jd.bluedragon.common.dto.strandreport.request.ConfigStrandReasonData;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizStrandReportDetailService;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportDealService;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/4/6 2:17 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/distribution-web-context.xml")
public class JyBizTaskStrandReportDealServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(JyBizTaskStrandReportDealServiceImplTest.class);
    
    @Autowired
    private JyBizTaskStrandReportDealService jyBizTaskStrandReportDealService;

    @Test
    public void artificialCreateStrandReportTask() {
        try {
            JyStrandReportTaskCreateReq request = new JyStrandReportTaskCreateReq();
            request.setSiteCode(910);
            request.setSiteName("北京通州分拣中心");
            request.setOperateUserErp("wuyoude");
            request.setOperateUserName("吴有德");
            request.setStrandCode(1);
            request.setStrandReason("传站车辆不足");
            request.setNextSiteCode(39);
            request.setNextSiteName("石景山营业部");
            List<String> proveUrlList = Lists.newArrayList();
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            request.setProveUrlList(proveUrlList);
            InvokeResult<JyStrandReportTaskVO> result = jyBizTaskStrandReportDealService.artificialCreateStrandReportTask(request);

            Assert.assertTrue(result.codeSuccess());
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void systemCreateStrandReportTask() {
        try {
            JyStrandReportTaskCreateReq request = new JyStrandReportTaskCreateReq();
            request.setTransportRejectBiz("transportRejectBiz11");
            request.setSiteCode(910);
            request.setSiteName("北京通州分拣中心");
            request.setOperateUserErp("wuyoude");
            request.setOperateUserName("吴有德");
            request.setStrandCode(1);
            request.setStrandReason("传站车辆不足");
            request.setNextSiteCode(39);
            request.setNextSiteName("石景山营业部");
            List<String> proveUrlList = Lists.newArrayList();
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            proveUrlList.add("http://test.storage.jd.com/dms-feedback/13c72d01-7e8a-4e11-8941-c3fef5e77ad5.jpg?Expires=1946465841&AccessKey=a7ogJNbj3Ee9YM1O&Signature=8Z0AG4t%2F5cx3YhpzR%2B661oLj2Ko%3D");
            request.setProveUrlList(proveUrlList);
            InvokeResult<JyStrandReportTaskVO> result = jyBizTaskStrandReportDealService.systemCreateStrandReportTask(request);

            Assert.assertTrue(result.codeSuccess());
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void cancelStrandReportTask() {
        try {
            JyStrandReportTaskCreateReq request = new JyStrandReportTaskCreateReq();
            request.setBizId("JY_STRAND_00013001");
            request.setOperateUserErp("bjxings");
            InvokeResult<Void> result = jyBizTaskStrandReportDealService.cancelStrandReportTask(request);
            Assert.assertTrue(result.codeSuccess());
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void queryStrandReason() {
        try {
            InvokeResult<List<ConfigStrandReasonData>> result = jyBizTaskStrandReportDealService.queryStrandReason();
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void queryStrandScanType() {
        try {
            InvokeResult<List<JyBizStrandScanTypeEnum>> result = jyBizTaskStrandReportDealService.queryStrandScanType();
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void strandScan() {
        try {
            JyStrandReportScanReq scanRequest = new JyStrandReportScanReq();
            scanRequest.setBizId("JY_STRAND_00017001");
            scanRequest.setSiteCode(910);
            scanRequest.setSiteName("北京通州分拣中心");
            scanRequest.setPositionCode("GW00019001");
            scanRequest.setOperateUserErp("wuyoude");
            scanRequest.setScanBarCode("JDX000236450536-1-3-");
            scanRequest.setScanType(JyBizStrandScanTypeEnum.WAYBILL.getCode());

            InvokeResult<JyStrandReportScanResp> result = jyBizTaskStrandReportDealService.strandScan(scanRequest);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void cancelStrandScan() {
        try {
            JyStrandReportScanReq scanRequest = new JyStrandReportScanReq();
            scanRequest.setBizId("JY_STRAND_00014001");
            scanRequest.setSiteCode(910);
            scanRequest.setSiteName("北京通州分拣中心");
            scanRequest.setOperateUserErp("bjxings");
            scanRequest.setScanBarCode("JDX000235707765-2-2-");
            scanRequest.setContainerCode("JDX000235707765");

            InvokeResult<JyStrandReportScanResp> result = jyBizTaskStrandReportDealService.cancelStrandScan(scanRequest);
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void strandReportSubmit() {
        try {
            JyStrandReportScanReq scanRequest = new JyStrandReportScanReq();
            scanRequest.setBizId("JY_STRAND_00017001");
            scanRequest.setOperateUserErp("wuyoude");

            InvokeResult<Void> result = jyBizTaskStrandReportDealService.strandReportSubmit(scanRequest);

            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void scanContainerDeal() {
        try {
            String text = "{\n" +
                    "  \"id\" : 6,\n" +
                    "  \"bizId\" : \"JY_STRAND_00017001\",\n" +
                    "  \"scanBarCode\" : \"JDX000236450481-2-2-\",\n" +
                    "  \"containerCode\" : \"JDX000236450481\",\n" +
                    "  \"containerInnerCount\" : 2,\n" +
                    "  \"scanType\" : 1,\n" +
                    "  \"isCancel\" : 0,\n" +
                    "  \"gridCode\" : \"ZYZCQ-01\",\n" +
                    "  \"siteCode\" : 910,\n" +
                    "  \"siteName\" : \"北京通州分拣中心\",\n" +
                    "  \"createUserErp\" : \"wuyoude\",\n" +
                    "  \"updateUserErp\" : \"wuyoude\",\n" +
                    "  \"createTime\" : 1681715824000,\n" +
                    "  \"updateTime\" : 1681715824000,\n" +
                    "  \"yn\" : 1,\n" +
                    "  \"ts\" : 1681715824000\n" +
                    "}";
            jyBizTaskStrandReportDealService.scanContainerDeal(JsonHelper.fromJson(text, JyBizStrandReportDetailEntity.class));
            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Autowired
    private JyBizStrandReportDetailService jyBizStrandReportDetailService;
    
    @Test
    public void queryStrandReportTaskPageList() {
        try {

//            JyStrandReportTaskPageReq pageReq = new JyStrandReportTaskPageReq();
//            pageReq.setSiteCode(910);
//            pageReq.setPageNo(1);
//            pageReq.setPageSize(10);
//            InvokeResult<JyStrandReportTaskPageResp> result = jyBizTaskStrandReportDealService.queryStrandReportTaskPageList(pageReq);

            List<StrandDetailSumEntity> strandDetailSumEntities = jyBizStrandReportDetailService.queryTotalInnerScanNumByBizIds(Lists.newArrayList("JY_STRAND_00014001", "JY_STRAND_00017001"));

            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void queryStrandReportTaskDetail() {
        try {
            String bizId = "JY_STRAND_00017001";
            InvokeResult<JyStrandReportTaskDetailVO> result = jyBizTaskStrandReportDealService.queryStrandReportTaskDetail(bizId);

            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void queryPageStrandReportTaskDetail() {
        try {

            JyStrandReportScanPageReq detailPageReq = new JyStrandReportScanPageReq();
            detailPageReq.setBizId("JY_STRAND_00017001");
            detailPageReq.setPageNum(1);
            detailPageReq.setPageSize(10);
            InvokeResult<List<JyStrandReportScanVO>> result = jyBizTaskStrandReportDealService.queryPageStrandReportTaskDetail(detailPageReq);

            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }

    @Test
    public void regularDealStrandReport() {
        try {

            Assert.assertTrue(true);
        }catch (Throwable e){
            logger.error("服务异常", e);
            Assert.fail();
        }
    }
    
}