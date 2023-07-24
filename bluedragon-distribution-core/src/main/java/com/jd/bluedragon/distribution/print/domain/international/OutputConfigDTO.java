package com.jd.bluedragon.distribution.print.domain.international;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/7/19 3:50 PM
 */
public class OutputConfigDTO {

    /**
     * 输出方式配置可选的值，0(Printer，输出到打印机)，1(File，输出到文件)
     */
    private Integer outputType;

    /**
     * 输出到本地文件格式，PDF-1，手持贴标机专用-2，蓝牙打印指令集-3。图片-4 当outputType=1时，此项必填。
     */
    private Integer fileFormat;

    /**
     * fileFormat=1时枚举值：1：URL
     * fileFormat=2时枚举值：2：base64字符串；
     * fileFormat=3时枚举值：3：ESC/POS；4：CPCL ；5：cpcl指令打印BMP；6：tspl；7：tspl指令打印BMP
     */
    private Integer dataFormat;

    public OutputConfigDTO outputType(Integer outputType){
        this.outputType = outputType;
        return this;
    }
    public OutputConfigDTO fileFormat(Integer fileFormat){
        this.fileFormat = fileFormat;
        return this;
    }
    public OutputConfigDTO dataFormat(Integer dataFormat){
        this.dataFormat = dataFormat;
        return this;
    }

    public Integer getOutputType() {
        return outputType;
    }

    public void setOutputType(Integer outputType) {
        this.outputType = outputType;
    }

    public Integer getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(Integer fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }
}
