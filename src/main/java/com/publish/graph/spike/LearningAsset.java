package com.publish.graph.spike;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class LearningAsset {
	


	@Id
	private String id;
	
	private String type;
	
	@Relationship(type = "ASSOCIATED_WITH", direction = Relationship.OUTGOING)
	public Set<LearningAsset> assets;
	
	
	public LearningAsset() {
		// TODO Auto-generated constructor stub
	}
	
	public LearningAsset(String id , String type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void addAssociation(LearningAsset la) {
		
		if(assets == null) {
			assets = new HashSet<>();
		}
		assets.add(la);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LearningAsset other = (LearningAsset) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "LearningAsset [id=" + id + ", type=" + type + ", assets=" + assets + "]";
	}
	
	
}
