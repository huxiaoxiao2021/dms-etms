package com.jd.bluedragon.distribution.goodsPrint.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月16日 16时:31分
 */
@Service("goodsPrintService")
public class GoodsPrintServiceImpl implements GoodsPrintService {
    @Override
    public List<GoodsPrintDto> query(GoodsPrintDto goodsPrintDto) {
        List<GoodsPrintDto> result = Lists.newArrayList();
        if (goodsPrintDto == null || goodsPrintDto.getSendCode() == null) {
            return result;
        }
        for (int i = 1; i < 100000; i++) {
            GoodsPrintDto goodsPrintDto1 = new GoodsPrintDto();
            goodsPrintDto1.setBoxCode("box" + i);
            goodsPrintDto1.setSendCode("sendcode" + i);
            goodsPrintDto1.setConsignWare("这是什么" + i);
            goodsPrintDto1.setOperateTime(new Date());
            goodsPrintDto1.setCreateSiteName("马驹桥");
            goodsPrintDto1.setReceiveSiteName("上海");
            goodsPrintDto1.setVendorId("11111" + i);
            goodsPrintDto1.setWaybillCode("waybill" + i);
            result.add(goodsPrintDto1);
        }
        return result;
    }

    @Override
    public List<List<Object>> export(GoodsPrintDto goodsPrintDto) {
        List<List<Object>> resList = new ArrayList<List<Object>>();
        List<Object> heads = new ArrayList<Object>();

        //添加表头
        heads.add("发货批次号");
        heads.add("发货日期");
        heads.add("始发网点");
        heads.add("目的网点");
        heads.add("箱号");
        heads.add("订单号");
        heads.add("运单号");
        heads.add("托寄物品名");
        resList.add(heads);

        List<GoodsPrintDto> data = query(goodsPrintDto);
        if (data != null && data.size() > 0) {
            for (GoodsPrintDto goodsPrintDto1 : data) {
                List<Object> body = Lists.newArrayList();
                body.add(goodsPrintDto1.getSendCode());
                body.add(goodsPrintDto1.getOperateTime() == null ? null : DateHelper.formatDate(goodsPrintDto1.getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDD));
                body.add(goodsPrintDto1.getCreateSiteName());
                body.add(goodsPrintDto1.getReceiveSiteName());
                body.add(goodsPrintDto1.getBoxCode());
                body.add(goodsPrintDto1.getVendorId());
                body.add(goodsPrintDto1.getWaybillCode());
                body.add(goodsPrintDto1.getConsignWare());
                resList.add(body);
            }
        }
        return resList;
    }
}
