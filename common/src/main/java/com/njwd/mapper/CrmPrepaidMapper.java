package com.njwd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.kettlejob.dto.CrmConsumeDto;
import com.njwd.entity.kettlejob.dto.CrmPrepaidDto;
import com.njwd.entity.kettlejob.dto.CrmPrepaidPayTypeDto;
import com.njwd.entity.kettlejob.vo.CrmPrepaidPayTypeVo;
import com.njwd.entity.kettlejob.vo.CrmPrepaidVo;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * @Description 会员消息记录
 * @Author 会员消费记录
 * @Date 2019/11/19
 **/

@Repository
public interface CrmPrepaidMapper extends BaseMapper {

    /**
     * 新增会员充值记录
     * @param list
     * @return
     */
    Integer addCrmCardPrepaid(List<CrmPrepaidDto> list);

    /**
     * 查询会员充值记录
     * @param crmPrepaidDto
     * @return
     */
    List<CrmPrepaidVo> findCrmCardPrepaidBatch(@Param("crmCardPrepaidDto") CrmPrepaidDto crmPrepaidDto);

    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updateCrmCardPrepaid(List<CrmPrepaidDto> list);

    /**
     * 查询会员充值记录支付明细
     * @param crmPrepaidDto
     * @return
     */
    List<CrmPrepaidPayTypeVo> findPrepaidPayTypeBatch(@Param("crmCardPrepaidDto") CrmPrepaidDto crmPrepaidDto);


    /**
     * 批量修改
     * @param list
     * @return
     */
    Integer updatePrepaidPayType(List<CrmPrepaidPayTypeDto> list);

    /**
     * 新增会员充值记录
     * @param list
     * @return
     */
    Integer addPrepaidPayType(List<CrmPrepaidPayTypeDto> list);

    /**
     * 洗数据
     * @param prepaidDto
     * @return
     */
    Integer updateCleanPrepaid(@Param("prepaidDto") CrmPrepaidDto prepaidDto);
    /**
     * 洗数据
     * @param prepaidDto
     * @return
     */
    Integer updateCleanPrepaidPayType(@Param("prepaidDto") CrmPrepaidDto prepaidDto);

     /**
     * 查询未清洗的储值数量
     * @param prepaidDto
     * @return
     */
    Integer findUnCleanPrepaidNum(@Param("prepaidDto") CrmPrepaidDto prepaidDto);

    /**
     * 查询未清洗的储值支付明细数量
     * @param prepaidDto
     * @return
     */
    Integer findUnCleanPrepaidPayType(@Param("prepaidDto") CrmPrepaidDto prepaidDto);

    /**
     * 查询储值流水的最大时间
     * @param prepaidDto
     * @return
     */
    String findMaxPrepaidTime(@Param("prepaidDto") CrmPrepaidDto prepaidDto);
    /**
     * 查询未清洗干净的数据
     * 返回基础数据标识
     * @param prepaidDto
     * @return
     */
    List<Map<String,Object>> findUnCleanCode(@Param("prepaidDto") CrmPrepaidDto prepaidDto);

    List<CrmPrepaidVo> findEarlyCardPrepaidGroupByShop(MembershipCardAnalysisDto queryDto);
    /**
     * 查询未清洗干净的数据
     * 返回基础数据标识
     * @param prepaidDto
     * @return
     */
    List<Map<String,Object>> findUnCleanPayTypeCode(@Param("prepaidDto") CrmPrepaidDto prepaidDto);

}
