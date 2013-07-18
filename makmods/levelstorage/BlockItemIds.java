package makmods.levelstorage;

import java.util.HashMap;
import java.util.Map;

public class BlockItemIds {
	public static final BlockItemIds instance = new BlockItemIds();
	
	private final Map<String, Integer> ids = new HashMap<String, Integer>();
	
	public void addId(String forWhat, int id) {
		ids.put(forWhat, id);
	}
	
	public int getIdFor(String forWhat) {
		return ids.get(forWhat);
	}
	
	private BlockItemIds() {}
}
