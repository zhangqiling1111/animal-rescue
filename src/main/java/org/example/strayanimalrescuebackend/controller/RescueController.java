package org.example.strayanimalrescuebackend.controller;


import org.example.strayanimalrescuebackend.model.RescueRecord;
import org.example.strayanimalrescuebackend.model.RescueReport;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import org.example.strayanimalrescuebackend.service.RescueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/rescue")
public class RescueController {
    @Autowired
    private RescueService rescueService;

    /**
     * 查看所有的救助记录
     * @return responseResult
     */
    @GetMapping("/getRescueRecords")
    public ResponseResult getRescueRecords(
            @RequestParam(required = false,defaultValue = "-1") int recordId,
            @RequestParam(required = false,defaultValue = "-1") int shelterId,
            @RequestParam(required = false,defaultValue = "-1") int animalId
            ){
        System.out.println("recordId:" + recordId + ",shelterId:" + shelterId + ",animalId:" + animalId);

        return rescueService.getRescueRecords(recordId,shelterId,animalId);
    }


    /**
     * 用户提交救助申请，后端增加救助申请信息
     * @return responseResult
     */
    @PostMapping("/summitReport")
    public ResponseResult addRescueReport(
            @RequestHeader("token") String token,
            @RequestParam("shelterId") int shelterId,
            @RequestParam("location") String location,
            @RequestParam("healthStatus") String  healthStatus,
            @RequestParam("rescuePhoto") MultipartFile file
    ){
        RescueReport rescueReport = new RescueReport();
        rescueReport.setShelterId(shelterId);
        rescueReport.setLocation(location);
        rescueReport.setHealthStatus(healthStatus);
        System.out.println("1请求参数："+rescueReport);
        return rescueService.addRescueReport(token,rescueReport,file);
    }

    /**
     * 获取普通用户的救助申请详情
     * @param token
     * @return re
     */
    @GetMapping("/getReportByUserId")
    public ResponseResult getReportByUserId(
            @RequestHeader("token") String token
    ){
        return rescueService.getReportByUserId(token);

    }

    /**
     * 获取救助站要处理的救助申请详情
     * @return re
     */
    @GetMapping("/getReportByShelterId")
    public ResponseResult getReportByShelterId(
            @RequestParam int shelterId
    ){
        return rescueService.getReportByShelterId(shelterId);

    }


    /**
     * 救助站审核救助申请
     * @return re
     */
    @PutMapping("/checkReport")
    public ResponseResult checkReport(
            @RequestHeader("token") String token,
            @RequestParam int shelterId,
            @RequestParam int reportId,
            @RequestParam String status
            ){
        return rescueService.checkReport(token,shelterId,reportId,status);
    }

    @PostMapping("/uploadRescueRecord")
    public ResponseResult uploadRescueRecord(
            @RequestBody RescueRecord rescueRecord
            ){
        return rescueService.uploadRescueRecord(rescueRecord);
    }


}
