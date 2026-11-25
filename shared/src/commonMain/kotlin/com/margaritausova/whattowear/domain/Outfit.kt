package com.margaritausova.whattowear.domain

data class Outfit(val title: String, val items: String, val extra: String)

fun Weather.outfit(): Outfit {
    val items = when {
        apparentTemperature < -10 -> "Термобельё, свитер, тёплый пуховик и зимние ботинки."
        apparentTemperature < 0 -> "Свитер, пуховик, утеплённые брюки и ботинки."
        apparentTemperature < 10 -> "Джемпер, тёплая куртка, брюки и закрытая обувь."
        apparentTemperature < 18 -> "Лонгслив, лёгкая куртка, джинсы и кроссовки."
        apparentTemperature < 25 -> "Футболка, лёгкие брюки и кроссовки."
        else -> "Свободная футболка, шорты или льняные брюки и лёгкая обувь."
    }
    val extras = buildList {
        if (apparentTemperature < 0) add("Не забудь шапку и перчатки.")
        if (weatherCode in listOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)) {
            add(if (windSpeed >= 8) "Надень дождевик и непромокаемую обувь." else "Возьми зонт и надень непромокаемую обувь.")
        }
        if (weatherCode in listOf(71, 73, 75, 77, 85, 86)) add("Выбери обувь с нескользящей подошвой.")
        if (windSpeed >= 8) add("Нужен верхний слой, который защищает от ветра.")
        if (weatherCode in 0..1 && apparentTemperature >= 18) add("Пригодятся головной убор и солнечные очки.")
    }
    return Outfit("Что надеть", items, extras.joinToString(" ").ifEmpty { "Слой, который можно снять, всегда пригодится." })
}

fun Weather.condition(): String = when (weatherCode) {
    0 -> "Солнечно"
    1, 2 -> "Переменная облачность"
    3 -> "Пасмурно"
    45, 48 -> "Туман"
    51, 53, 55, 56, 57 -> "Моросит"
    61, 63, 65, 66, 67, 80, 81, 82 -> "Идёт дождь"
    71, 73, 75, 77, 85, 86 -> "Идёт снег"
    95, 96, 99 -> "Гроза"
    else -> "Погода сейчас"
}
