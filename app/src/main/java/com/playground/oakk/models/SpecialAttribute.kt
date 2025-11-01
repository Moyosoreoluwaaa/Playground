package com.playground.oakk.models

/**
 * Represents a special attribute for a financial product.
 */
sealed class SpecialAttribute {
    data class Points(val value: Int) : SpecialAttribute()
    data class Badge(val iconUrl: String) : SpecialAttribute()
}
/**
 * Represents a user's financial product, such as a card or an account.
 *
 * @param id Unique identifier for the product.
 * @param name The display name of the product.
 * @param imageUrl A URL to the product's logo or image.
 * @param balance The current balance, pre-formatted for display.
 * @param specialAttribute An optional special characteristic, like reward points.
 */
data class Product(
    val id: String,
    val name: String,
    val imageUrl: String,
    val balance: String,
    val specialAttribute: SpecialAttribute? = null
)

/**
 * Represents a single financial transaction.
 *
 * @param id Unique identifier for the transaction.
 * @param merchantName The name of the merchant.
 * @param merchantLogoUrl A URL to the merchant's logo.
 * @param amount The transaction amount, pre-formatted for display.
 * @param type The category of the transaction (e.g., "Purchase").
 * @param date The date of the transaction, pre-formatted for display.
 */
data class Transaction(
    val id: String,
    val merchantName: String,
    val merchantLogoUrl: String,
    val amount: String,
    val type: String,
    val date: String
)