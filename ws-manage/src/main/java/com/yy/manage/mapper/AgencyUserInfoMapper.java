package com.yy.manage.mapper;

import java.util.List;
import com.yy.manage.domain.AgencyUserInfo;

/**
 * 代理用户信息Mapper接口
 * 
 * @author yy
 * @date 2024-06-07
 */
public interface AgencyUserInfoMapper 
{
    /**
     * 查询代理用户信息
     * 
     * @param id 代理用户信息主键
     * @return 代理用户信息
     */
    public AgencyUserInfo selectAgencyUserInfoById(String id);

    /**
     * 查询代理用户信息列表
     * 
     * @param agencyUserInfo 代理用户信息
     * @return 代理用户信息集合
     */
    public List<AgencyUserInfo> selectAgencyUserInfoList(AgencyUserInfo agencyUserInfo);

    /**
     * 新增代理用户信息
     * 
     * @param agencyUserInfo 代理用户信息
     * @return 结果
     */
    public int insertAgencyUserInfo(AgencyUserInfo agencyUserInfo);

    /**
     * 修改代理用户信息
     * 
     * @param agencyUserInfo 代理用户信息
     * @return 结果
     */
    public int updateAgencyUserInfo(AgencyUserInfo agencyUserInfo);

    /**
     * 删除代理用户信息
     * 
     * @param id 代理用户信息主键
     * @return 结果
     */
    public int deleteAgencyUserInfoById(String id);

    /**
     * 批量删除代理用户信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgencyUserInfoByIds(String[] ids);
}
