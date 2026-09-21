package gt.uvg.lab09.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface VersusNavKey: NavKey{
    @Serializable
    data object Catalog: VersusNavKey

    @Serializable
    data class Detail(val productId: Int): VersusNavKey

    @Serializable
    data class ProfileRoute(val profileId: Int) : VersusNavKey

    @Serializable
    data object Order : VersusNavKey
}
