package makmods.levelstorage.logic;

import net.minecraft.item.Item;

public class IDMeta {
	
	private Item id;
	private int metadata;

	public IDMeta(Item item, int metadata) {
		this.id = item;
		this.metadata = metadata;
	}

	public Item getID() {
		return id;
	}

	public void setID(Item id) {
		this.id = id;
	}

	public int getMetadata() {
		return metadata;
	}

	public void setMetadata(int metadata) {
		this.metadata = metadata;
	}
	
	public IDMeta clone() {
		return new IDMeta(id, metadata);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof IDMeta))
			return false;
		IDMeta other = (IDMeta)obj;
		return other.id == this.id && other.metadata == this.metadata;
	}
}
