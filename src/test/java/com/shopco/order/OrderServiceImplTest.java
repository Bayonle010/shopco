package com.shopco.order;

import com.shopco.core.exception.ResourceNotFoundException;
import com.shopco.core.response.ApiResponse;
import com.shopco.order.dto.request.CompleteOrderRequest;
import com.shopco.order.entity.Order;
import com.shopco.order.enums.OrderStatus;
import com.shopco.order.repository.OrderItemRepository;
import com.shopco.order.repository.OrderRepository;
import com.shopco.order.service.impl.OrderServiceImpl;
import com.shopco.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID orderId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
    }


    @Test
    void completeOrderAsAdmin_shouldCompleteOrder_whenValidAndPending() {
        // given
        doNothing().when(userService).verifyAdmin(authentication);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setConfirmationCode("1234");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        CompleteOrderRequest request = new CompleteOrderRequest("1234");

        // when
        ResponseEntity<ApiResponse> response =
                orderService.completeOrderAsAdmin(orderId, request, authentication);

        // then
        assertEquals(200, response.getStatusCode().value());

        // order should now be completed
        assertEquals(OrderStatus.COMPLETED, order.getOrderStatus());
        assertNotNull(order.getCompletedAt());

        // verify repo interactions
        verify(userService).verifyAdmin(authentication);
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(order);
    }


    @Test
    void completeOrderAsAdmin_shouldThrow_whenOrderNotFound() {
        // given
        doNothing().when(userService).verifyAdmin(authentication);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        CompleteOrderRequest request = new CompleteOrderRequest("1234");

        // when / then
        assertThrows(ResourceNotFoundException.class,
                () -> orderService.completeOrderAsAdmin(orderId, request, authentication));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void completeOrderAsAdmin_shouldThrow_whenOrderAlreadyCompleted() {
        // given
        doNothing().when(userService).verifyAdmin(authentication);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setConfirmationCode("1234");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        CompleteOrderRequest request = new CompleteOrderRequest("1234");

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.completeOrderAsAdmin(orderId, request, authentication));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void completeOrderAsAdmin_shouldThrow_whenConfirmationCodeIsWrong() {
        // given
        doNothing().when(userService).verifyAdmin(authentication);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setConfirmationCode("4321"); // different from request

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        CompleteOrderRequest request = new CompleteOrderRequest("1234");

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.completeOrderAsAdmin(orderId, request, authentication));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void completeOrderAsAdmin_shouldThrow_whenConfirmationCodeIsBlank() {
        // given
        doNothing().when(userService).verifyAdmin(authentication);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setConfirmationCode("1234");

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        CompleteOrderRequest request = new CompleteOrderRequest("   "); // blank

        // when / then
        assertThrows(IllegalArgumentException.class,
                () -> orderService.completeOrderAsAdmin(orderId, request, authentication));

        verify(orderRepository).findById(orderId);
        verify(orderRepository, never()).save(any());
    }


}
