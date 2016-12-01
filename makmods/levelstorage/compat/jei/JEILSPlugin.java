package makmods.levelstorage.compat.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;

@JEIPlugin
public class JEILSPlugin implements IModPlugin {

    public void registerIngredients(IModIngredientRegistration registry) {

    }

    public void registerItemSubtypes(ISubtypeRegistry registry) {

    }

    public void register(IModRegistry registry) {
        registry.getJeiHelpers();
    }

    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

    }
}
