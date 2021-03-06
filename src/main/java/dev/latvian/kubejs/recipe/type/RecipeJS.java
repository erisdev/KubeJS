package dev.latvian.kubejs.recipe.type;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.documentation.P;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import dev.latvian.kubejs.script.data.VirtualKubeJSDataPack;
import dev.latvian.kubejs.util.UtilsJS;
import net.minecraft.util.ResourceLocation;

/**
 * @author LatvianModder
 */
public abstract class RecipeJS
{
	public ResourceLocation id;
	public String group;

	public abstract RecipeTypeJS getType();

	public abstract JsonObject toJson();

	public void addToDataPack(VirtualKubeJSDataPack pack)
	{
		pack.addData(new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath() + ".json"), toJson().toString());
	}

	public RecipeJS id(@P("id") Object _id)
	{
		id = UtilsJS.getID(_id);
		return this;
	}

	public RecipeJS group(@P("group") String g)
	{
		group = g;
		return this;
	}

	protected JsonObject create()
	{
		JsonObject json = new JsonObject();
		json.addProperty("type", getType().id.toString());

		if (!group.isEmpty())
		{
			json.addProperty("group", group);
		}

		return json;
	}

	public boolean hasInput(IngredientJS ingredient)
	{
		return false;
	}

	public boolean hasOutput(IngredientJS ingredient)
	{
		return false;
	}

	@Override
	public String toString()
	{
		return id + "[" + getType().id + "]";
	}
}