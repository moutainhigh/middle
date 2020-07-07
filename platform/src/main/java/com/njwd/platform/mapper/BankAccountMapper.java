package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.BankAccountDto;
import com.njwd.entity.platform.vo.BankAccountVo;

import java.util.List;

public interface BankAccountMapper {
    int deleteByPrimaryKey(Long bankAccountId);

    int insert(BankAccountDto record);

    int updateByPrimaryKeySelective(BankAccountDto record);

    int updateByPrimaryKey(BankAccountDto record);

    /**
     * @Description: 添加收款账户持久层
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 9:40
     */
    int insertBankAccount(BankAccountDto bankAccountDto);

    /**
     * @Description:依据各种条件查询银行账号
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 10:2
     */
    List<BankAccountVo> selectAccount(BankAccountDto bankAccountDto);

    /**
     * @Description: 删除的收款账户列表
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 11:30
     */
    void deleteBankAccount(BankAccountDto bankAccountDto);

    /**
     * 修改收款账户
     * @param bankAccountDto
     * @return
     */
    int updateBankAccount(BankAccountDto bankAccountDto);
}