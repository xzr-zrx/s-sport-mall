package cn.bugstack.domain.mall.model.entity;

import cn.bugstack.domain.mall.model.valobj.PaymentStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentQueryResultEntity {
    private PaymentStatusVO status;
    private String thirdPartyTradeNo;
    private String rawStatus;
}
