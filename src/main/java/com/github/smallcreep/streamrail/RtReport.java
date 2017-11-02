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
    public File export() throws IOException, URISyntaxException {
        final Streamrail streamrail = this.reports
            .origin();
        streamrail
            .request()
            .uri()
            .path(
                "/generate-custom-report/" + this.id
            ).back()
            .method(Request.POST)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK);
        return new RetryReport(
            this,
            this.req
                .uri()
                .set(
                    new URI(
                        streamrail.base()
                    )
                ).path("/data-export/data/custom/" + this.id)
                .back()
        ).export();
    }

    @Override
    public JsonObject json() throws IOException {
        return new RtJson(this.req).fetch();
    }
}
