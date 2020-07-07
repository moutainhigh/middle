package com.njwd.entity.kettlejob;

import com.njwd.entity.basedata.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author jds
 * @Description 巡店项目
 * @create 2019/11/13 9:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PsItem  extends BaseModel {

    private static final long serialVersionUID = 42L;

    /**
     * 应用id
     */
    private String appId;

    /**
     *项目ID
     */
    private String itemId;

    /**
     * 项目名
     */
    private String itemName;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 状态
     */
    private Boolean active;

    /**
     * 分类ID
     */
    private String typeId;

    /**
     * 最后修改时间
     */
    private Date lastUpdateTime;

}
