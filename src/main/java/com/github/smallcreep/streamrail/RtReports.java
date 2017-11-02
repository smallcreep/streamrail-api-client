package com.github.smallcreep.streamrail;

import com.jcabi.http.Request;

/**
 * Custom reports StreamRail.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
final class RtReports implements Reports {

    /**
     * Origin Request.
     */
    private final Request origin;

    /**
     * Request.
     */
    private final Request req;

    /**
     * Origin StreamRail entrypoint.
     */
    private final Streamrail streamrail;

    /**
     * Ctor.
     * @param origin Origin StreamRail entrypoint
     */
    RtReports(final Streamrail origin) {
        this(origin.request(), origin);
    }

    /**
     * Ctor.
     * @param req Request
     * @param origin Origin StreamRail entrypoint
     */
    RtReports(final Request req, final Streamrail origin) {
        this.origin = req;
        this.req = req.uri()
                      .path("/custom-reports")
                      .back();
        this.streamrail = origin;
    }

    @Override
    public Report report(final String id) {
        return new RtReport(this, this.req, id);
    }

    @Override
    public Streamrail origin() {
        return this.streamrail;
    }
}
