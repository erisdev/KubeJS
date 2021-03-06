package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.item.EmptyItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeErrorJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import net.minecraft.item.crafting.IRecipeSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ShapedRecipeJS extends RecipeJS
{
	public static final RecipeTypeJS TYPE = new RecipeTypeJS(IRecipeSerializer.CRAFTING_SHAPED)
	{
		@Override
		public RecipeJS create(ListJS args)
		{
			if (args.size() != 3)
			{
				return new RecipeErrorJS("Shaped recipe requires 3 arguments - result, pattern and keys!");
			}

			if (!(args.get(2) instanceof Map))
			{
				return new RecipeErrorJS("Shaped recipe pattern is empty!");
			}

			ShapedRecipeJS recipe = new ShapedRecipeJS();
			recipe.result = ItemStackJS.of(args.get(0));

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Shaped recipe result " + args.get(0) + " is not a valid item!");
			}

			ListJS pattern = ListJS.orSelf(args.get(1));

			if (pattern.isEmpty())
			{
				return new RecipeErrorJS("Shaped recipe pattern is empty!");
			}

			for (Object p : pattern)
			{
				recipe.pattern.add(String.valueOf(p));
			}

			MapJS key = MapJS.of(args.get(2));

			if (key == null || key.isEmpty())
			{
				return new RecipeErrorJS("Shaped recipe key map is empty!");
			}

			for (String k : key.keySet())
			{
				IngredientJS i = IngredientJS.of(key.get(k));

				if (!i.isEmpty())
				{
					recipe.key.put(k, i);
				}
				else
				{
					return new RecipeErrorJS("Shaped recipe ingredient " + key.get(k) + " with key '" + k + "' is not a valid ingredient!");
				}
			}

			return recipe;
		}

		@Override
		public RecipeJS create(JsonObject json)
		{
			ShapedRecipeJS recipe = new ShapedRecipeJS();

			recipe.result = ItemStackJS.fromRecipeJson(json.get("result"));

			if (recipe.result.isEmpty())
			{
				return new RecipeErrorJS("Shaped recipe result " + json.get("result") + " is not a valid item!");
			}

			for (JsonElement e : json.get("pattern").getAsJsonArray())
			{
				recipe.pattern.add(e.getAsString());
			}

			if (recipe.pattern.isEmpty())
			{
				return new RecipeErrorJS("Shaped recipe pattern is empty!");
			}

			for (Map.Entry<String, JsonElement> entry : json.get("key").getAsJsonObject().entrySet())
			{
				IngredientJS i = IngredientJS.fromRecipeJson(entry.getValue());

				if (!i.isEmpty())
				{
					recipe.key.put(entry.getKey(), i);
				}
				else
				{
					return new RecipeErrorJS("Shaped recipe ingredient " + entry.getValue() + " with key '" + entry.getKey() + "' is not a valid ingredient!");
				}
			}

			if (recipe.key.isEmpty())
			{
				return new RecipeErrorJS("Shaped recipe key map is empty!");
			}

			return recipe;
		}
	};

	private final List<String> pattern = new ArrayList<>();
	private final Map<String, IngredientJS> key = new HashMap<>();
	private ItemStackJS result = EmptyItemStackJS.INSTANCE;

	@Override
	public RecipeTypeJS getType()
	{
		return TYPE;
	}

	@Override
	public JsonObject toJson()
	{
		JsonObject json = create();

		JsonArray patternJson = new JsonArray();

		for (String s : pattern)
		{
			patternJson.add(s);
		}

		json.add("pattern", patternJson);

		JsonObject keyJson = new JsonObject();

		for (Map.Entry<String, IngredientJS> entry : key.entrySet())
		{
			keyJson.add(entry.getKey(), entry.getValue().toJson());
		}

		json.add("key", keyJson);

		json.add("result", result.getResultJson());
		return json;
	}

	@Override
	public boolean hasInput(IngredientJS ingredient)
	{
		for (IngredientJS in : key.values())
		{
			if (in.anyStackMatches(ingredient))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasOutput(IngredientJS ingredient)
	{
		return ingredient.test(result);
	}
}