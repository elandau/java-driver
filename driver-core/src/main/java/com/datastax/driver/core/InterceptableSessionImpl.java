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

import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * @see InterceptableSession
 */
public final class InterceptableSessionImpl implements InterceptableSession {
    private final Session session;
    private final SessionChannel channel;
    
    private static final SessionAction<RegularStatement, PreparedStatement> ACTION_PREPARE_STATEMENT =
        new SessionAction<RegularStatement, PreparedStatement>() {
            @Override
            public ListenableFuture<PreparedStatement> send(final Session session, final RegularStatement statement) {
                return session.prepareAsync(statement);
            }
        };
    
    private static final SessionAction<Statement, ResultSet> ACTION_STATEMENT_QUERY =
        new SessionAction<Statement, ResultSet>() {
            @Override
            public ListenableFuture<ResultSet> send(final Session session, final Statement statement) {
                return session.executeAsync(statement);
            }
        };

    public static InterceptableSessionImpl create(Session session) {
        return new InterceptableSessionImpl(session);
    }
    
    public InterceptableSessionImpl(Session session) {
        this.session = session;
        this.channel = new SessionChannelImpl();
    }
    
    private InterceptableSessionImpl(Session session, SessionChannel channel) {
        this.session = session;
        this.channel = channel;
    }
    
    @Override
    public InterceptableSession intercept(final SessionInterceptor interceptor) {
        return new InterceptableSessionImpl(session, new InterceptingSessionChannel(channel, interceptor));
    }
    
    @Override
    public String getLoggedKeyspace() {
        return session.getLoggedKeyspace();
    }

    @Override
    public Session init() {
        return session.init();
    }

    @Override
    public ListenableFuture<Session> initAsync() {
        return session.initAsync();
    }

    @Override
    public ResultSet execute(String query) {
        try {
            return Uninterruptibles.getUninterruptibly(executeAsync(query));
        } catch (ExecutionException e) {
            throw DriverThrowables.propagateCause(e);
        }
    }

    @Override
    public ResultSet execute(String query, Object... values) {
        try {
            return Uninterruptibles.getUninterruptibly(executeAsync(query, values));
        } catch (ExecutionException e) {
            throw DriverThrowables.propagateCause(e);
        }
    }

    @Override
    public ResultSet execute(Statement statement) {
        try {
            return Uninterruptibles.getUninterruptibly(executeAsync(statement));
        } catch (ExecutionException e) {
            throw DriverThrowables.propagateCause(e);
        }
    }

    @Override
    public ResultSetFuture executeAsync(final String query) {
        return executeAsync(new SimpleStatement(query));
    }

    @Override
    public ResultSetFuture executeAsync(final String query, final Object... values) {
        return executeAsync(new SimpleStatement(query, values));
    }

    @Override
    public ResultSetFuture executeAsync(final Statement statement) {
        SettableResultSetFutureCallback future = new SettableResultSetFutureCallback();
        SessionCall<Statement, ResultSet> call = channel.newCall(ACTION_STATEMENT_QUERY);
        call.call(future, session, statement);
        return future;
    }
    
    @Override
    public ResultSet execute(String query, Map<String, Object> values) {
        try {
            return Uninterruptibles.getUninterruptibly(executeAsync(query, values));
        } catch (ExecutionException e) {
            throw DriverThrowables.propagateCause(e);
        }
    }

    @Override
    public ResultSetFuture executeAsync(String query, Map<String, Object> values) {
        return executeAsync(new SimpleStatement(query, values));
    }

    @Override
    public PreparedStatement prepare(String query) {
        try {
            return Uninterruptibles.getUninterruptibly(prepareAsync(query));
        } catch (ExecutionException e) {
            throw DriverThrowables.propagateCause(e);
        }
    }

    @Override
    public PreparedStatement prepare(RegularStatement statement) {
        try {
            return Uninterruptibles.getUninterruptibly(prepareAsync(statement));
        } catch (ExecutionException e) {
            throw DriverThrowables.propagateCause(e);
        }
    }

    @Override
    public ListenableFuture<PreparedStatement> prepareAsync(String query) {
        return prepareAsync(new SimpleStatement(query));
    }

    @Override
    public ListenableFuture<PreparedStatement> prepareAsync(RegularStatement statement) {
        SessionCall<RegularStatement, PreparedStatement> call = channel.newCall(ACTION_PREPARE_STATEMENT);
        FutureCallbackToListenableFutureAdapter<PreparedStatement> future = new FutureCallbackToListenableFutureAdapter<PreparedStatement>();
        call.call(future, session, statement);
        return future;
    }

    @Override
    public CloseFuture closeAsync() {
        return session.closeAsync();
    }

    @Override
    public void close() {
        session.close();
    }

    @Override
    public boolean isClosed() {
        return session.isClosed();
    }

    @Override
    public Cluster getCluster() {
        return session.getCluster();
    }

    @Override
    public State getState() {
        return session.getState();
    }
}
