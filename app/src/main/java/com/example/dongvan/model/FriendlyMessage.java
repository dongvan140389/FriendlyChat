/**
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dongvan.model;

public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String idSend;
    private String idReceiver;


    public FriendlyMessage() {
    }

    public FriendlyMessage(String text, String name, String photoUrl, String idSend, String idReceiver) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
        this.idSend = idSend;
        this.idReceiver = idReceiver;
    }

    public String getIdSend() {
        return idSend;
    }

    public void setIdSend(String idSend) {
        this.idSend = idSend;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
