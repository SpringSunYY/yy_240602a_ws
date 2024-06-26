package com.yy.manage.service;

import java.util.List;

import com.yy.manage.domain.AgencyUserInfo;

/**
 * 代理用户信息Service接口
 *
 * @author yy
 * @date 2024-06-07
 */
public interface IAgencyUserInfoService {
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
     * 批量删除代理用户信息
     *
     * @param ids 需要删除的代理用户信息主键集合
     * @return 结果
     */
    public int deleteAgencyUserInfoByIds(String[] ids);

    /**
     * 删除代理用户信息信息
     *
     * @param id 代理用户信息主键
     * @return 结果
     */
    public int deleteAgencyUserInfoById(String id);

    /**
     * @description: 批量插入代理用户
     * @author: YY
     * @method: batchInsertAgencyUserInfo
     * @date: 2024/6/7 16:46
     * @param:
     * @param: agencyUserInfo
     * @return: int
     **/
    int batchInsertAgencyUserInfo(AgencyUserInfo agencyUserInfo);
}
