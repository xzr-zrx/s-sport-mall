package cn.bugstack.trigger.job;
import cn.bugstack.domain.mall.service.IMallService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component @ConditionalOnProperty(name="jobs.scheduling-enabled",havingValue="true")
public class OutboxPublishJob {
    private final IMallService service; public OutboxPublishJob(IMallService service){this.service=service;}
    @Scheduled(cron="${jobs.outbox-cron:0 * * * * ?}") public void run(){service.publishOutbox();}
}
