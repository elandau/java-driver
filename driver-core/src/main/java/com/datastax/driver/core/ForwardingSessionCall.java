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
 * Base for most {@link SessionCall} instances normally created in a {@link SessionInterceptor}.
 *
 * @param <ReqT>
 * @param <RespT>
 */
public abstract class ForwardingSessionCall<ReqT, RespT> implements SessionCall<ReqT, RespT> {

    protected final SessionCall<ReqT, RespT> delegate;
    
    public ForwardingSessionCall(SessionCall<ReqT, RespT> delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void call(FutureCallback<RespT> listener, Session session, ReqT request) {
        delegate.call(listener, session, request);
    }

    @Override
    public void cancel(boolean mayInterruptIfRunning) {
        delegate.cancel(mayInterruptIfRunning);
    }

}
