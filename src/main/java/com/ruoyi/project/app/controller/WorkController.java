package com.ruoyi.project.app.controller;

import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.project.production.devWorkOrder.domain.DevWorkOrder;
import com.ruoyi.project.production.devWorkOrder.service.IDevWorkOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app工单交互逻辑
 *
 * @Author: Rainey
 * @Date: 2019/9/26 17:50
 * @Version: 1.0
 **/
@RestController
@RequestMapping("/work")
public class WorkController {

    /**
     * logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkController.class);

    @Autowired
    private IDevWorkOrderService workOrderService;

    /**
     * 编辑保存工单
     */
    @RequestMapping("/appWorkSave")
    public AjaxResult appWorkSave(@RequestBody DevWorkOrder workOrder) {
        try {
            int i = workOrderService.updateDevWorkOrder(workOrder);
            return i > 0 ? AjaxResult.success() : AjaxResult.error();
        } catch (BusinessException e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 通过工单id拉取工单信息
     */
    @RequestMapping("/appSelectWorkById")
    public AjaxResult appSelectWorkById(@RequestBody DevWorkOrder workOrder){
        try {
            if (workOrder != null && workOrder.getId() != null) {
                return AjaxResult.success(workOrderService.appSelectWorkById(workOrder.getId()));
            }
        } catch (BusinessException e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error();
    }

}
