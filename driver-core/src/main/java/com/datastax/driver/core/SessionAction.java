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
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Encapsulate a call to one of {@link Session}'s asyncXxx methods
 * 
 * @param <ReqT>
 * @param <RespT>
 */
public interface SessionAction<ReqT, RespT> {
    /**
     * Perform the operation on the given Session
     * @param session Session instance on which the action is performed
     * @param request Input to the action.
     * @return Future for the operation.
     */
    ListenableFuture<RespT> send(Session session, ReqT request);
}
