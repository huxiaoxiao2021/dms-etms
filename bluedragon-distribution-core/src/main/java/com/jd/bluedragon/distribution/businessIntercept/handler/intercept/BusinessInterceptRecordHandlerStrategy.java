package com.jd.bluedragon.distribution.businessIntercept.handler.intercept;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 拦截报表处理策略类
 *
 * @author fanggang7
 * @since 2020-12-23 09:33:12 周三
 **/
@Service
public class BusinessInterceptRecordHandlerStrategy {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("businessInterceptRecordPackageHandler")
    private IBusinessInterceptRecordHandler businessInterceptRecordPackageHandler;

    @Autowired
    @Qualifier("businessInterceptRecordWaybillHandler")
    private IBusinessInterceptRecordHandler businessInterceptRecordWaybillHandler;

    @Autowired
    @Qualifier("businessInterceptRecordBoxHandler")
    private IBusinessInterceptRecordHandler businessInterceptRecordBoxHandler;

    public Response<Boolean> handle(SaveInterceptMsgDto msgDto) throws RuntimeException {

        /* 1. 按包裹提交 */
        if (Objects.equals(BusinessUtil.getBarCodeType(msgDto.getBarCode()), BarCodeType.PACKAGE_CODE)) {
            return businessInterceptRecordPackageHandler.handle(msgDto);
        }

        /* 2. 按运单提交 */
        if (Objects.equals(BusinessUtil.getBarCodeType(msgDto.getBarCode()), BarCodeType.WAYBILL_CODE)) {
            return businessInterceptRecordWaybillHandler.handle(msgDto);
        }

        /* 3. 按箱提交 */
        if (Objects.equals(BusinessUtil.getBarCodeType(msgDto.getBarCode()), BarCodeType.BOX_CODE)) {
            return businessInterceptRecordBoxHandler.handle(msgDto);
        }

        log.error("根据单号无法获取该数据的拦截消息处理策略：{}", JsonHelper.toJson(msgDto));
        // throw new RuntimeException(MessageFormat.format("获取该单号的拦截消息处理器失败：{0}",JsonHelper.toJson(msgDto)));
        Response<Boolean> result = new Response<Boolean>();
        result.toSucceed();
        result.setData(true);
        return result;
    }

}
