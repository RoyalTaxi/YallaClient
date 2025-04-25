package uz.yalla.client.feature.bonus.bonus_account.view

internal sealed interface BonusAccountIntent {
    data object OnNavigateBack : BonusAccountIntent
    data object OnBonusClicked: BonusAccountIntent
}