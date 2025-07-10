package androidx.media3.demo.compose

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.seconds

/**
 * A very basic slider sample just to test the new scrubbing feature.
 * Not perfect should use progress state from main branches.
 */
@Composable
fun PlayerSlider(player: ExoPlayer, modifier: Modifier = Modifier) {
    var currentProgressPercent by remember { mutableFloatStateOf(0f) }
    var isScrubbing by remember { mutableStateOf(false) }
    var currentPlayerPosition by remember { mutableLongStateOf(player.currentPosition) }
    var currentPlayerDuration by remember { mutableLongStateOf(player.duration) }
    LaunchedEffect(Unit) {
        while (isActive) {
            currentPlayerDuration = player.duration.coerceAtLeast(1)
            currentPlayerPosition = player.currentPosition.coerceIn(0, currentPlayerDuration)
            delay(1.seconds)
        }
    }
    player.isScrubbingModeEnabled = isScrubbing
    if (!isScrubbing) {
        currentProgressPercent = currentPlayerPosition.toFloat() / currentPlayerDuration.toFloat()
    }

    Slider(
        value = currentProgressPercent,
        valueRange = 0f..1f,
        modifier = modifier,
        onValueChange = { percentPosition ->
            if (!isScrubbing) {
                player.setSeekParameters(SeekParameters.CLOSEST_SYNC)
                isScrubbing = true
            }
            currentProgressPercent = percentPosition
            player.seekTo((percentPosition * player.duration).toLong())
        },
        onValueChangeFinished = {
            player.setSeekParameters(SeekParameters.DEFAULT)
            isScrubbing = false
        }
    )
}
