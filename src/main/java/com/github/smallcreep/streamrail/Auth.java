package com.github.smallcreep.streamrail;

import com.jcabi.http.Request;
import com.jcabi.http.request.ApacheRequest;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import org.cactoos.text.FormattedText;

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

    /**
     * Authorization by username and password in StreamRail.
     */
    final class Simple implements Auth {

        /**
         * Version of us.
         */
        private static final String USER_AGENT = String.format(
            "streamrail-api-client %s %s %s",
            Manifests.read("StreamRail-Version"),
            Manifests.read("StreamRail-Build"),
            Manifests.read("StreamRail-Date")
        );

        /**
         * Base url.
         */
        private final String url;

        /**
         * Basic request.
         */
        private final Request req;

        /**
         * Username.
         */
        private final String user;

        /**
         * Password.
         */
        private final String password;

        /**
         * Ctor.
         * @param user Username
         * @param password Password
         */
        Simple(final String user, final String password) {
            this(Streamrail.BASE_URL, user, password);
        }

        /**
         * Ctor.
         * @param url Base StreamRail url
         * @param user Username
         * @param password Password
         */
        Simple(final String url, final String user, final String password) {
            this(
                url,
                new ApacheRequest(url)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT),
                user,
                password
            );
        }

        /**
         * Ctor.
         * @param url Base StreamRail url
         * @param req Basic request
         * @param user Username
         * @param password Password
         * @checkstyle ParameterNumberCheck (6 lines)
         */
        Simple(
            final String url,
            final Request req,
            final String user,
            final String password
        ) {
            this.url = url;
            this.req = req;
            this.user = user;
            this.password = password;
        }

        @Override
        public Streamrail streamrail() throws IOException {
            final JsonObject auth = new RtJson(
                this.req
                    .header(HttpHeaders.ACCEPT, "application/json")
                    .header(
                        HttpHeaders.CONTENT_TYPE,
                        "application/x-www-form-urlencoded"
                    )
                    .uri()
                    .path("/api/v2/login")
                    .back()
                    .method(Request.POST)
                    .body()
                    .formParam("username", this.user)
                    .formParam("password", this.password)
                    .back()
            ).fetch();
            return new RtStreamrail(
                this.url,
                new FormattedText(
                    "%s %s",
                    auth.getString("token_type"),
                    auth.getString("access_token")
                ).asString()
            );
        }
    }
}
