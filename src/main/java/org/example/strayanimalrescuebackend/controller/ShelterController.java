package org.example.strayanimalrescuebackend.controller;

import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.Shelter;
import org.example.strayanimalrescuebackend.model.User;
import org.example.strayanimalrescuebackend.service.ShelterApplicationService;
import org.example.strayanimalrescuebackend.service.ShelterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ShelterController - 处理救助站相关的 HTTP 请求
 */
@RestController
@RequestMapping("/shelters")
public class ShelterController {
    private final ShelterService shelterService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShelterApplicationService shelterApplicationService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }

    /**
     * 获取所有救助站信息
     * @return Shelter 列表
     */
    @GetMapping("/all")
    public ResponseResult getAllShelters() {
        List<Shelter> shelters = shelterService.getAllShelters();
        return new ResponseResult(200, "获取所有救助站信息成功", shelters, (long) shelters.size());
    }

    /**
     * 根据救助站 ID 获取救助站信息
     * @param shelterId 救助站 ID
     * @return Shelter
     */
    @GetMapping("/{shelterId}")
    public ResponseResult getShelterById(@PathVariable int shelterId) {
        Shelter shelter = shelterService.getShelterById(shelterId);
        return new ResponseResult(200, "获取救助站信息成功", shelter, 1L);
    }

    /**
     * 用户申请注册救助站
     */
    @PostMapping("/apply")
    public ResponseResult applyForShelter(@RequestHeader("token") String token,
                                          @RequestBody Map<String, String> request) {
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return new ResponseResult(400, "无效的Token", null, 0L);
        }

        String shelterName = request.get("shelterName");
        String address = request.get("address");
        String contactInfo = request.get("contactInfo");

        // 所有业务逻辑交由 service 层处理
        return shelterApplicationService.applyForShelter(username, shelterName, address, contactInfo);
    }


    /**
     * 根据用户ID查看救助站申请详情
     * @param token 请求头中的token，包含用户身份信息
     * @return 返回救助站申请详情
     */
    @GetMapping("/application/details")
    public ResponseResult getShelterApplicationDetails(@RequestHeader("token") String token) {
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return new ResponseResult(400, "无效的Token", null, 0L);
        }

        // 调用服务层，获取用户的救助站申请详情
        return shelterService.getShelterApplicationDetailsByUserId(username);
    }

    /**
     * 取消救助站申请
     * @param token 用户的身份令牌
     * @param applicationId 申请ID
     * @return 取消结果
     */
    @DeleteMapping("/cancel/{applicationId}")
    public ResponseResult cancelShelterApplication(@RequestHeader("token") String token,
                                                   @PathVariable int applicationId) {
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return new ResponseResult(400, "无效的Token", null, 0L);
        }

        var user = userMapper.findByUsername(username);
        if (user == null) {
            return new ResponseResult(400, "用户不存在", null, 0L);
        }

        boolean isCancelled = shelterApplicationService.cancelApplication(applicationId, user.getUserId());

        if (isCancelled) {
            return new ResponseResult(200, "救助站申请已取消", null, 0L);
        } else {
            return new ResponseResult(400, "无法取消申请，可能不存在或已被处理", null, 0L);
        }
    }

    /**
     * 搜索救助站接口
     * @param shelterName 搜索的救助站名称
     * @param address 搜索的救助站地址
     * @return 返回符合条件的救助站列表
     */
    @GetMapping("/search")
    public ResponseResult searchShelters(@RequestParam(required = false) String shelterName,
                                         @RequestParam(required = false) String address) {
        return shelterService.searchShelters(shelterName, address);
    }

    /**
     * 根据用户ID修改救助站信息
     * @param token 用户的 token
     * @param shelterName 救助站名称
     * @param address 地址
     * @param contactInfo 联系方式
     * @return 更新结果
     */
    @PutMapping("/update")
    public ResponseResult updateShelter(@RequestHeader("token") String token,
                                        @RequestParam String shelterName,
                                        @RequestParam String address,
                                        @RequestParam String contactInfo) {
        // 解析token获取用户名
        String username = JwtUtil.extractUsername(token);
        if (username == null) {
            return new ResponseResult(400, "无效的Token", null, 0L);
        }

        // 根据用户名查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return new ResponseResult(400, "用户不存在", null, 0L);
        }

        int userId = user.getUserId();

        // 调用服务层更新救助站信息
        return shelterService.updateShelterInfoByUserId(userId, shelterName, address, contactInfo);
    }

}
