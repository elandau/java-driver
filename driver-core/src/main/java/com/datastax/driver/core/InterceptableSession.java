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
 * Extension to {@link com.datastax.driver.core.Session} adding support for operation interceptors
 * through which various functionality such as fallback, logging and tracing can be added.  Every
 * call to {@link InterceptableSession#intercept(SessionInterceptor)} creates a new 
 * InterceptableSession so it is possible to add interceptors for the entire session or just for
 * a single call.
 * 
 * Example usage,
 * 
 * {@code
 * InterceptableSession session = InterceptableSession.create(cluster.connect())
 *      .intercept(new FitInterceptor());      
 *      .intercept(new LoggingInterceptor())    <-- This will get called first
 * }
 * 
 * Note that interceptors are executed in reverse order so that the interceptor at the 
 * bottom of the chain is executed first.
 * 
 * Example per request interceptor
 * 
 * {@code
 * session
 *      .intercept(new FallbackInterceptor(someFallbackObject))
 *      .execute(query);
 * }
 * 
 * All blocking calls are redirected to the async calls and just call .get() on the future so that
 * blocking calls will still invoke the interceptor.
 * 
 */
public interface InterceptableSession extends Session {
    /**
     * Add an interceptor and return a new InterceptableSession.  Interceptors are invoked in 
     * backwards order with the last interceptor added being invoked first.
     * 
     * @param interceptor
     */
    InterceptableSession intercept(final SessionInterceptor interceptor);
}
