package com.publish.graph.spike;

import java.util.Arrays;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
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
/*		Arrays.asList(ids).stream().forEach( node -> {
			LearningAsset nodeLA = (LearningAsset) node;
			g.addV("LearningAsset").property(T.id, nodeLA.getId()).property("type", nodeLA.getType()).next();

		});*/
		
		if (ids.length == 0) return;
        final int BATCH_SIZE = 100;
        GraphTraversal traversal = null;
        for (int i=0; i<ids.length; i++) {
            if(traversal == null) {
    			LearningAsset nodeLA = (LearningAsset) ids[i];
                traversal = g.addV("LearningAsset").property(T.id, nodeLA.getId()).property("type", nodeLA.getType());
                continue;
            }
            if (i%BATCH_SIZE == 0) {
                // execute traversal
                traversal.toList();
                // reset traversal
                traversal = null;
            } else {
                // add to existing traversal
            	LearningAsset nodeLA = (LearningAsset) ids[i];
                traversal.addV("LearningAsset").property(T.id, nodeLA.getId()).property("type", nodeLA.getType());
            }
        }
        if (traversal != null) traversal.toList();
        

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
	
	 /* public  void createRelationShipBatch(LearningAsset course) {
			String parent = course.getId();
			if(course.assets !=null) {
				Object[] childs = course.assets.stream().map(l -> l.getId()).toArray();
				final int BATCH_SIZE = 100;
		        GraphTraversal traversal = null;
				Arrays.asList(childs).stream().forEach(childNode -> {
					traversal = g.addE("ASSOCIATED_WITH").from(g.V(parent)).to(g.V(childNode)).next();
				});
				 for (int i=0; i<childs.length; i++) {
					 if(traversal == null) {
			    			//LearningAsset nodeLA = (LearningAsset) childs[i];
			    			traversal = g.addE("ASSOCIATED_WITH").from(g.V(parent)).to(g.V(childs[i]));
			                 continue;
			             }
					 if (i%BATCH_SIZE == 0) {
			                // execute traversal
			                traversal.toList();
			                // reset traversal
			                traversal = null;
			            } else {
			                // add to existing traversal
			            	//LearningAsset nodeLA = (LearningAsset) childs[i];
			                traversal.addE("ASSOCIATED_WITH").from(g.V(parent)).to(g.V(childs[i]));
			            }
				 }
				 if (traversal != null) traversal.toList();
 				 course.assets.stream().forEach(la ->{
 					createRelationShipBatch(la);
					
				});
			}
		}*/
   
		public  void updateRelation( String existingId, String laterId) {
			 List<Edge> edgeList = g.V(existingId).inE("ASSOCIATED_WITH").toList();
			 g.addE("ASSOCIATED_WITH").from(g.V(laterId)).to(g.V(existingId)).next();
			
		}
}
