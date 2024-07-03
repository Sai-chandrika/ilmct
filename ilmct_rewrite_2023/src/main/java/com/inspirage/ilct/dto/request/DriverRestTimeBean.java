package com.inspirage.ilct.dto.request;

import com.inspirage.ilct.dto.ApiResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DriverRestTimeBean {
    private String id;

    @NotNull(message = "Country is required")
    private String country;

    @NotNull(message = "Drive Time Hours is required")
    private Integer driveHrs;
    @NotNull(message = "Drive Time Mins is required")
    private Integer driveMins;
    @NotNull(message = "Rest Time Hours is required")
    private Integer restHrs;
    @NotNull(message = "Rest Time Mins is required")
    private Integer restMins;

    public ApiResponse validate(DriverRestTimeBean bean) {
        return new ApiResponse(HttpStatus.OK, "Success");
    }


}
