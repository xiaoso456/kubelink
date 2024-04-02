package io.github.xiaoso456.kubelink.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseResult<T> implements Serializable {
    private Boolean success;
    private Integer code;
    private String msg;
    private T data;
}
