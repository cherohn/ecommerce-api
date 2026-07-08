package com.cherohn.ecommerce_api.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "O cliente e obrigatorio")
    private Long customerId;

    @NotEmpty(message = "O pedido deve ter ao menos um item")
    @Valid
    private List<OrderItemRequest> items;
}
