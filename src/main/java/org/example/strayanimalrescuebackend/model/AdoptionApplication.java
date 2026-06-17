package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdoptionApplication {
    // 申请唯一标识
    private int applicationId;

    // 申请人（USER角色）
    private int userId;

    // 被申请动物
    private int animalId;

    // 审核状态
    private String status;

    // 家访/面试时间
    private Date interviewTime;

    // 申请时间
    private Date appliedAt;

    private String notes; // 用于记录关于申请的备注信息

}
