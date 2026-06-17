package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class DonationRecord {
    private int donationId;
    private int donateUserId;//捐赠者Id
    private String donateUserName;//捐赠者姓名
    private int shelterId;//救助站Id
    private String shelterName;//救助站名称
    private int shelterUserId;//救助站负责人Id
    private String shelterUserName;//救助站负责人姓名
    private BigDecimal amount;
    private String itemType;
    private LocalDateTime donatedAt;
    private String shelterAddress;//救助站地址
    private String shelterContactInfo;//救助站联系方式
}
