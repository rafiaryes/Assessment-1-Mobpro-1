package com.rafiarya0114.temperatureconversion.ui.screen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rafiarya0114.temperatureconversion.R
import com.rafiarya0114.temperatureconversion.navigation.Screen
import com.rafiarya0114.temperatureconversion.ui.theme.TemperatureConversionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onErrorContainer,
                    titleContentColor = MaterialTheme.colorScheme.errorContainer,
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.About.route)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.tentang_aplikasi),
                            tint = MaterialTheme.colorScheme.errorContainer
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier) {
    var inputValue by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf(false) }
    var selectedUnitFrom by remember { mutableStateOf("Celsius") }
    var selectedUnitTo by remember { mutableStateOf("Fahrenheit") }
    var result by remember { mutableStateOf("") }

    val temperatureUnits = listOf("Celsius", "Fahrenheit", "Kelvin")
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.intro),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = inputValue,
            onValueChange = { inputValue = it },
            label = { Text (text = stringResource(R.string.nilai_suhu)) },
            supportingText = {ErrorHint(inputError)},
            isError = inputError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.dari))
                DropdownMenuTemperature(
                    options = temperatureUnits,
                    selectedOption = selectedUnitFrom,
                    onOptionSelected = { selectedUnitFrom = it }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.ke))
                DropdownMenuTemperature(
                    options = temperatureUnits,
                    selectedOption = selectedUnitTo,
                    onOptionSelected = { selectedUnitTo = it }
                )
            }
        }

        Button(
            onClick = {
                inputError = inputValue.isEmpty()
                if (inputError) return@Button

                try {
                    val value = inputValue.toDoubleOrNull() ?: 0.0
                    result = convertTemperature(value, selectedUnitFrom, selectedUnitTo)
                } catch (e: Exception) {
                    inputError = true
                }
            },
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        ) {
            Text(text = stringResource(R.string.konversi))
        }

        if(result.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text(
                text = stringResource(R.string.hasil_konversi, result),
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = {
                    shareData(
                        context = context,
                        message = context.getString(
                            R.string.bagikan_template,
                            inputValue,
                            selectedUnitFrom,
                            result,
                            selectedUnitTo
                        )
                    )
                },
                modifier = Modifier.padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Text(text = stringResource(R.string.bagikan))
            }
        }
    }
}

fun convertTemperature(value: Double, from: String, to: String): String {
    val result = when {
        from.equals(to, ignoreCase = true) -> value
        from.equals("Celsius", ignoreCase = true) && to.equals("Fahrenheit", ignoreCase = true) -> value * 9.0/5.0 + 32.0
        from.equals("Celsius", ignoreCase = true) && to.equals("Kelvin", ignoreCase = true) -> value + 273.15
        from.equals("Fahrenheit", ignoreCase = true) && to.equals("Celsius", ignoreCase = true) -> (value - 32.0) * 5.0/9.0
        from.equals("Fahrenheit", ignoreCase = true) && to.equals("Kelvin", ignoreCase = true) -> (value - 32.0) * 5.0/9.0 + 273.15
        from.equals("Kelvin", ignoreCase = true) && to.equals("Celsius", ignoreCase = true) -> value - 273.15
        from.equals("Kelvin", ignoreCase = true) && to.equals("Fahrenheit", ignoreCase = true) -> (value - 273.15) * 9.0/5.0 + 32.0
        else -> value
    }
    return "%.2f".format(result)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuTemperature(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ) {
            options.forEach{ option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ErrorHint(isError: Boolean) {
    if (isError) {
        Text(text = stringResource(R.string.input_invalid))
    }
}

private fun shareData(context: Context, message: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply{
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    if (shareIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(shareIntent)
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    TemperatureConversionTheme {
        MainScreen(rememberNavController())
    }
}