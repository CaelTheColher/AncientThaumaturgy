package me.cael.ancientthaumaturgy.vis.api.base;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

/**
 * A base vis storage implementation with a dynamic capacity, and per-side per-operation insertion and extraction limits.
 * {@link #getSideStorage} can be used to get a {@code VisStorage} implementation for a given side.
 * Make sure to override {@link #onFinalCommit} to call {@code markDirty} and similar functions.
 */
@SuppressWarnings({"unused", "UnstableApiUsage"})
public abstract class SimpleSidedVisContainer extends SnapshotParticipant<Long> {
	public long amount = 0;
	private final SideStorage[] sideStorages = new SideStorage[7];

	public SimpleSidedVisContainer() {
		for (int i = 0; i < 7; ++i) {
			sideStorages[i] = new SideStorage(i == 6 ? null : Direction.byId(i));
		}
	}

	/**
	 * @return The current capacity of this storage.
	 */
	public abstract long getCapacity();

	/**
	 * @return The maximum amount of vis that can be inserted in a single operation from the passed side.
	 */
	public abstract long getMaxInsert(@Nullable Direction side);

	/**
	 * @return The maximum amount of vis that can be extracted in a single operation from the passed side.
	 */
	public abstract long getMaxExtract(@Nullable Direction side);

	/**
	 * @return A {@link VisStorage} implementation for the passed side.
	 */
	public VisStorage getSideStorage(@Nullable Direction side) {
		return sideStorages[side == null ? 6 : side.getId()];
	}

	@Override
	protected Long createSnapshot() {
		return amount;
	}

	@Override
	protected void readSnapshot(Long snapshot) {
		amount = snapshot;
	}

	private class SideStorage implements VisStorage {
		private final Direction side;

		private SideStorage(Direction side) {
			this.side = side;
		}

		@Override
		public boolean supportsInsertion() {
			return getMaxInsert(side) > 0;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notNegative(maxAmount);

			long inserted = Math.min(getMaxInsert(side), Math.min(maxAmount, getCapacity() - amount));

			if (inserted > 0) {
				updateSnapshots(transaction);
				amount += inserted;
				return inserted;
			}

			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return getMaxExtract(side) > 0;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			StoragePreconditions.notNegative(maxAmount);

			long extracted = Math.min(getMaxExtract(side), Math.min(maxAmount, amount));

			if (extracted > 0) {
				updateSnapshots(transaction);
				amount -= extracted;
				return extracted;
			}

			return 0;
		}

		@Override
		public long getAmount() {
			return amount;
		}

		@Override
		public long getCapacity() {
			return SimpleSidedVisContainer.this.getCapacity();
		}
	}
}
