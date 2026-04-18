package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardIcon

val StandardCatalog =
    CardCatalog(
        RestaurantCard(
            activationRange = listOf(3),
            id = "cards.cafe",
            totalCards = 4,
            reward = 1,
            price = 2,
            icon = CardIcon.CAFE,
        ),
        RestaurantCard(
            activationRange = listOf(9, 10),
            id = "cards.family",
            totalCards = 4,
            reward = 2,
            price = 3,
            icon = CardIcon.CAFE,
        ),
    )
