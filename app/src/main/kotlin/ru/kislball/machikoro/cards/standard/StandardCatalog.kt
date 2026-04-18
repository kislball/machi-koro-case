package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardIcon

val StandardCatalog =
    CardCatalog(
        RestaurantCard(
            activationRange = listOf(3),
            id = "cards.cafe",
            reward = 1,
            price = 2,
            icon = CardIcon.CAFE,
        ),
        RestaurantCard(
            activationRange = listOf(9, 10),
            id = "cards.family",
            reward = 2,
            price = 3,
            icon = CardIcon.CAFE,
        ),
        NatureCard(
            activationRange = listOf(1),
            reward = 1,
            icon = CardIcon.WHEAT,
            price = 1,
            id = "cards.wheat",
        ),
        NatureCard(
            activationRange = listOf(2),
            reward = 1,
            icon = CardIcon.CATTLE,
            price = 1,
            id = "cards.farm",
        ),
        NatureCard(
            activationRange = listOf(5),
            reward = 1,
            icon = CardIcon.MOUNTAINS,
            price = 3,
            id = "cards.reserve",
        ),
        NatureCard(
            activationRange = listOf(9),
            reward = 5,
            icon = CardIcon.MOUNTAINS,
            price = 6,
            id = "cards.mine",
        ),
        NatureCard(
            activationRange = listOf(10),
            reward = 3,
            icon = CardIcon.MOUNTAINS,
            price = 3,
            id = "cards.apples",
        ),
        MediumEnterpriseCard(
            id = "cards.bakery",
            activationRange = listOf(2, 3),
            price = 1,
            reward = 1,
            icon = CardIcon.SHOP,
            revenueFrom = null,
        ),
        MediumEnterpriseCard(
            id = "cards.supermarket",
            activationRange = listOf(4),
            price = 2,
            reward = 3,
            icon = CardIcon.SHOP,
            revenueFrom = null,
        ),
        MediumEnterpriseCard(
            id = "cards.cheese_factory",
            activationRange = listOf(7),
            price = 5,
            reward = 3,
            icon = CardIcon.SHOP,
            revenueFrom = CardIcon.CATTLE,
        ),
        MediumEnterpriseCard(
            id = "cards.furniture",
            activationRange = listOf(8),
            price = 3,
            reward = 3,
            icon = CardIcon.FACTORY,
            revenueFrom = CardIcon.MOUNTAINS,
        ),
        MediumEnterpriseCard(
            id = "cards.vegetables",
            activationRange = listOf(11, 12),
            price = 2,
            reward = 2,
            icon = CardIcon.VEGETABLES,
            revenueFrom = CardIcon.WHEAT,
        ),
        StadiumCard(),
    )
