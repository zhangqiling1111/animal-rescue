package org.example.strayanimalrescuebackend.model;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class RescueReport {
    private int reportId;
    private int userId;
    private int shelterId;
    private String  location;
    private String photos;
    private String  healthStatus;
    private String  status;
    private LocalDateTime reportedAt;
}
