/*
 *      Copyright (C) 2012-2015 DataStax Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.datastax.driver.core;

/**
 * A channel provides an abstraction on top of the Session API to allow for interceptor support.
 */
public abstract class SessionChannel {
    /**
     * @param action - Action that will be performed.  Actions map to the asyncXxx methods of Session
     * @return Create a new call context for the action.
     */
    public abstract <ReqT, RespT> SessionCall<ReqT, RespT> newCall(SessionAction<ReqT, RespT> action);
}
