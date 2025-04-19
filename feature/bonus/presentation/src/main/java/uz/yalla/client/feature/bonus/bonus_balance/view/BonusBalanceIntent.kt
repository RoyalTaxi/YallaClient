package uz.yalla.client.feature.bonus.bonus_balance.view

internal sealed interface BonusBalanceIntent {
    data object OnNavigateBack : BonusBalanceIntent
}