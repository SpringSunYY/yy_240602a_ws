package com.yy.manage.mapper;

import java.util.List;
import com.yy.manage.domain.AgencyCountryInfo;

/**
 * 代理国家信息Mapper接口
 * 
 * @author yy
 * @date 2024-06-07
 */
public interface AgencyCountryInfoMapper 
{
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
     * 删除代理国家信息
     * 
     * @param id 代理国家信息主键
     * @return 结果
     */
    public int deleteAgencyCountryInfoById(String id);

    /**
     * 批量删除代理国家信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAgencyCountryInfoByIds(String[] ids);
}
