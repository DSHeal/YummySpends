package com.dsheal.yummyspends.common

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

const val LOG_TAG_NAV_CONTROLLER = "NavController"

fun NavController.navigateSafe(
    direction: NavDirections,
    navOptions: NavOptions? = null,
    navExtras: Navigator.Extras? = null
) {
    val action =
        currentDestination?.getAction(direction.actionId) ?: graph.getAction(direction.actionId)
    if (action != null && currentDestination?.id != action.destinationId) {
        navigate(direction.actionId, direction.arguments, navOptions, navExtras)
    } else {
        Log.i(
            LOG_TAG_NAV_CONTROLLER,
            "Failed to navigate with action ${direction.actionId} from currentDestination ${currentDestination?.id}",
            null
        )
    }
}
