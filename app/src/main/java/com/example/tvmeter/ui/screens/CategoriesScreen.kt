package com.example.tvmeter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import com.example.tvmeter.ui.components.tvFocusable
import com.example.tvmeter.viewmodels.CategoriesViewModel

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel
) {
    val categories by viewModel.categories.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { viewModel.addCategory() },
                modifier = Modifier.tvFocusable()
            ) {
                Text("+ Add Category")
            }
        }

        TvLazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .tvFocusable()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = category.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${category.appCount} apps",
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "📂",
                            fontSize = 24.sp
                        )
                    }
                }
            }
        }
    }
}