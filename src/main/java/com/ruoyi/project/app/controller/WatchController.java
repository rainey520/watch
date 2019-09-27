package com.ruoyi.project.app.controller;

import com.ruoyi.project.app.domain.WatchInfo;
import com.ruoyi.project.app.service.IWatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * app看板逻辑交互
 * @Author: Rainey
 * @Date: 2019/9/27 9:18
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/watch")
public class WatchController {

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(WatchController.class);

    @Autowired
    private IWatchService watchService;

    /**
     * 查看生产看板信息
     */
    @RequestMapping("/info")
    public Map<String,Object> appWatchInfo(@RequestBody WatchInfo watchInfo){
        return watchService.selectWatchInfo(watchInfo);
    }

}
