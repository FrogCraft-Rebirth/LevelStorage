package makmods.levelstorage.compat.jei;

import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fluids.FluidStack;

public class XpRegistryWrapper implements IRecipeWrapper {

	@Override
	public void getIngredients(IIngredients ingredients) {
		
	}

	@Override
	public List<?> getInputs() {
		return null;
	}

	@Override
	public List<?> getOutputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidInputs() {
		return null;
	}

	@Override
	public List<FluidStack> getFluidOutputs() {
		return null;
	}

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		
	}

	@Override
	public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
		
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		return null;
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		return false;
	}

}
