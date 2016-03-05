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
package com.datastax.driver.core.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.ForwardingSessionCall;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.SessionAction;
import com.datastax.driver.core.SessionCall;
import com.datastax.driver.core.SessionChannel;
import com.datastax.driver.core.SessionInterceptor;
import com.google.common.util.concurrent.FutureCallback;

final public class LoggingInterceptor implements SessionInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    @Override
    public <ReqT, RespT> SessionCall<ReqT, RespT> interceptCall(final SessionAction<ReqT, RespT> action, final SessionChannel next) {
        return new ForwardingSessionCall<ReqT, RespT>(next.newCall(action)) {
            @Override
            public void call(final FutureCallback<RespT> listener, Session session, final ReqT request) {
                LOG.debug("Starting {}", request);
                delegate.call(new FutureCallback<RespT>() {
                    @Override
                    public void onSuccess(RespT result) {
                        LOG.debug("Success {}", request);
                        listener.onSuccess(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        LOG.warn("Failure {}. {} ", request, t.getMessage());
                        listener.onFailure(t);
                    }
                }, session, request);
            }
        };
    }
}
