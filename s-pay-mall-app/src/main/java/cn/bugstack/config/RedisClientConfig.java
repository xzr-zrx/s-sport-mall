package cn.bugstack.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedisClientConfigProperties.class)
public class RedisClientConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisClientConfigProperties p) {
        Config config = new Config(); config.setCodec(JsonJacksonCodec.INSTANCE);
        String address = StringUtils.isNotBlank(p.getUrl()) ? p.getUrl() : "redis://" + p.getHost() + ":" + p.getPort();
        SingleServerConfig server = config.useSingleServer().setAddress(address)
                .setConnectionPoolSize(p.getPoolSize()).setConnectionMinimumIdleSize(p.getMinIdleSize())
                .setIdleConnectionTimeout(p.getIdleTimeout()).setConnectTimeout(p.getConnectTimeout())
                .setRetryAttempts(p.getRetryAttempts()).setRetryInterval(p.getRetryInterval())
                .setPingConnectionInterval(p.getPingInterval()).setKeepAlive(p.isKeepAlive());
        if (StringUtils.isNotBlank(p.getUsername())) server.setUsername(p.getUsername());
        if (StringUtils.isNotBlank(p.getPassword())) server.setPassword(p.getPassword());
        return Redisson.create(config);
    }
}
