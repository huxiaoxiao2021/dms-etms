package com.jd.bluedragon.distribution.printOnline.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.JdwlSignManager;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineBoxDTO;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineModalDTO;
import com.jd.bluedragon.distribution.printOnline.domain.PrintOnlineWaybillDTO;
import com.jd.bluedragon.distribution.printOnline.service.IPrintOnlineService;
import com.jd.bluedragon.distribution.sendprint.domain.PrintQueryCriteria;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintBoxEntity;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResult;
import com.jd.bluedragon.distribution.sendprint.domain.SummaryPrintResultResponse;
import com.jd.bluedragon.distribution.sendprint.service.SendPrintService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 无纸化线上打印业务实现类
 *
 * 刘铎
 * 2019年9月26日17:18:38
 */
@Service("printOnlineService")
public class PrintOnlineServiceImpl implements IPrintOnlineService {


    private Logger log = LoggerFactory.getLogger(PrintOnlineServiceImpl.class);

    @Autowired
    private SendPrintService sendPrintService;
    @Autowired
    private JdwlSignManager jdwlSignManager;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 逆向交接清单 线上签逻辑
     * 批次维度推送区块链
     * @param sendCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMSWORKER.PrintOnlineServiceImpl.reversePrintOnline", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWORKER)
    public boolean reversePrintOnline(String sendCode) {
        try{
            if(!sysConfigService.getConfigByName("reverse.print.online.switch")){
                return true;
            }
            if(!BusinessUtil.isSendCode(sendCode)){
                return true;
            }
            //根据批次号获取发货数据
            PrintOnlineModalDTO modalDTO = makeModalDTO(sendCode);
            if(modalDTO!=null){
                //推送数据
                return jdwlSignManager.aciton(modalDTO);
            }else{
                //无数据直接返回
                return true;
            }
        }catch (Exception e){
            log.error("逆向交接清单线上签异常:{}",sendCode,e);
            return false;
        }

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
        if(resultResponse != null){
            List<SummaryPrintResult> summaryPrintResults = resultResponse.getData();
            //此操作仅仅一个批次 所以summaryPrintResults只有一条记录
            if(CollectionUtils.isNotEmpty(summaryPrintResults)) {
                SummaryPrintResult summaryPrintResult = summaryPrintResults.get(0);
                //组装箱号列表信息
                pojo.setBoxes(makeBoxsDTO(summaryPrintResult));
                //组装运单列表信息
                pojo.setWaybills(makeWaybillsDTO(sendCode, createSiteCode));
                //组装其他描述信息
                pojo.setRemark(makeRemark(summaryPrintResult));
                //设置公共属性
                pojo.setCreateSiteName(summaryPrintResult.getSendSiteName());
                pojo.setReceiveSiteName(summaryPrintResult.getReceiveSiteName());
                pojo.setSendCode(summaryPrintResult.getSendCode());
                try {
                    if (StringUtils.isNotBlank(summaryPrintResult.getSendTime())) {
                        pojo.setSendTime(DateHelper.parseAllFormatDateTime(summaryPrintResult.getSendTime()));
                    }
                } catch (Exception e) {
                    log.error("线上签组装发货时间异常:{}" , summaryPrintResult.getSendTime(), e);
                }
            }else{
                log.warn("线上签无组装数据:{}" , sendCode);
                return null;
            }
        }else{
            log.warn("线上签组装数据接口返回空:{}" , sendCode);
            return null;
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
                try{
                    if(StringUtils.isNotBlank(summaryPrintBoxEntity.getLockTime())){
                        boxDTO.setSealTime(DateHelper.parseAllFormatDateTime(summaryPrintBoxEntity.getLockTime()));
                    }
                }catch (Exception e){
                    log.error("线上签组装锁时间异常:{}",summaryPrintBoxEntity.getLockTime(),e);
                }

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
