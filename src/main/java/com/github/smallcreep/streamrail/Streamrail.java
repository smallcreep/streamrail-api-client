package com.github.smallcreep.streamrail;

import com.jcabi.http.Request;

/**
 * Entrypoint StreamRail API.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface Streamrail {

    /**
     * Base url Cedato.
     */
    String BASE_URL = "https://partners.streamrail.com";

    /**
     * Get origin request.
     * @return Request
     */
    Request request();

}
