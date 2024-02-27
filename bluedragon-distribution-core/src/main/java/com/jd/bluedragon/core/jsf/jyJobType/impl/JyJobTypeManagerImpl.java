package com.jd.bluedragon.core.jsf.jyJobType.impl;

import com.jd.bluedragon.core.jsf.jyJobType.JyJobTypeManager;
import com.jdl.basic.api.domain.jyJobType.JyJobType;
import com.jdl.basic.api.service.jyJobType.JyJobTypeJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author pengchong28
 * @description 拣运工种服务实现
 * @date 2024/2/25
 */
@Slf4j
@Service("jyJobTypeService")
public class JyJobTypeManagerImpl implements JyJobTypeManager {
    @Autowired
    private JyJobTypeJsfService jyJobTypeJsfService;

    /**
     * 获取所有JyJobType对象的列表
     * @return 返回JyJobType对象的列表
     */
    @Override
    public List<JyJobType> getAll() {
        List<JyJobType> result = new ArrayList<>();
        try {
            result = jyJobTypeJsfService.queryAllList();
        } catch (Exception e) {
            log.error("JyJobTypeServiceImpl.getAll error,异常信息:【{}】", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 获取所有可用的JyJobType列表
     * @return 返回所有可用的JyJobType列表
     */
    @Override
    public List<JyJobType> getAllAvailable() {
        List<JyJobType> result = new ArrayList<>();
        try {
            result = jyJobTypeJsfService.queryAllAvailableList();
        } catch (Exception e) {
            log.error("JyJobTypeServiceImpl.getAllAvailable error,异常信息:【{}】", e.getMessage(), e);
        }
        return result;
    }

    @Override
    public List<JyJobType> getListByCondition(JyJobType query) {
        List<JyJobType> result = new ArrayList<>();
        try {
            Result<List<JyJobType>> listResult = jyJobTypeJsfService.queryListByCondition(query);
            if (Objects.nonNull(listResult) && listResult.isSuccess()){
                result = listResult.getData();
            }
        } catch (Exception e) {
            log.error("JyJobTypeServiceImpl.queryListByCondition error,异常信息:【{}】", e.getMessage(), e);
        }
        return result;
    }
}
