package mg.iray.app.ui.components.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mg.iray.app.R
import mg.iray.app.ui.components.irayFastClick
import mg.iray.app.ui.theme.BrandAccent
import mg.iray.app.ui.theme.BrandWhite
import mg.iray.app.ui.theme.FlagRed
import mg.iray.app.ui.theme.IrayFontFamily
import mg.iray.app.ui.theme.IrayTheme
import mg.iray.app.ui.theme.OutlineOnWhite
import mg.iray.app.ui.theme.TextPrimary
import mg.iray.app.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Champ date de naissance — ouvre un calendrier (plus de saisie manuelle bizarre).
 * Affiche TT/VV/TTTT. La valeur reste gérée par l’écran parent (rememberSaveable).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    hint: String,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    var showPicker by rememberSaveable { mutableStateOf(false) }
    val hasError = !error.isNullOrBlank()
    val display = value.ifBlank { "" }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = null,
            tint = if (hasError) FlagRed else BrandAccent,
            modifier = Modifier
                .padding(top = 28.dp)
                .size(22.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = IrayFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                ),
                color = TextSecondary,
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = display,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                placeholder = {
                    Text(
                        text = hint,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = IrayFontFamily,
                            fontSize = 16.sp,
                        ),
                        color = TextSecondary.copy(alpha = 0.7f),
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = stringResource(R.string.birthdate_picker_cd),
                        tint = BrandAccent,
                    )
                },
                singleLine = true,
                isError = hasError,
                supportingText = if (hasError) {
                    {
                        Text(
                            text = error.orEmpty(),
                            color = FlagRed,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = IrayFontFamily,
                            ),
                        )
                    }
                } else {
                    null
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = IrayFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = TextPrimary,
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledContainerColor = BrandWhite,
                    disabledTextColor = TextPrimary,
                    disabledBorderColor = if (hasError) FlagRed else OutlineOnWhite,
                    disabledPlaceholderColor = TextSecondary.copy(alpha = 0.7f),
                    disabledTrailingIconColor = BrandAccent,
                    disabledLabelColor = TextSecondary,
                    errorBorderColor = FlagRed,
                    errorSupportingTextColor = FlagRed,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .irayFastClick(onClick = { showPicker = true }),
            )
        }
    }

    if (showPicker) {
        val initialMillis = parseBirthdateToUtcMillis(value)
            ?: defaultBirthdateMillis()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis,
            selectableDates = BirthdateSelectableDates,
        )

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onValueChange(formatUtcMillisToBirthdate(millis))
                        }
                        showPicker = false
                    },
                ) {
                    Text(stringResource(R.string.birthdate_picker_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.birthdate_picker_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private object BirthdateSelectableDates : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val now = System.currentTimeMillis()
        val min = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.YEAR, -120)
        }.timeInMillis
        val max = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            add(Calendar.YEAR, -1)
        }.timeInMillis
        return utcTimeMillis in min..minOf(max, now)
    }

    override fun isSelectableYear(year: Int): Boolean {
        val current = Calendar.getInstance().get(Calendar.YEAR)
        return year in (current - 120)..(current - 1)
    }
}

private fun defaultBirthdateMillis(): Long =
    Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.YEAR, get(Calendar.YEAR) - 25)
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 12)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun parseBirthdateToUtcMillis(value: String): Long? {
    val parts = value.trim().split("/")
    if (parts.size != 3) return null
    val day = parts[0].toIntOrNull() ?: return null
    val month = parts[1].toIntOrNull() ?: return null
    val year = parts[2].toIntOrNull() ?: return null
    return try {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            isLenient = false
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    } catch (_: Exception) {
        null
    }
}

private fun formatUtcMillisToBirthdate(millis: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return sdf.format(Date(millis))
}

@Preview(showBackground = true)
@Composable
private fun BirthdatePickerFieldPreview() {
    IrayTheme {
        BirthdatePickerField(
            value = "12/05/1990",
            onValueChange = {},
            label = "Daty nahaterahana *",
            hint = "Safidio ny daty",
            modifier = Modifier.padding(16.dp),
        )
    }
}
