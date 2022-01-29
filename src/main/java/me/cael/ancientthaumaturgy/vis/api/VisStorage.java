package me.cael.ancientthaumaturgy.vis.api;

import me.cael.ancientthaumaturgy.vis.api.base.DelegatingVisStorage;
import me.cael.ancientthaumaturgy.vis.api.base.SimpleBatteryItem;
import me.cael.ancientthaumaturgy.vis.api.base.SimpleSidedVisContainer;
import me.cael.ancientthaumaturgy.vis.api.base.SimpleVisStorage;
import me.cael.ancientthaumaturgy.vis.impl.SimpleItemVisStorageImpl;
import me.cael.ancientthaumaturgy.vis.impl.VisImpl;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

/**
 * An object that can store vis.
 *
 * <p><ul>
 *     <li>{@link #supportsInsertion} and {@link #supportsExtraction} can be used to tell if insertion and extraction
 *     functionality are possibly supported by this storage.</li>
 *     <li>{@link #insert} and {@link #extract} can be used to insert or extract resources from this storage.</li>
 *     <li>{@link #getAmount} and {@link #getCapacity} can be used to query the current amount and capacity of this storage.
 *     There is no guarantee that the current amount of vis can be extracted,
 *     nor that something can be inserted if capacity > amount.
 *     If you want to know, you can simulate the operation with {@link #insert} and {@link #extract}.
 *     </li>
 * </ul>
 *
 * @see Transaction
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public interface VisStorage {
	/**
	 * Sided block access to vis storages.
	 * The {@code Direction} parameter may never be null.
	 * Refer to {@link BlockApiLookup} for documentation on how to use this field.
	 *
	 * <p>{@link SimpleVisStorage} and {@link SimpleSidedVisContainer} are provided as base implementations.
	 *
	 * <p>When the operations supported by a vis storage change,
	 * that is if the return value of {@link VisStorage#supportsInsertion} or {@link VisStorage#supportsExtraction} changes,
	 * the storage should notify its neighbors with a block update so that they can refresh their connections if necessary.
	 *
	 * <p>May only be queried on the logical server thread, never client-side or from another thread!
	 */
	BlockApiLookup<VisStorage, Direction> SIDED =
			BlockApiLookup.get(new Identifier("ancientthaumaturgy:sided_vis"), VisStorage.class, Direction.class);

	/**
	 * Item access to vis storages.
	 * Querying should always happen through {@link ContainerItemContext#find}.
	 *
	 * <p>{@link SimpleItemVisStorageImpl} is provided as an implementation example.
	 * Instances of it can be optained through {@link SimpleBatteryItem#createStorage}.
	 * Custom implementations should treat the context as a wrapper around a single slot,
	 * and always check the current item variant and amount before any operation, like {@code SimpleItemVisStorageImpl} does it.
	 * The check can be handled by {@link DelegatingVisStorage}.
	 *
	 * <p>This may be queried both client-side and server-side.
	 * Returned APIs should behave the same regardless of the logical side.
	 */
	ItemApiLookup<VisStorage, ContainerItemContext> ITEM =
			ItemApiLookup.get(new Identifier("ancientthaumaturgy:vis"), VisStorage.class, ContainerItemContext.class);

	/**
	 * Always empty vis storage.
	 */
	VisStorage EMPTY = VisImpl.EMPTY;

	/**
	 * Return false if calling {@link #insert} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer vis to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsInsertion() {
		return true;
	}

	/**
	 * Try to insert up to some amount of vis into this storage.
	 *
	 * @param maxAmount The maximum amount of vis to insert. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was inserted.
	 */
	long insert(long maxAmount, TransactionContext transaction);

	/**
	 * Return false if calling {@link #extract} will absolutely always return 0, or true otherwise or in doubt.
	 *
	 * <p>Note: This function is meant to be used by cables or other devices that can transfer vis to know if
	 * they should interact with this storage at all.
	 */
	default boolean supportsExtraction() {
		return true;
	}

	/**
	 * Try to extract up to some amount of vis from this storage.
	 *
	 * @param maxAmount The maximum amount of vis to extract. May not be negative.
	 * @param transaction The transaction this operation is part of.
	 * @return A nonnegative integer not greater than maxAmount: the amount that was extracted.
	 */
	long extract(long maxAmount, TransactionContext transaction);

	/**
	 * Return the current amount of vis that is stored.
	 */
	long getAmount();

	/**
	 * Return the maximum amount of vis that could be stored.
	 */
	long getCapacity();
}
