package org.example.strayanimalrescuebackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 响应体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult {
    private int code;
    private String message;
    private Object data;
    private Long total;//data的数量
}
