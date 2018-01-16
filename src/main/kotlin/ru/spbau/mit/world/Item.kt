package ru.spbau.mit.world

/**
 * Type of the [Item].
 * @author belaevstanislav
 */
enum class ItemType(val params: Params) {
    SWORD(Params(attack = +4)),
    SHIELD(Params(armor = +3)),
    MAGIC_HAT(Params(hp = +3, maxHp = +3))
}

/**
 * Status of the [Item].
 * @author belaevstanislav
 */
enum class ItemStatus {
    DROPPED,
    USED,
    STORED;

    companion object {
        val DEFAULT = DROPPED
    }
}

/**
 * Abstract class whit private constuctor. To create an instace use
 * static factorty method pattern.
 * @author belaevstanislav
 */
abstract class Item private constructor(pos: Pos,
                                        val type: ItemType,
                                        var status: ItemStatus) : Unit(pos) {
    object Factory {
        fun create(type: ItemType,
                   pos: Pos = Pos.NULL,
                   status: ItemStatus = ItemStatus.DEFAULT): Item {
            return object : Item(pos, type, status) {}
        }
    }
}


/**
 * Useful class using for interactions with [Creature] items.
 * @author belaevstanislav
 */
class Items {
    private val items = linkedSetOf<Item>()

    /**
     * Adding item to items.
     * @param item an item to add
     * @return Boolean if we added suckessfullt
     */
    fun addItem(item: Item) = items.add(item)

    /**
     * Deleting of item from items.
     * @param item an item to del
     * @return Boolean if we remove successfully
     */
    fun delItem(item: Item) = items.remove(item)

    /**
     * Adding item to items.
     * @return listOf()
     */
    fun getAll() = items.toList()

    /**
     * Getter for item.
     * @param status status of item
     * @return all items with part. status
     */
    fun get(status: ItemStatus) = getAll().filter { p -> p.status == status }

    /**
     * Getter for item with id.
     * @param status status of item
     * @return all items with part. status
     */
    fun getWithId(status: ItemStatus) = getAll().withIndex().filter { p -> p.value.status == status }

    /**
     * Getter for item with id.
     * @param id an id of item to get
     * @return get item using an id
     */
    fun getItem(id: Int) = getAll().toTypedArray()[id]

    /**
     * Finds a particular Item.
     * @param item an item to find
     * @return Item? maybe null because there is no such elements
     */
    fun findItem(item: Item) = getAll().find { p -> p === item }
}