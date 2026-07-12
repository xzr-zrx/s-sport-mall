package cn.bugstack.trigger.http;

import cn.bugstack.api.response.Response;
import cn.bugstack.domain.mall.service.IMallService;
import cn.bugstack.types.exception.AppException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/jobs")
public class InternalJobController {
    private final IMallService mallService;
    private final String jobToken;
    public InternalJobController(IMallService mallService, @Value("${jobs.token}") String jobToken) { this.mallService = mallService; this.jobToken = jobToken; }

    @PostMapping("/payment-reconcile") public Response<Void> reconcile(@RequestHeader("X-Job-Token") String token) { verify(token); mallService.listNeedReconcileOrders().forEach(mallService::reconcileOrder); return Response.success(); }
    @PostMapping("/timeout-close") public Response<Void> close(@RequestHeader("X-Job-Token") String token) { verify(token); mallService.listTimeoutOrders().forEach(mallService::closeTimeoutOrder); return Response.success(); }
    @PostMapping("/outbox-publish") public Response<Void> outbox(@RequestHeader("X-Job-Token") String token) { verify(token); mallService.publishOutbox(); return Response.success(); }
    private void verify(String token) { if (!jobToken.equals(token)) throw new AppException("JOB_403", "任务令牌无效"); }
}
