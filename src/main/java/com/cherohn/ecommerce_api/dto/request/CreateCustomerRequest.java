package com.cherohn.ecommerce_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "O nome nao pode estar em branco")
    private String name;

    @NotBlank(message = "O email nao pode estar em branco")
    private String email;

    private String phone;
}
