package com.ruoyi.project.production.productionLine.service;

import com.ruoyi.common.constant.DevConstants;
import com.ruoyi.common.constant.WorkConstants;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.jwt.JwtUtil;
import com.ruoyi.project.device.devCompany.domain.DevCompany;
import com.ruoyi.project.device.devCompany.mapper.DevCompanyMapper;
import com.ruoyi.project.device.devList.mapper.DevListMapper;
import com.ruoyi.project.production.devWorkOrder.domain.DevWorkOrder;
import com.ruoyi.project.production.devWorkOrder.mapper.DevWorkOrderMapper;
import com.ruoyi.project.production.productionLine.domain.ProductionLine;
import com.ruoyi.project.production.productionLine.mapper.ProductionLineMapper;
import com.ruoyi.project.production.timeRecord.domain.TimeRecord;
import com.ruoyi.project.production.timeRecord.mapper.TimeRecordMapper;
import com.ruoyi.project.production.workstation.domain.Workstation;
import com.ruoyi.project.production.workstation.mapper.WorkstationMapper;
import com.ruoyi.project.system.user.domain.User;
import com.ruoyi.project.system.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 生产线 服务层实现
 *
 * @author ruoyi
 * @date 2019-04-11
 */
@Service("productionLine")
public class ProductionLineServiceImpl implements IProductionLineService {
    @Autowired
    private ProductionLineMapper productionLineMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DevCompanyMapper companyMapper;

    @Autowired
    private DevWorkOrderMapper devWorkOrderMapper;

    @Autowired
    private WorkstationMapper workstationMapper;

    @Autowired
    private DevListMapper devListMapper;

    @Autowired
    private TimeRecordMapper timeRecordMapper;


    /**
     * 查询生产线信息
     *
     * @param id 生产线ID
     * @return 生产线信息
     */
    @Override
    public ProductionLine selectProductionLineById(Integer id) {
        return productionLineMapper.selectProductionLineById(id);
    }

    /**
     * 查询生产线列表
     *
     * @param productionLine 生产线信息
     * @return 生产线集合
     */
    @Override
    public List<ProductionLine> selectProductionLineList(ProductionLine productionLine) {
        User u = JwtUtil.getUser();
        if (u == null) return Collections.emptyList();
        // 查询公司信息
        DevCompany company = companyMapper.selectDevCompanyById(u.getCompanyId());
        if (company == null) {
            return Collections.emptyList();
        }
        productionLine.setCompanyId(u.getCompanyId());
        List<ProductionLine> list = productionLineMapper.selectProductionLineList(productionLine);
        User user;
        for (ProductionLine line : list) {
            if (u.getUserId().intValue() == line.getDeviceLiable() || u.getUserId().intValue() == line.getDeviceLiableTow()) {
                line.setLineSign(1);
            }
            // 设置产线责任人名称
            user = userMapper.selectUserInfoById(line.getDeviceLiable());
            if (user != null) {
                line.setDeviceLiableName(user.getUserName());
            }
            user = userMapper.selectUserInfoById(line.getDeviceLiableTow());
            if (user != null) {
                line.setDeviceLiableTowName(user.getUserName());
            }
        }
        return list;
    }

    /**
     * 新增生产线
     *
     * @param productionLine 生产线信息
     * @return 结果
     */
    @Override
    public int insertProductionLine(ProductionLine productionLine) {
        User user = JwtUtil.getUser();
        if (user == null) {
            throw new BusinessException("未登录或者登录超时");
        }
        // 查看用户公司等级
        DevCompany company = companyMapper.selectDevCompanyById(JwtUtil.getUser().getCompanyId());
        if (company == null) {
            throw new BusinessException("用户所在的公司不存在或者被删除");
        }
        if (company.getSign() == 0) {
            List<ProductionLine> productionLines = productionLineMapper.selectAllDef0(user.getCompanyId());
            if (StringUtils.isNotEmpty(productionLines) && productionLines.size() >= 3) {
                throw new BusinessException("非VIP用户最多只能新增三条产线");
            }
        }
        productionLine.setCompanyId(user.getCompanyId());
        productionLine.setCreate_by(user.getUserId().intValue());
        productionLine.setCreateTime(new Date());
        return productionLineMapper.insertProductionLine(productionLine);
    }

    /**
     * 修改生产线
     *
     * @param productionLine 生产线信息
     * @return 结果
     */
    @Override
    public int updateProductionLine(ProductionLine productionLine) {
        User sysUser = JwtUtil.getUser();
        if (sysUser == null) {
            throw new BusinessException("用户未登录或登录超时");
        }
        ProductionLine line = productionLineMapper.selectProductionLineById(productionLine.getId());
        if (line == null) {
            throw new BusinessException("产线不存在或被删除");
        }
        checkDeviceLiable(sysUser, line);
        return productionLineMapper.updateProductionLine(productionLine);
    }


