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
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * SessionChannel that executes the actions.  A single SessionChannelImpl will be at the end 
 * of the intreceptor chain.
 */
public final class SessionChannelImpl extends SessionChannel {
    @Override
    public <ReqT, RespT> SessionCall<ReqT, RespT> newCall(final SessionAction<ReqT, RespT> action) {
        return new SessionCall<ReqT, RespT>() {
            private ListenableFuture<RespT> future;

            @Override
            public void cancel(boolean mayInterruptIfRunning) {
                if (this.future != null) {
                    this.future.cancel(mayInterruptIfRunning);
                }
            }

            @Override
            public void call(final FutureCallback<RespT> listener, Session session, ReqT request) {
                try {
                    this.future = action.send(session, request);
                    Futures.addCallback(future, new FutureCallback<RespT>() {
                        @Override
                        public void onSuccess(RespT result) {
                            listener.onSuccess(result);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            listener.onFailure(t);
                        }
                    });
                } catch (Exception e) {
                    listener.onFailure(e);
                }
            }
        };
    }
}
