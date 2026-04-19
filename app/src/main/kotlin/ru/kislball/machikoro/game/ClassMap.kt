package ru.kislball.machikoro.game

class ClassMap {
  private val map = HashMap<Class<*>, Any>()

  fun <T : Any> set(value: T) {
    map[value::class.java] = value
  }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> get(clazz: Class<T>): T = (map[clazz] as T?) ?: throw NoSuchElementException()

  @Suppress("UNCHECKED_CAST") fun <T : Any> getOrNull(clazz: Class<T>): T? = map[clazz] as T?

  fun <T : Any> contains(clazz: Class<T>): Boolean = map.containsKey(clazz)

  fun <T : Any> remove(clazz: Class<T>) {
    map.remove(clazz)
  }
}

inline fun <reified T : Any> ClassMap.get(): T = get(T::class.java)

inline fun <reified T : Any> ClassMap.getOrNull(): T? = getOrNull(T::class.java)

inline fun <reified T : Any> ClassMap.contains(): Boolean = contains(T::class.java)

inline fun <reified T : Any> ClassMap.remove() = remove(T::class.java)

