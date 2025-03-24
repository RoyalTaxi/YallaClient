package uz.yalla.client.feature.order.presentation.main.view.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import uz.yalla.client.core.common.button.PrimaryButton
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import uz.yalla.client.feature.order.presentation.R
import uz.yalla.client.feature.order.presentation.components.AddCommentField
import uz.yalla.client.feature.order.presentation.main.view.MainSheetIntent.OrderCommentSheetIntent

private const val MAX_COMMENT_LENGTH = 100
private val SheetTopCornerShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
private val RoundedShape = RoundedCornerShape(30.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCommentBottomSheet(
    sheetState: SheetState,
    comment: String,
    onIntent: (OrderCommentSheetIntent) -> Unit
) {
    var currentComment by remember { mutableStateOf(comment) }

    ModalBottomSheet(
        shape = SheetTopCornerShape,
        containerColor = YallaTheme.color.gray2,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = { onIntent(OrderCommentSheetIntent.OnDismissRequest(currentComment)) }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .background(YallaTheme.color.gray2)
                .fillMaxWidth()
        ) {
            CommentContent(
                comment = currentComment,
                onCommentChange = { newComment ->
                    if (newComment.length <= MAX_COMMENT_LENGTH) {
                        currentComment = newComment
                    }
                },
                onClearComment = { currentComment = "" }
            )

            CommentFooter(
                onClose = { onIntent(OrderCommentSheetIntent.OnDismissRequest(currentComment)) }
            )
        }
    }
}

@Composable
private fun CommentContent(
    comment: String,
    onCommentChange: (String) -> Unit,
    onClearComment: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(
                color = YallaTheme.color.white,
                shape = RoundedShape
            )
            .padding(20.dp)
    ) {
        CommentHeader(
            title = stringResource(R.string.comment),
            onClearClick = onClearComment
        )

        CommentField(
            comment = comment,
            onCommentChange = onCommentChange
        )
    }
}

@Composable
private fun CommentHeader(
    title: String,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = YallaTheme.font.title,
            color = YallaTheme.color.black
        )

        TextButton(onClick = onClearClick) {
            Text(
                text = "Clear",
                color = YallaTheme.color.black,
                style = YallaTheme.font.label,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}

@Composable
private fun CommentField(
    comment: String,
    onCommentChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val shouldClearFocus by remember(comment) {
        derivedStateOf { comment.length >= MAX_COMMENT_LENGTH }
    }

    if (shouldClearFocus) {
        focusRequester.freeFocus()
    }

    Column {
        AddCommentField(
            commentText = comment,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.3f)
                .focusRequester(focusRequester),
            onCommentChange = onCommentChange
        )

        CommentCounter(current = comment.length)
    }
}

@Composable
private fun CommentCounter(
    current: Int
) {
    Text(
        text = "$current/$MAX_COMMENT_LENGTH",
        style = YallaTheme.font.label,
        color = YallaTheme.color.gray,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun CommentFooter(
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier.background(
            color = YallaTheme.color.white,
            shape = SheetTopCornerShape
        )
    ) {
        PrimaryButton(
            text = stringResource(R.string.close),
            onClick = onClose,
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        )
    }
}