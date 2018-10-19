Neptune POC:
NeptuneGraphController.createGraphFromCourse: When this endpoint is invoked it creates and persist the graph for complete digital assets in the Amazon Neptune. This method reads the json documents from the Couchbase database and populates java objects to form hierarchy of nodes where each node contains list of child nodes. This class uses following methods to persist nodes and edges:
	NeptuneUtils.createGraphNodes: This method persists the node in the database. This method takes around 70 secs to save 15,000 nodes. 
	NeptuneUtils.createRelationShip: This method persists the  edges between the nodes in the database. This method takes around 130 secs to save 15,000 edges.

NeptuneGraphController.find: This endpoint retrieves the subgraph from the given node.