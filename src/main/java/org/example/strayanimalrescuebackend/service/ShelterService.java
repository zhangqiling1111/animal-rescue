package org.example.strayanimalrescuebackend.service;

import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.ShelterMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.Shelter;
import org.example.strayanimalrescuebackend.model.ShelterApplication;
import org.example.strayanimalrescuebackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShelterService {
    private final ShelterMapper shelterMapper;

    @Autowired
    private UserMapper userMapper;

    public ShelterService(ShelterMapper shelterMapper) {
        this.shelterMapper = shelterMapper;
    }

    /**
     * 获取所有救助站信息
     *
     * @return List<Shelter>
     */
    public List<Shelter> getAllShelters() {
        return shelterMapper.findAllShelters();
    }

    /**
     * 根据救助站 ID 获取救助站信息
     *
     * @param shelterId 救助站 ID
     * @return Shelter
     */
    public Shelter getShelterById(int shelterId) {
        return shelterMapper.findShelterById(shelterId);
    }



    /**
     * 根据用户名获取用户 ID，并查看救助站申请详情
     *
     * @param username 用户名
     * @return 返回救助站申请详情
     */
    public ResponseResult getShelterApplicationDetailsByUserId(String username) {
        // 根据用户名查找用户 ID
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return new ResponseResult(400, "用户不存在", null, 0L);
        }

        int userId = user.getUserId();

        // 根据用户 ID 获取救助站申请记录
        ShelterApplication application = shelterMapper.findShelterApplicationByUserId(userId);
        if (application == null) {
            return new ResponseResult(400, "没有找到该用户的救助站申请记录", null, 0L);
        } else {
            return new ResponseResult(200, "成功获取救助站申请详情", application, 1L);
        }
    }


    /**
     * 搜索救助站
     *
     * @param shelterName 搜索的救助站名称
     * @param address     搜索的救助站地址
     * @return 返回搜索结果
     */
    public ResponseResult searchShelters(String shelterName, String address) {
        // 调用 Mapper 层进行数据库查询
        List<Shelter> shelters = shelterMapper.searchShelters(shelterName, address);

        if (shelters.isEmpty()) {
            return new ResponseResult(400, "没有找到符合条件的救助站", null, 0L);
        } else {
            return new ResponseResult(200, "成功找到救助站", shelters, (long) shelters.size());
        }
    }

    /**
     * 根据用户ID查找救助站信息并更新
     *
     * @param userId      用户ID
     * @param shelterName 救助站名称
     * @param address     地址
     * @param contactInfo 联系方式
     * @return 更新结果
     */
    public ResponseResult updateShelterInfoByUserId(int userId, String shelterName, String address, String contactInfo) {
        // 根据用户ID查找救助站信息
        Shelter shelter = shelterMapper.findShelterByUserId(userId);
        if (shelter == null) {
            return new ResponseResult(400, "未找到相关的救助站信息", null, 0L);
        }

        // 更新救助站信息
        shelter.setName(shelterName);  // 更新救助站名称
        shelter.setAddress(address);   // 更新地址
        shelter.setContactInfo(contactInfo); // 更新联系方式

        // 执行数据库更新操作
        int result = shelterMapper.updateShelter(shelter);
        if (result > 0) {
            return new ResponseResult(200, "救助站信息更新成功", shelter, 1L);
        } else {
            return new ResponseResult(400, "救助站信息更新失败", null, 0L);
        }
    }

}
