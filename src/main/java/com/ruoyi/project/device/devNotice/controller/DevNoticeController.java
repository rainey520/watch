package com.ruoyi.project.device.devNotice.controller;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.framework.jwt.JwtUtil;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.device.devNotice.domain.DevNotice;
import com.ruoyi.project.device.devNotice.service.IDevNoticeService;
import com.ruoyi.project.system.user.domain.User;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 公司消息通知 信息操作处理
 *
 * @author zqm
 * @date 2019-04-18
 */
@Controller
@RequestMapping("/device/devNotice")
public class DevNoticeController extends BaseController {
    private String prefix = "device/devNotice";

    @Autowired
    private IDevNoticeService devNoticeService;

    @RequiresPermissions("device:devNotice:view")
    @GetMapping()
    public String devNotice() {
        User user = JwtUtil.getUser();
        if (UserConstants.LANGUAGE_EN.equals(user.getLangVersion())) {
            return prefix + "/devNoticeEn";
        }
        return prefix + "/devNotice";
    }

    /**
     * 查询公司消息通知列表
     */
    @RequiresPermissions("device:devNotice:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(DevNotice devNotice, HttpServletRequest request) {
        startPage();
        List<DevNotice> list = devNoticeService.selectDevNoticeList(devNotice,request);
        return getDataTable(list);
    }


    /**
     * 导出公司消息通知列表
     */
    @RequiresPermissions("device:devNotice:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(DevNotice devNotice, HttpServletRequest request) {
        List<DevNotice> list = devNoticeService.selectDevNoticeList(devNotice,request);
        ExcelUtil<DevNotice> util = new ExcelUtil<DevNotice>(DevNotice.class);
        return util.exportExcel(list, "devNotice");
    }

    /**
     * 新增公司消息通知
     */
    @GetMapping("/add")
    public String add() {
        User user = JwtUtil.getUser();
        if (UserConstants.LANGUAGE_EN.equals(user.getLangVersion())) {
            return prefix + "/addEn";
        }
        return prefix + "/add";
    }

    /**
     * 新增保存公司消息通知
     */
    @RequiresPermissions("device:devNotice:add")
    @Log(title = "公司消息通知", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(DevNotice devNotice) {
        return toAjax(devNoticeService.insertDevNotice(devNotice));
    }

    /**
     * 修改公司消息通知
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, ModelMap mmap) {
        DevNotice devNotice = devNoticeService.selectDevNoticeById(id);
        mmap.put("devNotice", devNotice);
        User user = JwtUtil.getUser();
        if (UserConstants.LANGUAGE_EN.equals(user.getLangVersion())) {
            return prefix + "/editEn";
        }
        return prefix + "/edit";
    }

    /**
     * 修改保存公司消息通知
     */
    @RequiresPermissions("device:devNotice:add")
    @Log(title = "公司消息通知", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(DevNotice devNotice) {
        return toAjax(devNoticeService.updateDevNotice(devNotice));
    }

    /**
     * 删除公司消息通知
     */
    @RequiresPermissions("device:devNotice:add")
    @Log(title = "公司消息通知", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(devNoticeService.deleteDevNoticeByIds(ids));
    }

    /**
     * 发布消息
     * @param id
     * @return
     */
    @PostMapping("/publish")
    @ResponseBody
    public AjaxResult publish(Integer id){
       return toAjax(devNoticeService.publish(id));
    }

    /**
     * 下线消息
     * @param id
     * @return
     */
    @PostMapping("/publishEnd")
    @ResponseBody
    public AjaxResult publishEnd(Integer id){
        return toAjax(devNoticeService.publishEnd(id));
    }

}
