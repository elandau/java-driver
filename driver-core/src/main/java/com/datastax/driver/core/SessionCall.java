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

import com.google.common.util.concurrent.FutureCallback;

/**
 * An instance of a call to the Session.
 * 
 * @param <ReqT>
 * @param <RespT>
 */
public interface SessionCall<ReqT, RespT> {
    
    /**
     * Execute the call and pass the result on the provided Listener.
     * 
     * @param callback
     * @param session
     * @param request
     */
    void call(FutureCallback<RespT> callback, Session session, ReqT request);
    
    /**
     * Cancel a running call.
     * 
     * @param mayInterruptIfRunning
     */
    void cancel(boolean mayInterruptIfRunning);
}
