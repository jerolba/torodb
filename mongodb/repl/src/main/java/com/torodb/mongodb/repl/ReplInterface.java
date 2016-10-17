
package com.torodb.mongodb.repl;

import com.eightkdata.mongowp.OpTime;
import com.eightkdata.mongowp.WriteConcern;
import com.eightkdata.mongowp.bson.BsonObjectId;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.commands.general.GetLastErrorCommand.WriteConcernEnforcementResult;
import com.eightkdata.mongowp.mongoserver.api.safe.library.v3m0.pojos.MemberState;
import com.torodb.mongodb.annotations.Locked;
import java.io.Closeable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/**
 *
 */
@ThreadSafe
public interface ReplInterface {

    public MemberStateInterface freezeMemberState(boolean toChangeState);

    @ThreadSafe
    public static interface MemberStateInterface extends Closeable {

        MemberState getMemberState();

        boolean canNodeAcceptWrites(String database);

        boolean canNodeAcceptReads(String database);

        boolean canChangeMemberState();

        @Override
        void close();
    }

    public static interface PrimaryStateInterface extends MemberStateInterface {

        /**
         * Stops the current user thread up to {@linkplain WriteConcern#wtimeout
         * some milliseconds} or until the given optime is replicated to a set
         * of nodes that satisfies the given write concern, whichever comes
         * first.
         * <p>
         * @param ts
         * @param wc
         * @return
         */
        @Nonnull
        WriteConcernEnforcementResult awaitReplication(OpTime ts, WriteConcern wc);

        /**
         * Makes this node relinquish its primary condition for a given number
         * of milliseconds.
         * <p>
         * By default it will a given number of milliseconds until some
         * secondary node reach him, but if force is true, then it will be
         * downgraded immediately
         * <p>
         * @param force
         * @param waitTime
         * @param stepDownTime
         * @throws WriteNotAllowedException if this object has not been open with write permission
         */
        @Locked(exclusive = true)
        void stepDown(boolean force, long waitTime, long stepDownTime) throws WriteNotAllowedException;

        /**
         * Returns an local-unique id that identifies when this node became
         * primary or null if this node is not primary.
         * <p>
         * @return
         */
        @Nullable
        public BsonObjectId getOurElectionId();

    }

    public static interface RecoveryStateInterface extends MemberStateInterface {
        
    }

    public static interface SecondaryStateInferface extends MemberStateInterface {}

    public static class WriteNotAllowedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

    }

}
