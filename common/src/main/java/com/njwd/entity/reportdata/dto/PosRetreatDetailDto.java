package com.njwd.entity.reportdata.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import com.njwd.entity.reportdata.vo.PosRetreatDetailVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:退菜明细dto
 * @Author shenhf
 * @Date 2019/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PosRetreatDetailDto extends BaseQueryDto implements Serializable {

    private static final long serialVersionUID = 3196537579308531050L;
    private Page<PosRetreatDetailVo> page = new Page();
    /**
     * 门店ID
     */
    private List<String> shopIdList;
    /**
     * 门店ID
     */
    private List<String> orderTypeIdList;

    /**
     * 以下为 Excel导出用
     */
    private String modelType;

    private String menuName;

    private String shopTypeName;

    private String orgTree;

    private String orderTypeName;
}
