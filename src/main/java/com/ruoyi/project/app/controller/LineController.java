package com.ruoyi.project.app.controller;

import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.app.domain.LineData;
import com.ruoyi.project.app.service.ILineService;
import com.ruoyi.project.device.api.form.WorkDataForm;
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

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(LineController.class);

    @Autowired
    private ILineService lineService;


    @RequestMapping("/line")
    public AjaxResult lineAll() {
        try {
            return AjaxResult.success(lineService.selectAllLine());
        } catch (Exception e) {
            e.printStackTrace();
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
            return AjaxResult.error();
        }
    }

    /**
     * 计数器硬件端拉取工单信息
     */
    @RequestMapping("/getWorkInfo")
    public Map<String,Object> getWorkInfo(@RequestBody LineData lineData){
        return lineService.getWorkInfo(lineData);
    }

    /**
     * 计数器上传计数信息
     */
    @RequestMapping("/uploadWorkInfo")
    public Map<String,Object> uploadWorkInfo(@RequestBody WorkDataForm uploadInfo){
        return lineService.uploadWorkInfo(uploadInfo);
    }

    /**
     * 产线计数器硬件关联配置
     */
    @RequestMapping("/lineConfigJsCode")
    public Map<String,Object> lineConfigJsCode(@RequestBody LineData lineData){
        return lineService.lineConfigJsCode(lineData);
    }
}
