package org.example.strayanimalrescuebackend.service;

import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.mapper.DonationMapper;
import org.example.strayanimalrescuebackend.mapper.ShelterMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.DonationRecord;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.example.strayanimalrescuebackend.model.User;

import java.util.List;

@Service
public class DonationService {

    @Autowired
    private DonationMapper donationMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShelterMapper shelterMapper;
    /**
     * 增加捐赠记录
     */
    public ResponseResult summitDonation(String token, DonationRecord donationRecord) {
        ResponseResult responseResult = new ResponseResult();
        try {
            /*判断donationRecord是否为空*/
            if (donationRecord != null) {
                /*获取userId*/
                String username = JwtUtil.extractUsername(token);
                User user = userMapper.findByUsername(username);



                if (user == null){
                    responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                    responseResult.setMessage("用户不存在");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                } else {
                    int userId = user.getUserId();
                    donationRecord.setDonateUserId(userId);

                    if (shelterMapper.findShelterById(donationRecord.getShelterId()) == null) {
                        responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                        responseResult.setMessage("救助站不存在");
                        responseResult.setData(null);
                        responseResult.setTotal(0L);

                        return responseResult;
                    }

                    //新增加一条捐赠记录
                    donationMapper.addDonationRecord(donationRecord);
                    //判断是否增加成功
                    DonationRecord newDonationRecord = donationMapper.getDonationRecord(donationRecord.getDonationId());
                    if ( newDonationRecord != null){
                        responseResult.setCode(HttpStatus.OK.value());
                        responseResult.setMessage("提交成功");
                        responseResult.setData(newDonationRecord);
                        responseResult.setTotal(1L);
                    } else {
                        responseResult.setCode(HttpStatus.NOT_FOUND.value());
                        responseResult.setMessage("提交失败");
                        responseResult.setData(null);
                        responseResult.setTotal(0L);
                    }

                }
            } else {
                responseResult.setCode(HttpStatus.NOT_FOUND.value());
                responseResult.setMessage("提交的信息为空");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }
        } catch (Exception e){
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }


    /**
     * 获取个人捐赠记录
     */
    public ResponseResult getPersonalRecords(String token) {
        ResponseResult responseResult = new ResponseResult();
        try {
            /*获取userId*/
            String username = JwtUtil.extractUsername(token);
            User user = userMapper.findByUsername(username);

            if (user == null){
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("用户不存在");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            } else {
                int userId = user.getUserId();
                List<DonationRecord> donationRecordList = donationMapper.getPersonalRecords(userId);
                responseResult.setCode(HttpStatus.OK.value());
                responseResult.setMessage("查询成功");
                responseResult.setData(donationRecordList);
                responseResult.setTotal((long)donationRecordList.size());
            }
        } catch (Exception e) {
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    public ResponseResult getRecordsInShelter(int shelterId) {
        ResponseResult responseResult = new ResponseResult();
        try {
            List<DonationRecord> donationRecordList = donationMapper.getRecordsInShelter(shelterId);
            responseResult.setCode(HttpStatus.OK.value());
            responseResult.setMessage("查询成功");
            responseResult.setData(donationRecordList);
            responseResult.setTotal((long)donationRecordList.size());
        } catch (Exception e) {
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }
}
