package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.reportdata.RepPosDetailPay;
import com.njwd.entity.reportdata.dto.RepPosDetailPayDto;
import com.njwd.entity.reportdata.dto.querydto.FinQueryDto;
import com.njwd.entity.reportdata.vo.RepPosDetailPayVo;
import com.njwd.entity.reportdata.vo.fin.FinRentAccountedForVo;

import java.util.List;

/**
* @Description: 支付方式明细报表
* @Author: LuoY
* @Date: 2020/1/2 11:49
*/
public interface RepPosDetailPayMapper extends BaseMapper<RepPosDetailPay> {
    /** 
    * @Description: 查询支付方式明细表   
    * @Param: [repPosDetailPayDto] 
    * @return: java.util.List<com.njwd.entity.reportdata.vo.RepPosDetailPayVo> 
    * @Author: LuoY
    * @Date: 2020/1/2 13:34
    */ 
    List<RepPosDetailPayVo> findRepPosDetailPayInfoByCondition(RepPosDetailPayDto repPosDetailPayDto);


    /***
     * @Description: 查询支付金额
     * @Param: [queryDto]
     * @return: java.util.List<com.njwd.entity.reportdata.vo.FinRentAccountedForVo>
     * @Author: liBao
     * @Date: 2020/1/2 15:17
     */
    List<FinRentAccountedForVo> findRepPosPayVoInfoByCondition(FinQueryDto queryDto);
}