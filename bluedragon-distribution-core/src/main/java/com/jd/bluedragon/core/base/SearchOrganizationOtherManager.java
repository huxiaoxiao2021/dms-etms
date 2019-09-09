package com.jd.bluedragon.core.base;

import com.jd.ufo.common.utility.ResponseObject;
import com.jd.ufo.domain.ufo.Organization;
import com.jd.ufo.domain.ufo.SendpayOrdertype;

public interface SearchOrganizationOtherManager {

    Organization findFinancialOrg(SendpayOrdertype var1);
}
