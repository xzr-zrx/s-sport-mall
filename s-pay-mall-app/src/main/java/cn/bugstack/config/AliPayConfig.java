package cn.bugstack.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AliPayConfigProperties.class)
public class AliPayConfig {
    @Bean
    @ConditionalOnProperty(name = "alipay.enabled", havingValue = "true")
    public AlipayClient alipayClient(AliPayConfigProperties p) {
        return new DefaultAlipayClient(p.getGatewayUrl(), p.getAppId(), p.getMerchantPrivateKey(), p.getFormat(),
                p.getCharset(), p.getAlipayPublicKey(), p.getSignType());
    }
}
