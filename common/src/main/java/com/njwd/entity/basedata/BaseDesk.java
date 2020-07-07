package com.njwd.entity.basedata;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description: 台位表
 * @Author LuoY
 * @Date 2019/12/7
 */
@Data
public class BaseDesk implements Serializable {
    private static final long serialVersionUID = 7268060949587519397L;

    /**
     * 台位id
     */
    private String deskId;

    /**
     * 台位编号
     */
    private String deskNo;

    /**
     * 台位名称
     */
    private String deskName;

    /**
     * 台位区域id
     */
    private String deskAreaId;

    /**
     * 台位类型id
     */
    private String deskTypeId;

    /**
     * 门店id
     */
    private String shopId;

    /**
     * 集团id
     */
    private String enteId;

    /**
     * 人数
     */
    private Integer peopleNum;

    /**
     * 桌台区域类型编码
     */
    private String deskAreaTypeNo;
}
