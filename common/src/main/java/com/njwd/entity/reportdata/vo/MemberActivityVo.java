package com.njwd.entity.reportdata.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 会员活跃度vo
 * @author  ljc
 * @Data 2020/3/22
 */
@Getter
@Setter
public class MemberActivityVo {
    /**
     * 会员id
     */
    @ApiModelProperty(name="memberId",value = "会员id")
    private String memberId;
    /**
     * 会员名称
     */
    @ApiModelProperty(name="memberName",value = "会员名称")
    private String memberName;
    /**
     * 卡号
     */
    @ApiModelProperty(name="cardNo",value = "卡号")
    private String cardNo;
    /**
     * 手机号
     */
    @ApiModelProperty(name="mobile",value = "手机号")
    private String mobile;
    /**
     * 消费频次
     */
    @ApiModelProperty(name="consumeFrequency",value = "消费频次")
    private Integer consumeFrequency;

    /**
     * 留言板
     */
    @ApiModelProperty(name="messageBoard",value = "留言板")
    private Integer messageBoard;
    /**
     * 消息框
     */
    @ApiModelProperty(name="messageBox",value = "消息框")
    private Integer  messageBox;
    /**
     * 在看
     */
    @ApiModelProperty(name="look",value = "在看")
    private BigDecimal  look;
    /**
     * 查看
     */
    @ApiModelProperty(name="check",value = "查看")
    private String  check;
}
