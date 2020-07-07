package com.njwd.platform.service.impl;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.njwd.common.PlatformContant;
import com.njwd.entity.platform.dto.*;
import com.njwd.entity.platform.vo.AddMemberVo;
import com.njwd.entity.platform.vo.SimpleReturnVo;
import com.njwd.entity.platform.vo.UserVO;
import com.njwd.entity.platform.vo.VerificationReturnVo;
import com.njwd.exception.ResultCode;
import com.njwd.exception.ServiceException;
import com.njwd.platform.mapper.UserMapper;
import com.njwd.platform.mapper.UserRoleMapper;
import com.njwd.platform.service.UserService;
import com.njwd.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;
    @Resource
    public RedisUtils redisUtils;
    @Value("${njwdmss.crm_server}")
    private String crmServer;
    @Value("${njwdmss.clm_server}")
    private String clmServer;
    @Value("${njwdmss.url.add_member}")
    private String addMember;
    @Value("${njwdmss.url.do_deal_update_member}")
    private String doDealUpdateMember;
    @Value("${njwdmss.url.send_verification_code}")
    private String sendVerificationCode;
    /**
     * @Description:
     * @Param: AddUserDto
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/23 17:02
     */
    @Override
    @Transactional
    public void addUser(AddUserDto addUserDto) {
        //空位1、调用第三方接口查看验证码是否正确
        Boolean checkVerificationCode = false;
        checkVerificationCode = this.checkVerificationCode(addUserDto.getMobile(),addUserDto.getVerificationCode());
//        if(!("575757".equals(addUserDto.getVerificationCode()))){
        if(!(checkVerificationCode)){
           //验证码不正确抛不正确的异常
            throw new ServiceException(ResultCode.VERFICATION_CODE_ERROR);
        }else {
            //依据账号查询用户是否已经存在
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(addUserDto,userDto);
            UserVO userVO = userMapper.selectUserByCondition(userDto);
            if(userVO!=null){
                throw new ServiceException(ResultCode.MOBILE_USED);
            }
            //调用CRM接口将新添加的用户注册成会员(此处模拟)
            AddMemberDto addMemberDto = new AddMemberDto();
            addMemberDto.setEnterpriseId(PlatformContant.FixedParameter.ROOT_ORG_ID);
            addMemberDto.setEnterpriseName(PlatformContant.ReturnString.ENTERPRISE_NAME);
            addMemberDto.setPassWord(addUserDto.getPassword());
            addMemberDto.setTeleNo(addUserDto.getMobile());
            addMemberDto.setCompany(addUserDto.getCompanyName());
            String jsonString = JSON.toJSONString(addMemberDto);
            String addMemberVoString = HttpUtils.doRequest(crmServer,addMember,PlatformContant.ReturnString.CRM_URL_PAT+jsonString);
            if(StringUtil.isEmpty(addMemberVoString)){
                throw new ServiceException(ResultCode.CRM_CARD_FAIL);
            }
            AddMemberVo addMemberVo = JSON.parseObject(addMemberVoString,AddMemberVo.class);
            if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(addMemberVo.getStatus()))){
                throw new ServiceException(ResultCode.CRM_CARD_FAIL);
            }
            addUserDto.setCrmUserId(addMemberVo.getCardId());
            addUserDto.setCrmUserName(addUserDto.getCompanyName());
            //空位3、调用接口将密码进行加密
            AES aes = SecureUtil.aes(PlatformContant.Login.PWD);
            String password = aes.encryptBase64(addUserDto.getPassword());
            addUserDto.setPassword(password);
            //生成一个ID字符串
            String userId = this.getCodeKey("");
            //插入用户数据
            addUserDto.setUserId(userId);
            addUserDto.setCreateTime(new Date());
            addUserDto.setUpdateTime(new Date());
            addUserDto.setCrmUserName(userDto.getCompanyName());
            Integer addUser = userMapper.insertAddUser(addUserDto);
            //插入用户和角色的关联数据
            if(addUserDto.getUserRoleDtoList()!=null&&addUserDto.getUserRoleDtoList().size()>0){
                for(UserRoleDto userRoleDto:addUserDto.getUserRoleDtoList()){
                    userRoleDto.setUserId(addUserDto.getUserId());
                }
                Integer insertUserRole = userRoleMapper.insertList(addUserDto.getUserRoleDtoList());
            }
        }
    }

    /**
     * @Description:
     * @Param: LoginDto
     * @return: UserVO
     * @Author: huxianghong
     * @Date: 2020/3/24 9:22
     */
    @Override
    public UserVO doLoginUser(LoginDto loginDto) {
        UserVO returnUserVO = new UserVO();
        returnUserVO = userMapper.selectUserByMobile(loginDto);
        if(returnUserVO==null){
            //查不到用户，抛用户不存在的异常
            throw new ServiceException(ResultCode.NO_USER);
        }
        if(1==loginDto.getLoginType()){
            //将传入的密码加密成加密字符串
            AES aes = SecureUtil.aes(PlatformContant.Login.PWD);
            String password = aes.encryptBase64(loginDto.getPassword());
            if(returnUserVO.getPassword().equals(password)){
                //密码不能传到前端，所以将其置空
                returnUserVO.setPassword(null);
                dealRedis(returnUserVO);
                return returnUserVO;
            }else {
                //抛密码不正确的异常
                throw new ServiceException(ResultCode.PASSWORD_ERROR);
            }
           //账号密码登录业务
        }else if(2==loginDto.getLoginType()){
           //验证码登录时的业务
            Boolean checkVerificationCode = this.checkVerificationCode(loginDto.getMobile(),loginDto.getVerificationCode());
           if(StringUtils.isEmpty(loginDto.getVerificationCode())||!(checkVerificationCode)){
                //手动抛异常验证码错误
               throw new ServiceException(ResultCode.VERFICATION_CODE_ERROR);
           }else {
               //密码不能传到前端，所以将其置空
               returnUserVO.setPassword(null);
               dealRedis(returnUserVO);
               return returnUserVO;
           }
        }
        return returnUserVO;
    }

    /**
     * @Description: 将登录人的信息处理放入redis
     * @Param: UserVO
     * @return:
     * @Author: huxianghong
     * @Date: 2020/3/24 10:34
     */
    @Override
    public void dealRedis(UserVO userVO) {
        //生成token字符串
        String token = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
        userVO.setToken(token);
        //将登录信息放置到redis
        String loginUserJson = JSON.toJSONString(userVO);
        redisUtils.setH(token, PlatformContant.Login.LOGIN_USER,loginUserJson,(PlatformContant.Login.LOGIN_TIME).longValue());
    }

    /**
     * 获取登录者信息
     * @param request
     * @return
     */
    @Override
    public UserVO getCurrLoginUser(HttpServletRequest request) {
        UserVO userVO = new UserVO();
        String token = request.getHeader(PlatformContant.Login.AUTH_TOKEN_KEY);
        if(StringUtils.isEmpty(token)){
            throw new ServiceException(ResultCode.LOGIN_TIME_OUT);
        }else {
            if(redisUtils.getH(token,PlatformContant.Login.LOGIN_USER)==null){
                throw new ServiceException(ResultCode.LOGIN_TIME_OUT);
            }else {
                String loginUser = redisUtils.getH(token,PlatformContant.Login.LOGIN_USER).toString();
                redisUtils.expire(token,PlatformContant.Login.LOGIN_TIME, TimeUnit.SECONDS);
                userVO = JSON.parseObject(loginUser,UserVO.class);
            }
        }
        //密码不能传到前端，将其置空
        userVO.setPassword(null);
        return userVO;
    }

    /**
     * 字符串生成规则
     * @param key
     * @return
     */
    @Override
    public String getCodeKey(String key){
        String prefix = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        if(!(StringUtil.isEmpty(key))){
            return key + prefix + ((int) (90000 * Math.random()) + 10000);
        }else {
            return prefix + ((int) (90000 * Math.random()) + 10000);
        }

    }

    /**
     * @Description 安全设置页面信息查询（即为查询当前登录人信息）
     * @Author 胡翔鸿
     * @Data 2020/3/24 15:09
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @Override
    public UserVO findSecuritySetting(HttpServletRequest request) {
        UserVO userVO = new UserVO();
        String token = request.getHeader(PlatformContant.Login.AUTH_TOKEN_KEY);
        if(StringUtils.isEmpty(token)){
            throw new ServiceException(ResultCode.LOGIN_TIME_OUT);
        }else {
            if(redisUtils.getH(token,PlatformContant.Login.LOGIN_USER)==null){
                throw new ServiceException(ResultCode.LOGIN_TIME_OUT);
            }else {
                String loginUser = redisUtils.getH(token,PlatformContant.Login.LOGIN_USER).toString();
                redisUtils.expire(token,PlatformContant.Login.LOGIN_TIME, TimeUnit.SECONDS);
                userVO = JSON.parseObject(loginUser,UserVO.class);
            }
        }
        UserDto userDto = new UserDto();
        userDto.setUserId(userVO.getUserId());
        UserVO returnUserVo = new UserVO();
        //依据userId查询该用户信息
        returnUserVo = userMapper.selectUserById(userDto);
        String create_time = DateUtils.format(returnUserVo.getCreateTime(),"yyyyMMdd HH:mm:ss");
        //System.out.println("create_time是："+create_time);
        //计算得到账号的安全级别（依据密码复杂度分析）
        AES aes = SecureUtil.aes(PlatformContant.Login.PWD);
        String password = aes.decryptStr(returnUserVo.getPassword());
        //System.out.println("解密后密码是："+password);
        Integer securityLevel = this.findSecurityLevel(password);
        returnUserVo.setSecurityLevel(securityLevel);
        //密码不能传到前端，将其置空
        returnUserVo.setPassword(null);
        return returnUserVo;
    }

    /**
     * @Description 抽取方法，计算得到安全级别
     * @Author
     * @Data 2020/3/24 15:56
     * @Param String
     * @return Integer
     */
    @Override
    public Integer findSecurityLevel(String password) {
        Integer securityLevel = 1;
        if(password.length()>=8){
            Integer level = 0;
            if(password.matches(PlatformContant.RegularString.CONTAIN_NUMBER)){
                //判断是否有数字
                level++;
            }
            if(password.matches(PlatformContant.RegularString.CONTAIN_CAPITAL)){
                //判断是否有大写字母
                level++;
            }
            if(password.matches(PlatformContant.RegularString.CONTAIN_LOWERCASE)){
                //判断是否有小写字母
                level++;
            }
            if(!(password.matches(PlatformContant.RegularString.LETTER_DIGIT_REGEX))){
                //判断是否出了字母和数字以外还有其他符号
                level++;
            }
            securityLevel = level;
        }
        return securityLevel;
    }

    /**
     * @Description 修改密码业务
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @Override
    @Transactional
    public void doUpdatePassword(UserDto userDto) {
        //将明码进行加密
        AES aes = SecureUtil.aes(PlatformContant.Login.PWD);
        String base64Password = aes.encryptBase64(userDto.getPassword());
        userDto.setPassword(base64Password);
        //进行修改数据库
        int updatePassword = userMapper.updateUser(userDto);
    }

    /**
     * @Description 修改和设置邮箱接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @Override
    @Transactional
    public void doUpdateEmail(UserDto userDto) {
        //建立请求用于请求外部接口的DTO
      DealUpdateMemberDto dealUpdateMemberDto = new DealUpdateMemberDto();
      dealUpdateMemberDto.setCardId(Long.parseLong(userDto.getCrmUserId()));
      dealUpdateMemberDto.setMail(userDto.getEmail());
      dealUpdateMemberDto.setTeleNo(userDto.getMobile());
      dealUpdateMemberDto.setCompany(userDto.getCompanyName());
      String jsonString = JSON.toJSONString(dealUpdateMemberDto);
      System.out.println("形成的JSON字符串是："+jsonString);
      String dealUpdateMember = HttpUtils.doRequest(crmServer,doDealUpdateMember,PlatformContant.ReturnString.CRM_URL_PAT+jsonString);
      System.out.println("返回的的JSON字符串是："+dealUpdateMember);
      if(StringUtil.isEmpty(dealUpdateMember)){
          throw new ServiceException(ResultCode.CRM_UPDATE_EMAIL_FAIL);//会员系统修改失败
      }
      SimpleReturnVo simpleReturnVo = JSON.parseObject(dealUpdateMember,SimpleReturnVo.class);
      if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
          throw new ServiceException(ResultCode.CRM_UPDATE_EMAIL_FAIL);//会员系统修改失败
      }
      //会员系统通过后修改本系统的邮箱
      userDto.setCrmUserId(null);
      userDto.setMobile(null);
      userDto.setCompanyName(null);
      int updateEmail = userMapper.updateUser(userDto);
    }

    /**
     * @Description 修改和手机号接口
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return com.njwd.support.Result
     */
    @Override
    @Transactional
    public void doUpdateMobile(UserDto userDto, UpdateMobileDto updateMobileDto) {
        //检验验证码输入是否正确
//        if(!("575757".equals(updateMobileDto.getVerificationCode()))){
        Boolean checkVerificationCode = false;
        checkVerificationCode = this.checkVerificationCode(updateMobileDto.getOldMobile(),updateMobileDto.getVerificationCode());
        if(!checkVerificationCode){
            throw new ServiceException(ResultCode.VERFICATION_CODE_ERROR);
        }else {
            //依据账号查询用户是否已经存在(useDto中的手机号为新手机号)
            UserVO userVO = userMapper.selectUserByCondition(userDto);
            if(userVO!=null){
                throw new ServiceException(ResultCode.MOBILE_USED_OR_NOT_CHANGE);
            }else {
                //设置请求参数并发送CRM请求
                DealUpdateMemberDto dealUpdateMemberDto = new DealUpdateMemberDto();
                dealUpdateMemberDto.setTeleNo(userDto.getMobile());
                dealUpdateMemberDto.setCardId(Long.parseLong(userDto.getCrmUserId()));
                dealUpdateMemberDto.setMail(userDto.getEmail());
                dealUpdateMemberDto.setCompany(userDto.getCompanyName());
                String jsonString = JSON.toJSONString(dealUpdateMemberDto);
                System.out.println("形成的JSON字符串是："+jsonString);
                String dealUpdateMember = HttpUtils.doRequest(crmServer,doDealUpdateMember,PlatformContant.ReturnString.CRM_URL_PAT+jsonString);
                System.out.println("返回的的JSON字符串是："+dealUpdateMember);
                if(StringUtil.isEmpty(dealUpdateMember)){
                    throw new ServiceException(ResultCode.CRM_UPDATE_MOBILE_FAIL);//会员系统修改失败
                }
                SimpleReturnVo simpleReturnVo = JSON.parseObject(dealUpdateMember,SimpleReturnVo.class);
                if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
                    throw new ServiceException(ResultCode.CRM_UPDATE_MOBILE_FAIL);//会员系统修改失败
                }
                //会员系统通过后修改本系统手机号码
                userDto.setCrmUserId(null);//这里该字段不能被修改，所以置空
                userDto.setEmail(null);
                userDto.setCompanyName(null);
                int updateEmail = userMapper.updateUser(userDto);
            }
        }

    }

    @Override
    @Transactional
    public void doUpdateCompany(UserDto userDto) {
        //建立请求用于请求外部接口的DTO
        DealUpdateMemberDto dealUpdateMemberDto = new DealUpdateMemberDto();
        dealUpdateMemberDto.setCardId(Long.parseLong(userDto.getCrmUserId()));
        dealUpdateMemberDto.setMail(userDto.getEmail());
        dealUpdateMemberDto.setTeleNo(userDto.getMobile());
        dealUpdateMemberDto.setCompany(userDto.getCompanyName());
        String jsonString = JSON.toJSONString(dealUpdateMemberDto);
        System.out.println("形成的JSON字符串是："+jsonString);
        String dealUpdateMember = HttpUtils.doRequest(crmServer,doDealUpdateMember,PlatformContant.ReturnString.CRM_URL_PAT+jsonString);
        System.out.println("返回的的JSON字符串是："+dealUpdateMember);
        if(StringUtil.isEmpty(dealUpdateMember)){
            throw new ServiceException(ResultCode.CRM_UPDATE_EMAIL_FAIL);//会员系统修改失败
        }
        SimpleReturnVo simpleReturnVo = JSON.parseObject(dealUpdateMember,SimpleReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(simpleReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.CRM_UPDATE_EMAIL_FAIL);//会员系统修改失败
        }
        //会员系统通过后修改本系统公司名字
        userDto.setCrmUserId(null);//这里该字段不能被修改，所以置空
        userDto.setEmail(null);
        userDto.setMobile(null);
        userDto.setCrmUserName(userDto.getCompanyName());
        int updateEmail = userMapper.updateUser(userDto);
    }

    /**
     * @Description 获取手机验证码
     * @Author 胡翔鸿
     * @Data 2020/4/9 15:01
     * @Param HttpServletRequest
     * @return
     */
    @Override
    public void findVerificationCode(MobileDto mobileDto) {
        Map<String, String> map =  JSONObject.parseObject(JSONObject.toJSONString(mobileDto), Map.class);
        String params = "";
        for(Map.Entry entry:map.entrySet()){
            params = params+"&"+entry.getKey()+"="+entry.getValue();
        }
        String url = clmServer+sendVerificationCode+params;
        logger.info("形成的URL是：{}",url);
        String returnString  = HttpUtils.restPostJson(url,"");
        logger.info("返回的验证码有关JSON字符串是：{}",returnString);
        if(StringUtil.isEmpty(returnString)){
            throw new ServiceException(ResultCode.FIND_VERFICATION_CODE_ERROR);
        }
        VerificationReturnVo verificationReturnVo = JSON.parseObject(returnString,VerificationReturnVo.class);
        if(!(PlatformContant.ReturnString.RETURN_SUCCESS.equals(verificationReturnVo.getStatus()))){
            throw new ServiceException(ResultCode.FIND_VERFICATION_CODE_ERROR);
        }
        //将返回的验证码放入redis
        redisUtils.setH(PlatformContant.Login.VERIFICATION_CODE, mobileDto.getMobile(),returnString,(PlatformContant.Login.VERIFICATION_TIME).longValue());
    }

    /**
     * 检验输入的验证码是否正确
     * @param mobile
     * @param verificationCode
     * @return
     */
    @Override
    public Boolean checkVerificationCode(String mobile,String verificationCode) {
        if(redisUtils.getH(PlatformContant.Login.VERIFICATION_CODE,mobile)==null){
            throw new ServiceException(ResultCode.VERFICATION_CODE_TIME_OUT);
        }else {
            String verificationCodeString = redisUtils.getH(PlatformContant.Login.VERIFICATION_CODE,mobile).toString();
            logger.info("redis取出的验证码信息为：{}",verificationCodeString);
            VerificationReturnVo verificationReturnVo = JSON.parseObject(verificationCodeString,VerificationReturnVo.class);
            if(verificationReturnVo.getCode().equals(verificationCode)){
                return true;
            }else {
                throw new ServiceException(ResultCode.VERFICATION_CODE_ERROR);
            }
        }

    }
}
