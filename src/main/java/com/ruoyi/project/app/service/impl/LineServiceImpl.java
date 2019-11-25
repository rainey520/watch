package com.ruoyi.project.app.service.impl;

import com.ruoyi.common.constant.DevConstants;
import com.ruoyi.common.constant.WorkConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.jwt.JwtUtil;
import com.ruoyi.project.app.domain.LineData;
import com.ruoyi.project.app.service.ILineService;
import com.ruoyi.project.device.devCompany.mapper.DevCompanyMapper;
import com.ruoyi.project.device.devDeviceCounts.mapper.DevDataLogMapper;
import com.ruoyi.project.device.devList.domain.DevList;
import com.ruoyi.project.device.devList.mapper.DevListMapper;
import com.ruoyi.project.production.devWorkOrder.domain.DevWorkOrder;
import com.ruoyi.project.production.devWorkOrder.mapper.DevWorkOrderMapper;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;
import com.ruoyi.project.production.productionLine.mapper.ProductionLineMapper;
import com.ruoyi.project.production.workstation.domain.Workstation;
import com.ruoyi.project.production.workstation.mapper.WorkstationMapper;
import com.ruoyi.project.system.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 流水线工位配置硬件编码
     *
     * @param lineData 配置信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> lineCfJsCode(LineData lineData) {
        Map<String, Object> map = new HashMap<>(16);
        if (lineData == null || StringUtils.isEmpty(lineData.getJsCode()) || lineData.getStationId() == null) {
            map.put("code", 1);
            map.put("msg", "请求参数错误");
            return map;
        }
        DevList devList = devListMapper.selectDevListByCode(lineData.getJsCode());
        if (devList == null) {
            map.put("code", 1);
            map.put("msg", "硬件不存在或被删除");
            return map;
        }
        // 查询之前配置的工位信息
        if (DevConstants.DEV_SIGN_USED == devList.getSign()) {
            Workstation oldStation = workstationMapper.selectInfoByDevice(devList.getId(), 0, 0);
            if (oldStation != null) {
                if (oldStation.getId().equals(lineData.getStationId())) {
                    map.put("code", 1);
                    map.put("msg", "该工位已配置该硬件勿重复配置");
                    return map;
                }
                oldStation.setDevId(0);
                oldStation.setDevCode(null);
                workstationMapper.updateWorkstation(oldStation);
            }
        }
        // 查询工位信息
        Workstation station = workstationMapper.selectWorkstationById(lineData.getStationId());
        if (station == null) {
            map.put("code", 1);
            map.put("msg", "工位不存在或被删除");
            return map;
        }
        // 查询产线信息
        ProductionLine line = lineMapper.selectProductionLineById(station.getLineId());
        if (line == null) {
            map.put("code", 1);
            map.put("msg", "产线不存在或被删除");
            return map;
        }
        /**
         * 工位计数器配置逻辑
         */
        // 已经配置过
        if (station.getDevId() > 0) {
            // 还原之前硬件
            devListMapper.updateJsDevListInfo(station.getDevId(), null, station.getCompanyId(), DevConstants.DEV_SIGN_NOT_USE, null);
            // 修改最新硬件
            editStationJsCode(devList, station, line);

            // 未配置过
        } else {
            // 修改工位硬件信息
            editStationJsCode(devList, station, line);
        }
        map.put("code", 0);
        map.put("msg", "配置成功");
        return map;
    }

    /**
     * 获取产线工位列表
     *
     * @param lineData 上传参数
     * @return 工位列表
     */
    @Override
    public Map<String, Object> getStationList(LineData lineData) {
        Map<String, Object> map = new HashMap<>(16);
        User user = JwtUtil.getUser();
        if (user == null) {
            map.put("code",1);
            map.put("msg","用户未登录或登录超时");
            return map;
        }
        if (lineData == null || lineData.getLineId() == null) {
            map.put("code",1);
            map.put("msg","请求参数错误");
            return map;
        }
        map.put("code",0);
        map.put("msg","请求成功");
        map.put("data",workstationMapper.selectAllByLineId(lineData.getLineId()));
        return map;
    }

    /**
     * 修改工位硬件配置信息
     *
     * @param devList 上传硬件信息
     * @param station 工位信息
     * @param line    产线信息
     */
    private void editStationJsCode(DevList devList, Workstation station, ProductionLine line) {
        // 修改最新硬件
        devListMapper.updateJsDevListInfo(devList.getId(), line.getLineName() + "--" + station.getwName(),
                line.getCompanyId(), DevConstants.DEV_SIGN_USED, DevConstants.DEV_TYPE_LINE);
        // 修改工位信息
        station.setDevId(devList.getId());
        station.setDevCode(devList.getDeviceId());
        workstationMapper.updateWorkstation(station);
    }
}
