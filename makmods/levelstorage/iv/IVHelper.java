package makmods.levelstorage.iv;

public class IVHelper {
	
	public static IVStack createIV(int iv) {
		int leftover = iv % IVRegistry.IV_TO_FLUID_CONVERSION.getKey();
		int fluidAmt = iv / IVRegistry.IV_TO_FLUID_CONVERSION.getKey();
		return new IVStack(fluidAmt, leftover);
	}
	
}
