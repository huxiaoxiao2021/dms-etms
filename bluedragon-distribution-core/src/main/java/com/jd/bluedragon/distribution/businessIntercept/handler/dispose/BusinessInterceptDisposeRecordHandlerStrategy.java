package com.jd.bluedragon.distribution.businessIntercept.handler.dispose;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveDisposeAfterInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * 拦截报表处理
 *
 * @author fanggang7
 * @since 2020-12-23 09:33:12 周三
 **/
@Service
public class BusinessInterceptDisposeRecordHandlerStrategy {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("businessInterceptPackageRecordHandler")
    private IBusinessInterceptDisposeRecordHandler businessInterceptPackageRecordHandler;

    @Autowired
    @Qualifier("businessInterceptWaybillRecordHandler")
    private IBusinessInterceptDisposeRecordHandler businessInterceptWaybillRecordHandler;

    @Autowired
    @Qualifier("businessInterceptBoxRecordHandler")
    private IBusinessInterceptDisposeRecordHandler businessInterceptBoxRecordHandler;

    public Response<Boolean> handle(SaveDisposeAfterInterceptMsgDto msgDto) throws RuntimeException {

        /* 1. 按包裹提交 */
        if (Objects.equals(BusinessUtil.getBarCodeType(msgDto.getBarCode()), BarCodeType.PACKAGE_CODE)) {
            return businessInterceptPackageRecordHandler.handle(msgDto);
        }

        /* 2. 按运单提交 */
        if (Objects.equals(BusinessUtil.getBarCodeType(msgDto.getBarCode()), BarCodeType.WAYBILL_CODE)) {
            return businessInterceptWaybillRecordHandler.handle(msgDto);
        }

        /* 3. 按箱提交 */
        if (Objects.equals(BusinessUtil.getBarCodeType(msgDto.getBarCode()), BarCodeType.BOX_CODE)) {
            return businessInterceptBoxRecordHandler.handle(msgDto);
        }

        log.warn("根据单号无法获取该数据的拦截消息处理策略：{}", JsonHelper.toJson(msgDto));
        // throw new RuntimeException(MessageFormat.format("获取该单号的拦截消息处理器失败：{0}",JsonHelper.toJson(msgDto)));
        Response<Boolean> result = new Response<Boolean>();
        result.toSucceed();
        result.setData(true);
        return result;
    }

}
