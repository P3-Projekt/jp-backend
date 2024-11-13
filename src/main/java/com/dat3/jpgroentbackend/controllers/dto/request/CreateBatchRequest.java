package com.dat3.jpgroentbackend.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateBatchRequest {
    @NotNull
    public String plantTypeId;
    @NotNull
    public String trayTypeId;
    @NotNull
    public String createdByUsername;
    @NotNull
    @Min(0)
    public Integer amount;
}
