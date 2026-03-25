package com.groupec.feature.printreceiptguide

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.HelpInfoCard
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.LightBlue
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrintReceiptGuideScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val amazonUrl = "https://www.amazon.com/Thermal-Receipt-MUNBYN-Compatible-Business/dp/B0753B5KYG/ref=sr_1_3?crid=39HW6EKOMWSH4&dib=eyJ2IjoiMSJ9.Hl_JZLTiZJ-QEUDKflZXXKQHA0Dt3eCyH5AgvLe59FGdAgtPmv3hFki5hrcRApqZU9JQOBHfmnxry07W_0DCoGyniMUsa6Vvd7rzDNVstPMjryuE065fBg7JQnzT3wFSKFr9QDa3z_6jfEjwYScOBGCzUhVR1OKR7PlCwP_VcW3a-MQ36E3VxUN9XTAfetaiGSn9klE6Ofz3slJ3f5FiRR09OmQKBmOktrwMeB20DNY.p16QiT3dqjgKBcWxXN5InA9BD4s01UK2Gzq95jCvvZo&dib_tag=se&keywords=MUNBYN+IMP001&qid=1774194746&sprefix=munbyn+imp001%2Caps%2C192&sr=8-3"
        val aliexpressUrl = "https://fr.aliexpress.com/item/1005011680649867.html?spm=a2g0o.productlist.main.3.7828oToSoToSsa&algo_pvid=23a95f46-8af7-4811-80a0-6b6e08b2cc54&algo_exp_id=23a95f46-8af7-4811-80a0-6b6e08b2cc54-2&pdp_ext_f=%7B%22order%22%3A%221%22%2C%22eval%22%3A%221%22%2C%22fromPage%22%3A%22search%22%7D&pdp_npi=6%40dis%21EUR%2143.06%2139.79%21%21%21334.80%21309.40%21%40212a70c117742149669694968ee7ed%2112000056228627482%21sea%21FR%216079616976%21X%211%210%21n_tag%3A-29919%3Bd%3Aa8168a7d%3Bm03_new_user%3A-29895&curPageLogUid=gMYOwvPeTfbz&utparam-url=scene%3Asearch%7Cquery_from%3A%7Cx_object_id%3A1005011680649867%7C_p_origin_prod%3A"


        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.print_receipt_guide_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.print_receipt_guide_intro),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        GuideSection(number = 1, title = stringResource(R.string.print_receipt_step_1_title)) {
            PrinterCard(
                title = stringResource(R.string.print_receipt_step_1_card_2_title),
                description = stringResource(R.string.print_receipt_step_1_card_2_description),
                painter = painterResource(R.drawable.xprinter),
                buttonLabel = stringResource(R.string.print_receipt_step_1_card_2_cta),
                buttonColor = Green,
                accentColor = Primary,
                containerColor = White,
                url = aliexpressUrl
            )
            PrinterCard(
                title = stringResource(R.string.print_receipt_step_1_card_1_title),
                description = stringResource(R.string.print_receipt_step_1_card_1_description),
                painter = painterResource(R.drawable.imp001),
                buttonLabel = stringResource(R.string.print_receipt_step_1_card_1_cta),
                buttonColor = Primary,
                accentColor = LightBlue,
                containerColor = White,
                url = amazonUrl
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = White,
                tonalElevation = 2.dp,
                shadowElevation = 1.dp
            ) {
                Text(
                    modifier = Modifier
                        .border(width = 2.dp, color = Primary, shape = RoundedCornerShape(18.dp))
                        .padding(16.dp),
                    text = stringResource(R.string.print_receipt_step_1_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        GuideSection(number = 2, title = stringResource(R.string.print_receipt_step_2_title)) {
            InstructionCard(text = stringResource(R.string.print_receipt_step_2_body))
        }

        GuideSection(number = 3, title = stringResource(R.string.print_receipt_step_3_title)) {
            InstructionCard(text = stringResource(R.string.print_receipt_step_3_body))
        }

        GuideSection(number = 4, title = stringResource(R.string.print_receipt_step_4_title)) {
            UsageCard(
                title = stringResource(R.string.print_receipt_step_4_card_1_title),
                body = stringResource(R.string.print_receipt_step_4_card_1_body),
                iconTint = Green
            )
            UsageCard(
                title = stringResource(R.string.print_receipt_step_4_card_2_title),
                body = stringResource(R.string.print_receipt_step_4_card_2_body),
                iconTint = Primary
            )
        }

        HelpInfoCard(
            title = stringResource(R.string.print_receipt_help_title),
            description = stringResource(R.string.print_receipt_help_body),
            lines = listOf(
                stringResource(R.string.print_receipt_help_email) to AppIcons.ShareByEmail,
                //stringResource(R.string.print_receipt_help_chat) to AppIcons.Chat
            )
        )
    }
}

@Composable
private fun GuideSection(
    number: Int,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = White,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
            )
            content()
        }
    }
}

@Composable
private fun PrinterCard(
    title: String,
    description: String,
    painter: Painter,
    buttonLabel: String,
    buttonColor: Color,
    accentColor: Color,
    containerColor: Color,
    url: String
) {
    val uriHandler  = LocalUriHandler.current

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrinterVisual(accentColor = accentColor, painter = painter)
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            /*Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )*/
            DefaultButton(
                text = buttonLabel,
                modifier = Modifier.fillMaxWidth(),
                containerColor = buttonColor,
                onClick = {
                    uriHandler.openUri(url)
                }
            )
        }
    }
}

@Composable
private fun PrinterVisual(
    accentColor: Color,
    painter : Painter,
    ) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp)),
            //.background(accentColor.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.print_receipt_printer_visual_desc)
        )
    }
}

@Composable
private fun InstructionCard(text: String) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UsageCard(
    title: String,
    body: String,
    iconTint: Color
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Print,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}