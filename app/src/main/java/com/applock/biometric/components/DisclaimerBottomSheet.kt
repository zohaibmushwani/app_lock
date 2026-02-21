package com.applock.biometric.components

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DisclaimerBottomSheet(
//    onDismiss: () -> Unit,
//    onAccept: () -> Unit
//) {
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true
//    )
//
//    ModalBottomSheet(
//        onDismissRequest = onDismiss,
//        sheetState = sheetState,
//        containerColor = MaterialTheme.colorScheme.surface,
//        dragHandle = null,
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(24.sdp)
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Card(
//                modifier = Modifier.size(48.sdp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                ),
//                shape = RoundedCornerShape(12.sdp)
//            ) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Info,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
//                        modifier = Modifier.size(24.sdp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.sdp))
//
//            Text(
//                text = stringResource(R.string.disclaimer),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onSurface,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(24.sdp))
//
//            Surface(
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(16.sdp),
//                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
//            ) {
//                Text(
//                    text = stringResource(R.string.disclaimer_description),
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Start,
//                    modifier = Modifier.padding(20.sdp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.sdp))
//
//            // Accept button
//            Button(
//                onClick = onAccept,
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.sdp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Text(
//                    text = stringResource(R.string.accept_and_continue),
//                    style = MaterialTheme.typography.labelLarge,
//                    fontWeight = FontWeight.Medium,
//                    color = MaterialTheme.colorScheme.onPrimary,
//                    modifier = Modifier.padding(vertical = 4.sdp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.sdp))
//        }
//    }
//}


//@Composable
//fun DisclaimerBottomSheet(
//    show: Boolean,
//    onDismiss: () -> Unit,
//    onAccept: () -> Unit
//) {
//    BottomAlignedDialog(
//        showDialog = show,
//        onDismissRequest = onDismiss
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Card(
//                modifier = Modifier.size(48.dp),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer
//                ),
//                shape = RoundedCornerShape(12.dp)
//            ) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Info,
//                        contentDescription = stringResource(R.string.disclaimer),
//                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = stringResource(R.string.disclaimer),
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onSurface,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Surface(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(16.dp))
//                    .height(300.dp)
//                    .verticalScroll(
//                        rememberScrollState()
//                    ),
//                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
//            ) {
//                Text(
//                    text = stringResource(R.string.disclaimer_description),
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    textAlign = TextAlign.Start,
//                    modifier = Modifier.padding(20.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            Button(
//                onClick = onAccept,
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.primary
//                )
//            ) {
//                Text(
//                    text = stringResource(R.string.accept_and_continue),
//                    style = MaterialTheme.typography.labelLarge,
//                    fontWeight = FontWeight.Medium,
//                    color = MaterialTheme.colorScheme.onPrimary,
//                    modifier = Modifier.padding(vertical = 4.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}
