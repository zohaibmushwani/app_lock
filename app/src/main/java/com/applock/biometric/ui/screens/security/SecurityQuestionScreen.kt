package com.applock.biometric.ui.screens.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.applock.biometric.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityQuestionScreen(
    navController: NavController,
    onSuccess: () -> Unit,
    viewModel: SecurityQuestionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showQuestionsDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Security Question") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Setup Recovery",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select a security question and provide an answer to recover your password if you forget it.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Question Selector
            OutlinedCard(
                onClick = { showQuestionsDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.selectedQuestion.ifEmpty { "Select a Question" },
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (uiState.selectedQuestion.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Answer Input
            OutlinedTextField(
                value = uiState.answer,
                onValueChange = viewModel::onAnswerChange,
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::saveSecurityQuestion,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.selectedQuestion.isNotEmpty() && uiState.answer.isNotEmpty()
            ) {
                Text("Save & Continue")
            }
        }
    }

    if (showQuestionsDialog) {
        AlertDialog(
            onDismissRequest = { showQuestionsDialog = false },
            title = { Text("Select Question") },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(viewModel.questions) { question ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (question == uiState.selectedQuestion),
                                    onClick = {
                                        viewModel.selectQuestion(question)
                                        showQuestionsDialog = false
                                    }
                                )
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = (question == uiState.selectedQuestion),
                                onClick = null 
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(text = question, modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQuestionsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
