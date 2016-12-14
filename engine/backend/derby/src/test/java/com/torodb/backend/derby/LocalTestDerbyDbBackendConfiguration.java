package com.torodb.backend.derby;

import com.torodb.backend.driver.derby.DerbyDbBackendConfiguration;

public class LocalTestDerbyDbBackendConfiguration implements DerbyDbBackendConfiguration {

    @Override
    public boolean inMemory() {
        return true;
    }

    @Override
    public boolean embedded() {
        return true;
    }

    @Override
    public long getCursorTimeout() {
        return 10L * 60 * 1000;
    }

    @Override
    public long getConnectionPoolTimeout() {
        return 10_000;
    }

    @Override
    public int getConnectionPoolSize() {
        return 30;
    }

    @Override
    public int getReservedReadPoolSize() {
        return 10;
    }

    @Override
    public String getUsername() {
        return "torodb";
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getDbHost() {
        return "localhost";
    }

    @Override
    public String getDbName() {
        return "torod";
    }

    @Override
    public int getDbPort() {
        return 1527;
    }

    @Override
    public boolean includeForeignKeys() {
        return false;
    }
}