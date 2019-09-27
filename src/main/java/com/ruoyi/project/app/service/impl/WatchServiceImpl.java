package com.ruoyi.project.app.service.impl;

import com.ruoyi.common.constant.WorkConstants;
import com.ruoyi.common.support.Convert;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.jwt.JwtUtil;
import com.ruoyi.project.app.domain.WatchDetail;
import com.ruoyi.project.app.domain.WatchInfo;
import com.ruoyi.project.app.domain.WatchTem;
import com.ruoyi.project.app.service.IWatchService;
import com.ruoyi.project.production.devWorkOrder.domain.DevWorkOrder;
import com.ruoyi.project.production.devWorkOrder.domain.WorkLog;
import com.ruoyi.project.production.devWorkOrder.mapper.DevWorkOrderMapper;
import com.ruoyi.project.production.devWorkOrder.mapper.WorkLogMapper;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;
import com.ruoyi.project.production.productionLine.mapper.ProductionLineMapper;
import com.ruoyi.project.production.workExceptionList.domain.WorkExceptionList;
import com.ruoyi.project.production.workExceptionList.mapper.WorkExceptionListMapper;
import com.ruoyi.project.system.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 看板数据拉取接口实现
 * @Author: Rainey
 * @Date: 2019/9/27 9:35
 * @Version: 1.0
 **/
@Service
public class WatchServiceImpl implements IWatchService {

    @Autowired
    private ProductionLineMapper lineMapper;

    @Autowired
    private DevWorkOrderMapper workOrderMapper;

    @Autowired
    private WorkLogMapper workLogMapper;

    @Autowired
    private WorkExceptionListMapper workExcMapper;

    /**
     * 拉取看板信息
     * @param watchInfo 看板信息
     * @return 结果
     */
    @Override
    public Map<String, Object> selectWatchInfo(WatchInfo watchInfo) {
        Map<String,Object> map = new HashMap<>(16);
        User user = JwtUtil.getUser();
        if (user == null) {
            map.put("msg","用户未登录或者登录超时");
            map.put("code",1);
            return map;
        }
        if (watchInfo == null) {
            map.put("msg","请求失败");
            map.put("code",1);
            return map;
        }
        WatchInfo info = new WatchInfo();
        // 查看看板明细
        if (StringUtils.isNotNull(watchInfo.getLineId())) {
            WatchDetail watchDetail = new WatchDetail();
            // 查看产线信息
            ProductionLine line = lineMapper.selectProductionLineById(watchInfo.getLineId());
            if (line == null) {
                map.put("msg","产线不存在或被删除");
                map.put("code",1);
                return map;
            }
            watchDetail.setLine(line);
            // 查询正在该产线正在进行的工单信息
            DevWorkOrder workOrder = workOrderMapper.selectWorkByCompandAndLine(user.getCompanyId(), watchInfo.getLineId(), WorkConstants.SING_LINE);
            // 查询当天所有的工单信息
            watchDetail.setTodayWork(workOrderMapper.selectDayWorkOrder(WorkConstants.SING_LINE, user.getCompanyId(), line.getId()));
            if (workOrder == null) {
                info.setWatchDetail(watchDetail);
                map.put("msg","没有正在进行的工单");
                map.put("data",info);
                map.put("code",0);
                return map;
            }
            watchDetail.setWorkOrder(workOrder);
            // 查看工单异常信息
            List<WorkExceptionList> excList = workExcMapper.selectWorkExceAllByWorkId(workOrder.getId());
            watchDetail.setExcList(excList);
            // 查看拉长输入明细
            List<WorkLog> workLogList = workLogMapper.selectWorkLogListByWorkId(workOrder.getId(),user.getCompanyId());
            watchDetail.setWorkLogList(workLogList);
            info.setWatchDetail(watchDetail);
            map.put("msg","请求成功");
            map.put("data",info);
            map.put("code",0);
            return map;

            // 查看汇总信息
        } else if (StringUtils.isNotEmpty(watchInfo.getLineIds())){
            List<WatchTem> watchList = new ArrayList<>();
            WatchTem watchTem= null;
            Integer[] lineIds = Convert.toIntArray(watchInfo.getLineIds());
            ProductionLine line = null;
            DevWorkOrder workOrder = null;
            for (Integer lineId : lineIds) {
                line = lineMapper.selectProductionLineById(lineId);
                if (line != null) {
                    watchTem = new WatchTem();
                    // 设置产线信息
                    watchTem.setLine(line);
                    // 查看该产线正在进行的工单
                    workOrder = workOrderMapper.selectWorkByCompandAndLine(user.getCompanyId(), line.getId(), WorkConstants.SING_LINE);
                    if (workOrder != null) {
                        watchTem.setWorkOrder(workOrder);
                    }
                    watchList.add(watchTem);
                }
            }
            info.setWatchList(watchList);
            map.put("msg","请求成功");
            map.put("data",info);
            map.put("code",0);
            return map;
        }
        map.put("msg","请求失败");
        map.put("code",1);
        return map;
    }
}
