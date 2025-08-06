package com.informatorio.info_market.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoRequestDTO {

    @NotBlank(message = "La marca no puede estar vacía")
    private String marca;

    @NotNull(message = "El porcentaje de descuento no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El descuento debe ser mayor que 0")
    private Double porcentajeDescuento;
}