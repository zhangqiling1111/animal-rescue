package org.example.strayanimalrescuebackend.controller;

import org.example.strayanimalrescuebackend.mapper.ShelterApplicationMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.ShelterApplication;
import org.example.strayanimalrescuebackend.service.ShelterApplicationService;
import org.example.strayanimalrescuebackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController - 管理员相关操作控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ShelterApplicationService shelterApplicationService;

    @Autowired
    private ShelterApplicationMapper shelterApplicationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    /**
     * 获取所有救助站申请
     */
    @GetMapping("/shelterApplications")
    public ResponseResult getAllApplications() {
        List<ShelterApplication> applications = shelterApplicationService.getAllApplications();
        return new ResponseResult(200, "获取成功", applications, (long) applications.size());
    }

    /**
     * 审核通过救助站申请
     */
    @PostMapping("/approveApplication/{applicationId}")
    public ResponseResult approveApplication(@PathVariable int applicationId) {
        ShelterApplication application = shelterApplicationMapper.getApplicationById(applicationId);

        if (application == null || "已通过".equals(application.getStatus())) {
            return new ResponseResult(400, "申请不存在或已经处理过", null, 0L);
        }

        userMapper.updateUserRole(application.getUserId(), "SHELTER");
        shelterApplicationMapper.updateApplicationStatus(applicationId, "已通过");

        return new ResponseResult(200, "申请已通过并成功添加救助站", null, 1L);
    }

    /**
     * 审核拒绝救助站申请
     */
    @PostMapping("/rejectApplication/{applicationId}")
    public ResponseResult rejectApplication(@PathVariable int applicationId) {
        boolean result = shelterApplicationService.rejectApplication(applicationId);

        if (result) {
            return new ResponseResult(200, "申请已拒绝", null, 1L);
        } else {
            return new ResponseResult(400, "申请不存在或已经处理过", null, 0L);
        }
    }

    /**
     * 冻结用户账号
     */
    @PostMapping("/freezeUser/{userId}")
    public ResponseResult freezeUser(@PathVariable Long userId) {
        String message = userService.freezeUser(userId);
        return new ResponseResult(200, message, null, 1L);
    }

    /**
     * 解冻用户账号
     */
    @PostMapping("/unfreezeUser/{userId}")
    public ResponseResult unfreezeUser(@PathVariable Long userId) {
        String message = userService.unfreezeUser(userId);
        return new ResponseResult(200, message, null, 1L);
    }
}
