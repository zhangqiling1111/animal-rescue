package org.example.strayanimalrescuebackend.service;

import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.ShelterApplicationMapper;
import org.example.strayanimalrescuebackend.mapper.ShelterMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.ShelterApplication;
import org.example.strayanimalrescuebackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.example.strayanimalrescuebackend.model.ResponseResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShelterApplicationService {

    @Autowired
    private ShelterApplicationMapper shelterApplicationMapper;

    @Autowired
    private ShelterMapper shelterMapper;  // 用于新增救助站

    @Autowired
    private UserMapper userMapper;

    // 获取所有救助站申请
    public List<ShelterApplication> getAllApplications() {
        return shelterApplicationMapper.getAllApplications();
    }


    // 拒绝申请，更新状态为已拒绝
    public boolean rejectApplication(int applicationId) {
        // 获取该申请
        ShelterApplication application = shelterApplicationMapper.getApplicationById(applicationId);

        if (application == null || "已拒绝".equals(application.getStatus())) {
            return false;  // 如果申请不存在或已经拒绝
        }

        // 更新申请状态为已拒绝
        shelterApplicationMapper.updateApplicationStatus(applicationId, "已拒绝");

        return true;
    }

    /**
     * 取消救助站申请
     * @param applicationId 申请ID
     * @param userId 用户ID
     * @return 是否成功取消
     */
    public boolean cancelApplication(int applicationId, int userId) {
        // 查询申请信息
        var application = shelterApplicationMapper.getApplicationById(applicationId);

        // 确保申请存在且属于该用户，并且状态未被处理
        if (application == null || application.getUserId() != userId || !"待审核".equals(application.getStatus())) {
            return false;
        }

        // 删除该申请
        shelterApplicationMapper.deleteApplication(applicationId);
        return true;
    }

    /**
     * 救助站申请
     */
    public ResponseResult applyForShelter(String username, String shelterName, String address, String contactInfo) {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return new ResponseResult(400, "用户不存在", null, 0L);
        }

        int userId = user.getUserId();

        // 检查是否已经是救助站
        if ("shelter".equalsIgnoreCase(user.getRole())) {
            return new ResponseResult(400, "您已是救助站用户，无需重复申请", null, 0L);
        }

        // 检查是否已申请
        int count = shelterMapper.checkApplicationExists(userId);
        if (count > 0) {
            return new ResponseResult(400, "该用户已提交申请，正在审核中", null, 0L);
        }

        // 构造申请
        ShelterApplication application = new ShelterApplication();
        application.setUserId(userId);
        application.setShelterName(shelterName);
        application.setAddress(address);
        application.setContactInfo(contactInfo);
        application.setStatus("待审核");

        int result = shelterApplicationMapper.insertShelterApplication(application);
        if (result > 0) {
            ShelterApplication savedApplication = shelterApplicationMapper.getApplicationById(application.getApplicationId());
            return new ResponseResult(200, "申请提交成功，请等待管理员审核", savedApplication, 1L);
        } else {
            return new ResponseResult(400, "申请提交失败", null, 0L);
        }
    }

}
