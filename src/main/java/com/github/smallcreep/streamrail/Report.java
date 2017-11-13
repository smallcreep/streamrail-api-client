package com.github.smallcreep.streamrail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;

/**
 * Custom report StreamRail.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public interface Report extends JsonReadable {

    /**
     * Set range report.
     * @param start Start date
     * @param end End date
     * @return Report
     * @throws IOException If fails
     */
    Report range(ZonedDateTime start, ZonedDateTime end) throws IOException;

    /**
     * Generate report.
     * @return Report
     * @throws IOException If fails
     */
    Report generate() throws IOException;

    /**
     * Export custom StreamRail report.
     * @return Report file
     * @throws IOException If fails
     * @throws URISyntaxException If base url doesn't URI
     */
    File export() throws IOException, URISyntaxException;
}
