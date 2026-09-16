package mg.iray.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import kotlinx.coroutines.launch

/**
 * Clic avec petit effet press visible - l’animation part avant [onClick]
 * (évite que la navigation coupe l’effet).
 */
@Composable
fun Modifier.irayFastClick(
    enabled: Boolean = true,
    role: Role? = Role.Button,
    onClick: () -> Unit,
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var busy by remember { mutableStateOf(false) }

    return this
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
            translationY = (1f - scale.value) * 24f
        }
        .clickable(
            enabled = enabled && !busy,
            interactionSource = interactionSource,
            indication = null,
            role = role,
            onClick = {
                if (busy) return@clickable
                busy = true
                scope.launch {
                    scale.animateTo(
                        targetValue = 0.955f,
                        animationSpec = tween(80, easing = FastOutSlowInEasing),
                    )
                    scale.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(100, easing = FastOutSlowInEasing),
                    )
                    onClick()
                    busy = false
                }
            },
        )
}
