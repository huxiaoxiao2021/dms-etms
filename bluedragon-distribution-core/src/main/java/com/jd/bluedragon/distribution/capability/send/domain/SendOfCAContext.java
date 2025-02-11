package com.jd.bluedragon.distribution.capability.send.domain;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.board.domain.Board;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import lombok.Data;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *  发货能力域上下文
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2023/8/15
 * @Description:
 */
@Data
public class SendOfCAContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 作业处理条码，如包裹号，运单号，箱号等
     */
    private String barCode;

    /**
     * 发货维度  如 包裹  运单
     */
    private SendDimensionEnum sendDimension;

    /**
     * 发货业务场景
     */
    private SendChainModeEnum sendChain;

    /**
     * 入参转换的的sendM
     */
    private SendM requestTurnToSendM;

    /**
     * 入参
     */
    private SendRequest request;

    /**
     * 出参
     */
    private JdVerifyResponse<SendResult> response;

    /**
     * 运单对象
     */
    private Waybill waybill;

    /**
     * 箱号对象
     */
    private Box box;

    /**
     * 板对象
     */
    private Board board;

    /**
     * 执行回调（对外方式）
     */
    private String callBackDealHandlerAlias;


    /**
     * 校验回调（对外方式）
     */
    private String callBackVerifyHandlerAlias;
}
