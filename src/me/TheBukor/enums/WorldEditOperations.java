package me.TheBukor.enums;

public enum WorldEditOperations {
	SET("set"),
	WALLS("walls");
	
	private final String id;
	
	WorldEditOperations(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
}
