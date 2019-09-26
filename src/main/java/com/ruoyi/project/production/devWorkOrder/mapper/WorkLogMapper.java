package com.ruoyi.project.production.devWorkOrder.mapper;

import com.ruoyi.project.production.devWorkOrder.domain.WorkLog;

/**
 * 拉长录入 数据层
 * @Author: Rainey
 * @Date: 2019/9/26 12:16
 * @Version: 1.0
 **/
public interface WorkLogMapper {

    /**
     * 新增记录
     *
     * @param workLog 工单录入记录
     * @return 结果
     */
    public int insertWorkLog(WorkLog workLog);
}
