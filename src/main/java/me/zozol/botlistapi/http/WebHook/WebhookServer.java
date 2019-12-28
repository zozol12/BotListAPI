/*
 * Copyright 2019 Mikołaj 'zozol' Szmalc
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package me.zozol.botlistapi.http.WebHook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.undertow.Undertow;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import me.zozol.botlistapi.http.ListedBot;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class WebhookServer {
    public static class Builder {
        private String authorization = null;
        private String botId = null;
        private String host = "localhost";
        private int port = 8080;
        private boolean silent = false;

        public Builder setBotId(String botId) {
            this.botId = botId;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setAuthorization(String authorization) {
            this.authorization = authorization;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public Builder setSilent(boolean silent) {
            this.silent = silent;
            return this;
        }

        public WebhookServer build() {
            if (botId == null)
                throw new NullPointerException("id bota nieprawidłowe!");
            return new WebhookServer(authorization, host, port, silent);
        }
    }

    private Undertow server;

    private Set<WebhookListener> webhookListeners;
    private Logger logger;

    public WebhookServer(String authorization, String host, int port, boolean silent) {
        webhookListeners = new HashSet<>();
        logger = ListedBot.logger;
        server = Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(exchange -> {
                    if (!silent)
                        logger.info("[Webserver > dblista.pl] Przetwarzam żądanie: " + exchange.getRequestURL());
                    HeaderMap headerValues = exchange.getRequestHeaders();
                    try {
                        AtomicBoolean allowed = new AtomicBoolean(false);

                        if (authorization == null) {
                            allowed.set(true);
                        } else {
                            headerValues.forEach(value -> value.forEach(s -> {
                                if (value.getHeaderName().toString().equals("Authorization") && s.equals(authorization))
                                    allowed.set(true);
                            }));
                        }

                        if (!allowed.get()) {
                            logger.warn("[Webserver > dblista.pl] Problem z autoryzacją");
                            exchange.getResponseHeaders().put(Headers.STATUS, 400);
                            return;
                        }

                        exchange.getRequestReceiver().receiveFullString((httpServerExchange, s) -> {
                            if (!silent) logger.info("[Webserver > dblista.pl] Przetwarzanie żądania...");
                            JsonElement element = new JsonParser().parse(s);

                            JsonObject object = element.getAsJsonObject();

                            if (object.get("type").toString().equals("vote")) {
                                if (!silent) logger.info("[Webserver > dblista.pl] żądanie nie jest głosem...");
                                return;
                            }

                            String user = object.get("user").getAsString();
                            String bot = object.get("bot").getAsString();

                            callListeners(user, bot);

                            if (!silent)
                                logger.info("[Webserver > dblista.pl] Żądanie przetworzone");
                            exchange.getResponseHeaders().put(Headers.STATUS, 200);
                        });
                    } catch (Exception ignored) {
                        if (!silent)
                            logger.warn("[Webserver > dblista.pl] Nie można obsłużyć żądania");
                        exchange.getResponseHeaders().put(Headers.STATUS, 400);
                    }
                }).build();
    }

    private void callListeners(String userId, String botId) {
        webhookListeners.forEach(listener -> listener.onUserVote(userId, botId));
    }

    public void registerListeners(Set<WebhookListener> listeners) {
        webhookListeners.addAll(listeners);
    }

    public void start() {
        server.start();
    }
}
