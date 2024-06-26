package com.yy.manage.service.impl;

import java.util.List;

import com.yy.common.exception.ServiceException;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.CountryInfoMapper;
import com.yy.manage.domain.CountryInfo;
import com.yy.manage.service.ICountryInfoService;
import org.yaml.snakeyaml.events.Event;

import static com.yy.common.constant.DictDataConstants.WS_IS_DELETE_0;

/**
 * 国家信息Service业务层处理
 * 
 * @author yy
 * @date 2024-06-07
 */
@Service
public class CountryInfoServiceImpl implements ICountryInfoService 
{
    @Autowired
    private CountryInfoMapper countryInfoMapper;

    /**
     * 查询国家信息
     * 
     * @param id 国家信息主键
     * @return 国家信息
     */
    @Override
    public CountryInfo selectCountryInfoById(String id)
    {
        return countryInfoMapper.selectCountryInfoById(id);
    }

    /**
     * 查询国家信息列表
     * 
     * @param countryInfo 国家信息
     * @return 国家信息
     */
    @Override
    public List<CountryInfo> selectCountryInfoList(CountryInfo countryInfo)
    {
        return countryInfoMapper.selectCountryInfoList(countryInfo);
    }

    /**
     * 新增国家信息
     * 
     * @param countryInfo 国家信息
     * @return 结果
     */
    @Override
    public int insertCountryInfo(CountryInfo countryInfo)
    {
        //查询是否有此国家
        CountryInfo info = new CountryInfo();
        info.setName(countryInfo.getName());
        List<CountryInfo> countryInfos = countryInfoMapper.selectCountryInfoList(info);
        if (StringUtils.isNotEmpty(countryInfos)) {
            throw new ServiceException("此国家已存在！！！");
        }

        countryInfo.setId(IdUtils.fastSimpleUUID());
        countryInfo.setAllName(countryInfo.getName()+"-"+countryInfo.getAdName()+"-"+countryInfo.getPhoneCode());
        countryInfo.setCreateBy(SecurityUtils.getUsername());
        countryInfo.setCreateTime(DateUtils.getNowDate());
        return countryInfoMapper.insertCountryInfo(countryInfo);
    }

    /**
     * 修改国家信息
     * 
     * @param countryInfo 国家信息
     * @return 结果
     */
    @Override
    public int updateCountryInfo(CountryInfo countryInfo)
    {
        countryInfo.setAllName(countryInfo.getName()+"-"+countryInfo.getAdName()+"-"+countryInfo.getPhoneCode());
        countryInfo.setUpdateTime(DateUtils.getNowDate());
        return countryInfoMapper.updateCountryInfo(countryInfo);
    }

    /**
     * 批量删除国家信息
     * 
     * @param ids 需要删除的国家信息主键
     * @return 结果
     */
    @Override
    public int deleteCountryInfoByIds(String[] ids)
    {
        return countryInfoMapper.deleteCountryInfoByIds(ids);
    }

    /**
     * 删除国家信息信息
     * 
     * @param id 国家信息主键
     * @return 结果
     */
    @Override
    public int deleteCountryInfoById(String id)
    {
        return countryInfoMapper.deleteCountryInfoById(id);
    }
}
