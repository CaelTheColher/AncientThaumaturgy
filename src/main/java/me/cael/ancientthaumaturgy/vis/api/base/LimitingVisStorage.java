package me.cael.ancientthaumaturgy.vis.api.base;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Objects;

/**
 * A vis storage that will apply additional per-insert and per-extract limits to another storage.
 */
public class LimitingVisStorage implements VisStorage {
	protected final VisStorage backingStorage;
	protected final long maxInsert, maxExtract;

	/**
	 * Create a new limiting storage.
	 * @param backingStorage Storage to delegate to.
	 * @param maxInsert The maximum amount of vis that can be inserted in one operation.
	 * @param maxExtract The maximum amount of vis that can be extracted in one operation.
	 */
	public LimitingVisStorage(VisStorage backingStorage, long maxInsert, long maxExtract) {
		Objects.requireNonNull(backingStorage);
		StoragePreconditions.notNegative(maxInsert);
		StoragePreconditions.notNegative(maxExtract);

		this.backingStorage = backingStorage;
		this.maxInsert = maxInsert;
		this.maxExtract = maxExtract;
	}

	@Override
	public boolean supportsInsertion() {
		return maxInsert > 0 && backingStorage.supportsInsertion();
	}

	@Override
	public long insert(long maxAmount, TransactionContext transaction) {
		return backingStorage.insert(Math.min(maxAmount, maxInsert), transaction);
	}

	@Override
	public boolean supportsExtraction() {
		return maxExtract > 0 && backingStorage.supportsExtraction();
	}

	@Override
	public long extract(long maxAmount, TransactionContext transaction) {
		return backingStorage.extract(Math.min(maxAmount, maxExtract), transaction);
	}

	@Override
	public long getAmount() {
		return backingStorage.getAmount();
	}

	@Override
	public long getCapacity() {
		return backingStorage.getCapacity();
	}
}
