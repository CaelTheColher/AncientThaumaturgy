package me.cael.ancientthaumaturgy.vis.impl;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import me.cael.ancientthaumaturgy.vis.api.base.DelegatingVisStorage;
import me.cael.ancientthaumaturgy.vis.api.base.SimpleBatteryItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Note: instances of this class do not perform any context validation,
 * that is handled by the DelegatingVisStorage they are wrapped behind.
 */
@SuppressWarnings({"UnstableApiUsage"})
public class SimpleItemVisStorageImpl implements VisStorage {
	public static VisStorage createSimpleStorage(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		StoragePreconditions.notNegative(capacity);
		StoragePreconditions.notNegative(maxInsert);
		StoragePreconditions.notNegative(maxExtract);

		Item startingItem = ctx.getItemVariant().getItem();

		return new DelegatingVisStorage(
				new SimpleItemVisStorageImpl(ctx, capacity, maxInsert, maxExtract),
				() -> ctx.getItemVariant().isOf(startingItem) && ctx.getAmount() > 0
		);
	}

	private final ContainerItemContext ctx;
	private final long capacity;
	private final long maxInsert, maxExtract;

	private SimpleItemVisStorageImpl(ContainerItemContext ctx, long capacity, long maxInsert, long maxExtract) {
		this.ctx = ctx;
		this.capacity = capacity;
		this.maxInsert = maxInsert;
		this.maxExtract = maxExtract;
	}

	/**
	 * Try to set the vis of the stack to {@code energyAmountPerCount}, return true if success.
	 */
	private boolean trySetEnergy(long energyAmountPerCount, long count, TransactionContext transaction) {
		ItemStack newStack = ctx.getItemVariant().toStack();
		SimpleBatteryItem.setStoredVisUnchecked(newStack, energyAmountPerCount);
		ItemVariant newVariant = ItemVariant.of(newStack);

		// Try to convert exactly `count` items.
		try (Transaction nested = transaction.openNested()) {
			if (ctx.extract(ctx.getItemVariant(), count, nested) == count && ctx.insert(newVariant, count, nested) == count) {
				nested.commit();
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsert > 0;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long insertedPerCount = Math.min(maxInsert, Math.min(maxAmountPerCount, capacity - currentAmountPerCount));

		if (insertedPerCount > 0) {
			if (trySetEnergy(currentAmountPerCount + insertedPerCount, count, transaction)) {
				return insertedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtract > 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		long count = ctx.getAmount();

		long maxAmountPerCount = maxAmount / count;
		long currentAmountPerCount = getAmount() / count;
		long extractedPerCount = Math.min(maxExtract, Math.min(maxAmountPerCount, currentAmountPerCount));

		if (extractedPerCount > 0) {
			if (trySetEnergy(currentAmountPerCount - extractedPerCount, count, transaction)) {
				return extractedPerCount * count;
			}
		}

		return 0;
	}

	@Override
	public long getAmount() {
		return ctx.getAmount() * SimpleBatteryItem.getStoredVisUnchecked(ctx.getItemVariant().getNbt());
	}

	@Override
	public long getCapacity() {
		return ctx.getAmount() * capacity;
	}
}
