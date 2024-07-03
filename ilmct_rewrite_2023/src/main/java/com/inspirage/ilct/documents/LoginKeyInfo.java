package com.inspirage.ilct.documents;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 02-11-2023
 */
@Getter
@Setter
@Document(collection = "login_key_info")
public class LoginKeyInfo extends BaseDoc{
    @Indexed
    private String ipAddress;
    private LocalDateTime requestedOn;
    private String hash;

    public LoginKeyInfo(String ipAddress) {
        super();
        this.ipAddress = ipAddress;
        this.requestedOn = LocalDateTime.now();
        this.hash = RandomStringUtils.randomAlphanumeric(32);
    }
}
