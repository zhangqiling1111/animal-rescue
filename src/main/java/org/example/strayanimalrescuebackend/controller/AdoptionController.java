package org.example.strayanimalrescuebackend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.User;
import org.example.strayanimalrescuebackend.service.AdoptionService;
import org.example.strayanimalrescuebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;
    private final UserMapper userMapper;

    public AdoptionController(AdoptionService adoptionService, UserService userService, UserMapper userMapper) {
        this.adoptionService = adoptionService;
        this.userMapper = userMapper;
    }

    /**
     * 用户申请领养流浪动物（通过 Token 获取 userId）
     */
    @PostMapping("/apply/{animalId}")
    public ResponseResult applyForAdoption(@PathVariable int animalId,
                                           @RequestBody Map<String, String> requestBody,
                                           HttpServletRequest request) {
        String reason = requestBody.get("reason");
        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            return new ResponseResult(401, "未提供有效的 Token", null, 0L);
        }

        Map<String, Object> result = adoptionService.applyForAdoption(animalId, token, reason);
        return new ResponseResult((int) result.get("code"), (String) result.get("message"), result.get("data"), 1L);
    }

    /**
     * 用户取消已提交的领养申请
     */
    @DeleteMapping("/cancel/{applicationId}")
    public ResponseResult cancelAdoption(@PathVariable int applicationId, HttpServletRequest request) {
        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            return new ResponseResult(401, "未提供有效的 Token", null, 0L);
        }

        Map<String, Object> result = adoptionService.cancelAdoptionApplication(applicationId, token);
        return new ResponseResult((int) result.get("code"), (String) result.get("message"), result.get("data"), 1L);
    }

    /**
     * 用户查看自己提交的所有领养申请信息
     */
    @GetMapping("/details")
    public ResponseResult getAdoptionDetails(HttpServletRequest request) {
        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            return new ResponseResult(401, "未提供有效的 Token", null, 0L);
        }

        try {
            Map<String, Object> result = adoptionService.getAdoptionDetailsByToken(token);
            return new ResponseResult((int) result.get("code"), (String) result.get("message"), result.get("data"), (Long) result.getOrDefault("total", 0L));
        } catch (Exception e) {
            return new ResponseResult(500, "服务器错误：" + e.getMessage(), null, 0L);
        }
    }

    /**
     * 根据动物品种搜索用户的领养申请记录
     */
    @GetMapping("/searchByBreed")
    public ResponseResult searchAdoptionApplicationsByBreed(@RequestParam String breed, HttpServletRequest request) {
        String token = request.getHeader("token");

        if (token == null || token.isEmpty()) {
            return new ResponseResult(401, "未提供有效的 Token", null, 0L);
        }

        try {
            String username = JwtUtil.extractUsername(token);
            User user = userMapper.findByUsername(username);

            if (user == null) {
                return new ResponseResult(403, "用户不存在", null, 0L);
            }

            List<Map<String, Object>> resultList = adoptionService.searchAdoptionApplicationsByBreed(user, breed);

            return new ResponseResult(200, "查询成功", resultList, (long) resultList.size());
        } catch (Exception e) {
            return new ResponseResult(500, "Token 解析失败：" + e.getMessage(), null, 0L);
        }
    }


    /**
     * 获取某个救助站的领养申请（支持分页）
     */
    @GetMapping("/shelter/{shelterId}")
    public ResponseResult getAdoptionApplicationsByShelter(@PathVariable int shelterId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> result = adoptionService.getAdoptionApplicationsByShelterId(shelterId, page, pageSize);
        return new ResponseResult((int) result.get("code"), (String) result.get("message"), result.get("data"), (Long) result.getOrDefault("total", 0L));
    }

    /**
     * 修改领养申请的审核状态
     */
    @PutMapping("/update-status")
    public ResponseResult updateAdoptionApplicationStatus(@RequestParam int applicationId,
                                                          @RequestParam String status,
                                                          @RequestParam(required = false) String notes) {
        Map<String, Object> result = adoptionService.updateAdoptionApplicationStatus(applicationId, status, notes);
        return new ResponseResult((int) result.get("code"), (String) result.get("message"), result.get("data"), 1L);
    }

    /**
     * 根据用户ID获取其所有领养申请
     */
    @GetMapping("/user/{userId}")
    public ResponseResult getApplicationsByUserId(@PathVariable int userId) {
        try {
            List<Map<String, Object>> applications = adoptionService.getApplicationsByUserId(userId);
            return new ResponseResult(200, "查询成功", applications, (long) applications.size());
        } catch (Exception e) {
            return new ResponseResult(500, "查询失败：" + e.getMessage(), null, 0L);
        }
    }

}
