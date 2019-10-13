package _128_bit_guy.redtech.common.init;

import _128_bit_guy.redtech.common.RedTech;
import _128_bit_guy.redtech.common.recipe.serializer.CoverAdditionRecipe;
import _128_bit_guy.redtech.common.recipe.serializer.CoverCreationRecipe;
import _128_bit_guy.redtech.common.recipe.serializer.CoverSplitRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.Function;

public class ModRecipeSerializers {
    public static RecipeSerializer<CoverCreationRecipe> COVER_CREATION;
    public static RecipeSerializer<CoverSplitRecipe> COVER_SPLIT;
    public static RecipeSerializer<CoverAdditionRecipe> COVER_ADDITION;

    public static void init() {
        COVER_CREATION = registerSpecial("cover_creation", CoverCreationRecipe::new);
        COVER_SPLIT = registerSpecial("cover_split", CoverSplitRecipe::new);
        COVER_ADDITION = registerSpecial("cover_addition", CoverAdditionRecipe::new);
    }

    @SuppressWarnings("unchecked")
    private static <R extends Recipe<?>, T extends SpecialRecipeSerializer<R>> T registerSpecial(String id, Function<Identifier, R> supplier) {
        return (T) Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(RedTech.ID, id), new SpecialRecipeSerializer<>(supplier));
    }
}
