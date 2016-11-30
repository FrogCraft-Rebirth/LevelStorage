package makmods.levelstorage.init;

import java.util.regex.Pattern;

import makmods.levelstorage.api.XpStack;
import makmods.levelstorage.registry.XpStackRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class LSIMCHandler {

	public static final LSIMCHandler instance = new LSIMCHandler();
	
	private static final Pattern STACK_PARSER = Pattern.compile("(\\S*):(\\S*):(\\S*)\\*([0-9]*)");

	private LSIMCHandler() {
		;
	}

	public void handle(String key, String value) {
		try {
			if (key == "add-xp") { //Simply a test
				String[] parsedItemStack = STACK_PARSER.split(value);
				String id = parsedItemStack[0] + ":" + parsedItemStack[1];
				int meta = Integer.parseInt(parsedItemStack[2]);
				int stackValue = Integer.parseInt(parsedItemStack[3]);
				/*String[] stackAndValue = value.split(",");
				int stackValue = Integer.parseInt(stackAndValue[1]);
				String[] idAndMeta = stackAndValue[0].split(":");
				String id = idAndMeta[0];
				int meta = Integer.parseInt(idAndMeta[1]);*/
				XpStackRegistry.instance.pushToRegistry(new XpStack(
						GameRegistry.makeItemStack(id, meta, 1, ""), stackValue));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error occured during parsing XpStackRegistry IMC message parsing.", e);
		}
	}
}
