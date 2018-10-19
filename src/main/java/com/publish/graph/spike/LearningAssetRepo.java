package com.publish.graph.spike;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LearningAssetRepo extends Neo4jRepository<LearningAsset, String> {
	

	@Query("match((n:LearningAsset)-[r1:ASSOCIATED_WITH]->(m:LearningAsset{id:{0}})),"
			+ "(a:LearningAsset{id:{1}}) create (a)-[r2:ASSOCIATED_WITH]->(m) delete r1")
			public void updateGraph(String id1, String id2);
}

