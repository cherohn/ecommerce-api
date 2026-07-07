package com.cherohn.ecommerce_api.dto.request;

import com.cherohn.ecommerce_api.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "O status e obrigatorio")
    private OrderStatus status;
}
