package com.jd.bluedragon.distribution.web.btob.express.weight;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/b2b/express/weight")
public class B2BExpressWeightController
{
    @RequestMapping("/index")
    public String getIndexPage()
    {

        return "/b2bExpress/weight/weighByWaybill";
    }

    public Object insertWaybillWeight()
    {

        return null;
    }


    public void validateParam()
    {

    }


}
