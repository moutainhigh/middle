package com.njwd.platform.controller;

import com.njwd.entity.platform.dto.BankAccountDto;
import com.njwd.entity.platform.vo.BankAccountVo;
import com.njwd.entity.platform.vo.UserVO;
import com.njwd.platform.service.BankAccountService;
import com.njwd.platform.service.UserService;
import com.njwd.support.BaseController;
import com.njwd.support.Result;
import com.njwd.utils.FastUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description 收款账户管理
 * @Date 2020/3/24 17:43
 * @Author 胡翔鸿
 */
@Api(value = "BankAccountController",tags = "收款账户管理接口")
@RestController
@RequestMapping("bankAccount")
public class BankAccountController extends BaseController {

    @Resource
    UserService userService;
    @Resource
    BankAccountService bankAccountService;
    /**
     * @Description:添加收款账户接口
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/24 18:04
     */
    @ApiOperation(value = "添加收款账户接口",notes = "添加收款账户")
    @PostMapping("/doAddBankAccount")
    public Result doAddBankAccount(HttpServletRequest request , @RequestBody BankAccountDto bankAccountDto){
        //进行非空校验
        FastUtils.checkParams(bankAccountDto,bankAccountDto.getAccountType());
        if(bankAccountDto.getAccountType()==0){
            FastUtils.checkParams(bankAccountDto,bankAccountDto.getAccountType(),bankAccountDto.getAccountName(),bankAccountDto.getBankName(),
                    bankAccountDto.getAccountNumber(),bankAccountDto.getLinkman(),bankAccountDto.getMobile(),bankAccountDto.getVerificationCode());
        }else {
            FastUtils.checkParams(bankAccountDto,bankAccountDto.getAccountType(),bankAccountDto.getAccountName(),bankAccountDto.getBankName(),
                    bankAccountDto.getAccountNumber(),bankAccountDto.getMobile(),bankAccountDto.getVerificationCode());
        }
        //先获取登录者信息
        UserVO userVO = userService.getCurrLoginUser(request);
        //将userId放入业务入参
        bankAccountDto.setUserId(userVO.getUserId());
        bankAccountService.doAddBankAccount(bankAccountDto);
        return ok();
    }

    /**
     * @Description:修改收款账户接口
     * @Param: BankAccountDto
     * @return: Result
     * @Author: huxianghong
     * @Date: 2020/4/14 19:04
     */
    @ApiOperation(value = "修改收款账户接口",notes = "修改收款账户接口")
    @PostMapping("/doUpdateBankAccount")
    public Result doUpdateBankAccount(HttpServletRequest request , @RequestBody BankAccountDto bankAccountDto){
        //进行非空校验
        FastUtils.checkParams(bankAccountDto,bankAccountDto.getAccountType(),bankAccountDto.getBankAccountId());
        if(bankAccountDto.getAccountType()==0){
            FastUtils.checkParams(bankAccountDto,bankAccountDto.getAccountType(),bankAccountDto.getAccountName(),bankAccountDto.getBankName(),
                    bankAccountDto.getAccountNumber(),bankAccountDto.getLinkman(),bankAccountDto.getMobile(),bankAccountDto.getVerificationCode());
        }else {
            FastUtils.checkParams(bankAccountDto,bankAccountDto.getAccountType(),bankAccountDto.getAccountName(),bankAccountDto.getBankName(),
                    bankAccountDto.getAccountNumber(),bankAccountDto.getMobile(),bankAccountDto.getVerificationCode());
        }
        //先获取登录者信息
        UserVO userVO = userService.getCurrLoginUser(request);
        bankAccountService.doUpdateBankAccount(bankAccountDto);
        return ok();
    }

    /**
     * @Description:删除收款账户接口
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/24 18:04
     */
    @ApiOperation(value = "删除收款账户接口",notes = "删除收款账户")
    @PostMapping("/deleteBankAccount")
    public Result deleteBankAccount(HttpServletRequest request , @RequestBody BankAccountDto bankAccountDto){
        //进行非空校验
        FastUtils.checkParams(bankAccountDto,bankAccountDto.getBankAccountId());
        //先获取登录者信息
        UserVO userVO = userService.getCurrLoginUser(request);
        bankAccountService.deleteBankAccount(bankAccountDto);
        return ok();
    }

    /**
     * @Description:查询登录用户的收款账户列表
     * @Param:
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/25 11:04
     */
    @ApiOperation(value = "查询登录用户的收款账户列表",notes = "查询登录用户的收款账户列表")
    @PostMapping("/findBankAccountList")
    public Result<List<BankAccountVo>> findBankAccountList(HttpServletRequest request){
        //先获取登录者信息
        UserVO userVO = userService.getCurrLoginUser(request);
        //将userId放入业务入参
        BankAccountDto bankAccountDto = new BankAccountDto();
        bankAccountDto.setUserId(userVO.getUserId());
        List<BankAccountVo> bankAccountList = bankAccountService.findBankAccountList(bankAccountDto);
        return ok(bankAccountList);
    }
}
