package com.ruoyi.project.app.service.impl;

import com.ruoyi.common.constant.CompanyConstants;
import com.ruoyi.common.constant.DevConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.constant.WorkConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.jwt.JwtUtil;
import com.ruoyi.project.app.domain.LineData;
import com.ruoyi.project.app.service.ILineService;
import com.ruoyi.project.device.api.form.ApiWorkForm;
import com.ruoyi.project.device.api.form.WorkDataForm;
import com.ruoyi.project.device.devCompany.domain.DevCompany;
import com.ruoyi.project.device.devCompany.mapper.DevCompanyMapper;
import com.ruoyi.project.device.devDeviceCounts.domain.DevDataLog;
import com.ruoyi.project.device.devDeviceCounts.mapper.DevDataLogMapper;
import com.ruoyi.project.device.devList.domain.DevList;
import com.ruoyi.project.device.devList.mapper.DevListMapper;
import com.ruoyi.project.production.devWorkOrder.domain.DevWorkOrder;
import com.ruoyi.project.production.devWorkOrder.mapper.DevWorkOrderMapper;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;
import com.ruoyi.project.production.productionLine.mapper.ProductionLineMapper;
import com.ruoyi.project.production.workstation.mapper.WorkstationMapper;
import com.ruoyi.project.system.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LineServiceImpl implements ILineService {

    @Autowired
    private ProductionLineMapper lineMapper;

    @Autowired
    private WorkstationMapper workstationMapper;

    @Autowired
    private DevWorkOrderMapper devWorkOrderMapper;

    @Autowired
    private DevListMapper devListMapper;

    @Autowired
    private DevCompanyMapper companyMapper;

    @Autowired
    private DevDataLogMapper devDataLogMapper;

    @Override
    public List<ProductionLine> selectAllLine() {
        User user = JwtUtil.getUser();
        //查询对应公司所以的产线信息
        List<ProductionLine> lines = lineMapper.selectAllProductionLineByCompanyId(user.getCompanyId());
        if (lines != null) {
            for (ProductionLine line : lines) {
                //查询对应产线的工位信息
                line.setWorkstations(workstationMapper.selectAllCodeNotNullByLineId(user.getCompanyId(), line.getId()));
                line.setStatus(0);
                //查询正在进行的工单
                DevWorkOrder workOrder = devWorkOrderMapper.selectWorkByCompandAndLine(user.getCompanyId(), line.getId(), WorkConstants.SING_LINE);
                if (workOrder != null) {
                    line.setStatus(1);
                }
            }
            return lines;
        }
        return null;
    }

    /**
     * 拉取所有的产线信息
     *
     * @return 结果
     */
    @Override
    public List<ProductionLine> selectAllLineList() {
        User user = JwtUtil.getUser();
        if (user == null) {
            return Collections.emptyList();
        }
        return lineMapper.selectAllProductionLineByCompanyId(user.getCompanyId());
    }

    /**
     * 硬件端拉取工单信息
     *
     * @param lineData 上传信息
     * @return 结果
     */
    @Override
    public Map<String, Object> getWorkInfo(LineData lineData) {
        Map<String, Object> map = new HashMap<>(16);
        if (lineData == null || StringUtils.isEmpty(lineData.getJsCode())) {
            map.put("status", 0);
            map.put("msg", "请求参数错误");
            return map;
        }
        // 查询计数器硬件信息
        DevList devInfo = devListMapper.selectDevListByDevId(lineData.getJsCode());
        if (devInfo == null || devInfo.getCompanyId() == null || devInfo.getLineId() == null) {
            map.put("status", 0);
            map.put("msg", "硬件不存在或未进行公司产线相关配置");
            return map;
        }
        // 查询公司信息
        DevCompany company = companyMapper.selectDevCompanyById(devInfo.getCompanyId());
        if (company == null) {
            map.put("status", 0);
            map.put("msg", "公司不存在或被删除");
            return map;
        }
        // 查询产线信息
        ProductionLine line = lineMapper.selectProductionLineById(devInfo.getLineId());
        if (line == null || CompanyConstants.LINE_DELETED.equals(line.getDefId())) {
            map.put("status", 0);
            map.put("msg", "产线不存在或被删除");
            return map;
        }
        ApiWorkForm workInfo = new ApiWorkForm();

        // 返回公司产线相关信息为流水线
        workInfo.setCompanyId(company.getCompanyId());
        workInfo.setCompanyName(company.getComName());
        workInfo.setLineId(line.getId());
        workInfo.setLineName(line.getLineName());
        workInfo.setTag(WorkConstants.SING_LINE);

        // 查询该产线正在进行的工单信息
        DevWorkOrder order = devWorkOrderMapper.selectWorkByCompandAndLine(company.getCompanyId(), devInfo.getLineId(), WorkConstants.SING_LINE);
        if (order == null) {
            map.put("status", 1);
            map.put("data", workInfo);
            map.put("msg", "无进行中工单");
            return map;
        }
        // 设置工单相关信息
        workInfo.setWorkId(order.getId());
        workInfo.setWorkCode(order.getWorkorderNumber());
        workInfo.setProductCode(order.getProductCode());
        // 工单实际产量
        workInfo.setActualNum(order.getCumulativeNumber());
        workInfo.setProductName(order.getProductName());
        workInfo.setWorkNumber(order.getProductNumber());
        workInfo.setWorkorderStatus(order.getOperationStatus());
        map.put("status", 2);
        map.put("data", workInfo);
        map.put("msg", "请求成功");
        return map;
    }

    /**
     * 计数器上传工单数据逻辑操作
     *
     * @param uploadInfo 上传信息
     * @return 结果
     */
    @Override
    public Map<String, Object> uploadWorkInfo(WorkDataForm uploadInfo) {
        Map<String, Object> map = new HashMap<>(16);
        if (uploadInfo == null || StringUtils.isEmpty(uploadInfo.getCode())) {
            map.put("status", 0);
            map.put("msg", "请求参数错误");
            return map;
        }
        // 查询计数器硬件信息
        DevList devInfo = devListMapper.selectDevListByDevId(uploadInfo.getCode());
        if (devInfo == null || devInfo.getCompanyId() == null || devInfo.getLineId() == null) {
            map.put("code", 0);
            map.put("msg", "硬件不存在或未进行公司产线相关配置");
            return map;
        }
        // 查询公司信息
        DevCompany company = companyMapper.selectDevCompanyById(devInfo.getCompanyId());
        if (company == null) {
            map.put("code", 0);
            map.put("msg", "公司不存在或被删除");
            return map;
        }
        // 查询产线信息
        ProductionLine line = lineMapper.selectProductionLineById(devInfo.getLineId());
        if (line == null || CompanyConstants.LINE_DELETED.equals(line.getDefId())) {
            map.put("code", 0);
            map.put("msg", "产线不存在或被删除");
            return map;
        }
        // 上传日志记录
        DevDataLog devDataLog = new DevDataLog();
        devDataLog.setCompanyId(devInfo.getCompanyId());
        devDataLog.setDataTotal(uploadInfo.getD1());
        devDataLog.setDevId(devInfo.getId());
        devDataLog.setDelData(0);
        devDataLog.setScType(WorkConstants.SING_LINE);
        devDataLog.setLineId(devInfo.getLineId());

        // 查询该产线正在进行的工单信息
        DevWorkOrder order = devWorkOrderMapper.selectWorkByCompandAndLine(company.getCompanyId(), devInfo.getLineId(), WorkConstants.SING_LINE);
        // 存在进行的工单并且需要计数
        if (order != null && order.getPbSign().equals(WorkConstants.PB_SIGN_YES)) {
            // 工单属于进行状态
            if (WorkConstants.OPERATION_STATUS_STARTING.equals(order.getOperationStatus())) {
                countWorkNum(uploadInfo, devInfo, devDataLog, order);

                // 工单暂停中
            } else {
                order.setPbSign(WorkConstants.PB_SIGN_NO);
                countWorkNum(uploadInfo, devInfo, devDataLog, order);
            }
        } else {
            /**
             * 查询最近完成的工单信息
             */
            order = devWorkOrderMapper.selectLatelyCompleteWork(devInfo.getCompanyId(), devInfo.getLineId(), WorkConstants.SING_LINE);
            if (order != null && order.getSign() == 1) {
                devDataLog.setWorkId(order.getId());
                order.setCumulativeNumber(uploadInfo.getD1());
                order.setSign(0);
                devWorkOrderMapper.updateDevWorkOrder(order);
                //查询对应日志上传数据数据
                DevDataLog log = devDataLogMapper.selectLineWorkDevIo(order.getLineId(), order.getId(), devInfo.getId(), null, WorkConstants.SING_LINE);
                if (log != null) {
                    devDataLog.setDelData(devDataLog.getDataTotal() - log.getDataTotal());
                }
            }
        }
        devDataLog.setCreateDate(new Date());
        devDataLog.setCreateTime(new Date());
        devDataLogMapper.insertDevDataLog(devDataLog);


        map.put("code", 1);
        map.put("msg", "无工单");
        map.put("status", 0);
        map.put("num", 0);
        map.put("workCode", null);
        DevWorkOrder workOrder = devWorkOrderMapper.selectWorkByCompandAndLine(devInfo.getCompanyId(), devInfo.getLineId(), WorkConstants.SING_LINE);
        if (workOrder != null) {
            map.put("code", 1);
            map.put("msg", "进行中");
            map.put("status", workOrder.getOperationStatus());
            map.put("num", workOrder.getCumulativeNumber());
            map.put("workCode", workOrder.getWorkorderNumber());
        }
        return map;
    }

    /**
     * 计算工单数量
     *
     * @param uploadInfo 上传信息
     * @param devInfo    硬件信息
     * @param devDataLog 上报日志
     * @param order      工单信息
     */
    private void countWorkNum(WorkDataForm uploadInfo, DevList devInfo, DevDataLog devDataLog, DevWorkOrder order) {
        devDataLog.setWorkId(order.getId());
        //查询对应日志上传数据数据
        DevDataLog log = devDataLogMapper.selectLineWorkDevIo(order.getLineId(), order.getId(), devInfo.getId(), null, WorkConstants.SING_LINE);
        if (log != null) {
            devDataLog.setDelData(devDataLog.getDataTotal() - log.getDataTotal());
        } else {
            devDataLog.setDelData(uploadInfo.getD1());
        }
        // 更新工单的累计产量
        order.setCumulativeNumber(uploadInfo.getD1());
        devWorkOrderMapper.updateDevWorkOrder(order);
    }


    /**
     * 计数器硬件与产线关联配置
     *
     * @param lineData 关联数据
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> lineConfigJsCode(LineData lineData) {
        Map<String, Object> map = new HashMap<>(16);
        User user = JwtUtil.getUser();
        if (user == null) {
            map.put("code",0);
            map.put("msg", UserConstants.NOT_LOGIN);
            return map;
        }
        if (lineData == null || StringUtils.isEmpty(lineData.getJsCode()) || lineData.getLineId() == null) {
            map.put("code",0);
            map.put("msg", "配置参数错误");
            return map;
        }
        // 查询产线信息
        ProductionLine line = lineMapper.selectProductionLineById(lineData.getLineId());
        if (line == null) {
            map.put("code",0);
            map.put("msg", "产线不存在或被删除");
            return map;
        }
        // 查询硬件信息
        DevList newDev = devListMapper.selectDevListByDevId(lineData.getJsCode());
        if (newDev == null) {
            map.put("code",0);
            map.put("msg", "计数器硬件不存在");
            return map;
        }
        // 查询之前配置过的产线信息
        if (newDev.getLineId() != null) {
            if (DevConstants.DEV_CONFIRMTAG_NO.equals(lineData.getConfirmTag())) {
                map.put("code",2);
                map.put("msg", "该硬件已经关联过产线");
                return map;
            } else {
                ProductionLine oldLine = lineMapper.selectProductionLineById(newDev.getLineId());
                if (oldLine != null) {
                    // 还原之前配置产线为手动
                    oldLine.setManual(CompanyConstants.LINE_COLLECT_MANUAL);
                    lineMapper.updateProductionLine(oldLine);
                }
            }
        }
        // 查询配置的产线是否已经关联计数器硬件信息
        DevList oldDev = devListMapper.selectDevListByLineId(lineData.getLineId());
        if (oldDev != null) {
            // 已经配置过
            if (DevConstants.DEV_CONFIRMTAG_NO.equals(lineData.getConfirmTag())) {
                map.put("code",2);
                map.put("msg", "该产线已经关联过计数器编码");
                return map;

            // 更新之前硬件配置
            } else {
                devListMapper.updateDevLineTag(DevConstants.DEV_SIGN_NOT_USE,null,oldDev.getDeviceId(),null);
            }
        }
        line.setManual(CompanyConstants.LINE_COLLECT_AUTO);
        line.setRemark(line.getRemark());
        lineMapper.updateProductionLine(line);
        newDev.setCompanyId(user.getCompanyId());
        newDev.setSign(DevConstants.DEV_SIGN_USED);
        newDev.setLineId(line.getId());
        newDev.setDeviceName(line.getLineName() + "--计数硬件");
        devListMapper.updateDevList(newDev);
        map.put("code",1);
        map.put("msg","请求成功");
        return map;
    }
}
