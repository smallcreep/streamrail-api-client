/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017, Ilia Rogozhin (ilia.rogozhin@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.smallcreep.streamrail;

import java.io.File;
import java.io.IOException;
import java.net.HttpRetryException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.JsonObject;

/**
 * Retry request export while don't read file.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class RetryReport implements Report {

    /**
     * Origin Report.
     */
    private final Report origin;

    /**
     * Waiting milliseconds between requests.
     */
    private final long waiting;

    /**
     * Max count to retry requests.
     */
    private final int max;

    /**
     * Request counter.
     */
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Ctor.
     * @param origin Origin Report
     * @checkstyle MagicNumberCheck (3 lines)
     */
    public RetryReport(final Report origin) {
        this(origin, 2000L, 20);
    }

    /**
     * Ctor.
     * @param origin Origin Report
     * @param waiting Waiting milliseconds between requests
     * @param max Max count to retry requests
     */
    public RetryReport(
        final Report origin,
        final long waiting,
        final int max
    ) {
        this.origin = origin;
        this.waiting = waiting;
        this.max = max;
    }

    @Override
    public Report range(
        final ZonedDateTime start,
        final ZonedDateTime end
    ) throws IOException {
        return new RetryReport(
            this.origin.range(start, end),
            this.waiting,
            this.max
        );
    }

    @Override
    public File export() throws IOException {
        while (
            this.counter.getAndSet(this.counter.get() + 1) < this.max) {
            try {
                return this.origin.export();
            } catch (final IOException | URISyntaxException exception) {
                // do nothing
            }
            try {
                Thread.sleep(this.waiting);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
        throw new HttpRetryException(
            "Not found correct request",
            HttpURLConnection.HTTP_NOT_FOUND
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return this.origin.json();
    }

    @Override
    public Report generate() throws IOException {
        return new RetryReport(
            this.origin.generate(),
            this.waiting,
            this.max
        );
    }
}
