package com.yy.manage.service;

import java.util.List;

import com.yy.manage.domain.AgencyCountryInfo;

/**
 * 代理国家信息Service接口
 *
 * @author yy
 * @date 2024-06-07
 */
public interface IAgencyCountryInfoService {
    /**
     * 查询代理国家信息
     *
     * @param id 代理国家信息主键
     * @return 代理国家信息
     */
    public AgencyCountryInfo selectAgencyCountryInfoById(String id);

    /**
     * 查询代理国家信息列表
     *
     * @param agencyCountryInfo 代理国家信息
     * @return 代理国家信息集合
     */
    public List<AgencyCountryInfo> selectAgencyCountryInfoList(AgencyCountryInfo agencyCountryInfo);

    /**
     * 新增代理国家信息
     *
     * @param agencyCountryInfo 代理国家信息
     * @return 结果
     */
    public int insertAgencyCountryInfo(AgencyCountryInfo agencyCountryInfo);

    /**
     * 修改代理国家信息
     *
     * @param agencyCountryInfo 代理国家信息
     * @return 结果
     */
    public int updateAgencyCountryInfo(AgencyCountryInfo agencyCountryInfo);

    /**
     * 批量删除代理国家信息
     *
     * @param ids 需要删除的代理国家信息主键集合
     * @return 结果
     */
    public int deleteAgencyCountryInfoByIds(String[] ids);

    /**
     * 删除代理国家信息信息
     *
     * @param id 代理国家信息主键
     * @return 结果
     */
    public int deleteAgencyCountryInfoById(String id);

    /**
     * @description: 批量处理插入
     * @author: YY
     * @method: batchInsertAgencyCountryInfo
     * @date: 2024/6/7 15:55
     * @param:
     * @param: agencyCountryInfo
     * @return: int
     **/
    int batchInsertAgencyCountryInfo(AgencyCountryInfo agencyCountryInfo);
}
