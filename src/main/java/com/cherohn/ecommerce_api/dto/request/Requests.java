package com.cherohn.ecommerce_api.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Requests {

    @NotBlank(message = "O nome nao pode estar em branco")
    private String name;

    private String description;

    @NotNull(message = "o preco e obrigatorio")
    @DecimalMin(value = "0.01", message = "o preco deve ser maior que zero")
    private BigDecimal preco;

    @NotNull(message = "A quantidade em estoque e obrigatoria")
    @Min(value = 0, message = "o estoque nao pode ser negativo")
    private Integer stockQuantity;

    @NotNull(message = "A quantidade e obrigatoria")
    private Long category_id;
}
