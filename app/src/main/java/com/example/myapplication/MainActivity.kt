package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecipeApp()
        }
    }
}

@Composable
fun RecipeApp() {
    var isDarkTheme by remember { mutableStateOf(false) }
    var selectedScreen by rememberSaveable { mutableStateOf("Главная") }

    val colors = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colors) {
        Scaffold(
            bottomBar = { BottomNavigationBar(selectedScreen) { selectedScreen = it } }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (selectedScreen) {
                    "Главная" -> HomeScreen(isDarkTheme) { isDarkTheme = it }
                    "Избранное" -> FavoritesScreen()
                    "Профиль" -> ProfileScreen()
                }
            }
        }
    }
}

// Главная страница осталась без изменений
@Composable
fun HomeScreen(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    val recipes = listOf(
        Recipe("Греческий салат", R.drawable.image1, "Средний", 30),
        Recipe("Тост с авокадо", R.drawable.image2, "Легкий", 15),
        Recipe("Лосось с овощами", R.drawable.image3, "Тяжелый", 45)
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(isDarkTheme, onThemeChange)
        Spacer(modifier = Modifier.height(16.dp))
        Chips()
        Spacer(modifier = Modifier.height(16.dp))
        RecipeList(recipes)
    }
}

@Composable
fun FavoritesScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Избранное", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Профиль", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun SearchBar(isDarkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    var searchText by remember { mutableStateOf("") }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("Поиск рецептов...", color = Color.Gray) },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isDarkTheme,
            onCheckedChange = onThemeChange,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun BottomNavigationBar(selectedScreen: String, onScreenSelected: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedScreen == "Главная",
            onClick = { onScreenSelected("Главная") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
            label = { Text("Главная") }
        )
        NavigationBarItem(
            selected = selectedScreen == "Избранное",
            onClick = { onScreenSelected("Избранное") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Избранное") },
            label = { Text("Избранное") }
        )
        NavigationBarItem(
            selected = selectedScreen == "Профиль",
            onClick = { onScreenSelected("Профиль") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Профиль") },
            label = { Text("Профиль") }
        )
    }
}

data class Recipe(val name: String, val imageRes: Int, val difficulty: String, val cookingTime: Int)

@Composable
fun Chips() {
    var selectedChips by remember { mutableStateOf(setOf<String>()) }

    Row(modifier = Modifier.fillMaxWidth()) {
        FilterChip("Сложные", selectedChips, { selectedChips = toggleSelection("Сложные", selectedChips) }, Modifier.weight(1f))
        FilterChip("Быстрые", selectedChips, { selectedChips = toggleSelection("Быстрые", selectedChips) }, Modifier.weight(1f))
        FilterChip("Без мяса", selectedChips, { selectedChips = toggleSelection("Без мяса", selectedChips) }, Modifier.weight(1f))
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        FilterChip("Десерты", selectedChips, { selectedChips = toggleSelection("Десерты", selectedChips) }, Modifier.weight(1f))
        FilterChip("Закуски", selectedChips, { selectedChips = toggleSelection("Закуски", selectedChips) }, Modifier.weight(1f))
        FilterChip("Горячее", selectedChips, { selectedChips = toggleSelection("Горячее", selectedChips) }, Modifier.weight(1f))
    }
}

fun toggleSelection(chip: String, selectedChips: Set<String>): Set<String> {
    return if (selectedChips.contains(chip)) {
        selectedChips - chip
    } else {
        selectedChips + chip
    }
}

@Composable
fun FilterChip(label: String, selectedChips: Set<String>, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val isSelected = selectedChips.contains(label)
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Text(label)
    }
}

@Composable
fun RecipeList(recipes: List<Recipe>) {
    LazyColumn {
        items(recipes.size) { index ->
            RecipeCard(recipe = recipes[index])
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe) {
    var isVisible by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }
    val favoriteScale by animateFloatAsState(if (isFavorite) 1.2f else 1f, label = "")

    LaunchedEffect(Unit) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut()
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { }
        ) {
            Box {
                Column {
                    Image(
                        painter = painterResource(recipe.imageRes),
                        contentDescription = recipe.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recipe.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "Сложность: ${recipe.difficulty}\nВремя приготовления: ${recipe.cookingTime} мин",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Подробнее")
                    }
                }

                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).scale(favoriteScale)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    }
}

// ---------- ПРЕВЬЮ ДЛЯ ПРОСМОТРА В ANDROID STUDIO ----------

@Preview(showBackground = true)
@Composable
fun PreviewRecipeApp() {
    RecipeApp()
}

@Preview(showBackground = true)
@Composable
fun PreviewFavoritesScreen() {
    FavoritesScreen()
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    ProfileScreen()
}
