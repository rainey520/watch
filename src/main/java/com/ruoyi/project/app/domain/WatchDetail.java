package com.ruoyi.project.app.domain;

import com.ruoyi.project.page.pageInfo.domain.PageReal;
import com.ruoyi.project.page.pageInfo.domain.PageStandard;
import com.ruoyi.project.production.devWorkData.domain.DevWorkData;
import com.ruoyi.project.production.devWorkOrder.domain.DevWorkOrder;
import com.ruoyi.project.production.devWorkOrder.domain.WorkLog;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;
import com.ruoyi.project.production.workExceptionList.domain.WorkExceptionList;

import java.io.Serializable;
import java.util.List;

/**
 * 看板明细信息
 * @Author: Rainey
 * @Date: 2019/9/27 9:29
 * @Version: 1.0
 **/
public class WatchDetail implements Serializable {
    private static final long serialVersionUID = 1770290765496393891L;
    /** 产线信息 */
    private ProductionLine line;
    /**  */
    private DevWorkOrder workOrder;
    /** 工单异常信息 */
    private List<WorkExceptionList> excList;

    /** 当天工单信息 */
    private List<DevWorkOrder> todayWork;
    /******************** 产线手动输入模式 ************************/
    /** 该工单录入明细 */
    private List<WorkLog> workLogList;
    /******************** 产线自动采集模式 ************************/
    /***  标准产量 */
    private PageStandard standard;
    /***  实际产量 */
    private PageReal real;
    /***  工位数据相关详情 */
    private List<DevWorkData> stationDataList;

    public List<DevWorkData> getStationDataList() {
        return stationDataList;
    }

    public void setStationDataList(List<DevWorkData> stationDataList) {
        this.stationDataList = stationDataList;
    }

    public PageStandard getStandard() {
        return standard;
    }

    public void setStandard(PageStandard standard) {
        this.standard = standard;
    }

    public PageReal getReal() {
        return real;
    }

    public void setReal(PageReal real) {
        this.real = real;
    }

    public List<DevWorkOrder> getTodayWork() {
        return todayWork;
    }

    public void setTodayWork(List<DevWorkOrder> todayWork) {
        this.todayWork = todayWork;
    }

    public List<WorkExceptionList> getExcList() {
        return excList;
    }

    public void setExcList(List<WorkExceptionList> excList) {
        this.excList = excList;
    }

    public ProductionLine getLine() {
        return line;
    }

    public void setLine(ProductionLine line) {
        this.line = line;
    }

    public DevWorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(DevWorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public List<WorkLog> getWorkLogList() {
        return workLogList;
    }

    public void setWorkLogList(List<WorkLog> workLogList) {
        this.workLogList = workLogList;
    }

    @Override
    public String toString() {
        return "WatchDetail{" +
                "line=" + line +
                ", workOrder=" + workOrder +
                ", excList=" + excList +
                ", workLogList=" + workLogList +
                ", todayWork=" + todayWork +
                '}';
    }
}
