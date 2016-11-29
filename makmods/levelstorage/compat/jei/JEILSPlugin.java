package makmods.levelstorage.compat.jei;

import mezz.jei.api.*;
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
