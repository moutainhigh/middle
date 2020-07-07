package com.njwd.reportdata.service.impl;

import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.vo.CrmCardVo;
import com.njwd.reportdata.mapper.CrmCardMapper;
import com.njwd.reportdata.service.CrmCardService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description
 * @Author: ZhuHC
 * @Date: 2020/2/14 10:02
 */
@Service
public class CrmCardServiceImpl implements CrmCardService {
    @Resource
    private CrmCardMapper crmCardMapper;

    /**
     * @Author ZhuHC
     * @Date  2020/2/14 10:03
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.CrmCardVo>
     * @Description 查询期初会员卡数量
     */
    @Override
    public List<CrmCardVo> findEarlyCardNumGroupByShop(MembershipCardAnalysisDto queryDto) {
        return crmCardMapper.findEarlyCardNumGroupByShop(queryDto);
    }
    /**
     * @Author ZhuHC
     * @Date  2020/2/14 10:03
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.CrmCardVo>
     * @Description 查询区间内新增会员卡数量
     */
    @Override
    public List<CrmCardVo> findIncreaseCardNumGroupByShop(MembershipCardAnalysisDto queryDto) {
        return crmCardMapper.findIncreaseCardNumGroupByShop(queryDto);
    }
}
