package com.ruoyi.project.app.controller;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.app.domain.LineData;
import com.ruoyi.project.app.service.ILineService;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;
import com.ruoyi.project.production.productionLine.service.IProductionLineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * app端产线交互控制层
 *
 * @author zqm
 */
@RestController
@RequestMapping("/app")
public class LineController {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LineController.class);

    @Autowired
    private ILineService lineService;

    @Autowired
    private IProductionLineService productionLineService;


    @RequestMapping("/line")
    public AjaxResult lineAll() {
        try {
            return AjaxResult.success(lineService.selectAllLine());
        } catch (Exception e) {
            LOGGER.error("拉取所有产线出现异常：" + e.getMessage());
            return AjaxResult.error();
        }
    }

    /**
     * app拉取所有的产线信息
     */
    @RequestMapping("/lineList")
    public AjaxResult lineList() {
        try {
            return AjaxResult.success(lineService.selectAllLineList());
        } catch (Exception e) {
            LOGGER.error("app拉取所有的产线信息：" + e.getMessage());
            return AjaxResult.error();
        }
    }

    /**
     * 产线工位配置计数器编码
     */
    @RequestMapping("/lineCfJsCode")
    public Map<String, Object> lineCfJsCode(@RequestBody LineData lineData) {
        return lineService.lineCfJsCode(lineData);
    }

    /**
     * 获取产线工位列表
     */
    @RequestMapping("/getStationList")
    public Map<String, Object> getStationList(@RequestBody LineData lineData) {
        return lineService.getStationList(lineData);
    }

    /**
     * 修改产线自动收集采集模式
     */
    @RequestMapping("/changeLineManual")
    public AjaxResult changeLineManual(@RequestBody ProductionLine line) {
        try {
            int row = productionLineService.changeStatus(line);
            return row > 0 ? AjaxResult.success() : AjaxResult.error();
        } catch (BusinessException e) {
            LOGGER.error("修改产线自动收集采集模式：" + e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }
}
