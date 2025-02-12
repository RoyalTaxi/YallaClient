package uz.yalla.client.feature.android.payment.top_up_balance.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import uz.yalla.client.feature.android.payment.R
import uz.yalla.client.feature.core.design.theme.YallaTheme
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BalanceInputField(
    balance: String,
    modifier: Modifier = Modifier,
    onBalanceChange: (String) -> Unit
) {
    val currency = stringResource(R.string.fixed_cost).replace("%s", "").trim()
    // Create a scroll state for horizontal scrolling
    val scrollState = rememberScrollState()
    // Optionally, to auto-scroll to the end, you can use BringIntoViewRequester:
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    LaunchedEffect(balance) {
        bringIntoViewRequester.bringIntoView()
    }

    // Use BasicTextField with a custom decorationBox that places the input text
    // and the trailing currency text in the same row.
    BasicTextField(
        value = balance,
        onValueChange = onBalanceChange,
        singleLine = true,
        textStyle = YallaTheme.font.headline.copy(color = YallaTheme.color.black),
        cursorBrush = SolidColor(YallaTheme.color.black),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        // Notice we don’t pass any built‑in decoration to avoid extra padding.
        decorationBox = { innerTextField ->
            // We wrap our content in a Box that fills the width and applies a horizontal scroll.
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    // Adjust the vertical padding as desired. Notice horizontal padding is set to zero.
                    .padding(vertical = 22.dp)
                    .horizontalScroll(scrollState)
                    // Bring-into-view ensures that when the text changes, the trailing text is visible.
                    .then(Modifier.bringIntoViewRequester(bringIntoViewRequester))
            ) {
                // Use a Row so that the inner text and the trailing text are placed next to each other.
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // You can add spacing here if needed (or leave it 0 if you want them tight).
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    // Draw the actual text field content.
                    innerTextField()

                    // Only show the trailing text if there is some input.
                    if (balance.isNotEmpty()) {
                        Text(
                            text = currency,
                            style = YallaTheme.font.headline,
                            color = YallaTheme.color.black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    )
}

class NumberWithSpaceVisualTransformation(private val currency: String) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText = formatNumberWithSpaces(text.text)
        return TransformedText(
            AnnotatedString(formattedText),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return formattedText.length
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return text.text.length
                }
            }
        )
    }
}

private fun formatNumberWithSpaces(input: String): String {
    if (input.isEmpty()) return ""

    val formatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    return formatter.format(input.toLongOrNull() ?: 0).replace(",", " ")
}