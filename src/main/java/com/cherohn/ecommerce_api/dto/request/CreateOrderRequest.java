package com.cherohn.ecommerce_api.dto.request;

import com.cherohn.ecommerce_api.model.OrderItem;
import jakarta.validation.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private List<OrderItem> items;
}
