package com.ruoyi.project.app.service;

import com.ruoyi.project.app.domain.LineData;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;

import java.util.List;
import java.util.Map;

/**
 * 查询工位操作
 */
public interface ILineService {
    /**
     * 查询所以产线，及其工位信息
     * @return 结果
     */
    List<ProductionLine> selectAllLine();

    /**
     * 拉取所有的产线信息
     * @return 结果
     */
    List<ProductionLine> selectAllLineList();

    /**
     * 流水线工位配置硬件编码
     * @param lineData 配置信息
     * @return 结果
     */
    Map<String, Object> lineCfJsCode(LineData lineData);

    /**
     * 获取产线工位列表
     * @param lineData 上传参数
     * @return 工位列表
     */
    Map<String, Object> getStationList(LineData lineData);
}
