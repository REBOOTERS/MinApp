package com.engineer.compose.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.engineer.compose.ui.ui.theme.MiniAppTheme


class ComposeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
//                NewFeatureScreen()
                MiniAppTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colorScheme.background) {
                        MessageCard(Message("lucy", "hello world"))
                    }
                }
            }
        }
    }
}

@Composable
fun NewFeatureScreen() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "11", style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "11", style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "11", style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { /* Handle click */ }, Modifier.fillMaxWidth()) {
            Text(text = "11")
        }
    }
}