package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
import java.util.List;
public class CancelComBoardDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 8158324710994501051L;
    private List<String> packageCodeList;
    private String boardCode;

    public List<String> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<String> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
