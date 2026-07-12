package cn.bugstack.trigger.job;

import cn.bugstack.domain.mall.service.IMallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "jobs.scheduling-enabled", havingValue = "true")
public class PaymentReconcileJob {
    private final IMallService service;

    public PaymentReconcileJob(IMallService service) {
        this.service = service;
    }

    @Scheduled(cron = "${jobs.payment-reconcile-cron:0 */2 * * * ?}")
    public void run() {
        service.listNeedReconcileOrders().forEach(orderNo -> {
            try {
                service.reconcileOrder(orderNo);
            } catch (Exception e) {
                log.error("payment reconcile failed orderNo={}", orderNo, e);
            }
        });
    }
}
