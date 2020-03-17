package com.jd.bluedragon.distribution.material.dao;

import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

public interface MaterialSendDao extends Dao<DmsMaterialSend> {

    boolean insert(DmsMaterialSend record);

    int batchInsertOnDuplicate(List<DmsMaterialSend> list);

    int deleteBySendCode(String sendCode, Long createSiteCode);

    int logicalDeleteBatchSendBySendCode(String sendCode, Long createSiteCode);

    List<DmsMaterialSend> listBySendCode(String sendCode, Long createSiteCode);
}