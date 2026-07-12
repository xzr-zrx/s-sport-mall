package cn.bugstack.trigger.job;

import cn.bugstack.domain.mall.service.IMallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "jobs.scheduling-enabled", havingValue = "true")
public class TimeoutCloseOrderJob {
    private final IMallService service;

    public TimeoutCloseOrderJob(IMallService service) {
        this.service = service;
    }

    @Scheduled(cron = "${jobs.timeout-close-cron:0 */5 * * * ?}")
    public void run() {
        service.listTimeoutOrders().forEach(orderNo -> {
            try {
                service.closeTimeoutOrder(orderNo);
            } catch (Exception e) {
                log.error("timeout close failed orderNo={}", orderNo, e);
            }
        });
    }
}
