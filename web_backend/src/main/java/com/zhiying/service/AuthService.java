package com.zhiying.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhiying.common.exception.BusinessException;
import com.zhiying.entity.UserEntity;
import com.zhiying.mapper.UserMapper;
import com.zhiying.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final String DEFAULT_AVATAR = null; // 前端展示时用默认图
    private static final String[] NICKNAME_PREFIX = {"小", "阿", "萌", "星", "云", "风", "叶", "雨", "月", "花"};
    private static final String[] NICKNAME_SUFFIX = {"叶子", "星星", "微风", "小鹿", "兔子", "猫咪", "晴天", "彩虹", "薄荷", "柠檬"};
    private static final Random RANDOM = new Random();

    public Map<String, Object> register(String email, String password) {
        if (userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getEmail, email)) != null) {
            throw new BusinessException("邮箱已被注册");
        }
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        user.setAvatar(DEFAULT_AVATAR);
        user.setNickname(generateRandomNickname());
        userMapper.insert(user);
        return buildAuthResponse(user);
    }

    private String generateRandomNickname() {
        String p = NICKNAME_PREFIX[RANDOM.nextInt(NICKNAME_PREFIX.length)];
        String s = NICKNAME_SUFFIX[RANDOM.nextInt(NICKNAME_SUFFIX.length)];
        return p + s + RANDOM.nextInt(999);
    }

    public void updateProfile(Long userId, String nickname, String avatar) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("用户不存在");
        if (nickname != null && !nickname.isBlank()) user.setNickname(nickname.trim());
        if (avatar != null) user.setAvatar(avatar.trim().isEmpty() ? null : avatar.trim());
        userMapper.updateById(user);
    }

    public Map<String, Object> login(String email, String password) {
        UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getEmail, email));
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("邮箱或密码错误");
        }
        return buildAuthResponse(user);
    }

    public Map<String, Object> getCurrentUser(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) return null;
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("email", user.getEmail());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("role", user.getRole());
        return data;
    }

    private Map<String, Object> buildAuthResponse(UserEntity user) {
        String token = jwtUtils.generateToken(user.getId(), user.getEmail(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", user.getId());
        data.put("email", user.getEmail());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("role", user.getRole());
        return data;
    }
}
