package org.example.strayanimalrescuebackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.strayanimalrescuebackend.model.PasswordUpdateRequest;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.User;
import org.example.strayanimalrescuebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.example.strayanimalrescuebackend.Util.JwtUtil;

import java.util.List;
import java.util.Map;

/**
 * user - 用户相关的 HTTP 请求
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user) {
        String result = userService.registerUser(user);
        if ("注册成功".equals(result)) {
            return new ResponseResult(200, result, null, 0L);
        } else {
            return new ResponseResult(400, result, null, 0L);
        }
    }

    /**
     * 用户登录，返回 Token
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> result = userService.loginUser(loginRequest.get("username"), loginRequest.get("password"));
        boolean success = (boolean) result.get("success");
        String message = (String) result.get("message");

        if (success) {
            return new ResponseResult(200, message, result, 1L);
        } else {
            return new ResponseResult(400, message, null, 0L);
        }
    }

    /**
     * 修改用户个人信息
     */
    @PutMapping("/profile")
    public ResponseResult updateProfile(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            return new ResponseResult(401, "未提供有效的 Token", null, 0L);
        }

        try {
            // 解析 token 获取用户名
            String username = JwtUtil.extractUsername(token);
            Map<String, Object> result = userService.updateUserProfile(username, user);

            // 检查 result 是否包含 "success" 和 "message" 字段，防止 NullPointerException
            if (result == null || !result.containsKey("success") || !result.containsKey("message")) {
                return new ResponseResult(500, "服务端错误：返回结果不完整", null, 0L);
            }

            // 获取 success 和 message
            boolean success = (Boolean) result.get("success");
            String message = (String) result.get("message");

            if (success) {
                return new ResponseResult(200, message, result.get("user"), 1L);
            } else {
                return new ResponseResult(400, message, null, 0L);
            }
        } catch (Exception e) {
            return new ResponseResult(500, "Token 解析失败: " + e.getMessage(), null, 0L);
        }
    }

    /**
     * 修改密码接口
     */
    @PutMapping("/password")
    public ResponseResult updatePassword(@RequestHeader("token") String token,
                                         @RequestBody PasswordUpdateRequest request) {
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return new ResponseResult(401, "无效的Token", null, 0L);
        }

        Map<String, Object> result = userService.updatePassword(username, request.getCurrentPassword(), request.getNewPassword());
        boolean success = (boolean) result.get("success");
        String message = (String) result.get("message");

        if (success) {
            return new ResponseResult(200, message, null, 0L);
        } else {
            return new ResponseResult(400, message, null, 0L);
        }
    }

    /**
     * 查看所有用户信息
     */
    @GetMapping("/getUsersInfo")
    public ResponseResult getUsersInfo() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseResult(200, "用户信息获取成功", users, (long) users.size());
        } catch (Exception e) {
            return new ResponseResult(500, "获取用户信息失败: " + e.getMessage(), null, 0L);
        }
    }

    /**
     * 根据用户 ID 查询用户信息
     */
    @GetMapping("/getUserById/{id}")
    public ResponseResult getUserById(@PathVariable("id") Long id) {
        try {
            User user = userService.getUserById(id);
            if (user != null) {
                return new ResponseResult(200, "查询成功", user, 1L);
            } else {
                return new ResponseResult(404, "用户不存在", null, 0L);
            }
        } catch (Exception e) {
            return new ResponseResult(500, "查询失败: " + e.getMessage(), null, 0L);
        }
    }
}
