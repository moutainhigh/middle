package com.njwd.entity.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Ente {
    /**
     * 企业ID
     */
    private Long enteId;

    /**
    * 企业名称
    */
    private String enteName;
}