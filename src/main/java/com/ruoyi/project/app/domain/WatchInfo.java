package com.ruoyi.project.app.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Rainey
 * @Date: 2019/9/27 9:20
 * @Version: 1.0
 **/
public class WatchInfo implements Serializable {

    private static final long serialVersionUID = -8749265720711763804L;
    /** 产线id或者产线id集合 */
    private Integer lineId;
    private String lineIds;

    /** 查看明细 */
    private WatchDetail watchDetail;
    /** 查看汇总 */
    private List<WatchTem> watchList;

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public String getLineIds() {
        return lineIds;
    }

    public void setLineIds(String lineIds) {
        this.lineIds = lineIds;
    }

    public WatchDetail getWatchDetail() {
        return watchDetail;
    }

    public void setWatchDetail(WatchDetail watchDetail) {
        this.watchDetail = watchDetail;
    }

    public List<WatchTem> getWatchList() {
        return watchList;
    }

    public void setWatchList(List<WatchTem> watchList) {
        this.watchList = watchList;
    }

    @Override
    public String toString() {
        return "WatchInfo{" +
                "lineId=" + lineId +
                ", lineIds='" + lineIds + '\'' +
                ", watchDetail=" + watchDetail +
                ", watchList=" + watchList +
                '}';
    }
}
