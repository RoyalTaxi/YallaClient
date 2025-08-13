package uz.yalla.client.feature.payment.card_list.model

import uz.yalla.client.feature.payment.card_list.view.CardListIntent

/**
 * Extension function to handle intents for the CardListViewModel.
 * This separates the intent handling logic from the ViewModel implementation.
 */
 fun CardListViewModel.onIntent(intent: CardListIntent) {
    when (intent) {
        is CardListIntent.LoadCardList -> {
            getCardList()
        }
        
        is CardListIntent.OnDeleteCard -> {
            setSelectedCardId(intent.cardId)
        }
        
        is CardListIntent.ConfirmDeleteCard -> {
            deleteCard(intent.cardId)
            setConfirmDeleteCardDialogVisibility(false)
        }
        
        is CardListIntent.DismissDeleteConfirmationDialog -> {
            setConfirmDeleteCardDialogVisibility(false)
        }
        
        is CardListIntent.EditCards -> {
            setEditCardEnabled(intent.editCardEnabled)
        }
        
        is CardListIntent.SelectPaymentType -> {
            selectPaymentType(intent.paymentType)
        }
        
        is CardListIntent.DismissErrorDialog -> {
            dismissErrorDialog()
        }
        
        // Navigation intents are handled in the Route
        is CardListIntent.AddNewCard,
        is CardListIntent.AddCorporateAccount,
        is CardListIntent.AddBusinessAccount,
        is CardListIntent.OnNavigateBack,
        is CardListIntent.SelectDefaultCard -> {
            // These are handled in the Route
        }
    }
}