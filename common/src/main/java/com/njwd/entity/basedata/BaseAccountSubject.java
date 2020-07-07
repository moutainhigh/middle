package com.njwd.entity.basedata;

import lombok.Data;

/**
* @Description: 会计科目
* @Author: LuoY
* @Date: 2020/2/6 16:31
*/
@Data
public class BaseAccountSubject {
    /**
     * 科目id
     */
    private String accountSubjectId;

    /**
     * 企业id
     */
    private String enteId;

    /**
     * 公司id
     */
    private String companyId;

    /**
     * 科目类型
     */
    private String subjectTypeId;

    /**
     * 会计科目表id
     */
    private String subjectId;

    /**
     * 会计要素项id
     */
    private String accountElementItemId;

    /**
     * 上级科目id
     */
    private String upId;

    /**
     * 科目code
     */
    private String accountSubjectCode;

    /**
     * 科目名称
     */
    private String accountSubjectName;

    /**
     * 科目全称
     */
    private String fullName;

    /**
     * 科目级次
     */
    private Integer subjectLevel;

    /**
     * 科目方向
     */
    private Integer balanceDirection;
}

