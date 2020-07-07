package com.njwd.reportdata.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.njwd.entity.basedata.dto.BaseShopDto;
import com.njwd.entity.basedata.vo.BaseShopVo;
import com.njwd.entity.reportdata.dto.MembershipCardAnalysisDto;
import com.njwd.entity.reportdata.vo.MembershipCardAnalysisVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author ZhuHC
 * @Date  2019/11/27 14:31
 * @Description
 */
@Repository
public interface BaseShopMapper extends BaseMapper<BaseShopDto> {

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 14:15
     * @Param [baseShopDto]
     * @return java.util.List<com.njwd.entity.reportdata.vo.BaseShopVo>
     * @Description 查询门店相关信息
     */
    List<BaseShopVo> findShopInfo(@Param("baseShopDto")BaseShopDto baseShopDto);

    //带门店面积的查询
    List<BaseShopVo> findShopInfoForArea(@Param("baseShopDto")BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2020/4/1 10:48
     * @Param
     * @return
     * @Description 查询门店开业关停时间
     */
    List<BaseShopVo> findShopDate(@Param("baseShopDto")BaseShopDto baseShopDto);

    /**
     * @Description 查询门店下拉
     * @Author 郑勇浩
     * @Data 2020/3/5 11:02
     * @Param [param]
     * @return java.util.List<com.njwd.entity.basedata.vo.BaseShopVo>
     */
    List<BaseShopVo> findShopList(@Param("param")BaseShopDto param);

    /**
     * @Author ZhuHC
     * @Date  2019/11/27 15:41
     * @Param [baseShopDto]
     * @return java.lang.Integer
     * @Description 更新门店 门店面积 状态等信息
     */
    Integer updateShopInfoById(@Param("baseShopDto")BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:13
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 修改
     */
    Integer changeShopStatusInfoById(@Param("baseShopDto")BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:13
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 设置关停时间
     */
    Integer updateShopShutdownDate(@Param("baseShopDto")BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/11/28 11:13
     * @Param [baseShopDto]
     * @return com.njwd.support.Result<java.lang.Integer>
     * @Description 插入开业时间
     */
    Integer insertShopOpeningDate(@Param("baseShopDto")BaseShopDto baseShopDto);

    /**
     * @Author ZhuHC
     * @Date  2019/12/2 17:34
     * @Param
     * @return
     * @Description
     */
    Integer updateShopInfoList(@Param("baseShopVoList")List<BaseShopVo> baseShopVoList);
    /**
     * @Author ZhuHC
     * @Date  2019/12/2 17:34
     * @Param
     * @return
     * @Description
     */
    Integer insertReaShopInfoList(@Param("baseShopVoList")List<BaseShopVo> baseShopVoList);

    List<MembershipCardAnalysisVo> findShopDetail(@Param("queryDto")MembershipCardAnalysisDto queryDto);
}
