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

package me.zozol.botlistapi;

import me.zozol.botlistapi.http.WebHook.WebhookServer;
import me.zozol.botlistapi.http.ListedBot;
import me.zozol.botlistapi.http.WebHook.WebhookListener;

import java.util.HashSet;
import java.util.Set;

public interface BotListAPI {
    Bot getBot(String botId);

    Bot getBot();

    Boolean setStats(int servers, int users);

    class builder{

        private String botId = null;
        private String botToken = null;
        private String webhookUrl = null;
        private Integer webhookPort = null;
        private String webhookAuth = null;
        private Set<WebhookListener> listeners= new HashSet<>();



        public builder setBotToken(String botToken){
            this.botToken = botToken;
            return this;
        }

        public builder setInfo(String botToken, String botId){
            this.botToken = botToken;
            this.botId = botId;
            return this;
        }

        public builder registerListener(WebhookListener listener){
            listeners.add(listener);
            return this;
        }

        public builder setBotId(String botId) {
            this.botId = botId;
            return this;
        }

        public builder setWebhookUrl(String webhookUrl,int port) {
            this.webhookUrl = webhookUrl;
            this.webhookPort= port;
            return this;
        }

        public builder setWebhookAuth(String webhookAuth) {
            this.webhookAuth = webhookAuth;
            return this;
        }

        public BotListAPI build() {
            if(botToken == null||botId == null)
                throw new IllegalArgumentException("Nie podano id lub tokenu!");
            if(!listeners.isEmpty()) {
                WebhookServer server = new WebhookServer.Builder().setHost(webhookUrl).setPort(webhookPort).setAuthorization(webhookAuth).setBotId(botId).build();
                server.registerListeners(listeners);
                server.start();
            }
            return new ListedBot(botToken, botId);
        }

    }

}
