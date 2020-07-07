package com.njwd.reportdata.mapper;

import com.njwd.entity.basedata.vo.BaseUserVo;
import com.njwd.entity.reportdata.dto.StaffAnalysisDto;
import com.njwd.entity.reportdata.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author ZhuHC
 * @Date  2020/2/10 11:57
 * @Description
 */
@Repository
public interface StaffAnalysisMapper {

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffOnJobAnalysisVo>
     * @Author ZhuHC
     * @Date 2020/2/7 17:52
     * @Param [queryDto]
     * @Description 在职员工分析
     */
    List<BaseUserVo> findOnJobStaffList(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * @return java.util.List<com.njwd.entity.reportdata.vo.StaffOnJobAnalysisVo>
     * @Author ZhuHC
     * @Date 2020/2/7 17:52
     * @Param [queryDto]
     * @Description 离职员工分析
     */
    List<BaseUserVo> findSeparatedStaffList(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    StaffTypeInfoVo findStaffNumList(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

   /**
    * @Author ZhuHC
    * @Date  2020/4/3 15:09
    * @Param
    * @return
    * @Description 工作时长
    */
    List<StaffWorkHoursVo> findStaffWorkHoursList(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

   /**
    * @Author ZhuHC
    * @Date  2020/4/3 15:09
    * @Param
    * @return
    * @Description 查询前厅后厨员工人数
    */
    List<EfficiencyPerCapitaVo> findStaffListByNo(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date  2020/3/25 10:33
     * @Param
     * @return
     * @Description 门店客流
     */
    List<EfficiencyPerCapitaVo> findCustomNumByShop(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * @Author ZhuHC
     * @Date  2020/4/3 15:10
     * @Param
     * @return
     * @Description 门店菜品数量
     */
    List<EfficiencyPerCapitaVo> findDishesNumByShop(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * 人效分析
     * @param staffAnalysisDto
     * @return
     */
    List<EffectAnalysisVo> findUserNumByShopIds(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * 人效分析
     * @param staffAnalysisDto
     * @return
     */
    List<EffectAnalysisVo> findEffectAnalysis(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);
    /**
     * 员工userId
     * @param staffAnalysisDto
     * @return
     */
    List<String> findUserIdListByShopIds(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * 品牌奖金
     * @param staffAnalysisDto
     * @return
     */
    List<BrandBonusVo> findUserInfoListByShopIds(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);

    /**
     * @Description: 查询参数信息
     * @Author: 周鹏
     * @Date: 2020/03/27
     */
    List<DictVo> findDictListByModelName(Map<String,Object> map);
    /**
     * 查询员工出勤时长
     * @param staffAnalysisDto
     * @return
     */
    List<BrandBonusVo> findUserAttendHour(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);
    /**
     * 查询员工请假时长
     * @param staffAnalysisDto
     * @return
     */
    List<BrandBonusVo> findUserLeaveHour(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);
    /**
     * 查询净利润
     * @param queryDto
     * @return
     */
    List<BrandBonusVo> findNetProfitList(@Param("queryDto") StaffAnalysisDto queryDto);
    /**
     * 品牌奖金-查询门店信息
     * @param queryDto
     * @return
     */
    List<BrandBonusVo> findShopInfo(@Param("staffAnalysisDto") StaffAnalysisDto queryDto);

    /**
     * 查询门店员工信息
     * @param queryDto
     * @return
     */
    List<BaseUserVo> findUserInfoByShopIds (@Param("staffAnalysisDto") StaffAnalysisDto queryDto);

    /**
     * 查询员工出勤标准工时
     * @param staffAnalysisDto
     * @return
     */
    List<BrandBonusVo> findUserStandardHour(@Param("staffAnalysisDto") StaffAnalysisDto staffAnalysisDto);
}