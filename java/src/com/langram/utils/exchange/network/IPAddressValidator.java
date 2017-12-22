package com.langram.utils.exchange.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressValidator {

    private Pattern pattern;

    private static final String IPADDRESS_PATTERN =
                    "2(?:2[4-9]|3\\d)(?:\\.(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]\\d?|0)){3}";

    public IPAddressValidator() {
        pattern = Pattern.compile(IPADDRESS_PATTERN);
    }

    /**
     * Validate ip address with regular expression
     *
     * @param ip ip address for validation
     * @return true valid multicast ip address, false invalid ip address
     */
    public boolean validate(final String ip) {
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}