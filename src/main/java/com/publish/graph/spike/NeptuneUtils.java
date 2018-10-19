package com.publish.graph.spike;

import java.util.Arrays;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NeptuneUtils {

	
	@Autowired
	private GraphTraversalSource g;
	
	public void createGraphNodes(Object[] ids) {
		Arrays.asList(ids).stream().forEach( node -> {
			LearningAsset nodeLA = (LearningAsset) node;
			g.addV("LearningAsset").property(T.id, nodeLA.getId()).property("type", nodeLA.getType()).next();

		});
	}
	
	public  List readNodes( String id,  int depth) {
		return g.V(id).emit().repeat(__.out()).until(__.loops().is(P.gt(depth))).tree().toList();
		
	}
	   public  void createRelationShip(LearningAsset course) {
			String parent = course.getId();
			if(course.assets !=null) {
				Object[] childs = course.assets.stream().map(l -> l.getId()).toArray();
				Arrays.asList(childs).stream().forEach(childNode -> {
					g.addE("ASSOCIATED_WITH").from(g.V(parent)).to(g.V(childNode)).next();
				});
				course.assets.stream().forEach(la ->{
					createRelationShip(la);
					
				});
			}
		}
   
		public  void updateRelation( String existingId, String laterId) {
			 List<Edge> edgeList = g.V(existingId).inE("ASSOCIATED_WITH").toList();
			 g.addE("ASSOCIATED_WITH").from(g.V(laterId)).to(g.V(existingId)).next();
			
		}
}
