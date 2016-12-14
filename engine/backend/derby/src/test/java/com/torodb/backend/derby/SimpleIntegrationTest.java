package com.torodb.backend.derby;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.torodb.backend.DbBackendService;
import com.torodb.backend.DslContextFactory;
import com.torodb.backend.DslContextFactoryImpl;
import com.torodb.backend.SqlHelper;
import com.torodb.backend.SqlInterface;
import com.torodb.backend.SqlInterfaceDelegate;
import com.torodb.backend.derby.schema.DerbySchemaUpdater;
import com.torodb.backend.driver.derby.DerbyDbBackendConfiguration;
import com.torodb.backend.driver.derby.DerbyDriverProvider;
import com.torodb.backend.driver.derby.OfficialDerbyDriver;
import com.torodb.core.backend.IdentifierConstraints;

public class SimpleIntegrationTest {

    
    public static void main(String[] args) throws SQLException, InterruptedException, ExecutionException{
        SimpleIntegrationTest test = new SimpleIntegrationTest();
        test.canUpdateSchema();
    }
    
    public void canUpdateSchema() throws SQLException, InterruptedException, ExecutionException {
        DerbyDriverProvider driver = new OfficialDerbyDriver();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        DerbyErrorHandler errorHandler = new DerbyErrorHandler();
        
        IdentifierConstraints constraints = new DerbyIdentifierConstraints();
        
        DerbyDataTypeProvider provider = new DerbyDataTypeProvider();
        SqlHelper sqlHelper = new SqlHelper(provider, errorHandler);
        DerbyDbBackendConfiguration configuration = new LocalTestDerbyDbBackendConfiguration();
        DbBackendService dbBackendService = new DerbyDbBackend(threadFactory, configuration, driver, errorHandler);       
        dbBackendService.startAsync();
        dbBackendService.awaitRunning();
        DslContextFactory dslContextFactory = new DslContextFactoryImpl(provider);
        
        DerbyDbBackend dbBackend = new DerbyDbBackend(threadFactory, configuration, driver, errorHandler);
        
        DerbyMetaDataReadInterface metaDataReadInterface = new DerbyMetaDataReadInterface(sqlHelper);
        DerbyStructureInterface derbyStructureInterface = new DerbyStructureInterface(dbBackend, metaDataReadInterface, sqlHelper, constraints);
        
        DerbyMetaDataWriteInterface metadataWriteInterface = new DerbyMetaDataWriteInterface(metaDataReadInterface, sqlHelper); 
            
        SqlInterface sqlInterface = new SqlInterfaceDelegate(null, metadataWriteInterface, provider, derbyStructureInterface, null, null, null, errorHandler, dslContextFactory, dbBackendService);
        DerbySchemaUpdater schemaUpdater = new DerbySchemaUpdater(sqlInterface, sqlHelper);
        
        try (Connection connection = sqlInterface.getDbBackend().createWriteConnection()) {
            schemaUpdater.checkOrCreate(dslContextFactory.createDslContext(connection));
            connection.commit();
        }
    }
}
