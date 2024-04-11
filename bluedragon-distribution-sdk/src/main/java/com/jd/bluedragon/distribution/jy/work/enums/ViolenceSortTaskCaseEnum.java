package com.jd.bluedragon.distribution.jy.work.enums;

public enum ViolenceSortTaskCaseEnum {
    YES("case_yichang_baoli", "暴力分拣"),
    NO("case_yichang_feibaoli", "非暴力分拣")
    ;

    ViolenceSortTaskCaseEnum(String caseCode, String caseName) {
        this.caseCode = caseCode;
        this.caseName = caseName;
    }

    private String caseCode;
    private String caseName;

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }
}
