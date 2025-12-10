package com.shopco.payment;

import com.shopco.cart.entity.Cart;
import com.shopco.cart.service.CartService;
import com.shopco.core.response.ApiResponse;
import com.shopco.order.entity.Order;
import com.shopco.order.enums.OrderStatus;
import com.shopco.order.service.OrderService;
import com.shopco.payment.dto.response.MonnifyTransactionStatusResponse;
import com.shopco.payment.entity.PaymentTransaction;
import com.shopco.payment.enums.PaymentStatus;
import com.shopco.payment.service.PaymentTransactionService;
import com.shopco.payment.service.impl.MonnifyPaymentServiceImpl;
import com.shopco.payment.util.Base64FormatConversion;
import com.shopco.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonnifyPaymentServiceImplTest {

    @Mock
    private Base64FormatConversion base64FormatConversion;

    @Mock
    private RestClient paymentRestClient;

    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentTransactionService paymentTransactionService;

    @Mock
    private OrderService orderService;

    @Spy
    @InjectMocks
    private MonnifyPaymentServiceImpl monnifyPaymentService;

    /**
     * Happy path:
     *  - Monnify returns PAID
     *  - PaymentTransaction is INITIALIZED
     *  - Amount matches cart
     *  - We update payment, create order, clear cart
     */
    @Test
    void completeCheckout_shouldCompletePaymentAndCreateOrder_whenPaidAndNotProcessed() {
        String txRef = "TXN-123";

        // 1) Fake Monnify status response
        MonnifyTransactionStatusResponse statusResponse = mock(MonnifyTransactionStatusResponse.class);
        MonnifyTransactionStatusResponse.Body body = mock(MonnifyTransactionStatusResponse.Body.class);

        when(statusResponse.requestSuccessful()).thenReturn(true);
        when(statusResponse.responseBody()).thenReturn(body);
        when(body.paymentStatus()).thenReturn("PAID");
        when(body.paymentReference()).thenReturn("PAY-123");
        when(body.amountPaid()).thenReturn(BigDecimal.valueOf(100.00));

        // 2) Stub internal call instead of RestClient chain
        doReturn(statusResponse)
                .when(monnifyPaymentService)
                .fetchTransactionStatusFromProvider(txRef);

        // 3) PaymentTransaction + Cart + Order
        PaymentTransaction paymentTx = mock(PaymentTransaction.class);
        Cart cart = mock(Cart.class);

        when(paymentTransactionService.resolvePaymentTransaction("PAY-123"))
                .thenReturn(paymentTx);
        when(paymentTx.getStatus()).thenReturn(PaymentStatus.INITIALIZED);
        when(paymentTx.getCart()).thenReturn(cart);
        when(cart.getTotalAmount()).thenReturn(new BigDecimal("100.00"));

        Order order = new Order();
        UUID orderId = UUID.randomUUID();
        UUID cartId = UUID.randomUUID();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setConfirmationCode("1234");
        order.setCartId(cartId);
        order.setAmount(new BigDecimal("100.00"));

        when(orderService.createOrderFromCart(eq(cart), anyString()))
                .thenReturn(order);

        // 4) Execute
        ResponseEntity<ApiResponse> response =
                monnifyPaymentService.completeCheckout(txRef);

        // 5) Assert HTTP + body
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
//        assertTrue(response.getBody().status());
//        assertEquals("payment verified and order created", response.getBody().message());

        // 6) Verify interactions
        verify(monnifyPaymentService).fetchTransactionStatusFromProvider(txRef);
        verify(paymentTransactionService).resolvePaymentTransaction("PAY-123");
        verify(paymentTransactionService).updatePaymentAsPaid(paymentTx, body);
        verify(orderService).createOrderFromCart(eq(cart), anyString());
        verify(cartService).clearCart(cart);
    }

    /**
     * Idempotency case:
     *  - Monnify says PAID
     *  - PaymentTransaction already PAID
     *  - We do NOT create another order or clear cart
     */
    @Test
    void completeCheckout_shouldReturnAlreadyProcessed_whenPaymentAlreadyPaid() {
        String txRef = "TXN-456";

        MonnifyTransactionStatusResponse statusResponse = mock(MonnifyTransactionStatusResponse.class);
        MonnifyTransactionStatusResponse.Body body = mock(MonnifyTransactionStatusResponse.Body.class);

        when(statusResponse.requestSuccessful()).thenReturn(true);
        when(statusResponse.responseBody()).thenReturn(body);
        when(body.paymentStatus()).thenReturn("PAID");
        when(body.paymentReference()).thenReturn("PAY-456");

        doReturn(statusResponse)
                .when(monnifyPaymentService)
                .fetchTransactionStatusFromProvider(txRef);

        PaymentTransaction paymentTx = mock(PaymentTransaction.class);
        when(paymentTransactionService.resolvePaymentTransaction("PAY-456"))
                .thenReturn(paymentTx);
        when(paymentTx.getStatus()).thenReturn(PaymentStatus.PAID);

        ResponseEntity<ApiResponse> response =
                monnifyPaymentService.completeCheckout(txRef);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
//        assertTrue(response.getBody().status());
//        assertEquals("Payment already processed", response.getBody().message());

        verify(paymentTransactionService).resolvePaymentTransaction("PAY-456");
        verify(paymentTransactionService, never()).updatePaymentAsPaid(any(), any());
        verify(orderService, never()).createOrderFromCart(any(), anyString());
        verify(cartService, never()).clearCart(any());
    }

    /**
     * Failure case:
     *  - Monnify returns status != PAID (e.g. PENDING)
     *  - validatePaymentIsPaid throws IllegalArgumentException
     *  - Service catches it and returns 500 with error ApiResponse
     */
    @Test
    void completeCheckout_shouldReturn500_whenPaymentNotPaid() {
        String txRef = "TXN-789";

        MonnifyTransactionStatusResponse statusResponse = mock(MonnifyTransactionStatusResponse.class);
        MonnifyTransactionStatusResponse.Body body = mock(MonnifyTransactionStatusResponse.Body.class);

        when(statusResponse.requestSuccessful()).thenReturn(true);
        when(statusResponse.responseBody()).thenReturn(body);
        when(body.paymentStatus()).thenReturn("PENDING");

        doReturn(statusResponse)
                .when(monnifyPaymentService)
                .fetchTransactionStatusFromProvider(txRef);

        ResponseEntity<ApiResponse> response =
                monnifyPaymentService.completeCheckout(txRef);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
//        assertFalse(response.getBody().status());
        // you can also assert message contains "Payment not completed" if you like:
        // assertTrue(response.getBody().message().contains("Unexpected error during checkout completion"));

        verify(paymentTransactionService, never()).resolvePaymentTransaction(anyString());
        verify(paymentTransactionService, never()).updatePaymentAsPaid(any(), any());
        verify(orderService, never()).createOrderFromCart(any(), anyString());
        verify(cartService, never()).clearCart(any());
    }
}
