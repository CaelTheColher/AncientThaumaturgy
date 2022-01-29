package me.cael.ancientthaumaturgy.vis.api.base;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import me.cael.ancientthaumaturgy.vis.impl.SimpleItemVisStorageImpl;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

/**
 * Simple battery-like item. If this is implemented on an item:
 * <ul>
 *     <li>The vis will directly be stored in the NBT.</li>
 *     <li>Helper functions in this class to work with the stored vis can be used.</li>
 *     <li>A VisStorage will automatically be provided for queries through {@link VisStorage#ITEM}.</li>
 * </ul>
 */
// TODO: Consider adding a tooltip and a recipe input -> output vis transfer handler like RC has.
public interface SimpleBatteryItem {
	String VIS_KEY = "vis";

	/**
	 * Return a base vis storage implementation for items, with fixed capacity, and per-operation insertion and extraction limits.
	 * This is used internally for items that implement SimpleBatteryItem, but it may also be used outside of that.
	 * The vis is stored in the {@code vis} tag of the stacks, the same as the constant {@link #VIS_KEY}.
	 *
	 * <p>Stackable vis containers are supported just fine, and they will distribute vis evenly.
	 * For example, insertion of 3 units of vis into a stack of 2 items using this class will either insert 0 or 2 depending on the remaining capacity.
	 */
	static VisStorage createStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		return SimpleItemVisStorageImpl.createSimpleStorage(ctx, capacity, maxInsert, maxExtract);
	}

	/**
	 * @return The max vis that can be stored in this item.
	 */
	long getVisCapacity();

	/**
	 * @return The max amount of vis that can be inserted in this item in a single operation.
	 */
	long getVisMaxInput();

	/**
	 * @return The max amount of vis that can be extracted from this item in a single operation.
	 */
	long getVisMaxOutput();

	/**
	 * @return The vis stored in the stack.
	 * @throws IllegalArgumentException If the count of the stack is not exactly 1!
	 */
	default long getStoredVis(ItemStack stack) {
		if (stack.getCount() != 1) {
			throw new IllegalArgumentException("Invalid count: " + stack.getCount());
		}

		return getStoredVisUnchecked(stack);
	}

	/**
	 * Set the vis stored in the stack.
	 * @throws IllegalArgumentException If the count of the stack is not exactly 1!
	 */
	default void setStoredVis(ItemStack stack, long newAmount) {
		if (stack.getCount() != 1) {
			throw new IllegalArgumentException("Invalid count: " + stack.getCount());
		}

		setStoredVisUnchecked(stack, newAmount);
	}

	/**
	 * Try to use exactly {@code amount} vis if there is enough available and return true if successful,
	 * otherwise do nothing and return false.
	 * @throws IllegalArgumentException If the count of the stack is not exactly 1!
	 */
	default boolean tryUseVis(ItemStack stack, long amount) {
		long newAmount = getStoredVis(stack) - amount;

		if (newAmount < 0) {
			return false;
		} else {
			setStoredVis(stack, newAmount);
			return true;
		}
	}

	/**
	 * @return The currently stored vis, ignoring the count and without checking the current item.
	 */
	static long getStoredVisUnchecked(ItemStack stack) {
		return getStoredVisUnchecked(stack.getNbt());
	}

	/**
	 * @return The currently stored vis of this raw tag.
	 */
	static long getStoredVisUnchecked(@Nullable NbtCompound nbt) {
		return nbt != null ? nbt.getLong(VIS_KEY) : 0;
	}

	/**
	 * Set the vis, ignoring the count and without checking the current item.
	 */
	static void setStoredVisUnchecked(ItemStack stack, long newAmount) {
		if (newAmount == 0) {
			// Make sure newly crafted vis containers stack with emptied ones.
			stack.removeSubNbt(VIS_KEY);
		} else {
			stack.getOrCreateNbt().putLong(VIS_KEY, newAmount);
		}
	}
}
