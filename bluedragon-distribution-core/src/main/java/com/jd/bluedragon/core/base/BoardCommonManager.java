package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.dto.unloadCar.UnloadScanDetailDto;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.api.request.TransportServiceRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.transboard.api.dto.Board;

import java.util.Map;

/**
 * 组板公用逻辑
 *
 * @author: hujiping
 * @date: 2020/6/29 10:58
 */
public interface BoardCommonManager {

    /**
     * 箱/包裹是否发货校验
     * @param request
     * @return
     */
    InvokeResult isSendCheck(BoardCommonRequest request);

    /**
     * 包裹数限制
     * @param boardCode 板号
     * @param maxCount 包裹数最大限制
     * @return
     */
    InvokeResult packageCountCheck(String boardCode, Integer maxCount);

    /**
     * VER组板拦截校验
     * @param request
     * @return
     */
    InvokeResult boardCombinationCheck(BoardCommonRequest request);

    /**
     * 发送组板全程跟踪
     * @param request
     * @param operateType 操作类型
     */
    void sendWaybillTrace(BoardCommonRequest request, Integer operateType);

    /**
     * 创建单个组板
     * @param request
     * @return
     */
    InvokeResult<Board> createBoardCode(BoardCommonRequest request);

    /**
     * 组板转移
     * <p>
     *  1、将包裹号/箱号从原来的板上取消，绑定到新板
     *  2、发送取消旧板的全称跟踪和组到新板的全称跟踪
     * <p/>
     * @param request
     * @return
     */
    InvokeResult<String> boardMove(BoardCommonRequest request);

    /**
     * 组板转移(不校验板状态)
     * <p>
     *  1、将包裹号/箱号从原来的板上取消，绑定到新板
     *  2、发送取消旧板的全称跟踪和组到新板的全称跟踪
     * <p/>
     * @param request
     * @return
     */
    InvokeResult<String> boardMoveIgnoreStatus(BoardCommonRequest request);

    /**
     * 获取路由下一跳
     * @param waybillCode 运单号
     * @param siteCode 当前站点
     * @return
     */
    Integer getNextSiteCodeByRouter(String waybillCode, Integer siteCode);

    /**
     * 装卸车的拦截校验
     * @param barCode
     * @return
     */
    InvokeResult<String> interceptValidateUnloadCar(String barCode);

    /***
     * KA运单拦截
     */
    InvokeResult<String> kaWaybillCheck(String barCode, String waybillSign, InvokeResult<String> result);

    /**
     * 装卸车的拦截校验
     */
    InvokeResult<Boolean> loadUnloadInterceptValidate(String waybillCode, String waybillSign);

    /**
     * 装卸车的拦截校验
     *  hint：包含运单和包裹维度
     * @param transportServiceRequest
     * @return
     */
    InvokeResult<Boolean> loadUnloadInterceptValidate(TransportServiceRequest transportServiceRequest);

}
