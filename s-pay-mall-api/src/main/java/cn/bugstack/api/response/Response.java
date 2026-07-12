package cn.bugstack.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String info;
    private T data;

    public static <T> Response<T> success(T data) { return new Response<>("0000", "成功", data); }
    public static Response<Void> success() { return new Response<>("0000", "成功", null); }
    public static <T> Response<T> failure(String code, String info) { return new Response<>(code, info, null); }
}
