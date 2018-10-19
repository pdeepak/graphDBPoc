package com.publish.graph.spike;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GraphController {
	

	
	
	
	@Autowired
	CouchbaseTest couchbaseTest;
	
	LearningAsset course;
	@Autowired
	NeoUtils neoUtils;
	
	private ArrayList<String> nodes = new ArrayList<>();

	@Autowired
	private LearningAssetRepo learningAssetRepo;
	

	@GetMapping("/readGraphNeo/{id}/{depth}")
	public LearningAsset find(@PathVariable String id, @PathVariable int depth) {
		long startTime = System.currentTimeMillis();
		Optional<LearningAsset> object = learningAssetRepo.findById(id, depth);
        System.out.println("Total time in fetch query = "+(System.currentTimeMillis()-startTime));

		if (object.isPresent())
			return object.get();
		return null;
	}
	

		
	
	@GetMapping("/updateGraph1/{id1}/{id2}")
	public void UpdateGraph1(@PathVariable String id1 , @PathVariable String id2)  {
		long startTime = System.currentTimeMillis();
		learningAssetRepo.updateGraph(id1, id2);
        System.out.println("Total time in graph updation= "+(System.currentTimeMillis()-startTime));
	}

	
	
	@GetMapping("/graph/{id}")
	public String createGraphFromCourse(@PathVariable String id) {
		long startTime = System.currentTimeMillis();
		Map<String, Object> doc = couchbaseTest.getBucket().get(id).content().toMap();
		neoUtils.createNode(doc.get("id").toString(),doc.get("assetType").toString());
		neoUtils.createIndex();
		LearningAsset  course = new LearningAsset(doc.get("id").toString(),doc.get("assetType").toString());
		Map<String, Object> resources = (Map<String, Object>) doc.get("resources");
        resources.keySet().stream().forEach(resource ->{
        	 if(((Map<String, Object>)resources.get(resource)).get("resourceType").toString().equals("LEARNINGASSET")) {
        		 
        		Map<String, Object> laMap = couchbaseTest.getBucket().get(resource).content().toMap();
        		nodes.add(laMap.get("id").toString());
        		//neoUtils.createNode(laMap.get("id").toString(),laMap.get("assetType").toString());
             	LearningAsset la = new LearningAsset(laMap.get("id").toString(),laMap.get("assetType").toString());
             	//System.out.println("the learning asset is: " +  la);
             	//learningAssetRepo.save(la);
           	    //System.out.println("the learning asset for course is: " +  course);
             	course.addAssociation(la);
             	createGraph(laMap,la);
             	
        	 }
        });
        long endTime = System.currentTimeMillis();
        System.out.println("Total time in creating graph = "+(endTime-startTime));
        String bookmark = neoUtils.createNodes(nodes.toArray());
        System.out.println("Total time in saving graph = "+(System.currentTimeMillis()-endTime));
        //neoUtils.createRelation(resources.keySet().toArray(), doc.get("id").toString());
        //learningAssetRepo.save(course);
        endTime = System.currentTimeMillis();
        createRelationShip(course,bookmark);
        System.out.println("Total time in saving graph = "+(System.currentTimeMillis()-endTime));	
		return "SUCCCESS";
		
	}
	
	
	@GetMapping("/multiGraph/{id1}/{id2}/{id3}")
	public String createMultiGraph(@PathVariable String id1 ,@PathVariable String id2,@PathVariable String id3){
		List<String> courses = Arrays.asList(id1,id2,id3);
		courses.stream().forEach(id ->{
			createGraphFromCourse(id);
		});
		neoUtils.createRelationBetCourses(courses.toArray());
		return "SUCCCESS";	
	}
	
	public void createRelationShip(LearningAsset course , String bookmark) {
		
		String parent = course.getId();
		if(course.assets !=null) {
			Object[] childs = course.assets.stream().map(l -> l.getId()).toArray();
			neoUtils.createRel(childs, parent , bookmark);
			course.assets.stream().forEach(la ->{
				createRelationShip(la,bookmark);
				
			});
		}
	}

	
	public void createGraph(Map<String, Object> la , LearningAsset asset) {
		Map<String, Object> resources = (Map<String, Object>) la.get("resources");
		ArrayList<String> list = new ArrayList<>();
        resources.keySet().stream().forEach(resource ->{
        	 if(((Map<String, Object>)resources.get(resource)).get("resourceType").toString().equals("LEARNINGASSET")) {
        		Map<String, Object> laMap = couchbaseTest.getBucket().get(resource).content().toMap();
        		LearningAsset l  = new LearningAsset(laMap.get("id").toString(),laMap.get("assetType").toString());
        		nodes.add(laMap.get("id").toString());
        		asset.addAssociation(l);
        		//neoUtils.createNode(laMap.get("id").toString(),laMap.get("assetType").toString());
             	//list.add(laMap.get("id").toString());
             	createGraph(laMap,l);
        	 }
        });	
	}
	
}
