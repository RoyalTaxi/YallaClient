package uz.yalla.client.feature.payment.card_list.view

/**
 * Represents user intents for the card list screen.
 * These are actions that the user can take on the UI.
 */
 sealed interface CardListIntent {
    /**
     * Select a default card by ID.
     * @param cardId The ID of the card to select.
     */
    data class SelectDefaultCard(val cardId: Int) : CardListIntent

    /**
     * Delete a card by ID.
     * @param cardId The ID of the card to delete.
     */
    data class OnDeleteCard(val cardId: String) : CardListIntent

    /**
     * Enable or disable edit mode.
     * @param editCardEnabled Whether edit mode should be enabled.
     */
    data class EditCards(val editCardEnabled: Boolean) : CardListIntent

    /**
     * Select a payment type.
     * @param paymentType The payment type to select.
     */
    data class SelectPaymentType(val paymentType: uz.yalla.client.core.domain.model.PaymentType) : CardListIntent

    /**
     * Confirm deletion of a card.
     * @param cardId The ID of the card to delete.
     */
    data class ConfirmDeleteCard(val cardId: String) : CardListIntent

    /**
     * Add a new card.
     */
    data object AddNewCard : CardListIntent

    /**
     * Navigate back to the previous screen.
     */
    data object OnNavigateBack : CardListIntent

    /**
     * Add a corporate account.
     */
    data object AddCorporateAccount : CardListIntent

    /**
     * Add a business account.
     */
    data object AddBusinessAccount : CardListIntent

    /**
     * Load the card list.
     */
    data object LoadCardList : CardListIntent

    /**
     * Dismiss the error dialog.
     */
    data object DismissErrorDialog : CardListIntent

    /**
     * Dismiss the delete confirmation dialog.
     */
    data object DismissDeleteConfirmationDialog : CardListIntent
}
