package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.CardCatalog

val StandardCatalog =
    CardCatalog(
        listOf(
            RestaurantCard(
                acitvationRange = listOf(3),
                id = "cards.cafe",
                totalCards = 4,
                reward = 1,
                price = 2,
            ),
            RestaurantCard(
                acitvationRange = listOf(9, 10),
                id = "cards.family",
                totalCards = 4,
                reward = 2,
                price = 3,
            ),
        ))
