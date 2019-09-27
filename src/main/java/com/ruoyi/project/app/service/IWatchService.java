package com.ruoyi.project.app.service;

import com.ruoyi.project.app.domain.WatchInfo;

import java.util.Map;

/**
 * 看板数据服务层接口
 * @Author: Rainey
 * @Date: 2019/9/27 9:32
 * @Version: 1.0
 **/
public interface IWatchService {

    /**
     * 查看看板数据
     * @param watchInfo
     * @return 结果
     */
    Map<String, Object> selectWatchInfo(WatchInfo watchInfo);
}
