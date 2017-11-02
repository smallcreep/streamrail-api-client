package com.github.smallcreep.streamrail;

/**
 * Custom reports StreamRail.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface Reports {

    /**
     * Get report by id.
     * @param id Id report
     * @return Report
     */
    Report report(String id);

    /**
     * Get origin StreamRail.
     * @return StreamRail
     */
    Streamrail origin();

}
