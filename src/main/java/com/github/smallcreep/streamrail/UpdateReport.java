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
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * Report decorator, return JsonObject only updatable values.
 *
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
final class UpdateReport implements Report {

    /**
     * Origin report.
     */
    private final Report origin;

    /**
     * Ctor.
     * @param origin Origin report
     */
    UpdateReport(final Report origin) {
        this.origin = origin;
    }

    @Override
    public Report range(
        final ZonedDateTime start,
        final ZonedDateTime end
    ) throws IOException {
        return new UpdateReport(this.origin.range(start, end));
    }

    @Override
    public File export() throws IOException, URISyntaxException {
        return this.origin.export();
    }

    @Override
    public JsonObject json() throws IOException {
        final JsonObject full = this.origin.json()
                                           .getJsonObject("customReport");
        return Json.createObjectBuilder()
                   .add(
                       "customReport",
                       Json.createObjectBuilder(
                           full
                       )
                           .remove("id")
                           .remove("createdOn")
                           .remove("createdBy")
                           .remove("generating")
                           .remove("isSystemReport")
                           .remove("largeReport")
                           .remove("lastExecuted")
                           .remove("lastGenerated")
                           .remove("lastModified")
                           .remove("modifiedBy")
                           .remove("org")
                           .remove("reportStatus")
                           .remove("table")
                           .remove("totalRows")
                           .add("status", 0)
                           .build()
                   )
                   .build();
    }
}
