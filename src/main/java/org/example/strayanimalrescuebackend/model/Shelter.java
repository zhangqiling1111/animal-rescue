package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* 救助站实体
* */
@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class Shelter {
    private int shelterId;
    private int userId;
    private String name;
    private String address;
    private String contactInfo;
}
