package com.github.smallcreep.streamrail;

import com.jcabi.http.Request;
import com.jcabi.http.response.RestResponse;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.json.Json;
import javax.json.JsonObject;

/**
 * Custom report StreamRail.
 * @author Ilia Rogozhin (ilia.rogozhin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
final class RtReport implements Report {

    /**
     * Origin request.
     */
    private final Reports reports;

    /**
     * Request.
     */
    private final Request req;

    /**
     * Report id.
     */
    private final String id;

    /**
     * Ctor.
     * @param reports Reports
     * @param req Request
     * @param id Report id
     */
    RtReport(
        final Reports reports,
        final Request req,
        final String id
    ) {
        this.reports = reports;
        this.req = req.uri()
                      .path("/" + id)
                      .back();
        this.id = id;
    }

    @Override
    public Report range(
        final ZonedDateTime start,
        final ZonedDateTime end
    ) throws IOException {
        final JsonObject current = new UpdateReport(
            this
        )
            .json()
            .getJsonObject(
                "customReport"
            );
        final ZoneId zone = ZoneId.of(
            current.getString("timeZone")
        );
        this.req
            .method(Request.PUT)
            .body()
            .set(
                Json.createObjectBuilder()
                    .add(
                        "customReport",
                        Json.createObjectBuilder(
                            current
                        )
                            .add(
                                "startDate",
                                start.withZoneSameInstant(
                                    zone
                                )
                                     .toLocalDate()
                                     .toString()
                            )
                            .add(
                                "endDate",
                                end.withZoneSameInstant(
                                    zone
                                )
                                   .toLocalDate()
                                   .toString()
                            )
                            .build()
                    ).build()
            )
            .back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        return this;
    }

    @Override
    public Report generate() throws IOException {
        this.reports
            .origin()
            .request()
            .uri()
            .path(
                "/generate-custom-report/" + this.id
            ).back()
            .method(Request.POST)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        return this;
    }

    @Override
    public File export() throws IOException, URISyntaxException {
        if (this.generated()) {
            return new ExportReport(
                this,
                this.req
                    .uri()
                    .set(
                        new URI(
                            this.reports.origin().base()
                        )
                    ).path("/data-export/data/custom/" + this.id)
                    .back()
            ).export();
        }
        throw new IOException("Report didn't complete generate");
    }

    /**
     * Generated report checker.
     * @return True if report was generated
     * @throws IOException If fail
     */
    private boolean generated() throws IOException {
        return "DONE".equals(
            this.json()
                .getJsonObject("customReport")
                .getString("reportStatus")
        );
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.req).fetch();
    }
}
