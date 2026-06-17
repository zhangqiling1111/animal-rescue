package org.example.strayanimalrescuebackend.service;

import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.AdoptionMapper;
import org.example.strayanimalrescuebackend.mapper.AnimalMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.AdoptionApplication;
import org.example.strayanimalrescuebackend.model.Animal;
import org.example.strayanimalrescuebackend.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdoptionService {
    private final AdoptionMapper adoptionMapper;
    private final UserMapper userMapper;
    private final AnimalMapper animalMapper;

    public AdoptionService(AdoptionMapper adoptionMapper, UserMapper userMapper, AnimalMapper animalMapper) {
        this.adoptionMapper = adoptionMapper;
        this.userMapper = userMapper;
        this.animalMapper = animalMapper;
    }

    /**
     * 申请领养动物的方法
     */
    public Map<String, Object> applyForAdoption(int animalId, String token, String reason) {
        Map<String, Object> response = new HashMap<>();

        // 解析 Token 获取用户名
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return Map.of("code", 401, "message", "无效的 Token");
        }

        // 查询用户 ID
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Map.of("code", 404, "message", "用户不存在");
        }
        int userId = user.getUserId();

        // 查询动物信息
        Animal animal = animalMapper.getAnimalInfo(animalId);
        if (animal == null) {
            return Map.of("code", 404, "message", "动物不存在");
        }
        if (animal.getIsAdoptable() == 0) {
            return Map.of("code", 403, "message", "该动物当前不可领养");
        }

        // **检查用户是否已提交申请**
        int userApplicationCount = adoptionMapper.existsUserApplication(userId, animalId);
        if (userApplicationCount > 0) {
            // 查询该用户之前提交的申请记录
            AdoptionApplication existingApplication = adoptionMapper.findApplicationByUserAndAnimal(userId, animalId);

            response.put("code", 403);
            response.put("message", "你已经提交过该动物的领养申请");
            response.put("adoptionApplication", existingApplication);
            response.put("animalInfo", animal); // 也可以把动物信息一并返回
            return response;
        }


        // **检查是否已被领养**
        int adoptedCount = adoptionMapper.existsAdoptedAnimal(animalId);
        if (adoptedCount > 0) {
            return Map.of("code", 403, "message", "该动物已被他人申请领养");
        }

        // 创建领养申请对象
        AdoptionApplication adoptionApplication = new AdoptionApplication();
        adoptionApplication.setUserId(userId);
        adoptionApplication.setAnimalId(animalId);
        adoptionApplication.setNotes(reason);  // 将申请理由存入备注字段

        // 插入领养申请到 adoptionApplication 表
        try {
            adoptionMapper.insertAdoptionApplication(adoptionApplication);

            // 重新从数据库查询插入的申请信息，确保 status、interviewTime 也返回
            AdoptionApplication savedApplication = adoptionMapper.findApplicationByUserAndAnimal(userId, animalId);

            // 返回申请的相关信息
            response.put("code", 200);
            response.put("message", "领养申请提交成功");
            Map<String, Object> data = new HashMap<>();
            data.put("adoptionApplication", savedApplication);
            data.put("animalInfo", animal);

            response.put("code", 200);
            response.put("message", "领养申请提交成功");
            response.put("data", data);  // 👈 把结果放进 data 字段中

        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "提交失败，请稍后再试");
        }
        return response;
    }


    /**
     * 取消领养申请
     *
     * @param applicationId 申请 ID
     * @param token 用户 Token（用于身份验证）
     * @return 返回取消结果
     */
    public Map<String, Object> cancelAdoptionApplication(int applicationId, String token) {
        Map<String, Object> response = new HashMap<>();

        // 解析 Token 获取用户名
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return Map.of("code", 401, "message", "无效的 Token");
        }

        // 查询用户 ID
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Map.of("code", 404, "message", "用户不存在");
        }

        // 查询该申请是否存在并且属于该用户
        AdoptionApplication application = adoptionMapper.findAdoptionApplicationByApplicationId(applicationId);
        if (application == null || application.getUserId() != user.getUserId()) {
            return Map.of("code", 403, "message", "你无权取消此申请或申请不存在");
        }

        // 执行删除
        try {
            adoptionMapper.deleteAdoptionApplicationById(applicationId);
            response.put("code", 200);
            response.put("message", "领养申请取消成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "取消失败，请稍后再试");
        }

        return response;
    }

    /**
     * 通过 Token 获取用户的所有领养申请及对应动物信息
     * @param token 用户 Token
     * @return 领养申请详情列表，包含动物信息
     */
    public Map<String, Object> getAdoptionDetailsByToken(String token) {
        // 解析 token 获取用户名
        String username = JwtUtil.extractUsername(token);

        // 根据用户名查询用户信息
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Map.of("code", 403, "message", "用户不存在");
        }

        // 查询用户 ID
        Integer userId = user.getUserId();

        // 获取领养申请详情
        List<AdoptionApplication> applications = adoptionMapper.getAdoptionDetailsByUser(userId);

        // 构建包含动物信息的列表
        List<Map<String, Object>> detailedList = new ArrayList<>();
        for (AdoptionApplication application : applications) {
            Map<String, Object> item = new HashMap<>();
            item.put("application", application);

            // 查询动物信息（假设 application 中有 animalId）
            Animal animal = animalMapper.getAnimalInfo(application.getAnimalId());
            item.put("animal", animal);

            detailedList.add(item);
        }

        // 返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", detailedList);
        return response;
    }


    /**
     * 根据品种搜索领养申请记录
     *
     * @param user  当前用户
     * @param breed 动物品种
     * @return 包含申请信息、动物信息的列表，如果没有记录则返回提示信息
     */
    public List<Map<String, Object>> searchAdoptionApplicationsByBreed(User user, String breed) {
        // 查询该用户所有的申请中，动物品种为指定 breed 的申请
        List<AdoptionApplication> applications = adoptionMapper.findApplicationsByUserIdAndBreed(user.getUserId(), breed);

        List<Map<String, Object>> resultList = new ArrayList<>();

        for (AdoptionApplication application : applications) {
            Animal animal = animalMapper.getAnimalInfo(application.getAnimalId());
            if (animal != null) {
                Map<String, Object> item = Map.of(
                        "application", application,
                        "animal", animal
                );
                resultList.add(item);
            }
        }

        // 如果没有匹配的数据，返回带提示的内容
        if (resultList.isEmpty()) {
            Map<String, Object> emptyMessage = Map.of(
                    "message", "未找到符合该品种的领养申请记录"
            );
            resultList.add(emptyMessage);
        }

        return resultList;
    }


    /**
     * 分页查询某个救助站的领养申请
     * @param shelterId 救助站ID
     * @param page 当前页码（从 1 开始）
     * @param pageSize 每页显示条数
     * @return 领养申请列表
     */
    public Map<String, Object> getAdoptionApplicationsByShelterId(int shelterId, int page, int pageSize) {
        int offset = (page - 1) * pageSize; // 计算偏移量
        List<AdoptionApplication> applications = adoptionMapper.getAdoptionApplicationsByShelterId(shelterId, offset, pageSize);
        return Map.of("code", 200, "message", "查询成功", "data", applications);
    }

    // 更新领养申请状态和备注
    public Map<String, Object> updateAdoptionApplicationStatus(int applicationId, String status, String notes) {
        // 第一步：根据 ID 查询申请记录
        AdoptionApplication application = adoptionMapper.findAdoptionApplicationByApplicationId(applicationId);
        if (application == null) {
            return Map.of("code", 404, "message", "申请不存在");
        }

        // 第二步：检查状态是否是“审核中”
        if (!"审核中".equals(application.getStatus())) {
            return Map.of("code", 403, "message", "当前状态不允许修改，只能修改‘审核中’状态的申请");
        }

        // 第三步：设置备注（如果未填写）
        if ("已通过".equals(status)) {
            notes = (notes == null || notes.isEmpty()) ? "审核通过，等待后续通知" : notes;
        } else if ("已拒绝".equals(status)) {
            notes = (notes == null || notes.isEmpty()) ? "审核未通过，请完善信息后重新申请" : notes;
        }

        // 第四步：更新 notes 和 status 字段
        int notesUpdated = adoptionMapper.updateApplicationNotes(applicationId, notes);
        int statusUpdated = adoptionMapper.updateApplicationStatus(applicationId, status);

        if (notesUpdated > 0 && statusUpdated > 0) {
            // 第五步：重新查询更新后的申请记录
            AdoptionApplication updatedApplication = adoptionMapper.findAdoptionApplicationByApplicationId(applicationId);
            Animal animal = animalMapper.getAnimalInfo(updatedApplication.getAnimalId());

            // 构建 data 返回
            Map<String, Object> data = new HashMap<>();
            data.put("application", updatedApplication);
            data.put("animalInfo", animal);

            return Map.of(
                    "code", 200,
                    "message", "审核状态更新成功",
                    "data", data
            );
        } else {
            return Map.of("code", 400, "message", "审核状态更新失败");
        }
    }

    public List<Map<String, Object>> getApplicationsByUserId(int userId) {
        List<AdoptionApplication> applications = adoptionMapper.getAdoptionDetailsByUser(userId);
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (AdoptionApplication app : applications) {
            Map<String, Object> map = new HashMap<>();
            map.put("applicationId", app.getApplicationId());      // 申请ID
            map.put("userId", app.getUserId());                    // 用户ID
            map.put("animalId", app.getAnimalId());                // 动物ID
            map.put("status", app.getStatus());                    // 当前申请状态
            map.put("appliedAt", app.getAppliedAt());              // 申请时间
            map.put("interviewTime", app.getInterviewTime());      // 面试时间
            map.put("notes", app.getNotes());                      // 备注
            resultList.add(map);
        }
        return resultList;
    }


}
