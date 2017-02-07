/*
    Copyright 2017 Wira Mulia

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package org.osumercury.badgemaker;

/**
 *
 * @author wira
 */
public class Progress {
    private final ProgressCallback callback;
    private ProgressCallback completeCallback;

    public Progress(ProgressCallback c) {
        this.callback = c;
    }

    public void update() {
        callback.callback(this);
    }
    
    public void complete() {
        done = true;
        if(completeCallback != null) {
            completeCallback.callback(this);
        }
    }
    
    public void setCompletedCallback(ProgressCallback c) {
        completeCallback = c;
    }

    public volatile String text = "";
    public volatile float percent = 0.0f;
    public volatile boolean cancel = false;
    public volatile boolean done = false;
}
