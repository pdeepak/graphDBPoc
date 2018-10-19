package com.publish.graph.spike;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;

@Component
public class CouchbaseTest {
	
	private Bucket bucket = null;
	@Value("${couchbase.hostName}")
	private String hostName;
	@Value("${couchbase.username}")
	private String userName;
	@Value("${couchbase.password}")
	private String password;
	@Value("${couchbase.bucketname:lec}")
	private String bucketname;
	
	public CouchbaseTest() {
		test();
	}
	
public void test() 
   {
		Cluster cluster = CouchbaseCluster.create("couchbase5-kernel.demo-internal.com");
	    cluster.authenticate(userName, password);
	    bucket = cluster.openBucket(bucketname);		
	}

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}
	
	
	
		
}
