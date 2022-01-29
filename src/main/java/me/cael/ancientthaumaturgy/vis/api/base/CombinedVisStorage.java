/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.cael.ancientthaumaturgy.vis.api.base;

import me.cael.ancientthaumaturgy.vis.api.VisStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * A {@link VisStorage} wrapping multiple storages.
 *
 * <p>The storages passed to {@linkplain CombinedVisStorage#CombinedVisStorage the constructor} will be iterated in order.
 *
 * @param <S> The class of every part. {@code ? extends VisStorage<T>} can be used if the parts are of different types.
 */
@ApiStatus.Experimental
public class CombinedVisStorage<S extends VisStorage> implements VisStorage {
    public List<S> parts;

    public CombinedVisStorage(List<S> parts) {
        this.parts = parts;
    }

    @Override
    public boolean supportsInsertion() {
        for (S part : parts) {
            if (part.supportsInsertion()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (S part : parts) {
            amount += part.insert(maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public boolean supportsExtraction() {
        for (S part : parts) {
            if (part.supportsExtraction()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (S part : parts) {
            amount += part.extract(maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    public long getAmount() {
        long totalAmount = 0;
        for (S part : parts) {
            totalAmount += part.getAmount();
        }
        return totalAmount;
    }

    @Override
    public long getCapacity() {
        long totalCapacity = 0;
        for (S part : parts) {
            totalCapacity += part.getCapacity();
        }
        return totalCapacity;
    }
}
