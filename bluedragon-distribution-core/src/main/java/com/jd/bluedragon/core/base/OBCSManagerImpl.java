package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.uad.api.claim.facade.claim.ClaimListByClueInfoQueryAPI;
import com.jd.uad.api.claim.facade.claim.resp.ClaimInfoRespDTO;
import com.jd.uad.api.core.APIResultDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service("obcsManager")
public class OBCSManagerImpl implements OBCSManager{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ClaimListByClueInfoQueryAPI claimListByClueInfoQueryAPI;

    /**
     * 获取理赔信息
     *
     * 整合多个理赔单
     *
     *  多个理赔单 按完成 > 理赔中 >无理赔 优先整合
        理赔金额计算理赔完成状态的理赔单加总
        结算主体 和商家事业部ID 均为相同数据 所以默认取一条就可以

     * @param clueType   线索类型   1-青龙运单号,2-盘亏单号,3-大件运单号,4- TC转运单（中小件）,7- ECLP理赔申请单号
     * @param clueValue  线索值
     * @return
     */
    @Override
    public LocalClaimInfoRespDTO getClaimListByClueInfo(int clueType, String clueValue){
        LocalClaimInfoRespDTO respDTO = new LocalClaimInfoRespDTO();
        try{
            APIResultDTO<List<ClaimInfoRespDTO>> apiResultDTO = claimListByClueInfoQueryAPI.getClaimListByClueInfo(clueType,clueValue);
            //理赔金额
            BigDecimal paymentRealMoney = new BigDecimal(0);
            String statusDesc = LocalClaimInfoRespDTO.LP_STATUS_NONE;
            //多个理赔单 按完成 > 理赔中 >无理赔 优先整合
            //理赔金额计算理赔完成状态的理赔单加总
            //结算主体 均为相同数据 所以默认取一条就可以
            if(apiResultDTO.isSuccess()){
                List<ClaimInfoRespDTO> list =  apiResultDTO.getResult();
                if(list!=null && list.size()>0){
                    for(ClaimInfoRespDTO claimInfoRespDTO : list){
                       String tempStatus = BusinessHelper.getLPStatus(claimInfoRespDTO.getStatus());
                       if(LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(tempStatus)){
                           paymentRealMoney = paymentRealMoney.add(claimInfoRespDTO.getPaymentRealMoney());
                           statusDesc = tempStatus;
                           //结算主体
                           respDTO.setSettleSubjectName(claimInfoRespDTO.getSettleSubjectName());
                           respDTO.setSettleSubjectCode(claimInfoRespDTO.getSettleSubjectCode());
                           //物权
                           respDTO.setGoodOwner(claimInfoRespDTO.getGoodOwner());
                           respDTO.setGoodOwnerName(claimInfoRespDTO.getGoodOwnerName());
                       }else if(LocalClaimInfoRespDTO.LP_STATUS_DOING.equals(tempStatus)){
                            if(LocalClaimInfoRespDTO.LP_STATUS_NONE.equals(statusDesc)){
                                statusDesc = tempStatus;
                            }
                       }
                    }

                }
                respDTO.setPaymentRealMoney(paymentRealMoney);
                respDTO.setStatusDesc(statusDesc);
            }else{
                logger.error("理赔接口失败"+clueValue+" 原因："+apiResultDTO.getMessage());
                return null;
            }


        }catch (Exception e){
            logger.error("理赔接口异常"+clueValue+" 原因：",e);
            return null;
        }


        return respDTO;

    }

}
