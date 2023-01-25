package com.example.suitup.common

import suitup.BuildConfig

object Constants {
    //Room
    const val DB_NAME = "suitup"

    //Retrofit
    const val BASE_URL = "https://api.yelp.com/v3/"
    const val YELP_API_KEY = BuildConfig.YELP_API_KEY
    const val NEARBY_LIMIT = 4
    const val HOT_NEW_LIMIT = 5
    const val DEALS_LIMIT = 5
    const val defaultCategory = "fashion"

    //DataStore
    const val DATASTORE_NAME = "settings"

    val FASHION_MNIST_LABELS = arrayOf(
        "T-shirt/top",
        "Trouser",
        "Pullover",
        "Dress",
        "Coat",
        "Sandal",
        "Shirt",
        "Sneaker",
        "Bag",
        "Ankle boot"
    )
    val FASHION_KEGGLE_LABELS = arrayOf(
        "Bottomwear", "Shoes", "Topwear", "Sandal", "Socks", "Bags", "Jewellery",
        "Innerwear", "Belts", "Makeup", "Nails", "Lips", "Saree", "Fragrance", "Watches",
        "Headwear", "Ties", "Free Gifts", "Dress", "Wallets", "Eyewear", "Flip Flops",
        "Apparel Set", "Loungewear and Nightwear", "Scarves", "Eyes", "Skin",
        "Accessories", "Skin Care", "Shoe Accessories", "Mufflers",
        "Beauty Accessories", "Bath and Body", "Gloves", "Water Bottle",
        "Sports Accessories"
    )

    val FASHION_KEGGLE_LABELS_ARTICLE = arrayOf(
        "Pendant", "Clutches", "Belts", "Tshirts", "Sports Shoes", "Casual Shoes",
        "Bra", "Flip Flops", "Trousers", "Bracelet", "Sandals", "Sweatshirts", "Ring",
        "Heels", "Kurtas", "Wallets", "Shirts", "Track Pants", "Sarees", "Tops",
        "Backpacks", "Briefs", "Flats", "Trunk", "Deodorant", "Innerwear Vests",
        "Lounge Pants", "Lipstick", "Bath Robe", "Watches", "Capris", "Bangle",
        "Travel Accessory", "Formal Shoes", "Shorts", "Ties", "Free Gifts", "Handbags",
        "Sunglasses", "Caps", "Sweaters", "Dresses", "Dupatta", "Earrings", "Jeans",
        "Scarves", "Rain Jacket", "Tracksuits", "Perfume and Body Mist", "Skirts",
        "Face Moisturisers", "Socks", "Suspenders", "Fragrance Gift Set", "Eyeshadow",
        "Nail Polish", "Foundation and Primer", "Messenger Bag", "Night suits",
        "Boxers", "Robe", "Jackets", "Lip Care", "Face Wash and Cleanser",
        "Kurta Sets", "Kajal and Eyeliner", "Kurtis", "Waistcoat", "Tunics", "Blazers",
        "Laptop Bag", "Lip Gloss", "Mobile Pouch", "Duffel Bag", "Headband",
        "Lip Liner", "Salwar and Dupatta", "Camisoles", "Jewellery Set", "Stockings",
        "Sports Sandals", "Patiala", "Leggings", "Mufflers", "Necklace and Chains",
        "Accessory Gift Set", "Lounge Shorts", "Highlighter and Blush", "Nightdress",
        "Baby Dolls", "Compact", "Jumpsuit", "Shoe Accessories", "Wristbands",
        "Tights", "Mask and Peel", "Trolley Bag", "Lounge Tshirts", "Eye Cream",
        "Swimwear", "Nail Essentials", "Face Scrub and Exfoliator", "Shrug",
        "Shoe Laces", "Water Bottle", "Beauty Accessory", "Churidar", "Gloves"
    )

    //Adapter Data
    val ATTRIBUTES =
        mapOf(
            "" to "",
            "Hot and New" to "hot_and_new",
            "Best Deals" to "deals"
        )
    val INTERVAL_RED = 0f..3.99f
    val INTERVAL_YELLOW = 4f..4.5f
    val INTERVAL_GREEN = 4.51f..5f
}