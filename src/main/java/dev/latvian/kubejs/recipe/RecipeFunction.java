package dev.latvian.kubejs.recipe;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.type.RecipeJS;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.server.ServerJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.util.WrappedJS;
import jdk.nashorn.api.scripting.AbstractJSObject;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * @author LatvianModder
 */
public class RecipeFunction extends AbstractJSObject implements WrappedJS
{
	public final RecipeTypeJS type;
	private final List<RecipeJS> recipes;

	public RecipeFunction(RecipeTypeJS t, List<RecipeJS> r)
	{
		type = t;
		recipes = r;
	}

	@Override
	public RecipeJS call(Object thiz, Object... args0)
	{
		ListJS args = ListJS.of(args0);

		if (args == null || args.isEmpty())
		{
			return new RecipeErrorJS("Recipe requires at least one argument!");
		}

		if (args.size() == 1)
		{
			MapJS map = MapJS.of(args.get(0));

			if (map != null)
			{
				JsonObject json = map.toJson();
				json.addProperty("type", type.id.toString());
				return add(type.create(json), args);
			}
			else
			{
				return new RecipeErrorJS("One argument recipes have to be a JSON object!");
			}
		}

		try
		{
			return add(type.create(args), args);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return add(new RecipeErrorJS(ex.toString()), args);
		}
	}

	private RecipeJS add(RecipeJS recipe, List args1)
	{
		if (recipe instanceof RecipeErrorJS)
		{
			ScriptType.SERVER.console.error("Broken '" + type.id + "' recipe: " + ((RecipeErrorJS) recipe).message);
			ScriptType.SERVER.console.error(args1);
			ScriptType.SERVER.console.error("");
			return recipe;
		}

		recipe.id = new ResourceLocation(type.id.getNamespace(), "kubejs_generated_" + recipes.size());
		recipe.group = "";

		if (ServerJS.instance.logAddedRecipes)
		{
			ScriptType.SERVER.console.info("Added '" + type.id + "' recipe: " + recipe.toJson());
		}

		recipes.add(recipe);
		return recipe;
	}

	@Override
	public String toString()
	{
		return type.id.toString();
	}
}