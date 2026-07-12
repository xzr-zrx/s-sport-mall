package cn.bugstack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "redis.sdk.config")
public class RedisClientConfigProperties {
    private String url;
    private String host = "127.0.0.1";
    private int port = 6379;
    private String username;
    private String password;
    private int poolSize = 16;
    private int minIdleSize = 2;
    private int idleTimeout = 10000;
    private int connectTimeout = 10000;
    private int retryAttempts = 3;
    private int retryInterval = 1000;
    private int pingInterval = 30000;
    private boolean keepAlive = true;
}
