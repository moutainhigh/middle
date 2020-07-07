package com.njwd.platform.service.impl;

import com.njwd.entity.platform.dto.BankAccountDto;
import com.njwd.entity.platform.vo.BankAccountVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.mapper.BankAccountMapper;
import com.njwd.platform.service.BankAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Description  收款账户管理业务
 * @Date 2020/3/24 17:13
 * @Author 胡翔鸿
 */
@Service
public class BankAccountServiceImpl implements BankAccountService {

    @Resource
    BankAccountMapper bankAccountMapper;
    /**
     * @Description: 添加收款账户业务
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 9:30
     */
    @Override
    @Transactional
    public void doAddBankAccount(BankAccountDto bankAccountDto) {
        //判断验证码是否正确
        if(!("575757".equals(bankAccountDto.getVerificationCode()))){
            throw new ServiceException(ResultCode.VERFICATION_CODE_ERROR);
        }else{
            //依据银行账号查看该银行账号是否已经存在
            List<BankAccountVo> bankAccountList = bankAccountMapper.selectAccount(bankAccountDto);
            if(bankAccountList!=null&&bankAccountList.size()>0){
                throw new ServiceException(ResultCode.BANK_ACCOUNT_USED);
            }else {
                //插入账户数据
                bankAccountDto.setCreateTime(new Date());
                bankAccountDto.setUpdateTime(new Date());
                if(bankAccountDto.getAccountType()==1){
                    //个人账户的联系人就是开户名，将其置为开户名
                    bankAccountDto.setLinkman(bankAccountDto.getAccountName());
                }
                int insertBankAccount = bankAccountMapper.insertBankAccount(bankAccountDto);
            }
        }
    }

    /**
     * @Description: 查询登录用户的收款账户列表
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 11:30
     */
    @Override
    public List<BankAccountVo> findBankAccountList(BankAccountDto bankAccountDto) {
        List<BankAccountVo> bankAccountList = bankAccountMapper.selectAccount(bankAccountDto);
        return bankAccountList;
    }

    /**
     * @Description: 删除的收款账户列表
     * @Param: BankAccountDto
     * @return: void
     * @Author: huxianghong
     * @Date: 2020/3/25 11:30
     */
    @Override
    public void deleteBankAccount(BankAccountDto bankAccountDto) {
       bankAccountMapper.deleteBankAccount(bankAccountDto);
    }

    /**
     * @Description:修改收款账户接口
     * @Param: BankAccountDto
     * @return: Result
     * @Author: huxianghong
     * @Date: 2020/4/14 19:04
     */
    @Override
    public void doUpdateBankAccount(BankAccountDto bankAccountDto) {
        //依据银行账号查看该银行账号是否已经存在
        List<BankAccountVo> bankAccountList = bankAccountMapper.selectAccount(bankAccountDto);
        if(bankAccountList!=null&&bankAccountList.size()>0){
            throw new ServiceException(ResultCode.BANK_ACCOUNT_USED);
        }else {
            //修改账户数据
            bankAccountDto.setUpdateTime(new Date());
            if(bankAccountDto.getAccountType()==1){
                //个人账户的联系人就是开户名，将其置为开户名
                bankAccountDto.setLinkman(bankAccountDto.getAccountName());
            }
            int updateBankAccount = bankAccountMapper.updateBankAccount(bankAccountDto);
        }
    }
}
