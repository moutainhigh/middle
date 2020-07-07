package com.njwd.entity.reportdata.vo;


import lombok.Data;

import java.util.List;

/**
 * @author ljc
 * @Description 会员消费充值统计报表
 * @create 2019/11/26
 */
@Data
public class MemberPrepaidConsumeStatiVo {

    /**
     * 会员消费统计
     */
    List<MemberPrepaidConsumeVo> cardConsumeList;
    /**
     * 会员储值统计
     */
    List<MemberPrepaidConsumeVo> cardPrepaidList;
    /**
     * 会员撤销消费统计
     */
    List<MemberPrepaidConsumeVo> revokeConsumeList;
    /**
     * 会员撤销储值统计
     */
    List<MemberPrepaidConsumeVo> revokePrepaidList;
}
