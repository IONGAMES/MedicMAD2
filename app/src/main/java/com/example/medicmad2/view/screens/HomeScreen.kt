package com.example.medicmad2.view.screens

import android.content.Context
import android.content.Intent
import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.medicmad2.R
import com.example.medicmad2.common.CartService
import com.example.medicmad2.model.CartItem
import com.example.medicmad2.model.CatalogItem
import com.example.medicmad2.ui.components.*
import com.example.medicmad2.ui.theme.*
import com.example.medicmad2.view.CartActivity
import com.example.medicmad2.viewmodel.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/*
Описание: Экран анализы/главная
Дата создания: 09.03.2023 9:40
Автор: Георгий Хасанов
*/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val mContext = LocalContext.current
    val sharedPreferences = mContext.getSharedPreferences("shared", Context.MODE_PRIVATE)

    var isRefreshing by rememberSaveable { mutableStateOf(false) }
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    var isSearchEnabled by rememberSaveable { mutableStateOf(false) }

    var searchText by rememberSaveable { mutableStateOf("") }
    val interactionSource = remember { MutableInteractionSource() }

    if (interactionSource.collectIsPressedAsState().value) {
        isSearchEnabled = true
    }

    var isVisible by rememberSaveable { mutableStateOf(true) }

    var isAlertDialogVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val categoryList = listOf(
        "Популярные",
        "Covid",
        "Комплексные",
        "Чекапы",
        "Биохимия",
        "Гормоны",
        "Иммунитет",
        "Витамины",
        "Аллергены",
        "Анализ крови",
        "Анализ мочи",
        "Анализ кала",
        "Только в клинике"
    )

    var selectedCategory by rememberSaveable { mutableStateOf("Популярные") }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collect {
            isVisible = it <= 10
        }
    }

    val response by viewModel.response.observeAsState()
    LaunchedEffect(response) {
        if (response == 200) {
            isLoading = false
        }
    }

    val message by viewModel.message.observeAsState()
    LaunchedEffect(message) {
        if (message != null) {
            isAlertDialogVisible = true
            isLoading = false
        }
    }

    var cart: MutableList<CartItem> = remember { mutableStateListOf() }

    var selectedCatalogItem by remember { mutableStateOf(CatalogItem(0, "", "", "", "", "", "", "",)) }

    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)

    LaunchedEffect(Unit) {
        isLoading = true
        viewModel.getNews()
        viewModel.getCatalog()

        cart.addAll(CartService().getCartData(sharedPreferences))
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = Color.White,
        sheetContent = {
            AnalysisCard(catalogItem = selectedCatalogItem) {
                scope.launch {
                    cart = CartService().addToCart(
                        CartItem(
                            selectedCatalogItem.id,
                            selectedCatalogItem.name,
                            selectedCatalogItem.price,
                            1
                        ),
                        cart
                    )

                    sheetState.hide()
                }
            }
        }
    ) {
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    isRefreshing = true
                    isLoading = true

                    viewModel.getNews()
                    viewModel.getCatalog()

                    isRefreshing = false
                }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AppTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = "", tint = secondaryTextColor) },
                            trailingIcon = {
                                if (isSearchEnabled) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_close),
                                        contentDescription = "",
                                        tint = secondaryTextColor,
                                        modifier = Modifier.clickable { searchText = "" }
                                    )
                                } else {

                                }
                            },
                            placeholder = { Text(text = "Искать анализы", fontSize = 16.sp, color = descriptionColor) },
                            contentPadding = PaddingValues(14.dp),
                            readOnly = !isSearchEnabled,
                            interactionSource = interactionSource,
                            modifier = Modifier
                                .weight(7f)
                                .drawBehind {
                                    drawLine(
                                        dividerColor,
                                        Offset(0f, size.height),
                                        Offset(size.width, size.height),
                                        0.5f
                                    )
                                }
                                .padding(start = 20.dp, end = 16.dp)
                        )
                        AnimatedVisibility(visible = isSearchEnabled) {
                            AppTextButton(
                                text = "Отменить",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                color = primaryColor,
                                modifier = Modifier.weight(3f).padding(end = 20.dp)
                            ) {
                                isSearchEnabled = false
                                searchText = ""
                            }
                        }
                    }
                    AnimatedVisibility(visible = !isSearchEnabled) {
                        Column {
                            AnimatedVisibility(
                                visible = isVisible,
                                modifier = Modifier.padding(start = 20.dp)
                            ) {
                                Column(Modifier.verticalScroll(rememberScrollState())) {
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        "Акции и новости",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.W600,
                                        color = descriptionColor
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    LazyRow {
                                        items(viewModel.news.distinct()) { item ->
                                            AppNewsItemCard(newsItem = item)
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                "Каталог анализов",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W600,
                                color = descriptionColor,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .padding(start = 20.dp)
                            ) {
                                for (cat in categoryList) {
                                    AppCategoryCard(
                                        title = cat,
                                        selectedItem = selectedCategory
                                    ) {
                                        selectedCategory = cat
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            ) {
                                item { Spacer(modifier = Modifier.height(24.dp)) }
                                items(viewModel.catalog.filter { it.category.lowercase() == selectedCategory.lowercase() }
                                    .distinct()) { item ->
                                    AppCatalogItemCard(
                                        catalogItem = item,
                                        itemInCart = cart.indexOfFirst { it.id == item.id } != -1,
                                        onButtonClick = {
                                            val itemIndex = cart.indexOfFirst { it.id == item.id }

                                            if (itemIndex == -1) {
                                                cart = CartService().addToCart(
                                                    CartItem(
                                                        item.id,
                                                        item.name,
                                                        item.price,
                                                        1
                                                    ),
                                                    cart
                                                )
                                            } else {
                                                cart = CartService().removeFromCart(
                                                    itemIndex,
                                                    cart
                                                )
                                            }

                                            CartService().saveCartData(sharedPreferences, cart)
                                        }
                                    ) {
                                        scope.launch {
                                            selectedCatalogItem = item

                                            sheetState.show()
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = isSearchEnabled) {
                        if (searchText.length >= 3) {
                            LazyColumn {
                                items(viewModel.catalog.filter { it.name.lowercase().contains(searchText.lowercase()) || it.bio.lowercase().contains(searchText.lowercase()) }.distinct()) { item ->
                                    AppSearchItemCard(
                                        catalogItem = item,
                                        searchText = searchText
                                    ) {
                                        scope.launch {
                                            selectedCatalogItem = item

                                            sheetState.show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }

    if (cart.isNotEmpty() && sheetState.currentValue != ModalBottomSheetValue.Expanded && !isSearchEnabled) {
        Box(modifier = Modifier
            .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .align(Alignment.BottomCenter)
            ) {
                var cartSumPrice = 0

                for (item in cart.distinct()) {
                    cartSumPrice += (item.price.toInt() * item.count)
                }

                AppCartButton(
                    text = "В корзину",
                    price = cartSumPrice.toString(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    val intent = Intent(mContext, CartActivity::class.java)
                    mContext.startActivity(intent)
                }
            }
        }
    }

    if (isAlertDialogVisible) {
        AlertDialog(
            onDismissRequest = { isAlertDialogVisible = false },
            title = { Text("Ошибка") },
            text = { Text(viewModel.message.value.toString()) },
            buttons = {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    AppTextButton(text = "OK") {
                        isAlertDialogVisible = false
                    }
                }
            }
        )
    }

    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator()
        }
    }
}