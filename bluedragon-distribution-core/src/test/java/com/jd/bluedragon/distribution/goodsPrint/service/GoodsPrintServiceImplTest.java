package com.jd.bluedragon.distribution.goodsPrint.service;

import com.jd.ql.dms.report.domain.GoodsPrintDto;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class GoodsPrintServiceImplTest extends TestCase {


    @InjectMocks
    private GoodsPrintServiceImpl goodsPrintService;
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testQuery() {
        GoodsPrintDto goodsPrintDto = new GoodsPrintDto();
        goodsPrintDto.setSendCode("654472-632-20230128181501812");
        goodsPrintService.query(goodsPrintDto);
    }
}