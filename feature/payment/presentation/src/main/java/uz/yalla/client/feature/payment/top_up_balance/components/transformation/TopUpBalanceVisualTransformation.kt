package uz.yalla.client.feature.payment.top_up_balance.components.transformation

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import uz.yalla.client.feature.payment.R

class TopUpBalanceVisualTransformation(private val context: Context) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val formattedText = formatAmount(text.text)
        return TransformedText(
            AnnotatedString(formattedText),
            AmountOffsetMapper(text.text, formattedText)
        )
    }

    private fun formatAmount(input: String): String {
        return input.filter { it.isDigit() }
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()
    }

    inner class AmountOffsetMapper(private val original: String, private val transformed: String) :
        OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val formatted = formatNumber(original.take(offset))
            return formatted.length
        }

        override fun transformedToOriginal(offset: Int): Int {
            var count = 0
            var index = 0
            while (count < offset && index < transformed.length) {
                if (transformed[index] != ' ') count++
                index++
            }
            return count
        }
    }

    private fun formatNumber(input: String) = input.reversed()
        .chunked(3)
        .joinToString(" ")
        .reversed()
}