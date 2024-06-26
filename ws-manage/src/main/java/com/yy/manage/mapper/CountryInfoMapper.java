package com.yy.manage.mapper;

import java.util.List;
import com.yy.manage.domain.CountryInfo;

/**
 * 国家信息Mapper接口
 * 
 * @author yy
 * @date 2024-06-07
 */
public interface CountryInfoMapper 
{
    /**
     * 查询国家信息
     * 
     * @param id 国家信息主键
     * @return 国家信息
     */
    public CountryInfo selectCountryInfoById(String id);

    /**
     * 查询国家信息列表
     * 
     * @param countryInfo 国家信息
     * @return 国家信息集合
     */
    public List<CountryInfo> selectCountryInfoList(CountryInfo countryInfo);

    /**
     * 新增国家信息
     * 
     * @param countryInfo 国家信息
     * @return 结果
     */
    public int insertCountryInfo(CountryInfo countryInfo);

    /**
     * 修改国家信息
     * 
     * @param countryInfo 国家信息
     * @return 结果
     */
    public int updateCountryInfo(CountryInfo countryInfo);

    /**
     * 删除国家信息
     * 
     * @param id 国家信息主键
     * @return 结果
     */
    public int deleteCountryInfoById(String id);

    /**
     * 批量删除国家信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCountryInfoByIds(String[] ids);
}
