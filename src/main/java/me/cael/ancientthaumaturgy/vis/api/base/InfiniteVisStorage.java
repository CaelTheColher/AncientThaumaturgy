package me.cael.ancientthaumaturgy.vis.api.base;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

/**
 * A vis storage that can't accept vis, but will allow extracting any amount of vis.
 * Creative batteries are a possible use case.
 * {@link #INSTANCE} can be used instead of creating a new object every time.
 */
public class InfiniteVisStorage implements VisStorage {
	public static final InfiniteVisStorage INSTANCE = new InfiniteVisStorage();

	@Override
	public boolean supportsInsertion() {
		return false;
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		return 0;
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		return maxAmount;
	}

	@Override
	public long getAmount() {
		return Long.MAX_VALUE;
	}

	@Override
	public long getCapacity() {
		return Long.MAX_VALUE;
	}
}