package uz.yalla.client.feature.payment.card_list.model

/**
 * Side effects for the card list feature.
 * These are one-time events that should be handled by the UI.
 */
sealed interface CardListSideEffect {
    /**
     * Navigate back to the previous screen.
     */
    data object NavigateBack : CardListSideEffect
    
    /**
     * Navigate to the add new card screen.
     */
    data object NavigateToAddCard : CardListSideEffect
    
    /**
     * Navigate to add corporate account screen.
     */
    data object NavigateToAddCorporateAccount : CardListSideEffect
    
    /**
     * Navigate to add business account screen.
     */
    data object NavigateToAddBusinessAccount : CardListSideEffect
    
    /**
     * Show the delete card confirmation dialog.
     * @param cardId The ID of the card to delete.
     */
    data class ShowDeleteConfirmation(val cardId: String) : CardListSideEffect
    
    /**
     * Show an error message.
     * @param messageId The resource ID of the error message.
     */
    data class ShowError(val messageId: Int) : CardListSideEffect
}