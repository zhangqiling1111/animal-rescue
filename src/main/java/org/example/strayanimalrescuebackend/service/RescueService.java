package org.example.strayanimalrescuebackend.service;

import com.alibaba.fastjson2.JSON;
import org.example.strayanimalrescuebackend.Util.AliOSSUtils;
import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.Util.StringUtils;
import org.example.strayanimalrescuebackend.mapper.RescueMapper;
import org.example.strayanimalrescuebackend.mapper.ShelterMapper;
import org.example.strayanimalrescuebackend.mapper.UserMapper;
import org.example.strayanimalrescuebackend.model.RescueRecord;
import org.example.strayanimalrescuebackend.model.RescueReport;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class RescueService {
    /**
     * 查看所有的救助记录
     *
     * @return responseResult
     */
    @Autowired
    private RescueMapper rescueMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShelterMapper shelterMapper;

    public ResponseResult getRescueRecords(int recordId, int shelterId, int animalId) {
        ResponseResult responseResult = new ResponseResult();
        List<RescueRecord> rescueRecordList = rescueMapper.getRescueRecords(recordId,shelterId,animalId);
        try {
            if (rescueRecordList.isEmpty()){
                responseResult.setCode(HttpStatus.NOT_FOUND.value());
                responseResult.setMessage("没有相关救助记录");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            } else {
                responseResult.setCode(HttpStatus.OK.value());
                responseResult.setMessage("获取成功");
                responseResult.setData(rescueRecordList);
                responseResult.setTotal((long)rescueRecordList.size());
            }
        } catch (Exception e){
            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }

        return responseResult;
    }

    public ResponseResult addRescueReport(String token, RescueReport rescueReport, MultipartFile file) {
        ResponseResult responseResult = new ResponseResult();
        try {
            if (rescueReport != null) {
                System.out.println("2请求参数:"+rescueReport);

                // 解析 token 获取用户ID
                String username = JwtUtil.extractUsername(token);
                if (username == null) {
                    responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                    responseResult.setMessage("无效token");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                    return responseResult;
                }
                // 获取用户ID
                User user = userMapper.findByUsername(username);
                if (user == null) {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("用户不存在");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                    return responseResult;
                }

                int userId = user.getUserId();

                System.out.println("查询到的user：" + user);
                System.out.println("查询到的userId：" + userId);

                rescueReport.setUserId(userId);

                System.out.println("3请求参数:"+rescueReport);

                //图片文件处理

                if (!file.isEmpty()){
                    System.out.println(file);
                    System.out.println(file.getSize());
                    System.out.println(file.getOriginalFilename());

                    String photoUrl = AliOSSUtils.upload(file);

                    System.out.println(photoUrl);

                    String jsonPhotos = JSON.toJSONString(photoUrl);
                    System.out.println(jsonPhotos);

                    rescueReport.setPhotos(jsonPhotos);

                } else {
                    responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                    responseResult.setMessage("上传图片内容为空");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                    return responseResult;
                }
                /*添加救助申请信息*/
                rescueMapper.addRescueReport(rescueReport);
                int reportId = rescueReport.getReportId();
                RescueReport newRescueReport = rescueMapper.getRescueReport(reportId);
                /*判断是否添加成功，获取添加成功后的救助申请信息，并返回给前端*/
                if (newRescueReport != null) {
                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("提交成功，等待救助站处理");
                    responseResult.setData(newRescueReport);
                    responseResult.setTotal(1L);

                } else {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("提交失败，服务器内部问题");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                }
            } else {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("参数为空，无法提交");
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

    public ResponseResult getReportByUserId(String token) {
        ResponseResult responseResult = new ResponseResult();

        try {
            // 解析 token 获取用户ID
            String username = JwtUtil.extractUsername(token);
            if (username == null) {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("无效token");
                responseResult.setData(null);
                responseResult.setTotal(0L);
                return responseResult;
            }
            // 获取用户ID
            User user = userMapper.findByUsername(username);
            if (user == null) {
                responseResult.setCode(HttpStatus.NOT_FOUND.value());
                responseResult.setMessage("用户不存在");
                responseResult.setData(null);
                responseResult.setTotal(0L);
                return responseResult;
            }
            int userId = user.getUserId();
            //获取登录用户的救助上报详情


            List<RescueReport> rescueReportList = StringUtils.removeQuotationMarks(rescueMapper.getReportByUserId(userId));

            responseResult.setTotal((long)rescueReportList.size());
            responseResult.setData(rescueReportList);
            responseResult.setCode(HttpStatus.OK.value());
            responseResult.setMessage("查询成功");
        } catch (Exception e) {

            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }

        return responseResult;
    }

    public ResponseResult getReportByShelterId(int shelterId) {
        ResponseResult responseResult = new ResponseResult();

        try {

     /*       List<RescueReport> rescueReportList = rescueMapper.getReportByUserId(shelterId);
            System.out.println(rescueReportList);*/

            List<RescueReport> rescueReportList = StringUtils.removeQuotationMarks(rescueMapper.getReportByShelterId(shelterId));

            System.out.println(rescueReportList);
            responseResult.setTotal((long)rescueReportList.size());
            responseResult.setData(rescueReportList);
            responseResult.setCode(HttpStatus.OK.value());
            responseResult.setMessage("查询成功");

        } catch (Exception e) {

            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    public ResponseResult checkReport(String token, int shelterId, int reportId, String status) {
        ResponseResult responseResult = new ResponseResult();
        try {
            //判断用户是否有修改权限
            // 解析 token 获取用户ID
            String username = JwtUtil.extractUsername(token);
            if (username == null) {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("无效token");
                responseResult.setData(null);
                responseResult.setTotal(0L);
                return responseResult;
            }
            // 获取用户ID
            User user = userMapper.findByUsername(username);
            if (user == null) {
                responseResult.setCode(HttpStatus.NOT_FOUND.value());
                responseResult.setMessage("用户不存在");
                responseResult.setData(null);
                responseResult.setTotal(0L);
                return responseResult;
            }
            int userId = user.getUserId();
            if (shelterMapper.findShelterById(shelterId).getUserId() == userId) {
                if (rescueMapper.getRescueReport(reportId) == null) {
                    responseResult.setCode(HttpStatus.NOT_FOUND.value());
                    responseResult.setMessage("该救助申请不存在");
                    responseResult.setData(null);
                    responseResult.setTotal(0L);
                } else {
                    rescueMapper.checkReport(reportId,shelterId,status);
                    responseResult.setCode(HttpStatus.OK.value());
                    responseResult.setMessage("操作成功");
                    responseResult.setData(StringUtils.removeQuotationMark(rescueMapper.getRescueReport(reportId)));
                    responseResult.setTotal(1L);
                }
            } else {
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("用户没有审核权限");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            }
        } catch (Exception e) {

            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }

    public ResponseResult uploadRescueRecord(RescueRecord rescueRecord) {
        ResponseResult responseResult = new ResponseResult();
        try {
            rescueMapper.insertRescueRecord(rescueRecord);
            RescueRecord newRescueRecord = rescueMapper.getRescueRecordById(rescueRecord.getRecordId());
            if (newRescueRecord == null){
                responseResult.setCode(HttpStatus.BAD_REQUEST.value());
                responseResult.setMessage("上传失败");
                responseResult.setData(null);
                responseResult.setTotal(0L);
            } else {

                responseResult.setCode(HttpStatus.OK.value());
                responseResult.setMessage("上传成功");
                responseResult.setData(newRescueRecord);
                responseResult.setTotal(1L);
            }
        } catch (Exception e) {

            responseResult.setCode(500);
            responseResult.setMessage(e.getMessage());
            responseResult.setData(null);
            responseResult.setTotal(0L);
        }
        return responseResult;
    }
}
