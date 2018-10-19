package com.publish.graph.spike;

import java.util.concurrent.TimeUnit;

import org.apache.tinkerpop.gremlin.driver.AuthProperties;
import org.apache.tinkerpop.gremlin.driver.Cluster;
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.util.empty.EmptyGraph;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableNeo4jRepositories("com.rishabh.data.graph")
@EnableTransactionManagement
public class DataGrapghApplication {
	
	@Value("${spring.data.neo4j.uri}")
    private String url;
	
	@Value("${spring.data.neo4j.username}")
    private String user;
	
	@Value("${spring.data.neo4j.password}")
    private String password;

	public static void main(String[] args) {
		SpringApplication.run(DataGrapghApplication.class, args);
	}
	
/*	@Bean
	public Driver ConfigConnectionPoolExample( )
	{
	   return GraphDatabase.driver( "bolt://0.0.0.0:7687", AuthTokens.basic( user, password ), Config.build()
	            .withMaxConnectionLifetime( 30, TimeUnit.MINUTES )
	            .withMaxConnectionPoolSize( 50 )
	            .withConnectionAcquisitionTimeout( 2, TimeUnit.MINUTES )
	            .toConfig() );
	}*/
	@Bean
	public  GraphTraversalSource getGraphTraversalObj() {
		   Cluster.Builder builder = Cluster.build();
	       builder.addContactPoint("glp-neptune-poc.cgtzemmgm1xd.us-east-2.neptune.amazonaws.com");
	       builder.port(8182);
	       builder.maxContentLength(1234567);
	       Cluster cluster = builder.create();
	       return EmptyGraph.instance().traversal().withRemote(DriverRemoteConnection.using(cluster));
		}

}
