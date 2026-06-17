package org.example.strayanimalrescuebackend.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data  //自动生成Getter、Setter、Tostring、
@NoArgsConstructor //无参构造
@AllArgsConstructor //全参构造
public class User {
    private Integer userId;
    private String username;
    private String email;
    private String passwordHash;
    private String role;
    private String phone;
    private Boolean isFrozen;
    private Date createdAt;
    private String avatarUrl;
}
