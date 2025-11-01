import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.language.domain.confettiParty

// Konfetti Library Imports
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit


/**
 * Main Composable that shows the button and triggers the confetti.
 */
@Composable
fun ConfettiCelebrationScreen() {
    // 1. State: The list of parties currently active. Initially empty.
    var parties by remember { mutableStateOf(listOf<Party>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 2. Confetti View: Observes the 'parties' state.
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = parties
        )

        // 3. Button: Triggers the animation on click.
        Button(
            onClick = {
                // Update the state: Assign a new list containing the party config.
                // This triggers the KonfettiView to recompose and start the burst.
                parties = listOf(confettiParty)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("🎉 Celebrate! 🎉")
        }
    }
}

// --- PREVIEW COMPOSABLE ---

/**
 * A Composable that shows the confetti immediately for design preview purposes.
 */
@Composable
fun PreviewConfettiRunning() {
    // Initialize the parties list with the configuration to see it playing
    val previewParties = listOf(confettiParty)

    Box(modifier = Modifier.fillMaxSize()) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = previewParties
        )

        // Button shown for context, but is non-functional in Preview
        Button(
            onClick = {confettiParty },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            Text("🎉 Celebrate! 🎉")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfettiScreenPreview() {
    PreviewConfettiRunning()
}