package com.njwd.entity.platform.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class DealUpdateMemberDto implements Serializable {

    /**
     * CRM会员号
     */
    private Long cardId;

    /**
     * 手机号
     */
    private String teleNo;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 公司名
     */
    private String company;
}
