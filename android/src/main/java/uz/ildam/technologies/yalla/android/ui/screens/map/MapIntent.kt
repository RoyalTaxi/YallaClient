package uz.ildam.technologies.yalla.android.ui.screens.map

sealed interface MapIntent {
    data object MoveToMyLocation : MapIntent
    data object MoveToMyRoute : MapIntent
    data object MoveToFirstLocation : MapIntent
    data object OpenDrawer : MapIntent
    data object DiscardOrder : MapIntent
}