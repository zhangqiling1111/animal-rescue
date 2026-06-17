package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class Animal {
    private int animalId;
    private int shelterId;
    private String shelterName;
    private String address;
    private String breed;
    private int age;
    private String healthStatus;
    private Integer isAdoptable;
    private LocalDateTime createdAt;
    private String animalPhoto;
}
