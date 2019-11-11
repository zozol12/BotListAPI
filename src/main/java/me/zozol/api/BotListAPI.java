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

package me.zozol.api;

import me.zozol.api.http.ListedBot;

public interface BotListAPI {
    Bot getBot(String botId);

    Bot getBot();

    Boolean setStats(int servers, int users);

    class builder{

        private String botId = null;
        private String botToken = null;



        public builder setBotToken(String botToken){
            this.botToken = botToken;
            return this;
        }

        public builder setInfo(String botToken, String botId){
            this.botToken = botToken;
            this.botId = botId;
            return this;
        }

        public builder setBotId(String botId) {
            this.botId = botId;
            return this;
        }

        public BotListAPI build() {
            if(botToken == null||botId == null)
                throw new IllegalArgumentException("Nie podano id lub tokenu!");

            return new ListedBot(botToken, botId);
        }

    }

}
