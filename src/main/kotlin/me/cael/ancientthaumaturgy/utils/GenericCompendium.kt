package me.cael.ancientthaumaturgy.utils

import net.minecraft.util.Identifier

abstract class GenericCompendium<T: Any> {

    protected val map = mutableMapOf<Identifier, T>()

    protected open fun <E: T> register(string: String, entry: E): E {
        return register(identifier(string), entry)
    }

    protected open fun <E: T> register(identifier: Identifier, entry: E): E {
        map[identifier] = entry
        return entry
    }

    abstract fun initialize()

}