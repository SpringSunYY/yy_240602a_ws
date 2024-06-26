package com.yy.manage.service.impl;

import java.util.List;

import com.yy.common.annotation.DataScope;
import com.yy.common.core.domain.entity.SysDept;
import com.yy.common.core.domain.entity.SysUser;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.AgencyCountryInfo;
import com.yy.system.service.ISysDeptService;
import com.yy.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.AgencyUserInfoMapper;
import com.yy.manage.domain.AgencyUserInfo;
import com.yy.manage.service.IAgencyUserInfoService;

import static com.yy.common.constant.DictDataConstants.WS_IS_DELETE_0;

/**
 * 代理用户信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-07
 */
@Service
public class AgencyUserInfoServiceImpl implements IAgencyUserInfoService {
    @Autowired
    private AgencyUserInfoMapper agencyUserInfoMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询代理用户信息
     *
     * @param id 代理用户信息主键
     * @return 代理用户信息
     */
    @Override
    public AgencyUserInfo selectAgencyUserInfoById(String id) {
        return agencyUserInfoMapper.selectAgencyUserInfoById(id);
    }

    /**
     * 查询代理用户信息列表
     *
     * @param agencyUserInfo 代理用户信息
     * @return 代理用户信息
     */
    @DataScope(deptAlias = "tb_agency_user_info",userAlias ="tb_agency_user_info")
    @Override
    public List<AgencyUserInfo> selectAgencyUserInfoList(AgencyUserInfo agencyUserInfo) {
        List<AgencyUserInfo> agencyUserInfos = agencyUserInfoMapper.selectAgencyUserInfoList(agencyUserInfo);
        for (AgencyUserInfo info : agencyUserInfos) {
            //判断是否为空并赋值
            SysUser sysUser = userService.selectUserById(info.getUserId());
            if (StringUtils.isNotNull(sysUser)) {
                info.setUserName(sysUser.getUserName());
            }
            SysDept sysDept = deptService.selectDeptById(info.getDeptId());
            if (StringUtils.isNotNull(sysDept)) {
                info.setDeptName(sysDept.getDeptName());
                info.setAgencyUserName(sysDept.getLeader());
            }
        }
        return agencyUserInfos;
    }

    /**
     * 新增代理用户信息
     *
     * @param agencyUserInfo 代理用户信息
     * @return 结果
     */
    @Override
    public int insertAgencyUserInfo(AgencyUserInfo agencyUserInfo) {
        agencyUserInfo.setId(IdUtils.fastSimpleUUID());
        agencyUserInfo.setDeptId(SecurityUtils.getDeptId());
        agencyUserInfo.setCreateBy(SecurityUtils.getUsername());
        agencyUserInfo.setCreateTime(DateUtils.getNowDate());
        return agencyUserInfoMapper.insertAgencyUserInfo(agencyUserInfo);
    }

    /**
     * 修改代理用户信息
     *
     * @param agencyUserInfo 代理用户信息
     * @return 结果
     */
    @Override
    public int updateAgencyUserInfo(AgencyUserInfo agencyUserInfo) {
        agencyUserInfo.setUpdateTime(DateUtils.getNowDate());
        return agencyUserInfoMapper.updateAgencyUserInfo(agencyUserInfo);
    }

    /**
     * 批量删除代理用户信息
     *
     * @param ids 需要删除的代理用户信息主键
     * @return 结果
     */
    @Override
    public int deleteAgencyUserInfoByIds(String[] ids) {
        return agencyUserInfoMapper.deleteAgencyUserInfoByIds(ids);
    }

    /**
     * 删除代理用户信息信息
     *
     * @param id 代理用户信息主键
     * @return 结果
     */
    @Override
    public int deleteAgencyUserInfoById(String id) {
        return agencyUserInfoMapper.deleteAgencyUserInfoById(id);
    }

    @Override
    public int batchInsertAgencyUserInfo(AgencyUserInfo agencyUserInfo) {
        Long[] users = agencyUserInfo.getUsers();
        for (Long userId : users) {
            AgencyUserInfo info = new AgencyUserInfo();
            info.setUserId(userId);
            info.setDeptId(SecurityUtils.getDeptId());
            List<AgencyUserInfo> agencyUserInfos = agencyUserInfoMapper.selectAgencyUserInfoList(info);
            if (StringUtils.isNotEmpty(agencyUserInfos)) {
                //如果存在则是更新
                AgencyUserInfo agency = agencyUserInfos.get(0);
                agency.setPrices(agencyUserInfo.getPrices());
                agency.setProportion(agencyUserInfo.getProportion());
                agency.setRemark(agencyUserInfo.getRemark());
                updateAgencyUserInfo(agency);
            } else {
                //不存在则是插入
                AgencyUserInfo agency = new AgencyUserInfo();
                agency.setPrices(agencyUserInfo.getPrices());
                agency.setProportion(agencyUserInfo.getProportion());
                agency.setUserId(userId);
                agency.setRemark(agencyUserInfo.getRemark());
                insertAgencyUserInfo(agency);
            }
        }
        return 1;
    }
}
