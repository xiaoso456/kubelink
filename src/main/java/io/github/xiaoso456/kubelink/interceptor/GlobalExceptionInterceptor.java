package io.github.xiaoso456.kubelink.interceptor;

import cn.hutool.core.exceptions.ExceptionUtil;
import io.github.xiaoso456.kubelink.exception.runtime.LinkRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionInterceptor{


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class )
    public String runtimeHandler(RuntimeException e){
        return ExceptionUtil.getRootCauseMessage(e);
    }

}
