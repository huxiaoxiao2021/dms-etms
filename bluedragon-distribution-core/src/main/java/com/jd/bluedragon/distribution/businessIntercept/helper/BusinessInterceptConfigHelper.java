package com.jd.bluedragon.distribution.businessIntercept.helper;

import com.jd.ql.dms.common.constants.DisposeNodeConstants;
import com.jd.ql.dms.common.constants.OperateDeviceTypeConstants;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 拦截配置
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-05-12 16:53:09 周三
 */
@Component
public class BusinessInterceptConfigHelper {

    // 拦截报表操作节点【分拣】类型
    @Value("${businessIntercept.operate.node.sorting}")
    private Integer interceptOperateNodeSorting;
    // 拦截报表操作节点【发货】类型
    @Value("${businessIntercept.operate.node.send}")
    private Integer interceptOperateNodeSend;
    // 拦截报表操作节点【打印】类型
    @Value("${businessIntercept.operate.node.print}")
    private Integer interceptOperateNodePrint;
    // 拦截报表操作节点【称重】类型
    @Value("${businessIntercept.operate.node.measureWeight}")
    private Integer interceptOperateNodeMeasureWeight;

    // 拦截报表设备【PDA】类型
    @Value("${businessIntercept.device.type.pda}")
    private Integer interceptOperateDeviceTypePda;
    // 拦截报表设备【自动化】设备类型
    @Value("${businessIntercept.device.type.automatic}")
    private Integer interceptOperateDeviceTypeAutomatic;
    // 拦截报表设备【dws】设备类型
    @Value("${businessIntercept.device.type.printClient}")
    private Integer interceptOperateDeviceTypePrintClient;
    // 拦截报表设备【dws】设备类型
    @Value("${businessIntercept.device.type.dws}")
    private Integer interceptOperateDeviceTypeDws;

    // 拦截报表处理节点【换单打印】类型
    @Value("${businessIntercept.dispose.node.exchangeWaybill}")
    private Integer interceptDisposeNodeExchangeWaybill;
    // 拦截报表处理节点【补称重】类型
    @Value("${businessIntercept.dispose.node.finishWeight}")
    private Integer interceptDisposeNodeFinishWeight;
    // 拦截报表处理节点【补打】类型
    @Value("${businessIntercept.dispose.node.reprint}")
    private Integer interceptDisposeNodeReprint;
    // 拦截报表处理节点【拆包】类型
    @Value("${businessIntercept.dispose.node.unpack}")
    private Integer interceptDisposeNodeUnpack;

    // 【PDA】操作后取消类型的拦截码
    @Value("${businessIntercept.pda.interceptCode.waybillCancel}")
    private String pdaInterceptCodeWaybillCancel;
    // 【自动化设备】操作后取消类型的拦截码
    @Value("${businessIntercept.automatic.interceptCode.waybillCancel}")
    private String automaticInterceptCodeWaybillCancel;

    public Integer getOperateNodeByConstants(Integer operateNode){
        switch (operateNode){
            case OperateNodeConstants.SORTING: {
                return interceptOperateNodeSorting;
            }
            case OperateNodeConstants.SEND: {
                return interceptOperateNodeSend;
            }
            case OperateNodeConstants.PRINT: {
                return interceptOperateNodePrint;
            }
            case OperateNodeConstants.MEASURE_WEIGHT: {
                return interceptOperateNodeMeasureWeight;
            }
            default:
                return interceptOperateNodeSorting;
        }
    }

    public Integer getOperateDeviceTypeByConstants(Integer deviceType){
        switch (deviceType){
            case OperateDeviceTypeConstants.PDA: {
                return interceptOperateDeviceTypePda;
            }
            case OperateDeviceTypeConstants.AUTOMATIC: {
                return interceptOperateDeviceTypeAutomatic;
            }
            case OperateDeviceTypeConstants.PRINT_CLIENT: {
                return interceptOperateDeviceTypePrintClient;
            }
            case OperateDeviceTypeConstants.MACHINE_DWS: {
                return interceptOperateDeviceTypeDws;
            }
            default:
                return interceptOperateDeviceTypePda;
        }
    }

    public Integer getDisposeNodeByConstants(Integer disposeNode){
        switch (disposeNode){
            case DisposeNodeConstants.EXCHANGE_WAYBILL: {
                return interceptDisposeNodeExchangeWaybill;
            }
            case DisposeNodeConstants.FINISH_WEIGHT: {
                return interceptDisposeNodeFinishWeight;
            }
            case DisposeNodeConstants.REPRINT: {
                return interceptDisposeNodeReprint;
            }
            case DisposeNodeConstants.UPPACK: {
                return interceptDisposeNodeUnpack;
            }
            default:
                return interceptDisposeNodeExchangeWaybill;
        }
    }

    public Integer getInterceptOperateNodeSorting() {
        return interceptOperateNodeSorting;
    }

