package ru.kislball.machikoro.cards.standard

import ru.kislball.machikoro.cards.common.Card
import ru.kislball.machikoro.cards.common.CardCatalog
import ru.kislball.machikoro.cards.common.CardIcon
import ru.kislball.machikoro.cards.standard.enterprises.BusinessCentreCard
import ru.kislball.machikoro.cards.standard.enterprises.MediumEnterpriseCard
import ru.kislball.machikoro.cards.standard.enterprises.NatureCard
import ru.kislball.machikoro.cards.standard.enterprises.RestaurantCard
import ru.kislball.machikoro.cards.standard.enterprises.StadiumCard
import ru.kislball.machikoro.cards.standard.enterprises.TVCentreCard
import ru.kislball.machikoro.cards.standard.sights.EntertainmentParkCard
import ru.kislball.machikoro.cards.standard.sights.RailwayStationCard
import ru.kislball.machikoro.cards.standard.sights.TVTowerCard

object StandardCatalog :
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
        TVCentreCard(),
        BusinessCentreCard(),
        RailwayStationCard(),
        TVTowerCard(),
        EntertainmentParkCard(),
    ) {
  override fun getStarterCards(): List<Card> {
    return listOf(
        this["cards.wheat"]
            ?: throw IllegalStateException("Wheat card has not been added to standard catalog"),
        this["cards.bakery"]
            ?: throw IllegalStateException("Wheat card has not been added to standard catalog"),
    )
  }
}
