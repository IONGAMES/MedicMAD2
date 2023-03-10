package com.example.medicmad2.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medicmad2.R
import com.example.medicmad2.ui.components.AppBackButton
import com.example.medicmad2.ui.components.AppTextButton
import com.example.medicmad2.ui.theme.*
import com.example.medicmad2.viewmodel.LoginViewModel

/*
Описание: Класс экрана создания пароля
Дата создания: 08.03.2023 13:40
Автор: Георгий Хасанов
*/
class CreatePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MedicMAD2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CreatePasswordContent()
                }
            }
        }
    }

    /*
    Описание: Контент экрана создания пароля
    Дата создания: 08.03.2023 13:40
    Автор: Георгий Хасанов
    */
    @Composable
    fun CreatePasswordContent() {
        val mContext = LocalContext.current

        val sharedPreferences = this.getSharedPreferences("shared", Context.MODE_PRIVATE)

        var password by rememberSaveable { mutableStateOf("") }

        LaunchedEffect(password) {
            if (password.length == 4) {
                with(sharedPreferences.edit()) {
                    putString("password", password)
                    apply()

                    val intent = Intent(mContext, CreateCardActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        Scaffold(
            topBar = {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp, vertical = 40.dp)
                ) {
                    AppTextButton(
                        text = "Пропустить",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W400
                    ) {
                        val intent = Intent(mContext, CreateCardActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier
                .padding(padding)
                .fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .widthIn(max = 350.dp)
                        .padding(20.dp)
                        .align(Alignment.Center)
                ) {
                    Text(
                        "Создайте пароль",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W700,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Для защиты ваших персональных данных",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W700,
                        color = descriptionColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(56.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        for (i in 0..3) {
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(13.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i < password.length) {
                                            primaryColor
                                        } else {
                                            Color.White
                                        }
                                    )
                                    .border(0.85.dp, primaryColor, CircleShape)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(56.dp))
                    LazyVerticalGrid(
                        modifier = Modifier.height(416.dp),
                        userScrollEnabled = false,
                        columns = GridCells.Fixed(3),
                        content = {
                            items(12) { index ->
                                if (index == 10) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(12.dp)
                                            .clip(CircleShape)
                                            .background(inputColor)
                                            .clickable {
                                                if (password.length < 4) {
                                                    password += "0"
                                                }
                                            }
                                    ) {
                                        Text(
                                            "0",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.W600,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                } else if (index == 11) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(12.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                if (password.isNotEmpty()) {
                                                    password =
                                                        password.substring(0, password.length - 1)
                                                }
                                            }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_delete),
                                            contentDescription = "",
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                } else if (index == 9) {} else {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .padding(12.dp)
                                            .clip(CircleShape)
                                            .background(inputColor)
                                            .clickable {
                                                password += "${index + 1}"
                                            }
                                    ) {
                                        Text(
                                            "${index + 1}",
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.W600,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}