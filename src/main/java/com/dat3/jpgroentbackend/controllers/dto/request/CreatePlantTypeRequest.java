package com.dat3.jpgroentbackend.controllers.dto.request;

import com.dat3.jpgroentbackend.model.PlantType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// This class is used to represent a CreatePlantTypeRequest
public class CreatePlantTypeRequest {
    @NotNull // NotNull is used to validate that the value is not null
    public String name;

    @NotNull
    @Min(0) // Min is used to validate that the value is greater than or equal to 0
    // Schema is used to provide metadata about the field
    @Schema(description = "The number of days it takes for the plant to pre-germinate")
    public Integer preGerminationDays;

    @NotNull
    @Min(0)
    @Schema(description = "The number of days it takes for the plant to be ready to harvest")
    public Integer growthTimeDays;

    @NotNull
    @Schema(description = "The preferred position of the plant", example = "1,2")
    public PlantType.PreferredPosition preferredPosition;

    @NotNull
    @Schema(description = "A list of days after the creation date, where the plant should be watered", example = "[1,5,7]")
    public int[] wateringSchedule;
}