    public BusinessInterceptConfigHelper setInterceptOperateNodeSorting(Integer interceptOperateNodeSorting) {
        this.interceptOperateNodeSorting = interceptOperateNodeSorting;
        return this;
    }

    public Integer getInterceptOperateNodeSend() {
        return interceptOperateNodeSend;
    }

    public BusinessInterceptConfigHelper setInterceptOperateNodeSend(Integer interceptOperateNodeSend) {
        this.interceptOperateNodeSend = interceptOperateNodeSend;
        return this;
    }

    public Integer getInterceptOperateNodePrint() {
        return interceptOperateNodePrint;
    }

    public BusinessInterceptConfigHelper setInterceptOperateNodePrint(Integer interceptOperateNodePrint) {
        this.interceptOperateNodePrint = interceptOperateNodePrint;
        return this;
    }

    public Integer getInterceptOperateNodeMeasureWeight() {
        return interceptOperateNodeMeasureWeight;
    }

    public BusinessInterceptConfigHelper setInterceptOperateNodeMeasureWeight(Integer interceptOperateNodeMeasureWeight) {
        this.interceptOperateNodeMeasureWeight = interceptOperateNodeMeasureWeight;
        return this;
    }

    public Integer getInterceptOperateDeviceTypePda() {
        return interceptOperateDeviceTypePda;
    }

    public BusinessInterceptConfigHelper setInterceptOperateDeviceTypePda(Integer interceptOperateDeviceTypePda) {
        this.interceptOperateDeviceTypePda = interceptOperateDeviceTypePda;
        return this;
    }

    public Integer getInterceptOperateDeviceTypeAutomatic() {
        return interceptOperateDeviceTypeAutomatic;
    }

    public BusinessInterceptConfigHelper setInterceptOperateDeviceTypeAutomatic(Integer interceptOperateDeviceTypeAutomatic) {
        this.interceptOperateDeviceTypeAutomatic = interceptOperateDeviceTypeAutomatic;
        return this;
    }

    public Integer getInterceptOperateDeviceTypePrintClient() {
        return interceptOperateDeviceTypePrintClient;
    }

    public BusinessInterceptConfigHelper setInterceptOperateDeviceTypePrintClient(Integer interceptOperateDeviceTypePrintClient) {
        this.interceptOperateDeviceTypePrintClient = interceptOperateDeviceTypePrintClient;
        return this;
    }

    public Integer getInterceptOperateDeviceTypeDws() {
        return interceptOperateDeviceTypeDws;
    }

    public BusinessInterceptConfigHelper setInterceptOperateDeviceTypeDws(Integer interceptOperateDeviceTypeDws) {
        this.interceptOperateDeviceTypeDws = interceptOperateDeviceTypeDws;
        return this;
    }

    public Integer getInterceptDisposeNodeExchangeWaybill() {
        return interceptDisposeNodeExchangeWaybill;
    }

    public BusinessInterceptConfigHelper setInterceptDisposeNodeExchangeWaybill(Integer interceptDisposeNodeExchangeWaybill) {
        this.interceptDisposeNodeExchangeWaybill = interceptDisposeNodeExchangeWaybill;
        return this;
    }

    public Integer getInterceptDisposeNodeFinishWeight() {
        return interceptDisposeNodeFinishWeight;
    }

    public BusinessInterceptConfigHelper setInterceptDisposeNodeFinishWeight(Integer interceptDisposeNodeFinishWeight) {
        this.interceptDisposeNodeFinishWeight = interceptDisposeNodeFinishWeight;
        return this;
    }

    public Integer getInterceptDisposeNodeReprint() {
        return interceptDisposeNodeReprint;
    }

    public BusinessInterceptConfigHelper setInterceptDisposeNodeReprint(Integer interceptDisposeNodeReprint) {
        this.interceptDisposeNodeReprint = interceptDisposeNodeReprint;
        return this;
    }

    public Integer getInterceptDisposeNodeUnpack() {
        return interceptDisposeNodeUnpack;
    }

    public BusinessInterceptConfigHelper setInterceptDisposeNodeUnpack(Integer interceptDisposeNodeUnpack) {
        this.interceptDisposeNodeUnpack = interceptDisposeNodeUnpack;
        return this;
    }

    public String getPdaInterceptCodeWaybillCancel() {
        return pdaInterceptCodeWaybillCancel;
    }

    public BusinessInterceptConfigHelper setPdaInterceptCodeWaybillCancel(String pdaInterceptCodeWaybillCancel) {
        this.pdaInterceptCodeWaybillCancel = pdaInterceptCodeWaybillCancel;
        return this;
    }

    public String getAutomaticInterceptCodeWaybillCancel() {
        return automaticInterceptCodeWaybillCancel;
    }

    public BusinessInterceptConfigHelper setAutomaticInterceptCodeWaybillCancel(String automaticInterceptCodeWaybillCancel) {
        this.automaticInterceptCodeWaybillCancel = automaticInterceptCodeWaybillCancel;
        return this;
    }
}
