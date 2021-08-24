package com.xxx.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxx.common.model.ApiResult;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Jarvan
 * @date 2018/12/20
 */
public class ResponseUtil {
    private ResponseUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 通过流写到前端
     *
     * @param objectMapper 对象序列化
     * @param response 返回
     * @param msg          返回信息
     * @param httpStatus   返回状态码
     */
    public static void responseWriter(ObjectMapper objectMapper, HttpServletResponse response, String msg, int httpStatus) throws IOException {
        ApiResult<Void> result = ApiResult.of(httpStatus, msg, null);
        responseWrite(objectMapper, response, result);
    }

    /**
     * 通过流写到前端
     * @param objectMapper 对象序列化
     * @param response .
     * @param obj .
     */
    public static void responseSucceed(ObjectMapper objectMapper, HttpServletResponse response, Object obj) throws IOException {
        ApiResult<Object> result = ApiResult.succeed(obj);
        responseWrite(objectMapper, response, result);
    }

    /**
     * 通过流写到前端
     * @param objectMapper .
     * @param response .
     * @param msg /
     */
    public static void responseFailed(ObjectMapper objectMapper, HttpServletResponse response, String msg) throws IOException {
        ApiResult<Object> result = ApiResult.failed(msg);
        responseWrite(objectMapper, response, result);
    }

    private static <T> void responseWrite(ObjectMapper objectMapper, HttpServletResponse response, ApiResult<T> result) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try (
                Writer writer = response.getWriter()
        ) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }
}
