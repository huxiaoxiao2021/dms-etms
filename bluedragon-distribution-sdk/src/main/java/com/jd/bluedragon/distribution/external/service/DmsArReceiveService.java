package com.jd.bluedragon.distribution.external.service;

import com.jd.bluedragon.distribution.transport.domain.ArSendRegister;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceive;
import com.jd.bluedragon.distribution.transport.domain.ArWaitReceiveRequest;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.domain.ListResponse;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/5/9.
 */
public interface DmsArReceiveService {

    /**
     * 获取箱号对应的航班号和数量
     *
     * @param barcode
     * @return
     */
    JdResponse<ArSendRegister> getArSendRegisterByBarcode(String barcode);

    /**
     * 根据条件查询待提货信息
     *
     * @param request
     * @return
     */
    ListResponse<ArWaitReceive> getARWaitReceive(ArWaitReceiveRequest request);

    /**
     * 根据运力名称（值为航班号/列车号）、发货日期获取发货登记信息
     *
     * @param transportName
     * @param sendDate
     * @return
     */
    JdResponse<List<ArSendRegister>> getArSendRegisterByTransInfo(String transportName, Date sendDate);

}
