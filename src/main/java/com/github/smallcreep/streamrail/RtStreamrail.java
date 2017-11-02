package com.github.smallcreep.streamrail;

import com.jcabi.http.Request;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.manifests.Manifests;
import javax.ws.rs.core.HttpHeaders;

/**
 * StreamRail API entrypoint.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class RtStreamrail implements Streamrail {

    /**
     * Version of us.
     */
    private static final String USER_AGENT = String.format(
        "streamrail-api-client %s %s %s",
        Manifests.read("StreamRail-Version"),
        Manifests.read("StreamRail-Build"),
        Manifests.read("StreamRail-Date")
    );

    /**
     * Basic request.
     */
    private final Request req;

    /**
     * Base url.
     */
    private final String base;

    /**
     * Ctor.
     * @param token StreamRail token
     */
    public RtStreamrail(final String token) {
        this(Streamrail.BASE_URL, token);
    }

    /**
     * Ctor.
     * @param url Base url StreamRail
     * @param token StreamRail token
     */
    public RtStreamrail(final String url, final String token) {
        this(
            url,
            new ApacheRequest(url)
                .uri()
                .path("/api/v2")
                .back()
                .header(HttpHeaders.ACCEPT, "application/json")
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .header(HttpHeaders.AUTHORIZATION, token)
                .method(Request.GET)
        );
    }

    /**
     * Ctor.
     * @param url Base url StreamRail
     * @param req Basic request
     */
    private RtStreamrail(final String url, final Request req) {
        this.base = url;
        this.req = req;
    }

    @Override
    public Request request() {
        return this.req;
    }

    @Override
    public Reports reports() {
        return new RtReports(this);
    }

    @Override
    public String base() {
        return this.base;
    }
}
