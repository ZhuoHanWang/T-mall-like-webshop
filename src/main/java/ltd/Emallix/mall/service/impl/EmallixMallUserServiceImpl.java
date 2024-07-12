
package ltd.Emallix.mall.service.impl;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallUserVO;
import ltd.Emallix.mall.dao.MallUserMapper;
import ltd.Emallix.mall.entity.MallUser;
import ltd.Emallix.mall.service.EmallixMallUserService;
import ltd.Emallix.mall.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
@Transactional
public class EmallixMallUserServiceImpl implements EmallixMallUserService {

    @Autowired
    private MallUserMapper mallUserMapper;

    @Override
    public PageResult getEmallixMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            EmallixMallUserVO EmallixMallUserVO = new EmallixMallUserVO();
            BeanUtil.copyProperties(user, EmallixMallUserVO);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, EmallixMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public EmallixMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        EmallixMallUserVO userTemp = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        MallUser userFromDB = mallUserMapper.selectByPrimaryKey(userTemp.getUserId());
        if (userFromDB != null) {
            if (StringUtils.hasText(mallUser.getNickName())) {
                userFromDB.setNickName(EmallixMallUtils.cleanString(mallUser.getNickName()));
            }
            if (StringUtils.hasText(mallUser.getAddress())) {
                userFromDB.setAddress(EmallixMallUtils.cleanString(mallUser.getAddress()));
            }
            if (StringUtils.hasText(mallUser.getIntroduceSign())) {
                userFromDB.setIntroduceSign(EmallixMallUtils.cleanString(mallUser.getIntroduceSign()));
            }
            if (mallUserMapper.updateByPrimaryKeySelective(userFromDB) > 0) {
                EmallixMallUserVO EmallixMallUserVO = new EmallixMallUserVO();
                BeanUtil.copyProperties(userFromDB, EmallixMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, EmallixMallUserVO);
                return EmallixMallUserVO;
            }
        }
        return null;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }
}
