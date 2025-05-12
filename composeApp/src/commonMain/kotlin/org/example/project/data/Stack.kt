package org.example.project.data

class Stack<T: Any> {
    private val items = mutableListOf<T>()

    fun push(item: T?) {
        item?.let {
            items.add(item)
        }
    }

    fun pop(): T? {
        return items.removeLastOrNull()
    }

    fun peek(): T? {
        return items.lastOrNull()
    }
}