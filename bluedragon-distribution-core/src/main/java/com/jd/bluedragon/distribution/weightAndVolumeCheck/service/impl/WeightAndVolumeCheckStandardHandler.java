package com.jd.bluedragon.distribution.weightAndVolumeCheck.service.impl;

import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.CheckExcessParam;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.dto.StandardDto;

public interface WeightAndVolumeCheckStandardHandler {

    StandardDto checkExcess(CheckExcessParam checkExcessParam);

}
