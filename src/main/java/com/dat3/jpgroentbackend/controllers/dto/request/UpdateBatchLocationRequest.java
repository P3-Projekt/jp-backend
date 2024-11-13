package com.dat3.jpgroentbackend.controllers.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class UpdateBatchLocationRequest {
    @NotNull
    @Schema(description = "Shelf id's mapped to amount of fields this batch has on that shelf", example = "{\"2\": 4}")
    public Map<Integer,Integer> locations;
}
