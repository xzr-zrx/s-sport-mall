package cn.bugstack.types.exception;

public class AppException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final String code;
    private final String info;

    public AppException(String message) { this("APP_ERROR", message); }
    public AppException(String code, String message) { super(message); this.code = code; this.info = message; }
    public AppException(String code, String message, Throwable cause) { super(message, cause); this.code = code; this.info = message; }
    public String getCode() { return code; }
    public String getInfo() { return info; }
}
