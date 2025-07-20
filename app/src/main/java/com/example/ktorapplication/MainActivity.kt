package com.example.ktorapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ktorapplication.ui.theme.KtorApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val mViewModel = viewModel<MainViewModel>()
    val res = mViewModel.createDataFlow.collectAsState().value
    val fetch = mViewModel.fetchDataFlow.collectAsState().value
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally){
        Button(
            onClick = { mViewModel.createEmployee() },
            content = {
                Text(
                    text = "Press to Add",
                )
            }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when(res){
                Response.Empty -> {
                    Text(
                        text = "Press the Button",
                        style = TextStyle(
                            color = Color.Yellow,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                is Response.Error -> {
                    Text(
                        text = res.msg,
                        style = TextStyle(
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Response.Loading -> {
                    CircularProgressIndicator()
                }
                is Response.Success -> {
                    Text(
                        text = res.data.message,
                        style = TextStyle(
                            color = Color.Green,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        Button(
            onClick = { mViewModel.fetch() },
            content = {
                Text(
                    text = "Press to Add",
                )
            }
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when(fetch){
                Response.Empty -> {
                    Text(
                        text = "Press the Button",
                        style = TextStyle(
                            color = Color.Yellow,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                is Response.Error -> {
                    Text(
                        text = fetch.msg,
                        style = TextStyle(
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Response.Loading -> {
                    CircularProgressIndicator()
                }
                is Response.Success -> {
                    Text(
                        text = fetch.data.total.toString(),
                        style = TextStyle(
                            color = Color.Green,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KtorApplicationTheme {
        Greeting("Android")
    }
}