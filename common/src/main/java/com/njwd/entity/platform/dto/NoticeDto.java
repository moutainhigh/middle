package com.njwd.entity.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * wd_notice
 * @author 
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NoticeDto implements Serializable {
    /**
     * 主键 默认自动递增
     */
    @ApiModelProperty(value = "主键 默认自动递增")
    private Long noticeId;

    /**
     * 公告类型
     */
    @ApiModelProperty(value = "公告类型")
    private Integer noticeType;

    /**
     * 公告标题
     */
    @ApiModelProperty(value = "公告标题")
    private String title;

    /**
     * 是否置顶 0否1是
     */
    @ApiModelProperty(value = "是否置顶 0否1是")
    private Integer isTop;

    /**
     * 是否删除 0否1是
     */
    @ApiModelProperty(value = "是否删除 0否1是")
    private Integer isDel;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    /**
     * 公告内容
     */
    @ApiModelProperty(value = "公告内容")
    private String remark;

}