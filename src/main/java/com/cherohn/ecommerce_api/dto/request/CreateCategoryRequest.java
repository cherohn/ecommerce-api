package com.cherohn.ecommerce_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import jakarta.validation.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {

    @NotBlank(message = "O nome nao pode estar em branco")
    private String name;

    private String description;
}
