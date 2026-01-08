package com.procalc.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.procalc.app.ui.CalculatorAction
import com.procalc.app.ui.CalculatorUiState
import com.procalc.app.ui.CalculatorViewModel
import com.procalc.app.ui.components.CalculatorButton
import com.procalc.app.ui.theme.ProCalcTheme
import com.procalc.app.ui.theme.Orange40

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProCalcTheme {
                val viewModel = viewModel<CalculatorViewModel>()
                val state by viewModel.state.collectAsState()
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (state.isHistoryVisible) {
                        HistoryScreen(
                            state = state,
                            onClose = { viewModel.onAction(CalculatorAction.ToggleHistory) },
                            onClear = { viewModel.onAction(CalculatorAction.ClearHistory) },
                            onItemClick = { viewModel.onAction(CalculatorAction.UseHistoryItem(it)) }
                        )
                    } else {
                        CalculatorScreen(state, viewModel::onAction)
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit
) {
    val buttonSpacing = 12.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // Display Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                // History Button
                IconButton(
                    onClick = { onAction(CalculatorAction.ToggleHistory) },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.weight(1f))

                // Expression
                Text(
                    text = state.expression,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    maxLines = 2
                )
                // Result
                Text(
                    text = state.result,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )
            }
        }
        
        HorizontalDivider(thickness = 0.5.dp, color = Color.Gray)

        // Buttons Grid
        Column(verticalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            // Row 1: AC, Del, %, /
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(
                    symbol = "AC",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Color.LightGray,
                    contentColor = Color.Black,
                    onClick = { onAction(CalculatorAction.Clear) }
                )
                CalculatorButton(
                    symbol = "⌫",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Color.LightGray,
                    contentColor = Color.Black,
                    onClick = { onAction(CalculatorAction.Delete) }
                )
                CalculatorButton(
                    symbol = "%",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Color.LightGray,
                    contentColor = Color.Black,
                    onClick = { onAction(CalculatorAction.Operation("%")) }
                )
                CalculatorButton(
                    symbol = "÷",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Orange40,
                    contentColor = Color.White,
                    onClick = { onAction(CalculatorAction.Operation("÷")) }
                )
            }
            // Row 2: 7, 8, 9, x
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton("7", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(7)) }
                CalculatorButton("8", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(8)) }
                CalculatorButton("9", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(9)) }
                CalculatorButton(
                    symbol = "×",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Orange40,
                    contentColor = Color.White,
                    onClick = { onAction(CalculatorAction.Operation("×")) }
                )
            }
            // Row 3: 4, 5, 6, -
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton("4", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(4)) }
                CalculatorButton("5", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(5)) }
                CalculatorButton("6", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(6)) }
                CalculatorButton(
                    symbol = "-",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Orange40,
                    contentColor = Color.White,
                    onClick = { onAction(CalculatorAction.Operation("-")) }
                )
            }
            // Row 4: 1, 2, 3, +
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton("1", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(1)) }
                CalculatorButton("2", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(2)) }
                CalculatorButton("3", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(3)) }
                CalculatorButton(
                    symbol = "+",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Orange40,
                    contentColor = Color.White,
                    onClick = { onAction(CalculatorAction.Operation("+")) }
                )
            }
            // Row 5: ., 0, (, ), =
            // Custom layout for the last row to include more
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                CalculatorButton(".", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Decimal) }
                CalculatorButton("0", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Number(0)) }
                 CalculatorButton("(", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Operation("(")) }
                  CalculatorButton(")", Modifier.aspectRatio(1f).weight(1f)) { onAction(CalculatorAction.Operation(")")) }
                CalculatorButton(
                    symbol = "=",
                    modifier = Modifier.aspectRatio(1f).weight(1f),
                    backgroundColor = Orange40,
                    contentColor = Color.White,
                    onClick = { onAction(CalculatorAction.Calculate) }
                )
            }
             Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
                 CalculatorButton("√", Modifier.aspectRatio(2f).weight(1f)) { onAction(CalculatorAction.Operation("√")) }
                 CalculatorButton("^", Modifier.aspectRatio(2f).weight(1f)) { onAction(CalculatorAction.Operation("^")) }
             }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: CalculatorUiState,
    onClose: () -> Unit,
    onClear: () -> Unit,
    onItemClick: (com.procalc.app.data.HistoryItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Text("←", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = onClear) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { padding ->
        if (state.history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_history))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(state.history) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onItemClick(item) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.expression,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "= ${item.result}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}