package com.shopco.cart.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartSummaryResponse {
    private BigDecimal subtotal;
    private BigDecimal discountTotal;
    private BigDecimal shipping;
    private BigDecimal tax;
    private BigDecimal total;


}
