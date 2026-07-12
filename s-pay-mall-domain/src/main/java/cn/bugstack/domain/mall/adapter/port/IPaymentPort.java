package cn.bugstack.domain.mall.adapter.port;

import cn.bugstack.domain.mall.model.entity.MallOrderEntity;
import cn.bugstack.domain.mall.model.entity.PaymentCreateResultEntity;
import cn.bugstack.domain.mall.model.entity.PaymentQueryResultEntity;

public interface IPaymentPort {
    PaymentCreateResultEntity createPagePayment(MallOrderEntity order);
    PaymentQueryResultEntity queryPayment(String orderNo);
    boolean closePayment(String orderNo);
}
