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

package me.zozol.botlistapi.http;

import com.fatboyindustrial.gsonjavatime.OffsetDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.zozol.botlistapi.Bot;
import me.zozol.botlistapi.BotListAPI;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

public class ListedBot implements BotListAPI {

    public static final Logger logger
            = LoggerFactory.getLogger(BotListAPI.class);

    private HttpPost post;

    private final Gson gson;

    private final String botToken, botId;

    public ListedBot(String botToken, String botId) {
        logger.info("inicjalizacja...");
        this.botToken = botToken;
        this.botId = botId;

        post=new HttpPost("https://dbl.kresmc.pl/api/stats/"+botId);

        this.gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeConverter())
                .create();

        logger.info("Pomyślna inicjalizacja.");
    }

    @Override
    public Bot getBot(String botId) {
        try {
            return gson.fromJson(new BufferedReader(new InputStreamReader(new URL("https://dbl.kresmc.pl/api/lista/"+botId).openStream(), StandardCharsets.UTF_8))
                    , Bot.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Bot getBot() {
        try {
            return gson.fromJson(new BufferedReader(new InputStreamReader(new URL("https://dbl.kresmc.pl/api/lista/"+botId).openStream(), StandardCharsets.UTF_8))
                    , Bot.class);
        } catch (IOException e) {
            return null;
        }
    }

    private Boolean executePost( Integer servers,  Integer users) throws IOException {
        post.addHeader("Authorization", botToken);
        post.addHeader("Content-Type", "application/json");

        String json = "{" +
                "\"servers\":" + servers + "," +
                "\"users\":" + users +
                "}";
        post.setEntity(new StringEntity(json));
        try (CloseableHttpClient httpClient = HttpClients.createDefault()){
             httpClient.execute(post);
             post=new HttpPost("https://dbl.kresmc.pl/api/stats/"+botId);
             return true;
        }catch (Exception e){
            e.printStackTrace();
            post=new HttpPost("https://dbl.kresmc.pl/api/stats/"+botId);
            return false;
        }
    }





    @Override
    public Boolean setStats(int servers, int users) {
        try {
            return executePost(servers,users);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
