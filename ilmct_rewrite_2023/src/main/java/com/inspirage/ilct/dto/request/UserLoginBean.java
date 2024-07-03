package com.inspirage.ilct.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 01-11-2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserLoginBean {
    private String userId;
    private String password;


}
