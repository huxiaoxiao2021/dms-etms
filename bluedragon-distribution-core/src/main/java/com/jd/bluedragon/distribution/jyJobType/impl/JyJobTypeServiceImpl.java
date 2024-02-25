package com.jd.bluedragon.distribution.jyJobType.impl;

import com.jd.bluedragon.distribution.jyJobType.JyJobTypeService;
import com.jdl.basic.api.domain.jyJobType.JyJobType;
import com.jdl.basic.api.domain.jyJobType.JyJobTypeQuery;
import com.jdl.basic.api.service.jyJobType.JyJobTypeJsfService;
import com.jdl.basic.common.utils.PageDto;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author pengchong28
 * @description 拣运工种服务实现
 * @date 2024/2/25
 */
@Slf4j
@Service("jyJobTypeService")
public class JyJobTypeServiceImpl implements JyJobTypeService {
    @Autowired
    private JyJobTypeJsfService jyJobTypeJsfService;

    /**
     * 获取所有JyJobType对象的列表
     * @return 返回JyJobType对象的列表
     */
    @Override
    public List<JyJobType> getAll() {
        ArrayList<JyJobType> result = new ArrayList<>();
        JyJobTypeQuery query = buildJyJobTypeQuery();
        try {
            Result<PageDto<JyJobType>> pageDtoResult = jyJobTypeJsfService.queryPageList(query);
            if (pageDtoResult.isSuccess() && !pageDtoResult.isEmptyData()){
                result = (ArrayList<JyJobType>)pageDtoResult.getData().getResult();
            }
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
        return getAll().stream()
            .filter(jyJobType -> Objects.equals(jyJobType.getStatus(), 1))
            .collect(Collectors.toList());
    }

    /**
     * 构建JyJobTypeQuery对象
     *
     * @return JyJobTypeQuery对象，根据查询条件构建的查询对象
     */
    private JyJobTypeQuery buildJyJobTypeQuery() {
        JyJobTypeQuery jyJobTypeQuery = new JyJobTypeQuery();
        jyJobTypeQuery.setPageSize(100);
        jyJobTypeQuery.setPageNumber(1);
        return jyJobTypeQuery;
    }
}
