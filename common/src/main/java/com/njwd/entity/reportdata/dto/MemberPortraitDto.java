package com.njwd.entity.reportdata.dto;

import com.njwd.entity.reportdata.dto.querydto.BaseQueryDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 会员画像dto
 */
@Getter
@Setter
public class MemberPortraitDto extends BaseQueryDto {

    /**
     * 会员卡id
     */
    private List<String> cardIdList;
}
