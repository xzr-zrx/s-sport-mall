package cn.bugstack.domain.mall;

import cn.bugstack.domain.mall.adapter.port.IPaymentPort;
import cn.bugstack.domain.mall.adapter.repository.IMallRepository;
import cn.bugstack.domain.mall.model.entity.MallOrderEntity;
import cn.bugstack.domain.mall.model.entity.PaymentQueryResultEntity;
import cn.bugstack.domain.mall.model.valobj.OrderStatusVO;
import cn.bugstack.domain.mall.model.valobj.PaymentStatusVO;
import cn.bugstack.domain.mall.service.MallService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MallServiceTest {
    private IMallRepository repository;
    private IPaymentPort paymentPort;
    private MallService service;

    @BeforeEach
    void setUp() {
        repository = mock(IMallRepository.class);
        paymentPort = mock(IPaymentPort.class);
        service = new MallService(repository, paymentPort);
    }

    @Test
    void shouldRejectMismatchedCallbackAmount() {
        when(repository.getOrder("O1")).thenReturn(MallOrderEntity.builder().orderNo("O1").payAmount(new BigDecimal("19.90")).build());
        assertTrue(service.verifyNotify("O1", new BigDecimal("19.90")));
        assertFalse(service.verifyNotify("O1", new BigDecimal("0.01")));
    }

    @Test
    void activeQueryMustNotMarkWaitingTradeAsPaid() {
        when(repository.getOrder("O2")).thenReturn(MallOrderEntity.builder().orderNo("O2").status(OrderStatusVO.PAYING).build());
        when(paymentPort.queryPayment("O2")).thenReturn(PaymentQueryResultEntity.builder().status(PaymentStatusVO.PAYING).build());
        service.reconcileOrder("O2");
        verify(repository, never()).markOrderPaid(anyString(), any());
    }

    @Test
    void activeQueryMarksOnlySuccessfulTradeAsPaid() {
        MallOrderEntity order = MallOrderEntity.builder().orderNo("O3").status(OrderStatusVO.PAYING).build();
        when(repository.getOrder("O3")).thenReturn(order);
        when(paymentPort.queryPayment("O3")).thenReturn(PaymentQueryResultEntity.builder()
                .status(PaymentStatusVO.SUCCESS).thirdPartyTradeNo("T3").build());
        when(repository.markOrderPaid("O3", "T3")).thenReturn(true);
        service.reconcileOrder("O3");
        verify(repository).markOrderPaid("O3", "T3");
        verify(repository).saveOutbox(eq("O3"), eq("ORDER_PAID"), anyString());
    }
}
