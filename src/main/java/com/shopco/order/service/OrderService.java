package com.shopco.order.service;

import com.shopco.cart.entity.Cart;
import com.shopco.order.entity.Order;

public interface OrderService {
    Order createOrderFromCart(Cart cart, String confirmationCode);
}
