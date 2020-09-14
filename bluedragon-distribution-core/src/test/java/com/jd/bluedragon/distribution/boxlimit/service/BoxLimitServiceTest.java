package com.jd.bluedragon.distribution.boxlimit.service;
import java.util.Arrays;
import java.util.Date;

import com.google.gson.Gson;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.dao.BoxLimitConfigDao;
import com.jd.bluedragon.distribution.boxlimit.domain.BoxLimitConfig;
import com.jd.bluedragon.distribution.boxlimit.service.impl.BoxLimitServiceImpl;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class BoxLimitServiceTest{
    private static final Logger log = LoggerFactory.getLogger(BoxLimitServiceTest.class);
    @InjectMocks
    private BoxLimitServiceImpl boxLimitService;
    @Mock
    private BaseMajorManager baseMajorManager;
    @Mock
    private BoxLimitConfigDao boxLimitConfigDao;
    @Before
    public void setUp() {
        BaseStaffSiteOrgDto dto = new BaseStaffSiteOrgDto();
        dto.setSiteName("mockSiteName");
        dto.setsId(1);
        Mockito.when(baseMajorManager.getBaseSiteBySiteId(Mockito.<Integer>any())).thenReturn(dto);
    }

    @Test
    public void listData() {
        BoxLimitQueryDTO dto = new BoxLimitQueryDTO();
        dto.setSiteName("测试");
        dto.setSiteId(1);
        dto.setPageNum(1);
        dto.setPageSize(10);
        dto.setOffset();

        PagerResult<BoxLimitVO> boxLimitVOPagerResult = boxLimitService.listData(dto);
        log.info("=======>>> listData:{}" + new Gson().toJson(boxLimitVOPagerResult));
    }

    @Test
    public void importData() {
        List<BoxLimitTemplateVO> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BoxLimitTemplateVO vo = new BoxLimitTemplateVO();
            vo.setSiteName("mockSiteName");
            vo.setSiteId(i + 1);
            vo.setLimitNum(10000 + i);
            data.add(vo);
        }
        LoginUser user = getLoginUser();

        BoxLimitConfig config = getBoxLimitConfig();

        List<BoxLimitConfig> configs = Arrays.asList(config);

        Mockito.when(boxLimitConfigDao.queryBySiteIds(Mockito.<Integer>anyList())).thenReturn(null);
        Mockito.when(boxLimitConfigDao.batchInsert(Mockito.<BoxLimitConfig>anyList())).thenReturn(1);
        JdResponse response = boxLimitService.importData(data, user);
        log.info("========>>>> import:{}", new Gson().toJson(response));
    }

    private BoxLimitConfig getBoxLimitConfig() {
        BoxLimitConfig config = new BoxLimitConfig();
        config.setId(0L);
        config.setSiteName("mockSiteName");
        config.setSiteId(0);
        config.setLimitNum(0);
        config.setOperatorErp("");
        config.setOperatorSiteId(0);
        config.setOperatorSiteName("");
        config.setOperatingTime(new Date());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        config.setYn(false);
        return config;
    }

    private LoginUser getLoginUser() {
        LoginUser user = new LoginUser();
        user.setUserId(0);
        user.setUserErp("");
        user.setUserName("");
        user.setStaffNo(0);
        user.setOrgId(0);
        user.setOrgName("");
        user.setSiteType(0);
        user.setSiteCode(0);
        user.setSiteName("");
        user.setDmsSiteCode("");
        return user;
    }

    @Test
    public void create() {
        BoxLimitDTO dto = getBoxLimitDTO();

        Mockito.when(boxLimitConfigDao.queryBySiteIds(Mockito.<Integer>anyList())).thenReturn(null);
        Mockito.when(boxLimitConfigDao.insert(Mockito.<BoxLimitConfig>any())).thenReturn(1);
        JdResponse response = boxLimitService.create(dto, getLoginUser());
        log.info("create:{}", new Gson().toJson(response));
    }

    private BoxLimitDTO getBoxLimitDTO() {
        BoxLimitDTO dto = new BoxLimitDTO();
        dto.setSiteName("mockSiteName");
        dto.setSiteId(0);
        dto.setId(0L);
        dto.setLimitNum(100);
        return dto;
    }

    @Test
    public void update() {
        BoxLimitDTO dto = getBoxLimitDTO();

        Mockito.when(boxLimitConfigDao.queryBySiteIds(Mockito.<Integer>anyList())).thenReturn(null);
        Mockito.when(boxLimitConfigDao.insert(Mockito.<BoxLimitConfig>any())).thenReturn(1);
        Mockito.when(boxLimitConfigDao.selectByPrimaryKey(Mockito.<Long>any())).thenReturn(getBoxLimitConfig());
        JdResponse response = boxLimitService.update(dto, getLoginUser());
        log.info("update:{}", new Gson().toJson(response));
    }

    @Test
    public void delete() {
        Mockito.when(boxLimitConfigDao.batchDelete(Mockito.<Long>anyList())).thenReturn(1);
        JdResponse response = boxLimitService.delete(Arrays.asList(1L), "testERP");
        log.info("delete:{}", new Gson().toJson(response));
    }

    @Test
    public void querySiteNameById() {
        JdResponse response = boxLimitService.querySiteNameById(1);
        log.info("querySiteNameById:{}", new Gson().toJson(response));
    }

    @Test
    public void queryLimitNumBySiteId() {
        Mockito.when(boxLimitConfigDao.queryLimitNumBySiteId(Mockito.<Integer>any())).thenReturn(1000);
        Integer id = boxLimitService.queryLimitNumBySiteId(1);
        log.info("queryLimitNumBySiteId: id = {}", id);
    }

}
