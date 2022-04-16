package com.annevonwolffen.domain

enum class Priority(val value: Int, val label: String, val serverName: String) {
    UNDEFINED(0, "Нет", "basic"),
    LOW(1, "Низкий", "low"),
    HIGH(2, "Высокий", "important")
}