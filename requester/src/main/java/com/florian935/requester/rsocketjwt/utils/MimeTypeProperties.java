package com.florian935.requester.rsocketjwt.utils;

import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

public abstract class MimeTypeProperties {

    public final static String BEARER_MIMETYPE_VALUE = "message/x.rsocket.authentication.bearer.v0";
    public final static MimeType BEARER_MIMETYPE = MimeTypeUtils.parseMimeType(BEARER_MIMETYPE_VALUE);

}
