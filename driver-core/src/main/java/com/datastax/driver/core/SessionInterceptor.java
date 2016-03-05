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

import com.datastax.driver.core.Session;

/**
 * Interface for intercepting calls to {@link Session}
 */
public interface SessionInterceptor {
    <ReqT, RespT> SessionCall<ReqT, RespT> interceptCall(SessionAction<ReqT, RespT> action, SessionChannel next);
    
    /**
     * Factory for creating an interceptor that is configured for the provided session.  A cluster
     * will likely keep list of ordered Factory objects that are invoked in sequence for each session
     * that is created.
     */
    public static interface Factory {
        SessionInterceptor create(Session session);
    }

}