    /**
     * 删除生产线对象
     *
     * @param id 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProductionLineById(Integer id) {
        User sysUser = JwtUtil.getUser();
        if (sysUser == null) {
            throw new BusinessException("用户未登录或登录超时");
        }
        ProductionLine productionLine = productionLineMapper.selectProductionLineById(id);
        if (productionLine == null) {
            throw new BusinessException("产线不存在或被删除");
        }
        checkDeviceLiable(sysUser, productionLine);
        //查询是否有工单信息
        DevWorkOrder workOrder = devWorkOrderMapper.selectWorkByLineId(id);
        if (workOrder != null) {
            throw new BusinessException("该产线有未完成工单，不能删除...");
        }
        //查询对应产线的工位信息
        List<Workstation> workstations = workstationMapper.selectAllByLineId(id);
        if (workstations != null && workstations.size() > 0) {
            for (Workstation workstation : workstations) {
                //将对应硬件设置为未配置
                if (workstation.getDevId() > 0) {
                    devListMapper.updateJsDevListInfo(workstation.getDevId(),null,productionLine.getCompanyId(),
                            DevConstants.DEV_SIGN_NOT_USE,null);
                }
                if (workstation.getcId() > 0) {
                    devListMapper.updateJsDevListInfo(workstation.getcId(),null,productionLine.getCompanyId(),
                            DevConstants.DEV_SIGN_NOT_USE,null);
                }
                if (workstation.geteId() > 0) {
                    devListMapper.updateJsDevListInfo(workstation.geteId(),null,productionLine.getCompanyId(),
                            DevConstants.DEV_SIGN_NOT_USE,null);
                }
                //删除工位
                workstationMapper.deleteWorkstationById(workstation.getId());
            }
        }

        return productionLineMapper.deleteProductionLineById(id);
    }

    @Override
    public List<Map<String, Object>> selectLineDev(int id) {
        return productionLineMapper.selectLineDev(id);
    }

    /**
     * 保存相关产线IO口配置
     *
     * @param line
     * @return
     */
    @Override
    public int updateLineConfigClear(ProductionLine line, HttpServletRequest request) {
        User sysUser = JwtUtil.getTokenUser(request);
        ProductionLine productionLine = productionLineMapper.selectProductionLineById(line.getId());
        checkDeviceLiable(sysUser, productionLine);
        try {
            if (line == null || line.getId() == null) return 0;
            if (line.getDevIo() != null && line.getDevIo().length > 0) {
                //将其产线修改为自动
                line.setManual(0);
                productionLineMapper.updateProductionLine(line);
            } else {
                //将其修改为手动
                line.setManual(1);
                productionLineMapper.updateProductionLine(line);
            }
            //将其产线配置清空
            productionLineMapper.updateLineConfigClear(line.getId());
            if (line.getDevIo() != null && line.getDevIo().length > 0) {
                for (Integer ioId : line.getDevIo()) {
                    //修改对应IO口的配置相关产线
                    productionLineMapper.updateLineConfig(ioId, line.getId());
                }
                if (line.getIsSign() > 0) {
                    productionLineMapper.updateIoSignLine(line.getIsSign());
                } else {
                    //获取第一个默认为警戒线判断依据
                    productionLineMapper.updateIoSignLine(line.getDevIo()[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 校验登录用户是否为生产线的责任人
     *
     * @param sysUser
     * @param productionLine
     */
    private void checkDeviceLiable(User sysUser, ProductionLine productionLine) {
        if (!User.isAdmin(sysUser) && !sysUser.getLoginName().equals(sysUser.getCreateBy())) {
            //不属于第一或者第二责任人不允许修改产线
            if (productionLine.getDeviceLiable() != null && productionLine.getDeviceLiableTow() != null) {
                if (sysUser.getUserId().longValue() != productionLine.getDeviceLiable() && sysUser.getUserId().longValue() != productionLine.getDeviceLiableTow()) {
                    throw new BusinessException("不是产线责任人，没有权限");
                }
            } else {
                throw new BusinessException("请先配置全产线责任人");
            }
        }
    }

    /**
     * 查询对应公司的所有生产线
     *
     * @return
     */
    @Override
    public List<ProductionLine> selectAllProductionLineByCompanyId() {
        User user = JwtUtil.getUser();
        if (user == null) return Collections.emptyList();
        return productionLineMapper.selectAllProductionLineByCompanyId(user.getCompanyId());
    }

    /**
     * 通过生产线id查询责任人名称
     *
     * @param lineId
     * @return
     */
    @Override
    public Map findDeviceLiableByLineId(Integer lineId) {
        ProductionLine productionLine = productionLineMapper.selectProductionLineById(lineId);
        Map<String, Object> map = new HashMap<>(16);
        map.put("user1", findName(productionLine.getDeviceLiable()));
        map.put("user2", findName(productionLine.getDeviceLiableTow()));
        return map;
    }

    /**
     * 通过用户id查询用户名称（责任人）
     *
     * @param userId
     * @return deviceLiable
     */
    private User findName(Integer userId) {
        return userMapper.selectUserInfoById(userId);
    }

    /**
     * 查询对应公司的所有生产线信息
     *
     * @return
     */
    public List<ProductionLine> selectProductionLineAll(Cookie[] cookies) {
        User user = JwtUtil.getTokenCookie(cookies);
        Integer companyId = null;
        if (!User.isAdmin(user)) {
            companyId = user.getCompanyId();
        }
        return productionLineMapper.selectAllProductionLineByCompanyId(companyId);
    }

    /**
     * 通过作业指导书id查询未配置的产线信息
     *
     * @param isoId     作业指导书id
     * @param companyId 公司id
     * @return 结果
     */
    @Override
    public List<ProductionLine> selectLineNotConfigByIsoId(Integer isoId, Integer companyId) {
        return productionLineMapper.selectAllProductionLineByCompanyId(companyId);
    }

    /**
     * 校验产线名的唯一性
     *
     * @param productionLine 产线对象
     * @return 结果
     */
    @Override
    public String checkLineNameUnique(ProductionLine productionLine) {
        ProductionLine uniqueLine = productionLineMapper.selectProductionLineByName(JwtUtil.getUser().getCompanyId(), productionLine.getLineName());
        if (StringUtils.isNotNull(uniqueLine) && !uniqueLine.getId().equals(productionLine.getId())) {
            return WorkConstants.LINE_NAME_NOT_UNIQUE;
        }
        return WorkConstants.LINE_NAME_UNIQUE;
    }

    /**
     * app端查询流水线列表
     *
     * @param productionLine 流水线对象
     * @return 结果
     */
    @Override
    public List<ProductionLine> appSelectLineList(ProductionLine productionLine) {
        User user = JwtUtil.getUser();
        if (user == null) {
            return Collections.emptyList();
        }
        User userUni = null;
        if (productionLine == null) {
            return productionLineMapper.selectAllDef0(user.getCompanyId());
        } else {
            productionLine.setCompanyId(user.getCompanyId());
            List<ProductionLine> productionLines = productionLineMapper.selectProductionLineList(productionLine);
            for (ProductionLine line : productionLines) {
                if (user.getSign() == 1 || user.getUserId().intValue() == line.getDeviceLiable() || user.getUserId().intValue() == line.getDeviceLiableTow()) {
                    // 有权限
                    line.setLineSign(1);
                } else {
                    // 无权限
                    line.setLineSign(0);
                }
                userUni = userMapper.selectUserInfoById(line.getDeviceLiable() == null ? 0 : line.getDeviceLiable());
                if (userUni != null) {
                    line.setDeviceLiableName(userUni.getUserName());
                }
                userUni = userMapper.selectUserInfoById(line.getDeviceLiableTow() == null ? 0 : line.getDeviceLiableTow());
                if (userUni != null) {
                    line.setDeviceLiableTowName(userUni.getUserName());
                }
            }
            return productionLines;
        }
    }

    @Override
    public List<ProductionLine> selectAllProductionLineByCompanyId(Cookie[] cookies) {
        User user = JwtUtil.getTokenCookie(cookies);
        if (user == null) return Collections.emptyList();
        return productionLineMapper.selectAllProductionLineByCompanyId(user.getCompanyId());
    }

    /**
     * 更新产线自动采集信息
     * @param line 产线信息
     * @return 结果
     */
    @Override
    public int changeStatus(ProductionLine line) {
        // 查询产线信息
        ProductionLine uniLine = productionLineMapper.selectProductionLineById(line.getId());
        if (uniLine == null) {
            throw new BusinessException("产线不存在或被删除");
        }
        User user = JwtUtil.getUser();
        if (user == null) {
            throw new BusinessException("用户未登录或登录超时");
        }
        if (user.getUserId() != uniLine.getDeviceLiable().intValue() && user.getUserId() != uniLine.getDeviceLiableTow().intValue()) {
            throw new BusinessException("不是产线责任人");
        }
        return productionLineMapper.updateLineStatus(line);
    }

    /**
     * 拉取负责人的产线列表
     * @return 产线列表
     */
    @Override
    public List<ProductionLine> selectMyLineList() {
        User user = JwtUtil.getUser();
        if (user == null) {
            return Collections.emptyList();
        }
        TimeRecord record;
        List<ProductionLine> lineList = productionLineMapper.selectMyLineList(user.getCompanyId(), user.getUserId().intValue());
        for (ProductionLine line : lineList) {
            record = timeRecordMapper.selectTimeRecordByLineIdToday(user.getCompanyId(), line.getId());
            if (record == null) {
                record = new TimeRecord();
            }
            line.setTimeRecord(record);
        }
        return lineList;
    }
}
