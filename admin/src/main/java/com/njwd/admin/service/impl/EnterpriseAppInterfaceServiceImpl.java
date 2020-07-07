package com.njwd.admin.service.impl;

import com.njwd.admin.mapper.EnterpriseAppInterfaceMapper;
import com.njwd.admin.service.EnterpriseAppInterfaceService;
import com.njwd.common.AdminConstant;
import com.njwd.entity.admin.dto.EnterpriseAppInterfaceDto;
import com.njwd.entity.admin.vo.EnterpriseAppInterfaceVo;
import com.njwd.utils.StringUtil;
import com.njwd.utils.idworker.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author XiaFq
 * @Description EnterpriseAppServiceImpl 企业应用service实现类
 * @Date 2019/11/11 9:48 上午
 * @Version 1.0
 */
@Service
public class EnterpriseAppInterfaceServiceImpl implements EnterpriseAppInterfaceService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EnterpriseAppInterfaceServiceImpl.class);

    @Resource
    private EnterpriseAppInterfaceMapper enterpriseAppInterfaceMapper;

    @Resource
    private IdWorker idWorker;

    /**
     * @param enterpriseAppInterfaceDto
     * @return List<EnterpriseAppInfoVo>
     * @Author XiaFq
     * @Description 查询企业应用-接口关联列表
     * @Date 2019/11/30 3:06 下午
     * @Param enterpriseAppInterfaceDto
     */
    @Override
    public List<EnterpriseAppInterfaceVo> queryList(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto) {
        return enterpriseAppInterfaceMapper.queryList(enterpriseAppInterfaceDto);
    }

    /**
     * @param enterpriseAppInterfaceDto
     * @return EnterpriseAppInterfaceVo
     * @Author XiaFq
     * @Description 根据id查询企业应用-接口关联关系信息
     * @Date 2019/11/30 3:11 下午
     * @Param enterpriseAppInterfaceDto
     */
    @Override
    public EnterpriseAppInterfaceVo queryInterfaceById(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto) {
        return enterpriseAppInterfaceMapper.queryInterfaceById(enterpriseAppInterfaceDto);
    }

    /**
     * @param enterpriseAppInterfaceDto
     * @return int -1 更新失败 0 更新成功
     * @Author XiaFq
     * @Description 保存企业应用-接口关联信息
     * @Date 2019/11/30 3:12 下午
     * @Param enterpriseAppInterfaceDto
     */
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public int saveInterface(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto) {
        try {
            String interfaceId = idWorker.nextId();
            enterpriseAppInterfaceDto.setInterfaceId(interfaceId);
            enterpriseAppInterfaceDto.setCreateTime(new Date());
            enterpriseAppInterfaceMapper.saveInterface(enterpriseAppInterfaceDto);
        } catch (Exception e) {
            e.printStackTrace();
            return AdminConstant.Number.ADD_FAILED;
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }

    /**
     * @param enterpriseAppInterfaceDto
     * @return int -1 更新失败 0 更新成功
     * @Author XiaFq
     * @Description 更新企业应用-接口关联信息
     * @Date 2019/11/30 3:12 下午
     * @Param enterpriseAppInterfaceDto
     */
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public int updateInterface(EnterpriseAppInterfaceDto enterpriseAppInterfaceDto) {
        try {
            EnterpriseAppInterfaceVo oldPojo = enterpriseAppInterfaceMapper.queryInterfaceById(enterpriseAppInterfaceDto);
            if (oldPojo != null) {
                enterpriseAppInterfaceDto.setInterfaceId(oldPojo.getInterfaceId());
                enterpriseAppInterfaceDto.setUpdateTime(new Date());
                enterpriseAppInterfaceMapper.updateInterface(enterpriseAppInterfaceDto);
            } else {
                return AdminConstant.Number.RECORD_NOT_EXIST;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AdminConstant.Number.UPDATE_FAILED;
        }
        return AdminConstant.Number.ADD_SUCCESS;
    }

    /**
     * @param interfaceId
     * @return int -1 更新失败 0 更新成功
     * @Author XiaFq
     * @Description 删除企业应用-接口关联信息
     * @Date 2019/11/30 3:13 下午
     * @Param enterpriseAppInterfaceDto
     */
    @Transactional(rollbackFor = Exception.class, readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public int deleteInterface(String interfaceId) {
        try {
            EnterpriseAppInterfaceDto dto = new EnterpriseAppInterfaceDto();
            dto.setInterfaceId(interfaceId);
            EnterpriseAppInterfaceVo oldPojo = enterpriseAppInterfaceMapper.queryInterfaceById(dto);
            if (oldPojo != null) {
                enterpriseAppInterfaceMapper.deleteInterface(interfaceId);
            } else {
                return AdminConstant.Number.DELETE_FAILED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AdminConstant.Number.DELETE_FAILED;
        }
        return AdminConstant.Number.DELETE_SUCCESS;
    }
}
