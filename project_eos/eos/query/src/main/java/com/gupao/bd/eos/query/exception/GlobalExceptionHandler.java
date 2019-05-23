package com.gupao.bd.eos.query.exception;

import com.alibaba.fastjson.JSONException;
import com.gupao.bd.eos.query.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    Response handleException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        logger.error(e.getMessage(), e);
        return new Response(50001, "系统内部错误，详细错误：" + e.getMessage());
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class,
            ServletRequestBindingException.class,
            IllegalArgumentException.class,
            IllegalStateException.class,
            JSONException.class,
            BindException.class,
            HttpRequestMethodNotSupportedException.class,
            UnsupportedOperationException.class})
    public @ResponseBody Response handBadRequestException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        logger.debug("bad request", e);
        return new Response(40001, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response processValidationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    private Response processFieldErrors(List<FieldError> fieldErrors) {
        StringBuilder errorString = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorString.append(fieldError.getDefaultMessage()).append("<br />");
        }
        return new Response(40001, errorString.toString());
    }
}
