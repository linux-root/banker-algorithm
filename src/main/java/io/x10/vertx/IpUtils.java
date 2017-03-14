package io.x10.vertx;

import io.vertx.core.http.HttpServerRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;

import java.util.StringTokenizer;

/**
 * Created by dangnh@eway.vn on 7/18/2016.
 * Not compatible with IPv6
 */
public class IpUtils {

    /**
     * Get IP with default header=X-Forwarded-For
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServerRequest request) {
        return IpUtils.getIp(request, "X-Forwarded-For");
    }

    public static String getIp(HttpServerRequest request, String header) {
        String ip = IpUtils.getIpFromHeader(request, header, true);
        if (StringUtils.isNotBlank(ip)) {
            return ip;
        }
        return request.remoteAddress().host();
    }

    public static String getIpFromHeader(HttpServerRequest request, String header, boolean allowPrivate) {
        String value = request.getHeader(header);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreTokens()) {
            String ip = tokenizer.nextToken().trim();
            if (!InetAddressValidator.getInstance().isValid(ip)) {
                continue;
            }
            if (!allowPrivate && IpUtils.isIPv4Private(ip)) {
                continue;
            }
            return ip;
        }

        return null;
    }


    public static boolean isIPv4Private(String ip) {
        String[] octets = ip.split("\\.");
        long longIp = (Long.parseLong(octets[0]) << 24) + (Integer.parseInt(octets[1]) << 16) + (Integer.parseInt(octets[2]) << 8) + Integer.parseInt(octets[3]);
        return (167772160L <= longIp && longIp <= 184549375L)   //10.0.0.0 ~ 10.255.255.255
                || (2886729728L <= longIp && longIp <= 2887778303L) //172.16.0.0 ~ 172.31.255.255
                || (3232235520L <= longIp && longIp <= 3232301055L);  //192.168.0.0 ~ 192.168.255.255
    }

}
