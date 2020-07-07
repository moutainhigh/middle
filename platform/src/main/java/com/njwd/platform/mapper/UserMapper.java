package com.njwd.platform.mapper;

import com.njwd.entity.platform.dto.AddUserDto;
import com.njwd.entity.platform.dto.LoginDto;
import com.njwd.entity.platform.dto.UserDto;
import com.njwd.entity.platform.vo.UserVO;

public interface UserMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(AddUserDto addUserDto);

    /**
     * @Description: 注册成功时添加用户信息
     * @Param: AddUserDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/23 17:33
     */
    int insertAddUser(AddUserDto addUserDto);

    UserDto selectByPrimaryKey(Long userId);

    /**
     * 修改用户信息
     * @param userDto
     * @return
     */
    int updateUser(UserDto userDto);

    int updateByPrimaryKey(UserDto record);

    /**
     * @Description: 利用账号（一般为手机号）查询用户
     * @Param: LoginDto
     * @return: UserVO
     * @Author: huxianghong
     * @Date: 2020/3/24 10:06
     */
    UserVO selectUserByMobile(LoginDto loginDto);

    /**
     * @Description: 利用用户id查询用户
     * @Param: LoginDto
     * @return: UserVO
     * @Author: huxianghong
     * @Date: 2020/3/24 10:06
     */
    UserVO selectUserById(UserDto userDto);

    /**
     * @Description: 依据各种条件查询用户
     * @Param: LoginDto
     * @return: UserVO
     * @Author: huxianghong
     * @Date: 2020/3/24 10:06
     */
    UserVO selectUserByCondition(UserDto userDto);
}