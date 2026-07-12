package cn.bugstack.trigger.http;

import cn.bugstack.api.response.Response;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleApp(AppException e) { return Response.failure(e.getCode(), e.getInfo()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> handleValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().isEmpty() ? "参数错误" : e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return Response.failure("VALIDATION_ERROR", message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<Void> handleUnknown(Exception e) { log.error("unexpected error", e); return Response.failure("SYSTEM_ERROR", "系统暂时不可用"); }
}
