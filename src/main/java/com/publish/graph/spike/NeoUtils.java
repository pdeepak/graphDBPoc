package com.publish.graph.spike;

import org.apache.commons.configuration.Configuration;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Values;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NeoUtils {
	
	@Autowired
	Driver driver;

	public void createIndex() {
		driver.session().run ( "CREATE CONSTRAINT ON (a:LearningAsset) ASSERT a.id IS UNIQUE" );
	}
	
	public void createNode(String id , String type) {
		
		try ( Session session = driver.session() )
	    {
	        session.writeTransaction( new TransactionWork<Integer>()
	        {
	            @Override
	            public Integer execute( Transaction tx )
	            {
	                return createNode( tx,id,type);	
	            }
	        } );
	    }
		
	}
	
	private static int createNode( Transaction tx, String id , String type)
	{
	    tx.run( "MERGE (a:LearningAsset {id: $id , type: $type})", Values.parameters( "id",id ,"type" ,type ) );
	    return 1;
	}
	
	
	public StatementResult createRelationShip(final Transaction tx, final Object[] ids, final String parentId)
	{
		
    return tx.run ( "UNWIND $ids as id MATCH(a:LearningAsset{id:$parentId}),(b:LearningAsset{id:id})"
     		+ "MERGE (a)-[r:ASSOCIATED_WITH]->(b)", Values.parameters( "ids",ids ,"parentId" ,parentId ));
	}
	private StatementResult saveCourse(final Transaction tx, final Object[] ids)
	{
	    return tx.run ( "UNWIND $ids as id MERGE (a:LearningAsset {id: id})", Values.parameters( "ids",ids));
	}
	
	public String createNodes(Object[] ids)
	{
		
		try ( Session session = driver.session() )
	    {
		 session.writeTransaction(tx -> saveCourse(tx, ids));
		 return session.lastBookmark();
	
	    }
	}
	
	
	public void createRel(Object[] ids, String parentId , String bookmark) {
		
		try ( Session session = driver.session( AccessMode.WRITE, bookmark) )
	    {
	        session.writeTransaction( new TransactionWork<Integer>()
	        {
	            @Override
	            public Integer execute( Transaction tx )
	            {
	                return createRelation( tx,ids,parentId);	
	            }
	        } );
	    }
		
	}
	public static int createRelation(Transaction tx, Object[] ids, String parentId)
	{
		
    tx.run ( "UNWIND $ids as id MATCH(a:LearningAsset{id:$parentId}),(b:LearningAsset{id:id})"
     		+ "MERGE (a)-[r:ASSOCIATED_WITH]->(b)", Values.parameters( "ids",ids ,"parentId" ,parentId ));
    return 1;
	}
	
	public void createRelationBetCourses(Object[] ids)
	{
		
		try ( Session session = driver.session() )
	    {
	        session.writeTransaction( new TransactionWork<Integer>()
	        {
	            @Override
	            public Integer execute( Transaction tx )
	            {
	                tx.run ( "MATCH(a:LearningAsset{id:$id1}),(b:LearningAsset{id:$id2}),(c:LearningAsset{id:$id3}) "
	                 		+ "MERGE (a)-[:ASSOCIATED_WITH]->(b) MERGE (b)-[:ASSOCIATED_WITH]->(c)", Values.parameters( "id1",ids[0],"id2",ids[1], "id3",ids[2] ));
	                return 1;
	            }
	        } );
	    }
	}

	   
}
