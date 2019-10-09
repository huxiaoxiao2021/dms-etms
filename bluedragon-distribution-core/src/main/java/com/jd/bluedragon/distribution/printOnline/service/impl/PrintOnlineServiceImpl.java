package com.jd.bluedragon.distribution.printOnline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.JdwlSignManager;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineBoxDTO;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineModalDTO;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.printOnline.service.IPrintOnlineService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintBoxEntity;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResult;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.dms.logger.annotation.BusinessLog;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 无纸化线上打印业务实现类
 *
 * 刘铎
 * 2019年9月26日17:18:38
 */
@Service("printOnlineService")
public class PrintOnlineServiceImpl implements IPrintOnlineService {

    @Autowired
    private SendPrintService sendPrintService;
    @Autowired
    private JdwlSignManager jdwlSignManager;

    /**
     * 逆向交接清单 线上签逻辑
     * 批次维度推送区块链
     * @param sendCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWORKER.PrintOnlineServiceImpl.reversePrintOnline", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    public boolean reversePrintOnline(String sendCode) {
        if(!BusinessUtil.isSendCode(sendCode)){
            return true;
        }
        //根据批次号获取发货数据
        PrintOnlineModalDTO modalDTO = makeModalDTO(sendCode);

        //推送数据
        boolean result = jdwlSignManager.aciton(modalDTO);
        return result;
    }

    /**
     * 组装推送数据消息体
     * @param sendCode
     * @return
     */
    private PrintOnlineModalDTO makeModalDTO(String sendCode){
        PrintOnlineModalDTO pojo = new PrintOnlineModalDTO();



        //根据批次号获取发货数据
        Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(sendCode);
        Integer receiveSiteCode = BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode);

        PrintQueryCriteria criteria = new PrintQueryCriteria();
        criteria.setSendCode(sendCode);
        criteria.setReceiveSiteCode(receiveSiteCode);
        criteria.setSiteCode(createSiteCode);
        //获取打印汇总数据
        SummaryPrintResultResponse resultResponse = sendPrintService.batchSummaryPrintQuery(criteria);
        if(resultResponse!=null){
            List<SummaryPrintResult> summaryPrintResults = resultResponse.getData();
            //此操作仅仅一个批次 所以summaryPrintResults只有一条记录
            if(!summaryPrintResults.isEmpty()){
                SummaryPrintResult summaryPrintResult = summaryPrintResults.get(0);
                //组装箱号列表信息
                pojo.setBoxes(makeBoxsDTO(summaryPrintResult));
                //组装运单列表信息
                pojo.setWaybills(makeWaybillsDTO(sendCode,createSiteCode));
                //组装其他描述信息
                pojo.setRemark(makeRemark(summaryPrintResult));
                //设置公共属性
                pojo.setCreateSiteName(summaryPrintResult.getSendSiteName());
                pojo.setReceiveSiteName(summaryPrintResult.getReceiveSiteName());
                pojo.setSendCode(summaryPrintResult.getSendCode());
                pojo.setSendTime(summaryPrintResult.getSendTime());
            }
        }

        return pojo;
    }

    /**
     * 组装其他描述信息
     * @param summaryPrintResult
     * @return
     */
    private String makeRemark(SummaryPrintResult summaryPrintResult){
        int count = 0;
        int boxCount = 0;
        int packCount = 0;
        int requiredPackNum = 0; //应发
        int actualPackNum = 0; //实发
        int sumVolume = 0;
        if(summaryPrintResult.getDetails()!=null && !summaryPrintResult.getDetails().isEmpty()){

            for(SummaryPrintBoxEntity summaryPrintBoxEntity : summaryPrintResult.getDetails()){
                count++;
                if(BusinessUtil.isBoxcode(summaryPrintBoxEntity.getBoxCode())){
                    boxCount++;
                }else{
                    packCount++;
                }
                requiredPackNum += summaryPrintBoxEntity.getPackageBarRecNum();
                actualPackNum += summaryPrintBoxEntity.getPackageBarNum();
                sumVolume += summaryPrintBoxEntity.getVolume();
            }
        }

        return "周转箱："+boxCount+"个，原包："+packCount+"个，合计："+count
                +"个，应发包裹总数："+requiredPackNum+"个，实发包裹总数："+actualPackNum+"个，总体积："+sumVolume+"m³";
    }

    /**
     * 组装箱子信息
     * @param summaryPrintResult
     * @return
     */
    private List<PrintOnlineBoxDTO> makeBoxsDTO(SummaryPrintResult summaryPrintResult){
        List<PrintOnlineBoxDTO> boxes = new ArrayList<>();
        if(summaryPrintResult.getDetails()!=null && !summaryPrintResult.getDetails().isEmpty()){
            for(SummaryPrintBoxEntity summaryPrintBoxEntity : summaryPrintResult.getDetails()){
                PrintOnlineBoxDTO boxDTO = new PrintOnlineBoxDTO();
                boxDTO.setBoxCode(summaryPrintBoxEntity.getBoxCode());
                boxDTO.setPackageNum(summaryPrintBoxEntity.getPackageBarNum());
                boxDTO.setSealCode1(summaryPrintBoxEntity.getSealNo1());
                boxDTO.setSealCode2(summaryPrintBoxEntity.getSealNo2());
                boxDTO.setVolume(summaryPrintBoxEntity.getVolume());
                boxDTO.setWaybillNum(summaryPrintBoxEntity.getWaybillNum());
                boxDTO.setSealTime(summaryPrintBoxEntity.getLockTime());
                boxes.add(boxDTO);
            }
        }
        return boxes;
    }

    /**
     * 组装打印运单维度数据
     * @param sendCode 批次号
     * @param createSiteCode 操作分拣中心
     * @return
     */
    private List<PrintOnlineWaybillDTO> makeWaybillsDTO(String sendCode,Integer createSiteCode){
        return sendPrintService.queryWaybillCountBySendCode(sendCode,createSiteCode);
    }

}
