package com.lookmyup.trojanhorse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CaisseRegionaleScreen() {
    val caissesList = listOf(
        CaisseRegionale("Alpes Provence", listOf("04", "05", "13", "84")),
        CaisseRegionale("Alsace Vosges", listOf("67", "68", "88")),
        CaisseRegionale("Anjou Maine", listOf("49", "53", "61", "72")),
        CaisseRegionale("Aquitaine", listOf("32", "33", "40", "47")),
        CaisseRegionale("Atlantique Vendée", listOf("44", "85")),
        CaisseRegionale("Brie Picardie", listOf("45", "60", "77", "80")),
        CaisseRegionale("Centre Est", listOf("01", "07", "26", "38", "69", "71")),
        CaisseRegionale(
            "Centre France",
            listOf("03", "15", "23", "36", "42", "43", "48", "58", "63")
        ),
        CaisseRegionale("Alpes Provence", listOf("04", "05", "13", "84")),
        CaisseRegionale("Alsace Vosges", listOf("67", "68", "88")),
        CaisseRegionale("Anjou Maine", listOf("49", "53", "61", "72")),
        CaisseRegionale("Aquitaine", listOf("32", "33", "40", "47")),
        CaisseRegionale("Atlantique Vendée", listOf("44", "85")),
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Ma Caisse régionale",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choisissez dans la liste",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                placeholder = { Text("Caisse régionale ou N° département") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Green
                    )
                },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(
                    unfocusedContainerColor = Color.Gray.copy(alpha = 0.1f),
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            // List of Caisses
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(caissesList) { caisse ->
                    CaisseRegionaleItem(caisse = caisse)
                }
            }
        }
    }
}

@Composable
fun CaisseRegionaleItem(caisse: CaisseRegionale) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // CA Logo
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White)
                .clip(RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CA",
                color = Color(0xFF006A4D),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = caisse.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Départements ${caisse.departments.joinToString(" · ")}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

data class CaisseRegionale(
    val name: String,
    val departments: List<String>
)

@Preview(showBackground = true)
@Composable
fun CaisseRegionaleScreenPreview() {
    MaterialTheme {
        CaisseRegionaleScreen()
    }
}