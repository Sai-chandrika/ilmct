package com.inspirage.ilct.dto.bean;

import lombok.Data;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 02-11-2023
 */
@Data
public class RequestBean {
    public String url ;

    public String IPProxyClientIP;

    public String ProxyClientIP;

    public String WLProxyClientIP;

    public String HTTPCLIENTIP;

    public String HTTP_X_FORWARDEDFOR;

    public String ipRemoteAddress;

    public String userAgent ;

    public Integer rawOffset;

}
