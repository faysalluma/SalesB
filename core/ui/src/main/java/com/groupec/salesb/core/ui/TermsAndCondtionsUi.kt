package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.HtmlText
import com.groupec.salesb.core.designsystem.component.StandardCheckbox
import com.groupec.salesb.core.designsystem.theme.Green

@Composable
fun TermsAndCondtionsUi(
    modifier: Modifier = Modifier,
    onCheckChanged: (Boolean) -> Unit
) {
    Box {
        val pleaseRead = stringResource(id = R.string.please_read)
        val termsLink = stringResource(id = R.string.terms_and_conditions_of_use)

        Row(
            modifier= Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StandardCheckbox { isChecked ->
                onCheckChanged(isChecked)
            }
            HtmlText(
                html = "$pleaseRead <a href=\"https://salesb.groupec.net/confidentiality.php\">$termsLink</a>",
                linkColor = Green
            )
        }
    }
}