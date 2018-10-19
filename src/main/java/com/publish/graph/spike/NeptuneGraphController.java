package com.publish.graph.spike;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NeptuneGraphController {

	
	@Autowired
	private CouchbaseTest couchbaseTest;
	@Autowired
	private NeptuneUtils neptuneUtils;
	private Set<LearningAsset> nodes = null;
	
	@GetMapping("/neptunegraph/{id}")
	public String createGraphFromCourse(@PathVariable String id) {
		long startTime = System.currentTimeMillis();
		Map<String, Object> doc = couchbaseTest.getBucket().get(id).content().toMap();
		nodes= new HashSet();
		LearningAsset  course = new LearningAsset(doc.get("id").toString(),doc.get("assetType").toString());
		nodes.add(course);
		Map<String, Object> resources = (Map<String, Object>) doc.get("resources");
        resources.keySet().stream().forEach(resource ->{
        	 if(((Map<String, Object>)resources.get(resource)).get("resourceType").toString().equals("LEARNINGASSET")) {
        		 
        		Map<String, Object> laMap = couchbaseTest.getBucket().get(resource).content().toMap();
        		//neoUtils.createNode(laMap.get("id").toString(),laMap.get("assetType").toString());
             	LearningAsset la = new LearningAsset(laMap.get("id").toString(),laMap.get("assetType").toString());
        		nodes.add(la);

             	//System.out.println("the learning asset is: " +  la);
             	//learningAssetRepo.save(la);
           	    //System.out.println("the learning asset for course is: " +  course);
             	course.addAssociation(la);
             	createGraph(laMap,la);
             	
        	 }
        });
        long endTime = System.currentTimeMillis();
        System.out.println("Total time in creating graph = "+(endTime-startTime));
        neptuneUtils.createGraphNodes(nodes.toArray());
        System.out.println("Total time in saving graph = "+(System.currentTimeMillis()-endTime));
        //neoUtils.createRelation(resources.keySet().toArray(), doc.get("id").toString());
        //learningAssetRepo.save(course);
        long endTimeReln = System.currentTimeMillis();
        neptuneUtils.createRelationShip(course);
        System.out.println("Total time in saving graph = "+(System.currentTimeMillis()-endTimeReln));	
		return "SUCCCESS";
		
	}
	
	@GetMapping("/readGraphNeptune/{id}/{depth}")
	public List find(@PathVariable String id, @PathVariable int depth) {
		 long startTime = System.currentTimeMillis();
		 List nodesList = neptuneUtils.readNodes(id, depth);
	     System.out.println("Total time in querying graph = "+(System.currentTimeMillis()-startTime));
		return  nodesList;
	}
	
	@GetMapping("/updateGraphNeptune/{id1}/{id2}")
	public void UpdateGraph1(@PathVariable String id1 , @PathVariable String id2)  {
		long startTime = System.currentTimeMillis();
		neptuneUtils.updateRelation(id1,id2);
        System.out.println("Total time in graph updation= " + (System.currentTimeMillis()- startTime));
	}
	public void createGraph(Map<String, Object> la , LearningAsset asset) {
		Map<String, Object> resources = (Map<String, Object>) la.get("resources");
		ArrayList<String> list = new ArrayList<>();
        resources.keySet().stream().forEach(resource ->{
        	 if(((Map<String, Object>)resources.get(resource)).get("resourceType").toString().equals("LEARNINGASSET")) {
        		Map<String, Object> laMap = couchbaseTest.getBucket().get(resource).content().toMap();
        		LearningAsset l  = new LearningAsset(laMap.get("id").toString(),laMap.get("assetType").toString());
        		nodes.add(l);
        		asset.addAssociation(l);
        		//neoUtils.createNode(laMap.get("id").toString(),laMap.get("assetType").toString());
             	//list.add(laMap.get("id").toString());
             	createGraph(laMap,l);
        	 }
        });	
	}
	
	
}
