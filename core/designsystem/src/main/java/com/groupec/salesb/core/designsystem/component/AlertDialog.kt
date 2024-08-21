package com.groupec.salesb.core.designsystem.component

import android.app.Activity
import android.os.Handler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.utils.Constants

@Composable
fun AppAlertInfoDialog(
    title: String?,
    icon: Painter?,
    tintIcon: Color?,
    message: String,
    textButton: String,
    closing: Activity?
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                // openDialog.value = false
            },
            icon = {
                icon?.let {
                    Icon(
                        it,
                        contentDescription = null,
                        tint = tintIcon ?: Primary,
                        modifier = Modifier.size(70.dp)
                    )
                }
            },
            title = {
                title?.let {
                    Text(it, color = Primary)
                }
            },
            text = {
                Text(text = message, textAlign = TextAlign.Center)
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DefaultButton(onClick = {
                        openDialog.value = false
                        closing?.finish()
                    }, text = textButton)
                }
            },
            dismissButton = {},
            modifier = Modifier.padding(20.dp)
        )
    }

    // Close dialog after 3.5s
    closing?.let {
        val handler = Handler()
        handler.postDelayed({
            if (openDialog.value) {
                openDialog.value = false
                it.finish()
            }
        }, Constants.DELAY_DIALOG_DISMISS)
    }
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayDialogConfig(setShowDialog: (Boolean) -> Unit, onSend :  (Long, Long?, Boolean, String?) -> Unit) {

    var amount by remember { mutableStateOf("") }
    val tip by remember { mutableStateOf("") }
    val transitionAuto = remember { mutableStateOf(false) }
    var externaltransactionreference by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column (
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    TextField(
                        value = amount,
                        onValueChange = {
                            amount = it
                            isError = it.isEmpty() // it.isEmpty() // validate (can use only false)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            containerColor = Color.Transparent,
                            focusedIndicatorColor =  LightGreen,
                            cursorColor = LightGreen
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = isError,
                        supportingText = {
                            if (isError) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(R.string.enter_amount),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        // label = { Text(stringResource(R.string.amount)) },
                        placeholder = { Text(stringResource(R.string.amount)) },
                        trailingIcon = {
                            if (isError)
                                Icon(
                                    Icons.Filled.Error,
                                    "error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                        },
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )

                    TextField(
                        value = externaltransactionreference,
                        onValueChange = { externaltransactionreference = it },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            containerColor = Color.Transparent,
                            focusedIndicatorColor =  LightGreen,
                            cursorColor = LightGreen
                        ),
                        placeholder = { Text(stringResource(R.string.externaltransactionreference)) },
                        modifier = Modifier
                            .padding(vertical = 6.dp)
                            .background(Color.White)
                    )

                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        Checkbox(
                            checked = transitionAuto.value,
                            onCheckedChange = { transitionAuto.value = it },
                            colors = CheckboxDefaults.colors(Color.Green)
                        )
                        Text(stringResource(R.string.transitionauto), modifier = Modifier.padding(top = 10.dp))
                    }

                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    )
                    {
                        TextButton(modifier = Modifier.padding(end = 4.dp),
                            onClick = {
                                setShowDialog(false)
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        SampleButton(onClick = {
                            if (amount.isEmpty()) {
                                isError = true
                            } else {
                                isError = false
                                // Perform action
                                val finalAmount = (amount.toFloat()*100).toLong()

                                var finalTip : Long ?  = null
                                if (!tip.isEmpty()) finalTip = (tip.toFloat()*100).toLong()

                                var externaltransactionreferenceVal : String ?  = null
                                if (!externaltransactionreference.isEmpty()) externaltransactionreferenceVal = externaltransactionreference

                                val transitionAutoVal = transitionAuto.value

                                onSend(finalAmount, finalTip, transitionAutoVal, externaltransactionreferenceVal)
                                setShowDialog(false) // Close Dialog
                            }
                        }, text = stringResource(R.string.send))

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefundDialog(lastTransactionId : String, lastTransactionVisility : Boolean,
                 setShowDialog: (Boolean) -> Unit, items : List<PaymentDTO>, onSend : (Int) -> Unit) {
    var transactionId by remember { mutableStateOf(lastTransactionId) }
    var isError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Column (
                    modifier = Modifier.padding(16.dp)

                ) {
                    TextField(
                        value = transactionId,
                        onValueChange = {
                            transactionId = it
                            isError = it.isEmpty() // it.isEmpty() // validate (can use only false)
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.Black,
                            containerColor = Color.Transparent,
                            focusedIndicatorColor =  LightGreen,
                            cursorColor = LightGreen
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        supportingText = {
                            if (isError) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(R.string.enter_transaction_id),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        // label = { Text(stringResource(R.string.amount)) },
                        placeholder = { Text(stringResource(R.string.transaction_id)) },
                        trailingIcon = {
                            if (isError)
                                Icon(
                                    Icons.Filled.Error,
                                    "error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                        },
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )

                    if (lastTransactionVisility){
                        Text(
                            text = stringResource(id = R.string._5_last_transactions),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }

                    LazyColumn (
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        items(items) { item ->
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 6.dp)
                                    .background(colorResource(id = R.color.colorBackground))
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable(onClick = {
                                        onSend(item.transactionId)
                                        setShowDialog(false) // Close Dialog
                                    })
                                    .fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(stringResource(R.string.transaction), style = MaterialTheme.typography.titleSmall)
                                    Text(item.transactionId.toString(), style = MaterialTheme.typography.bodyMedium)

                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    )
                    {
                        TextButton(modifier = Modifier.padding(end = 4.dp),
                            onClick = {
                                setShowDialog(false)
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }

                        SampleButton(onClick = {
                            if (transactionId.isEmpty()) {
                                isError = true
                            } else {
                                isError = false
                                // Perform action
                                onSend(transactionId.toInt())
                                setShowDialog(false) // Close Dialog
                            }
                        }, text = stringResource(R.string.send))

                    }
                }
            }
        }
    }
}*/