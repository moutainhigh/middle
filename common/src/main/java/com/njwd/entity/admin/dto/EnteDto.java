package com.njwd.entity.admin.dto;

import com.njwd.entity.admin.Ente;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 企业信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EnteDto extends Ente {
    /**
     * 0：集成中台 1：数据中台
     */
    private String adminOrReport;

    /**
     * 中台企业id 做类型转换
     */
    private String enterpriseId;
}
