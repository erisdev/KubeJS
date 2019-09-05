package dev.latvian.kubejs.item.ingredient;

import dev.latvian.kubejs.item.BoundItemStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * @author LatvianModder
 */
public class VanillaIngredient extends Ingredient
{
	public final IngredientJS ingredientJS;
	private ItemStack[] matchingStacksCache;
	private IntList validItemStacksPackedCache;

	public VanillaIngredient(IngredientJS i)
	{
		super(0);
		ingredientJS = i;
	}

	@Override
	public ItemStack[] getMatchingStacks()
	{
		if (matchingStacksCache == null)
		{
			Set<ItemStackJS> set = ingredientJS.getStacks();
			matchingStacksCache = new ItemStack[set.size()];
			int i = 0;

			for (ItemStackJS stack : set)
			{
				matchingStacksCache[i] = stack.itemStack();
				i++;
			}
		}

		return matchingStacksCache;
	}

	@Override
	public boolean apply(@Nullable ItemStack stack)
	{
		return stack != null && !stack.isEmpty() && ingredientJS.test(new BoundItemStackJS(stack));
	}

	@Override
	public IntList getValidItemStacksPacked()
	{
		if (validItemStacksPackedCache == null)
		{
			validItemStacksPackedCache = new IntArrayList(getMatchingStacks().length);

			for (ItemStack itemstack : getMatchingStacks())
			{
				validItemStacksPackedCache.add(RecipeItemHelper.pack(itemstack));
			}

			validItemStacksPackedCache.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return validItemStacksPackedCache;
	}

	@Override
	protected void invalidate()
	{
		matchingStacksCache = null;
		validItemStacksPackedCache = null;
	}
}