package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
 * 救助站申请表
 * */
@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class ShelterApplication {
    private int applicationId; // 申请唯一标识
    private int userId; // 申请人ID
    private String shelterName; // 申请的救助站名称
    private String address; // 地址
    private String contactInfo; // 联系方式
    private String status; // 审核状态：待审核、已通过、已拒绝
    private Date appliedAt; // 申请时间

}
