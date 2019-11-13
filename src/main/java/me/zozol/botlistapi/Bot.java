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

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Bot {
    private String id;
    @SerializedName("name")
    private String tag;
    @SerializedName("avatar")
    private String avatarUrl;

    @SerializedName("owner")
    private String ownerName;
    private String ownerId;

    private List<String> subowners;
    private List<String> tags;
    private String library;
    private boolean cert;

    private int votes;
    private String servers;
    private String users;


    public String getUsers() {
        return users;
    }

    public String getServers() {
        return servers;
    }

    public int getVotes() {
        return votes;
    }

    public boolean isCert() {
        return cert;
    }

    public String getLibrary() {
        return library;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getSubowners() {
        return subowners;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getTag() {
        return tag;
    }

    public String getId() {
        return id;
    }
}
