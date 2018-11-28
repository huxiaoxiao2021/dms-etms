package com.jd.bluedragon.distribution.signAndReturn.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.signAndReturn.SignReturnService;
import com.jd.bluedragon.distribution.signAndReturn.dao.SignReturnDao;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ExportExcelDownFee;
import com.jd.etms.waybill.api.WaybillQueryApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.DeliverInfoDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName: SignReturnServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/27 18:21
 */
@Service("signReturnService")
public class SignReturnServiceImpl implements SignReturnService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SignReturnDao signReturnDao;
    @Autowired
    private WaybillQueryApi waybillQueryApi;

    /**
     * 导出
     * @param condition
     * @param response
     */
    @Override
    public void toExport(SignReturnCondition condition, HttpServletResponse response) {

        try {
            //例子开始
            List<SignReturnPrintM> list = new ArrayList<SignReturnPrintM>();
            SignReturnPrintM signReturn = new SignReturnPrintM();
            signReturn.setBusiId(1);
            signReturn.setBusiName("京东");
            signReturn.setMergeCount(new Random().nextInt(1000));
            signReturn.setMergedWaybillCode("VA123321123");
            signReturn.setOperateTime(new Date());
            signReturn.setOperateUser("张三");
            signReturn.setOrgId("1234");
            signReturn.setReturnCycle("每周一次");
            list.add(signReturn);
            //例子结束
            HSSFWorkbook workbook = new HSSFWorkbook();
            OutputStream out = response.getOutputStream();
            //接下来循环list放到Excel表中
            //文件标题
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            String nowdate = formatter1.format(new Date());
            String title = null;
            title = "签单返回合单打印交接单-" + nowdate + ".xls";
            //TODO 第一个sheet页
            String sheettitle = "签单返回合单主表";
            String[] headers = new String[] {"签单返回合单运单号","商家编码","商家名称","返单周期","合单操作日期","合单操作机构","合单操作人","合单运单数"};
            List<Object[]> dataList = new ArrayList<Object[]>();
            Object[] objs = null;
            for(SignReturnPrintM signReturnPrintM : list){
                objs = new Object[headers.length];
                objs[0] = signReturnPrintM.getMergedWaybillCode();
                objs[1] = signReturnPrintM.getBusiId();
                objs[2] = signReturnPrintM.getBusiName();
                objs[3] = signReturnPrintM.getReturnCycle();
                objs[4] = DateHelper.formatDate(signReturnPrintM.getOperateTime());
                objs[5] = signReturnPrintM.getOrgId();
                objs[6] = signReturnPrintM.getOperateUser();
                objs[7] = signReturnPrintM.getMergeCount();
                dataList.add(objs);
            }
            //TODO 第二个sheet页
            String sheettitle1 = "合单的运单号明细";
            String[] headers1 = new String[] {"运单号","妥投时间"};
            List<Object[]> dataList1 = new ArrayList<Object[]>();
            Object[] objs1 = null;
            for(SignReturnPrintM signReturnPrintM : list){
                objs1 = new Object[headers1.length];
//                objs1[0] = signReturnPrintM.getWaybillCode();
//                objs1[1] = DateHelper.formatDate(signReturnPrintM.getDeliveredTime(), Constants.DATE_TIME_FORMAT);
                dataList1.add(objs1);
            }

            //使用流将数据导出
            //防止中文乱码
            String headStr = "attachment; filename=\"" + new String( title.getBytes("gb2312"), "ISO8859-1" ) + "\"";
            response.setContentType("octets/stream");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            ExportExcelDownFee ex ;
            ex = new ExportExcelDownFee(sheettitle, headers, dataList);
            ex.export(workbook,out);

            ex = new ExportExcelDownFee(sheettitle1, headers1, dataList1);
            ex.export(workbook,out);

            workbook.write(out);
            out.close();
        } catch (Exception e) {
            this.logger.error("根据"+condition.getWaybillCode()+"/"+condition.getWaybillCodeInMerged()+"导出excel失败!");
        }

    }

    @Override
    public PagerResult<SignReturnPrintM> getListByWaybillCode(SignReturnCondition condition) {

        List<SignReturnPrintM> signReturnPrintMList = signReturnDao.getListByWaybillCode(condition);
        List<SignReturnPrintM> signReturnPrintMListD = new ArrayList<SignReturnPrintM>();
        //获取妥投时间和返单周期
        for(SignReturnPrintM signReturnPrintM : signReturnPrintMList){
            signReturnPrintM.setReturnCycle("");

            List<String> list = new ArrayList<String>();
            Map<String,Date> map = new HashMap<String,Date>();
            //根据运单号获取妥投时间
            BaseEntity<DeliverInfoDto> deliverInfo = null;
            for(String waybillCode : list){
                deliverInfo = waybillQueryApi.getDeliverInfo(waybillCode);
                if(deliverInfo.getResultCode() == 1){
                    if(deliverInfo.getData() != null && deliverInfo.getData().getCreateTime() != null){
                        map.put(waybillCode,deliverInfo.getData().getCreateTime());
                    }
                }else {
                    map.put(waybillCode,null);
                }
            }
            signReturnPrintM.setMap(map);
            signReturnPrintMListD.add(signReturnPrintM);
        }
        PagerResult<SignReturnPrintM> result = new PagerResult<SignReturnPrintM>();
        result.setRows(signReturnPrintMListD);
        result.setTotal(signReturnPrintMListD.size());
        return null;
    }
}
