package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.enums.ExcepScanLabelEnum;
import com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum;
import lombok.Data;

@Data
public class SendPackageDto {
    private String packageCode;
    private ExcepScanLabelEnum excepScanLabelEnum;
}
