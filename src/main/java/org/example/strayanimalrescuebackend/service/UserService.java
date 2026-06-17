package org.example.strayanimalrescuebackend.service;

import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.ShelterMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.Shelter;
import org.example.strayanimalrescuebackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ShelterMapper shelterMapper;

    public UserService(ShelterMapper shelterMapper) {
        this.shelterMapper = shelterMapper;
    }

    /**
     * 用户注册
     */
    public String registerUser(User user) {
        // 检查用户名或邮箱是否已存在
        if (userMapper.checkUserExists(user.getUsername(), user.getEmail()) > 0) {
            return "用户名或邮箱已存在";
        }

        // 加密存储密码
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        // 插入数据库
        int result = userMapper.registerUser(user);
        return result > 0 ? "注册成功" : "注册失败";
    }

    /**
     * 用户登录，返回 JWT Token
     */
    public Map<String, Object> loginUser(String username, String password) {
        User user = userMapper.findByUsername(username);
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("message", "用户名不存在");
            response.put("success", false);
            return response;
        }

        // 检查账号是否被冻结
        if (Boolean.TRUE.equals(user.getIsFrozen())) {
            response.put("message", "账号已被冻结，请联系管理员");
            response.put("success", false);
            return response;
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            response.put("message", "密码错误");
            response.put("success", false);
            return response;
        }

        // 生成 JWT Token
        String token = JwtUtil.generateToken(username);

        // 构造返回数据
        response.put("message", "登录成功");
        response.put("success", true);
        response.put("token", token);
        response.put("user", user);

        // 根据用户类型添加额外信息
        String role = user.getRole();
        if ("SHELTER".equalsIgnoreCase(role)) {
            // 查询 shelter_id
            Shelter shelterId = shelterMapper.findShelterByUserId(user.getUserId());
            response.put("shelterId", shelterId);
        }

        return response;
    }


    /**
     * 更新用户个人信息
     */
    public Map<String, Object> updateUserProfile(String username, User updatedUser) {
        // 查询用户是否存在
        User existingUser = userMapper.findByUsername(username);
        if (existingUser == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);  // 确保包含 success 字段
            response.put("message", "用户不存在");
            return response;  // 返回错误信息
        }

        // 检查邮箱是否已被其他用户使用（排除当前用户）
        int emailExists = userMapper.checkEmailExists(updatedUser.getEmail(), username);
        if (emailExists > 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);  // 确保包含 success 字段
            response.put("message", "该邮箱已被其他用户占用");
            return response;  // 返回错误信息
        }

        // 只能修改部分字段
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAvatarUrl(updatedUser.getAvatarUrl());

        // 更新数据库
        int result = userMapper.updateUserProfile(existingUser);
        Map<String, Object> response = new HashMap<>();
        if (result > 0) {
            response.put("success", true);  // 标记操作成功
            response.put("message", "个人信息更新成功");
            response.put("user", existingUser);
        } else {
            response.put("success", false);  // 标记操作失败
            response.put("message", "个人信息更新失败");
        }
        return response;
    }


    /**
     * 修改密码
     */
    public Map<String, Object> updatePassword(String username, String currentPassword, String newPassword) {
        Map<String, Object> response = new HashMap<>();

        // 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            response.put("message", "用户不存在");
            response.put("success", false);
            return response;
        }

        // 校验当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            response.put("message", "密码错误");
            response.put("success", false);
            return response;
        }

        // 检查新密码是否与原密码一致
        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            response.put("message", "新密码不能与原密码相同");
            response.put("success", false);
            return response;
        }

        // 加密新密码
        String newPasswordHash = passwordEncoder.encode(newPassword);

        // 更新密码
        int result = userMapper.updatePassword(username, newPasswordHash);
        if (result > 0) {
            response.put("message", "密码修改成功");
            response.put("success", true);
        } else {
            response.put("message", "密码修改失败");
            response.put("success", false);
        }

        return response;
    }

    /**
     * 获取所有用户信息
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        // 调用 UserMapper 查询所有用户信息
        return userMapper.findAllUsers();
    }

    /**
     * 根据用户 ID 查询用户信息
     */
    public User getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    /**
     * 冻结用户账号
     */
    public String freezeUser(Long userId) {
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return "用户不存在";
        }

        if (Boolean.TRUE.equals(user.getIsFrozen())) {
            return "用户已经被冻结";
        }

        // 设置用户为冻结状态
        userMapper.updateUserFrozenStatus(userId, true);
        return "用户已成功冻结";
    }

    /**
     * 解冻用户账号
     */
    public String unfreezeUser(Long userId) {
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return "用户不存在";
        }

        if (Boolean.FALSE.equals(user.getIsFrozen())) {
            return "用户当前未被冻结";
        }

        // 解除用户冻结状态
        userMapper.updateUserFrozenStatus(userId, false);
        return "用户已成功解冻";
    }
}
