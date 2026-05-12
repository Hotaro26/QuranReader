package com.hotaro.quranreader.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hotaro.quranreader.ui.viewmodel.OnboardingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                when(page) {
                    0 -> Text("Step 1: Customise App")
                    1 -> Text("Step 2: Translation")
                    2 -> Text("Step 3: Ready!")
                }
            }
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        viewModel.completeOnboarding()
                        onComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(if (pagerState.currentPage < 2) "Next" else "Get Started")
            }
        }
    }
}
