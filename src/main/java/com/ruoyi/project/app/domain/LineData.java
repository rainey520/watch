package com.ruoyi.project.app.domain;

import java.io.Serializable;

/**
 * 产线数据
 * @Author: Rainey
 * @Date: 2019/10/28 10:09
 * @Version: 1.0
 **/
public class LineData implements Serializable {
    private static final long serialVersionUID = -1700027777755792063L;
    /** 计数器上传数量 */
    private Integer number;
    /** 计数器编码 */
    private String jsCode;
    /** 产线id */
    private Integer lineId;
    /** 确认标记 0、未确认，1、确认更新 */
    private Integer confirmTag;

    public Integer getConfirmTag() {
        return confirmTag;
    }

    public void setConfirmTag(Integer confirmTag) {
        this.confirmTag = confirmTag;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getJsCode() {
        return jsCode;
    }

    public void setJsCode(String jsCode) {
        this.jsCode = jsCode;
    }

    @Override
    public String toString() {
        return "LineData{" +
                "number=" + number +
                ", jsCode='" + jsCode + '\'' +
                ", lineId=" + lineId +
                ", confirmTag=" + confirmTag +
                '}';
    }
}
