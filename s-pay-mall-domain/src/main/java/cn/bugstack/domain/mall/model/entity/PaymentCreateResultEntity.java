package cn.bugstack.domain.mall.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateResultEntity {
    private String orderNo;
    private String paymentNo;
    private String htmlForm;
}
