package com.ruoyi.project.device.devNotice.mapper;

import com.ruoyi.project.device.devNotice.domain.DevNotice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 公司消息通知 数据层
 * 
 * @author zqm
 * @date 2019-04-18
 */
public interface DevNoticeMapper 
{
	/**
     * 查询公司消息通知信息
     * 
     * @param id 公司消息通知ID
     * @return 公司消息通知信息
     */
	public DevNotice selectDevNoticeById(Integer id);
	
	/**
     * 查询公司消息通知列表
     * 
     * @param devNotice 公司消息通知信息
     * @return 公司消息通知集合
     */
	public List<DevNotice> selectDevNoticeList(DevNotice devNotice);
	
	/**
     * 新增公司消息通知
     * 
     * @param devNotice 公司消息通知信息
     * @return 结果
     */
	public int insertDevNotice(DevNotice devNotice);
	
	/**
     * 修改公司消息通知
     * 
     * @param devNotice 公司消息通知信息
     * @return 结果
     */
	public int updateDevNotice(DevNotice devNotice);

	/**
     * 删除公司消息通知
     * 
     * @param id 公司消息通知ID
     * @return 结果
     */
	public int deleteDevNoticeById(Integer id);
	
	/**
     * 批量删除公司消息通知
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
	public int deleteDevNoticeByIds(String[] ids);

	/**
	 * 查询公司的所有的消息
	 * @param companyId
	 * @return
	 */
    List<DevNotice> selectAllNotice(@Param("companyId") Integer companyId);

}