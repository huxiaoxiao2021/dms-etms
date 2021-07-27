package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.working.WorkingClockRecordVo;
import com.jd.bluedragon.common.dto.working.WorkingClockRequest;
import com.jd.bluedragon.common.dto.working.WorkingPage;
import com.jd.bluedragon.core.base.WorkingClockDataManager;
import com.jd.bluedragon.external.gateway.service.WorkingClockService;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.base.Pager;
import com.jd.dms.wb.report.api.working.dto.WorkingClockDTO;
import com.jd.dms.wb.report.api.working.dto.WorkingClockRecordPdaVO;
import com.jd.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WorkingClockServiceImpl implements WorkingClockService {
    @Resource
    private WorkingClockDataManager workingClockDataManager;

    @Override
    public JdCResponse<WorkingPage<WorkingClockRecordVo>> pdaLocalClockRecord(WorkingClockRequest dto) {
        WorkingClockDTO req = getWorkingClockDTO(dto);

        BaseEntity<Pager<WorkingClockRecordPdaVO>> entity = workingClockDataManager.pdaLocalClockRecord(req);

        return getPageResponse(entity);
    }


    @Override
    public JdCResponse<WorkingPage<WorkingClockRecordVo>> pdaGroupMembersClockRecord(WorkingClockRequest dto) {
        WorkingClockDTO req = getWorkingClockDTO(dto);

        BaseEntity<Pager<WorkingClockRecordPdaVO>> entity = workingClockDataManager.pdaGroupMembersClockRecord(req);

        return getPageResponse(entity);
    }

    @Override
    public JdCResponse<Object> clock(WorkingClockRequest dto) {
        WorkingClockDTO req = getWorkingClockDTO(dto);

        BaseEntity<Object> entity = workingClockDataManager.clock(req);

        return getObjectResponse(entity);
    }

    @Override
    public JdCResponse<Object> disabledClock(WorkingClockRequest dto) {
        WorkingClockDTO req = getWorkingClockDTO(dto);

        BaseEntity<Object> entity = workingClockDataManager.disabledClock(req);

        return getObjectResponse(entity);
    }

    @Override
    public JdCResponse<Object> updateClockTime(WorkingClockRequest dto) {
        WorkingClockDTO req = getWorkingClockDTO(dto);

        BaseEntity<Object> entity = workingClockDataManager.updateClockTime(req);

        return getObjectResponse(entity);
    }

    @Override
    public JdCResponse<Object> confirmClockHours(WorkingClockRequest dto) {
        WorkingClockDTO req = getWorkingClockDTO(dto);

        BaseEntity<Object> entity = workingClockDataManager.confirmClockHours(req);

        return getObjectResponse(entity);
    }

    private WorkingClockDTO getWorkingClockDTO(WorkingClockRequest dto) {
        if (log.isInfoEnabled()) {
            log.info("计提打卡请求参数:{}", JSON.toJSONString(dto));
        }

        WorkingClockDTO req = new WorkingClockDTO();
        BeanUtils.copyProperties(dto, req);
        return req;
    }

    private JdCResponse<WorkingPage<WorkingClockRecordVo>> getPageResponse(BaseEntity<Pager<WorkingClockRecordPdaVO>> entity) {
        if (log.isInfoEnabled()) {
            log.info("计提打卡list响应:{}", JSON.toJSONString(entity));
        }
        JdCResponse<WorkingPage<WorkingClockRecordVo>> res = new JdCResponse<>();

        if (!entity.isSuccess()) {
            res.init(entity.getCode(), entity.getMessage());
            return res;
        }
        res.init(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        if (entity.getData() != null) {
            WorkingPage<WorkingClockRecordVo> page = new WorkingPage<>();
            res.setData(page);

            BeanUtils.copyProperties(entity.getData(), page);
            List<WorkingClockRecordPdaVO> data = entity.getData().getData();
            if (data != null) {
                List<WorkingClockRecordVo> list = new ArrayList<>();
                page.setData(list);

                for (WorkingClockRecordPdaVO d : data) {
                    WorkingClockRecordVo vo = new WorkingClockRecordVo();
                    BeanUtils.copyProperties(d, vo);
                    list.add(vo);
                }
            }
        }
        return res;
    }

    private JdCResponse<Object> getObjectResponse(BaseEntity<Object> entity ) {
        if (log.isInfoEnabled()) {
            log.info("计提打卡响应:{}", JSON.toJSONString(entity));
        }
        JdCResponse<Object> res = new JdCResponse<>();

        if (!entity.isSuccess()) {
            res.init(entity.getCode(), entity.getMessage());
            return res;
        }

        res.setData(entity.getData());
        res.init(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);

        return res;
    }
}
