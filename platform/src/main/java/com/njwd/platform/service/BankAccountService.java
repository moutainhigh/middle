package com.njwd.platform.service;

import com.njwd.entity.platform.dto.BankAccountDto;
import com.njwd.entity.platform.vo.BankAccountVo;

import java.util.List;

/**
 * @Description  收款账户管理业务
 * @Date 2020/3/24 17:12
 * @Author 胡翔鸿
 */
public interface BankAccountService {

    /**
     * @Description: 添加收款账户业务
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 9:30
     */
    void doAddBankAccount(BankAccountDto bankAccountDto);

    /**
     * @Description: 查询登录用户的收款账户列表
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 11:30
     */
    List<BankAccountVo> findBankAccountList(BankAccountDto bankAccountDto);

    /**
     * @Description: 删除的收款账户列表
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 11:30
     */
    void deleteBankAccount(BankAccountDto bankAccountDto);

    /**
     * @Description:修改收款账户接口
     * @Param: BankAccountDto
     * @return: Result
     * @Author: huxianghong
     * @Date: 2020/4/14 19:04
     */
    void doUpdateBankAccount(BankAccountDto bankAccountDto);
}
