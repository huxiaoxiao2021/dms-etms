package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.InvoiceParameters;
import com.jd.invoice.domain.InvoiceTemplate;
import com.jd.invoice.service.jsf.PrintTableJsfService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 调用财务生成发票
 * Created by wangtingwei on 2016/4/8.
 */
@Service("invoicePrintService")
public class DefaultInvoicePrintServiceImpl implements InvoicePrintService {

    private static final Log logger= LogFactory.getLog(DefaultInvoicePrintServiceImpl.class);

    private static final String INVOICE_SUCCESS=String.valueOf(1);

    @Qualifier("invoiceJsfService")
    @Autowired
    private PrintTableJsfService invoiceJsfService;

    @Override
    public InvokeResult<String> generateInvoice(InvoiceParameters parameters) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.invoiceJsfService.generateInvoice", Constants.UMP_APP_NAME_DMSWEB,false, true);
        InvokeResult<String> result=new InvokeResult<String>();
        try {
            InvoiceTemplate templte= invoiceJsfService.getInvoiceTemplateAndPrintNew(parameters.getOrderId(),parameters.getCky2(),parameters.getStoreId(), parameters.getErpAccount(),parameters.getRealName());
            if(logger.isInfoEnabled()){
                logger.info(MessageFormat.format("发票结果:{0}",JsonHelper.toJson(templte)));
            }
            if(INVOICE_SUCCESS.equals(templte.getResultCode())){
                result.success();
                result.setData(templte.getHtmlTemplate());
            }else{
                result.customMessage(InvokeResult.RESULT_NULL_CODE,templte.getResultMessage());
            }
        }catch (Throwable e){
            result.error(e);
            logger.error("生成发票",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
}
