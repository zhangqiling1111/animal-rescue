package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
/*救助记录*/
public class RescueRecord {
    private int recordId;
    private int shelterId;
    private int animalId;
    private LocalDateTime rescueTime;
    private String details;
}
