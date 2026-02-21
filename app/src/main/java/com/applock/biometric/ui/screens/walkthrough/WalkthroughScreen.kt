package com.applock.biometric.ui.screens.walkthrough

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.applock.biometric.R
import com.applock.biometric.common.SharedPrefsHelper
import com.applock.biometric.ui.theme.AppLockTheme
import com.applock.biometric.navigation.Screen
import kotlinx.coroutines.launch


data class WalkthroughPage(
    val title: Int,
    val description: Int,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WalkthroughScreen(navController: NavHostController) {
    val pages = remember {
        listOf(
            WalkthroughPage(
                R.string.walkthrough_title_1,
                R.string.walkthrough_desc_1,
                R.drawable.ic_launcher_foreground
            ),
            WalkthroughPage(
                R.string.walkthrough_title_2,
                R.string.walkthrough_desc_2,
                R.drawable.ic_launcher_foreground
            ),
        )
    }


    val coroutineScope = rememberCoroutineScope()
    var pageCount by remember { mutableIntStateOf(2) }
    val pagerState = rememberPagerState(initialPage = 0) { pageCount }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                beyondViewportPageCount = 1
            ) { page ->
                PageContent(page = pages[page])
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp)
                    .height(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pageCount) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(
                                    0xFFE0E0E0
                                ),
                                shape = CircleShape
                            )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        val lastContentPage = if (pageCount == 2) 1 else 2
                        if (pagerState.currentPage == lastContentPage) {
                            SharedPrefsHelper.setWalkthroughCompleted()
                            navController.navigate(Screen.PinSetup.route) {
                                popUpTo(Screen.Walkthrough.route) { inclusive = true }
                            }
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val lastContentPage = if (pageCount == 2) 1 else 2
                    Text(
                        text = if (pagerState.currentPage == lastContentPage) stringResource(
                            R.string.get_started
                        ) else stringResource(
                            R.string.next
                        ),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }

        }
    }
}


@Composable
fun PageContent(page: WalkthroughPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = stringResource(R.string.walkthrough_image),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = stringResource(page.description),
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WalkthroughScreenPreview() {
    AppLockTheme {
        WalkthroughScreen(navController = rememberNavController())
    }
}


