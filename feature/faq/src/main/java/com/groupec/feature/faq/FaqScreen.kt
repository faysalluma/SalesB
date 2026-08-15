package com.groupec.feature.faq

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.AppBadge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.White

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var expandedIndex by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        FaqItemCard(
            question = stringResource(R.string.faq_question_business_roles),
            expanded = expandedIndex == 0,
            onToggle = { expandedIndex = if (expandedIndex == 0) -1 else 0 }
        ) {
            HighlightedAnswerText(
                prefix = stringResource(R.string.faq_answer_business_roles_prefix),
                highlight = stringResource(R.string.faq_answer_business_roles_highlight),
                suffix = stringResource(R.string.faq_answer_business_roles_suffix)
            )
            AppBadge("salesb@groupec.net")
        }

        FaqItemCard(
            question = stringResource(R.string.faq_question_pc_install),
            expanded = expandedIndex == 1,
            onToggle = { expandedIndex = if (expandedIndex == 1) -1 else 1 }
        ) {
            Text(text = stringResource(R.string.faq_answer_pc_install))
        }

        FaqItemCard(
            question = stringResource(R.string.faq_question_delete_account),
            expanded = expandedIndex == 2,
            onToggle = { expandedIndex = if (expandedIndex == 2) -1 else 2 }
        ) {
            Text(text = stringResource(R.string.faq_answer_delete_account))
        }
    }
}

@Composable
private fun FaqItemCard(
    question: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TitleMedium(
                    modifier = Modifier.weight(1f),
                    title = question
                )
                Icon(
                    imageVector = AppIcons.ChevronDown,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (expanded) 180f else 0f)
                )
            }

            if (expanded) {
                Box(
                    modifier = Modifier
                        .padding(top = 22.dp, bottom = 24.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Silver2.copy(alpha = 0.55f))
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun HighlightedAnswerText(
    prefix: String,
    highlight: String,
    suffix: String
) {
    Text(
        text = buildAnnotatedString {
            append(prefix)
            append(" ")
            withStyle(
                style = SpanStyle(
                    color = Primary,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(highlight)
            }
            append(suffix)
        }
    )
}

