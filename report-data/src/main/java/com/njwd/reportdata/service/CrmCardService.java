package com.njwd.reportdata.service;

import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.vo.CrmCardVo;

import java.util.List;
/**
 * @Author ZhuHC
 * @Date  2020/2/14 10:05
 * @Description  会员卡分析
 */
public interface CrmCardService {
    /**
     * @Author ZhuHC
     * @Date  2020/2/14 10:03
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.CrmCardVo>
     * @Description 查询期初会员卡数量
     */
    List<CrmCardVo> findEarlyCardNumGroupByShop(MembershipCardAnalysisDto queryDto);
    /**
     * @Author ZhuHC
     * @Date  2020/2/14 10:03
     * @Param [queryDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.CrmCardVo>
     * @Description 查询区间内新增会员卡数量
     */
    List<CrmCardVo> findIncreaseCardNumGroupByShop(MembershipCardAnalysisDto queryDto);
}
