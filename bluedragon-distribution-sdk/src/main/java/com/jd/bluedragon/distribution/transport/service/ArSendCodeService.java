package com.jd.bluedragon.distribution.transport.service;

import com.jd.bluedragon.distribution.transport.domain.ArSendCode;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 * @author lixin39
 * @ClassName: ArSendCodeService
 * @Description: 发货批次表--Service接口
 * @date 2017年12月28日 09:46:12
 */
public interface ArSendCodeService extends Service<ArSendCode> {

    /**
     * 新增
     *
     * @param arSendCode
     * @return
     */
    boolean insert(ArSendCode arSendCode);

    /**
     * 修改
     *
     * @param arSendCode
     * @return
     */
    boolean update(ArSendCode arSendCode);

    /**
     * 批量新增
     *
     * @param sendRegisterId
     * @param sendCodes
     * @return
     */
    boolean batchAdd(Long sendRegisterId, String[] sendCodes, String createUser);

    /**
     * 根据单号获取批次号
     *
     * @param sendRegisterId
     * @return
     */
    List<ArSendCode> getBySendRegisterId(Long sendRegisterId);

    /**
     * 根据单号批量获取批次号
     *
     * @param sendRegisterIds
     * @return
     */
    List<ArSendCode> getBySendRegisterIds(List<Long> sendRegisterIds);

    /**
     * 根据单号删除批次号
     *
     * @param sendRegisterId
     * @return
     */
    boolean deleteBySendRegisterId(Long sendRegisterId, String userCode);

    /**
     *根据批次号查对应的sendRegisterId
     * @param sendCode
     * @return
     */
    ArSendCode getBySendCode(String sendCode);
}
