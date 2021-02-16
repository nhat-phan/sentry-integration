package net.ntworld.sentryIntegration

class InstanceCache<K, V> {
    private val map = mutableMapOf<K, V>()

    fun get(key: K, factory: () -> V): V {
        val item = map[key]
        if (null !== item) {
            return item
        }

        val created = factory.invoke()
        map[key] = created
        return created
    }

    fun clear(key: K) {
        map.remove(key)
    }
}