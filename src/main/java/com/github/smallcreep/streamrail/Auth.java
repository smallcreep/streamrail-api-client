package com.github.smallcreep.streamrail;

import java.io.IOException;

/**
 * Authorization in StreamRail.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface Auth {

    /**
     * Get Cedato with authorization.
     * @return Cedato
     * @throws IOException If fails
     */
    Streamrail streamrail() throws IOException;

}
