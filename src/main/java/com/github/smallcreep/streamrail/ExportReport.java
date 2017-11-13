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

import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import org.cactoos.io.InputOf;
import org.cactoos.io.LengthOf;
import org.cactoos.io.OutputTo;
import org.cactoos.io.TeeInput;

/**
 * Exported report to file.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
final class ExportReport implements Report {

    /**
     * Origin Report.
     */
    private final Report origin;

    /**
     * Request for get url report.
     */
    private final Request req;

    /**
     * Ctor.
     * @param origin Origin Report
     * @param req Request for get url report
     */
    ExportReport(final Report origin, final Request req) {
        this.origin = origin;
        this.req = req;
    }

    @Override
    public Report range(
        final ZonedDateTime start,
        final ZonedDateTime end) throws IOException {
        return new ExportReport(
            this.origin.range(start, end),
            this.req
        );
    }

    @Override
    public Report generate() throws IOException {
        return new ExportReport(
            this.origin.generate(),
            this.req
        );
    }

    @Override
    public File export() throws IOException, URISyntaxException {
        final File file = File.createTempFile("report", ".zip");
        new LengthOf(
            new TeeInput(
                new InputOf(
                    new URI(
                        this.req
                            .fetch()
                            .as(RestResponse.class)
                            .assertStatus(HttpURLConnection.HTTP_MOVED_TEMP)
                            .headers()
                            .get(HttpHeaders.LOCATION)
                            .get(0)
                    )
                ),
                new OutputTo(
                    file
                )
            )
        ).value();
        return file;
    }

    @Override
    public JsonObject json() throws IOException {
        return this.origin.json();
    }
}
