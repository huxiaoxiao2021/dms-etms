package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.waybill.domain.BaseResponseIncidental;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingResponse;
import com.jd.bluedragon.distribution.waybill.domain.LabelPrintingRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yanghongqiang on 2015/11/30.
 */
@Service("labelPrinting")
public class  LabelPrintingImpl implements LabelPrinting {

    private static final Logger log = Logger.getLogger(LabelPrintingImpl.class);

    public static final String LOG_PREFIX="包裹标签打印[LabelPrintingWSImpl] ";

    //分拣中心打印实现类
    @Autowired
    private LabelPrintingService dmsLablePrintingService;

//    //备件库打印实现类
//    @Autowired
//    private LabelPrintingService sepwmsLabelPrintingService;

    /**
     * 分拣中心打印接口
     */
    @Override
    public BaseResponseIncidental<LabelPrintingResponse> dmsPrint(LabelPrintingRequest request) {
        try {
            if(request==null){
                log.error(LOG_PREFIX+"分拣中心打印，参数为空");
                return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_PARMAETER,LabelPrintingResponse.MESSAGE_EMPTY_PARMAETER);
            }
            log.info(new StringBuilder(LOG_PREFIX).append("分拣中心打印，参数").append(request.toString()).toString());
            return dmsLablePrintingService.packageLabelPrint(request);
        } catch (Exception e) {
            log.error(LOG_PREFIX+"分拣中心打印接口异常，错误信息："+e.getMessage(), e);
            return new BaseResponseIncidental<LabelPrintingResponse>(JdResponse.CODE_SERVICE_ERROR,JdResponse.MESSAGE_SERVICE_ERROR);
        }
    }

//    /**
//     * 备件库打印接口
//     */
//    @Override
//    public BaseResponseIncidental<LabelPrintingResponse> spwmsPrint(LabelPrintingRequest request) {
//        try {
//            if(request==null){
//                log.error(LOG_PREFIX+"备件库打印，参数为空");
//                return new BaseResponseIncidental<LabelPrintingResponse>(LabelPrintingResponse.CODE_EMPTY_PARMAETER,LabelPrintingResponse.MESSAGE_EMPTY_PARMAETER);
//            }
//            log.info(new StringBuilder(LOG_PREFIX).append("备件库打印，参数").append(request.toString()).toString());
//
//            return sepwmsLabelPrintingService.packageLabelPrint(request);
//        } catch (Exception e) {
//            log.error(LOG_PREFIX+"备件库打印接口异常，错误信息："+e.getMessage(), e);
//            return new BaseResponseIncidental<LabelPrintingResponse>(BaseResponse.CODE_SOMETHING_ERROR,BaseResponse.MESSAGE_SOMETHING_ERROR);
//        }
//    }


}
