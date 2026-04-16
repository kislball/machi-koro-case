package ru.kislball.machikoro.cards

enum class CardKind(
    val type: CardType,
    val cardNameKey: String,
    val cardDescriptionKey: String,
    val basePrice: Int,
) {
    WHEAT_FIELD(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.wheat_field.name",
        cardDescriptionKey = "card.wheat_field.description",
        basePrice = 1,
    ),
    RANCH(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.ranch.name",
        cardDescriptionKey = "card.ranch.description",
        basePrice = 1,
    ),
    BAKERY(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.bakery.name",
        cardDescriptionKey = "card.bakery.description",
        basePrice = 1,
    ),
    CAFE(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.cafe.name",
        cardDescriptionKey = "card.cafe.description",
        basePrice = 2,
    ),
    CONVENIENCE_STORE(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.convenience_store.name",
        cardDescriptionKey = "card.convenience_store.description",
        basePrice = 2,
    ),
    FOREST(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.forest.name",
        cardDescriptionKey = "card.forest.description",
        basePrice = 3,
    ),
    STADIUM(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.stadium.name",
        cardDescriptionKey = "card.stadium.description",
        basePrice = 6,
    ),
    TV_STATION(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.tv_station.name",
        cardDescriptionKey = "card.tv_station.description",
        basePrice = 7,
    ),
    BUSINESS_CENTER(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.business_center.name",
        cardDescriptionKey = "card.business_center.description",
        basePrice = 8,
    ),
    CHEESE_FACTORY(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.cheese_factory.name",
        cardDescriptionKey = "card.cheese_factory.description",
        basePrice = 5,
    ),
    FURNITURE_FACTORY(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.furniture_factory.name",
        cardDescriptionKey = "card.furniture_factory.description",
        basePrice = 3,
    ),
    MINE(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.mine.name",
        cardDescriptionKey = "card.mine.description",
        basePrice = 6,
    ),
    FAMILY_RESTAURANT(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.family_restaurant.name",
        cardDescriptionKey = "card.family_restaurant.description",
        basePrice = 3,
    ),
    APPLE_ORCHARD(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.apple_orchard.name",
        cardDescriptionKey = "card.apple_orchard.description",
        basePrice = 3,
    ),
    FRUIT_AND_VEGETABLE_MARKET(
        type = CardType.ENTERPRISE,
        cardNameKey = "card.fruit_and_vegetable_market.name",
        cardDescriptionKey = "card.fruit_and_vegetable_market.description",
        basePrice = 2,
    ),
    TRAIN_STATION(
        type = CardType.SIGHT,
        cardNameKey = "card.train_station.name",
        cardDescriptionKey = "card.train_station.description",
        basePrice = 4,
    ),
    SHOPPING_MALL(
        type = CardType.SIGHT,
        cardNameKey = "card.shopping_mall.name",
        cardDescriptionKey = "card.shopping_mall.description",
        basePrice = 10,
    ),
    AMUSEMENT_PARK(
        type = CardType.SIGHT,
        cardNameKey = "card.amusement_park.name",
        cardDescriptionKey = "card.amusement_park.description",
        basePrice = 16,
    ),
    RADIO_TOWER(
        type = CardType.SIGHT,
        cardNameKey = "card.radio_tower.name",
        cardDescriptionKey = "card.radio_tower.description",
        basePrice = 22,
    ),
}
