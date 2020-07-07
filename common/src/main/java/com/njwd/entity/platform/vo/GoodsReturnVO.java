package com.njwd.entity.platform.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class GoodsReturnVO implements Serializable {

        @ApiModelProperty(value = "开始条数")
        private Integer fromNum;// 开始条数

        @ApiModelProperty(value = "返回执行状态(成功success)")
        private String status;	// 返回执行状态(成功success)

        @ApiModelProperty(value = "页码")
        private Integer page;	// 页码

        @ApiModelProperty(value = "每页条数")
        private Integer pageSize;// 每页条数

        @ApiModelProperty(value = "结束条数")
        private Integer endNum;// 结束条数

        @ApiModelProperty(value = "信息总条数")
        private Integer infoCount;// 信息总条数

        @ApiModelProperty(value = "总页数")
        private Integer pageNum;// 总页数

        @ApiModelProperty(value = "商品列表，结果集")
        private List<GoodsVO> listData;

        @ApiModelProperty(value = "商品分类ID")
        private Long typeId;// 商品分类ID

        @ApiModelProperty(value = "商品分类名称")
        private String typeName;// 商品分类名称

}
