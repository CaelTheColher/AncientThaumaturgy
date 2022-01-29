package me.cael.ancientthaumaturgy.vis.impl;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import me.cael.ancientthaumaturgy.vis.api.base.SimpleBatteryItem;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class VisImpl {
	static {
		VisStorage.ITEM.registerFallback((stack, ctx) -> {
			if (stack.getItem() instanceof SimpleBatteryItem battery) {
				return SimpleBatteryItem.createStorage(ctx, battery.getVisCapacity(), battery.getVisMaxInput(), battery.getVisMaxOutput());
			} else {
				return null;
			}
		});
	}

	public static final VisStorage EMPTY = new VisStorage() {
		@Override
		public boolean supportsInsertion() {
			return false;
		}

		@Override
		public long insert(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public boolean supportsExtraction() {
			return false;
		}

		@Override
		public long extract(long maxAmount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public long getAmount() {
			return 0;
		}

		@Override
		public long getCapacity() {
			return 0;
		}
	};
}
