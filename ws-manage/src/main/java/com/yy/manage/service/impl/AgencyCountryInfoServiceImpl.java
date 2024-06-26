package com.yy.manage.service.impl;

import java.util.List;

import com.yy.common.annotation.DataScope;
import com.yy.common.core.domain.entity.SysDept;
import com.yy.common.exception.ServiceException;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.CountryInfo;
import com.yy.manage.service.ICountryInfoService;
import com.yy.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.AgencyCountryInfoMapper;
import com.yy.manage.domain.AgencyCountryInfo;
import com.yy.manage.service.IAgencyCountryInfoService;
import org.springframework.transaction.annotation.Transactional;

import static com.yy.common.constant.DictDataConstants.WS_IS_DELETE_0;

/**
 * 代理国家信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-07
 */
@Service
public class AgencyCountryInfoServiceImpl implements IAgencyCountryInfoService {
    @Autowired
    private AgencyCountryInfoMapper agencyCountryInfoMapper;

    @Autowired
    private ICountryInfoService countryInfoService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询代理国家信息
     *
     * @param id 代理国家信息主键
     * @return 代理国家信息
     */
    @Override
    public AgencyCountryInfo selectAgencyCountryInfoById(String id) {
        return agencyCountryInfoMapper.selectAgencyCountryInfoById(id);
    }

    /**
     * 查询代理国家信息列表
     *
     * @param agencyCountryInfo 代理国家信息
     * @return 代理国家信息
     */
    @DataScope(deptAlias = "tb_agency_country_info")
    @Override
    public List<AgencyCountryInfo> selectAgencyCountryInfoList(AgencyCountryInfo agencyCountryInfo) {
        List<AgencyCountryInfo> agencyCountryInfos = agencyCountryInfoMapper.selectAgencyCountryInfoList(agencyCountryInfo);
        for (AgencyCountryInfo info : agencyCountryInfos) {
            SysDept sysDept = deptService.selectDeptById(info.getDeptId());
            if (StringUtils.isNotNull(sysDept)) {
                info.setDeptName(sysDept.getDeptName());
                info.setAgencyUserName(sysDept.getLeader());
            }
            CountryInfo countryInfo = countryInfoService.selectCountryInfoById(info.getCountryId());
            if (StringUtils.isNotNull(countryInfo)) {
                info.setCountryName(countryInfo.getName());
            }
        }
        return agencyCountryInfos;
    }

    /**
     * 新增代理国家信息
     *
     * @param agencyCountryInfo 代理国家信息
     * @return 结果
     */
    @Override
    public int insertAgencyCountryInfo(AgencyCountryInfo agencyCountryInfo) {
        agencyCountryInfo.setId(IdUtils.fastSimpleUUID());
        agencyCountryInfo.setCreateBy(SecurityUtils.getUsername());
        agencyCountryInfo.setDeptId(SecurityUtils.getDeptId());
        agencyCountryInfo.setCreateTime(DateUtils.getNowDate());
        return agencyCountryInfoMapper.insertAgencyCountryInfo(agencyCountryInfo);
    }

    /**
     * 修改代理国家信息
     *
     * @param agencyCountryInfo 代理国家信息
     * @return 结果
     */
    @Override
    public int updateAgencyCountryInfo(AgencyCountryInfo agencyCountryInfo) {
        agencyCountryInfo.setUpdateTime(DateUtils.getNowDate());
        return agencyCountryInfoMapper.updateAgencyCountryInfo(agencyCountryInfo);
    }

    /**
     * 批量删除代理国家信息
     *
     * @param ids 需要删除的代理国家信息主键
     * @return 结果
     */
    @Override
    public int deleteAgencyCountryInfoByIds(String[] ids) {
        return agencyCountryInfoMapper.deleteAgencyCountryInfoByIds(ids);
    }

    /**
     * 删除代理国家信息信息
     *
     * @param id 代理国家信息主键
     * @return 结果
     */
    @Override
    public int deleteAgencyCountryInfoById(String id) {
        return agencyCountryInfoMapper.deleteAgencyCountryInfoById(id);
    }

    @Transactional
    @Override
    public int batchInsertAgencyCountryInfo(AgencyCountryInfo agencyCountryInfo) {
        String[] countrys = agencyCountryInfo.getCountry();
        for (String country : countrys) {
            AgencyCountryInfo info = new AgencyCountryInfo();
            info.setCountryId(country);
            info.setDeptId(SecurityUtils.getDeptId());
            List<AgencyCountryInfo> agencyCountryInfos = agencyCountryInfoMapper.selectAgencyCountryInfoList(info);
            if (StringUtils.isNotEmpty(agencyCountryInfos)) {
                //如果存在则是更新
                AgencyCountryInfo agency = agencyCountryInfos.get(0);
                agency.setPrices(agencyCountryInfo.getPrices());
                agency.setProportion(agencyCountryInfo.getProportion());
                agency.setRemark(agencyCountryInfo.getRemark());
                updateAgencyCountryInfo(agency);
            } else {
                //不存在则是插入
                AgencyCountryInfo agency = new AgencyCountryInfo();
                agency.setPrices(agencyCountryInfo.getPrices());
                agency.setProportion(agencyCountryInfo.getProportion());
                agency.setCountryId(country);
                agency.setRemark(agencyCountryInfo.getRemark());
                insertAgencyCountryInfo(agency);
            }
        }
        return 1;
    }
}
