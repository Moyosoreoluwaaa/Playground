//package com.playground
//
//import android.os.Bundle
//import android.speech.tts.TextToSpeech
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.Animatable
//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.pager.VerticalPager
//import androidx.compose.foundation.pager.rememberPagerState
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.drawText
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.rememberTextMeasurer
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import org.json.JSONArray
//import org.json.JSONObject
//import java.util.*
//
//data class Node(
//    val id: Int,
//    val text: String,
//    val targetText: String,
//    val position: Offset,
//    var matched: Boolean = false
//)
//
//data class Scene(val id: Int, val name: String, val pairs: List<Pair<String, String>>)
//
//class MainActivity : ComponentActivity() {
//    private lateinit var tts: TextToSpeech
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize TTS
//        tts = TextToSpeech(this) { status ->
//            if (status == TextToSpeech.SUCCESS) {
//                tts.language = Locale.GERMAN
//            }
//        }
//
//        setContent {
//            LanguageLearningApp(tts)
//        }
//    }
//
//    override fun onDestroy() {
//        tts.shutdown()
//        super.onDestroy()
//    }
//}
//
//@Composable
//fun LanguageLearningApp(tts: TextToSpeech) {
//    val scope = rememberCoroutineScope()
//    val scenes = remember { loadMockScenes() }
//    val pagerState = rememberPagerState(pageCount = { scenes.size })
//    var streak by remember { mutableStateOf(0) }
//    var progress by remember { mutableStateOf(0f) }
//    var timer by remember { mutableStateOf(300) } // 5 minutes in seconds
//    var isTimedMode by remember { mutableStateOf(true) }
//    val nodes = remember { mutableStateListOf<Node>() }
//    var draggedNode by remember { mutableStateOf<Node?>(null) }
//    val ghostTrail = remember { Animatable(0f) }
//    val ropeColor = remember { Animatable(Color.White) }
//
//    LaunchedEffect(pagerState.currentPage) {
//        // Load new puzzle
//        nodes.clear()
//        val pairs = scenes[pagerState.currentPage].pairs
//        nodes.addAll(generateNodes(pairs))
//        progress = 0f
//    }
//
//    LaunchedEffect(timer) {
//        if (isTimedMode && timer > 0) {
//            delay(1000)
//            timer -= 1
//            if (timer % 10 == 0) {
//                scope.launch {
//                    ghostTrail.animateTo(1.2f, animationSpec = tween(200))
//                    ghostTrail.animateTo(1f, animationSpec = tween(200))
//                }
//            }
//        }
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Gray)
//    ) {
//        VerticalPager(
//            state = pagerState,
//            modifier = Modifier.fillMaxSize()
//        ) { page ->
//            CanvasGameplay(
//                nodes = nodes,
//                draggedNode = draggedNode,
//                onDragStart = { node -> draggedNode = node },
//                onDragEnd = { node ->
//                    val target = nodes.find { it.targetText == node.text && !it.matched }
//                    if (target != null && node.position.getDistanceTo(target.position) < 50f) {
//                        nodes[nodes.indexOf(node)] = node.copy(matched = true)
//                        nodes[nodes.indexOf(target)] = target.copy(matched = true)
//                        scope.launch {
//                            ropeColor.animateTo(Color.Green, animationSpec = tween(500))
//                            ropeColor.animateTo(Color.White)
//                            progress += 0.2f
//                            streak += 1
//                            if (progress >= 1f) {
//                                delay(500)
//                                pagerState.animateScrollToPage(page + 1)
//                            }
//                        }
//                        tts.speak(target.text, TextToSpeech.QUEUE_FLUSH, null, null)
//                    } else {
//                        scope.launch {
//                            ropeColor.animateTo(Color.Red, animationSpec = tween(200))
//                            ropeColor.animateTo(Color.White)
//                        }
//                    }
//                    draggedNode = null
//                },
//                onDrag = { node, offset ->
//                    nodes[nodes.indexOf(node)] = node.copy(position = offset)
//                },
//                ropeColor = ropeColor.value,
//                ghostTrail = ghostTrail.value
//            )
//        }
//
//        // UI Elements
//        Text(
//            text = scenes[pagerState.currentPage].name,
//            color = Color.White,
//            fontSize = 16.sp,
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(16.dp)
//        )
//        Text(
//            text = "$streak",
//            color = Color.White,
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(16.dp)
//        )
//        if (isTimedMode) {
//            Text(
//                text = "${timer / 60}:${timer % 60}",
//                color = Color.White,
//                fontSize = 20.sp,
//                modifier = Modifier
//                    .align(Alignment.TopCenter)
//                    .padding(16.dp)
//            )
//        }
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(4.dp)
//                .background(Color.Gray)
//                .align(Alignment.TopCenter)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(progress)
//                    .height(4.dp)
//                    .background(Color.White)
//            )
//        }
//    }
//}
//
//@Composable
//fun CanvasGameplay(
//    nodes: List<Node>,
//    draggedNode: Node?,
//    onDragStart: (Node) -> Unit,
//    onDragEnd: (Node) -> Unit,
//    onDrag: (Node, Offset) -> Unit,
//    ropeColor: Color,
//    ghostTrail: Float
//) {
//    val textMeasurer = rememberTextMeasurer()
//
//    Canvas(
//        modifier = Modifier
//            .fillMaxSize()
//            .pointerInput(Unit) {
//                detectDragGestures(
//                    onDragStart = { offset ->
//                        nodes.find { it.position.getDistanceTo(offset) < 30f }
//                            ?.let { onDragStart(it) }
//                    },
//                    onDragEnd = { draggedNode?.let { onDragEnd(it) } },
//                    onDrag = { change, dragAmount ->
//                        draggedNode?.let { node ->
//                            val newPos = node.position + Offset(dragAmount.x, dragAmount.y)
//                            onDrag(node, newPos)
//                        }
//                    }
//                )
//            }) {
//        // Draw dotted background
//        for (x in 0 until size.width.toInt() step 50) {
//            for (y in 0 until size.height.toInt() step 50) {
//                drawCircle(
//                    color = Color.White.copy(alpha = 0.05f),
//                    radius = 2f,
//                    center = Offset(x.toFloat(), y.toFloat())
//                )
//            }
//        }
//
//        // Draw ropes
//        nodes.filter { it.matched }.groupBy { it.text }.forEach { (_, pair) ->
//            if (pair.size == 2) {
//                drawLine(
//                    color = ropeColor,
//                    start = pair[0].position,
//                    end = pair[1].position,
//                    strokeWidth = 2f
//                )
//            }
//        }
//
//        // Draw nodes
//        nodes.forEach { node ->
//            drawCircle(
//                color = if (node.matched) Color.Gray else Color.White,
//                radius = 30f,
//                center = node.position
//            )
//            // Measure text to calculate centering offset
//            val textLayoutResult = textMeasurer.measure(
//                text = node.text,
//                style = TextStyle(fontSize = 14.sp)
//            )
//            val textWidth = textLayoutResult.size.width
//            val textHeight = textLayoutResult.size.height
//            drawText(
//                textLayoutResult = textLayoutResult,
//                color = Color.Black,
//                topLeft = Offset(
//                    x = node.position.x - textWidth / 2f,
//                    y = node.position.y - textHeight / 2f
//                )
//            )
//            if (node == draggedNode) {
//                drawCircle(
//                    color = Color.Gray.copy(alpha = 0.3f),
//                    radius = 30f * ghostTrail,
//                    center = node.position
//                )
//            }
//        }
//    }
//}
//
//private fun Offset.getDistanceTo(other: Offset): Float {
//    return kotlin.math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
//}
//
//private fun generateNodes(pairs: List<Pair<String, String>>): List<Node> {
//    val nodes = mutableListOf<Node>()
//    pairs.forEachIndexed { index, pair ->
//        val offset = 50f + index * 100f
//        nodes.add(Node(index * 2, pair.first, pair.second, Offset(100f, offset)))
//        nodes.add(Node(index * 2 + 1, pair.second, pair.first, Offset(300f, offset)))
//    }
//    return nodes
//}
//
//private fun loadMockScenes(): List<Scene> {
//    val json = """
//        [
//            {
//                "id": 1,
//                "name": "Morning Coffee",
//                "pairs": [
//                    ["coffee", "Kaffee"],
//                    ["I want coffee", "Ich möchte Kaffee"],
//                    ["one espresso", "Ein Espresso"],
//                    ["please", "Bitte"],
//                    ["thank you", "Danke"]
//                ]
//            },
//            {
//                "id": 2,
//                "name": "Bus Ride",
//                "pairs": [
//                    ["ticket", "Fahrkarte"],
//                    ["where is the bus", "Wo ist der Bus"],
//                    ["next stop", "Nächste Haltestelle"],
//                    ["how much", "Wie viel"],
//                    ["thank you", "Danke"]
//                ]
//            },
//            {
//                "id": 3,
//                "name": "At the Market",
//                "pairs": [
//                    ["apple", "Apfel"],
//                    ["how much is it", "Wie viel kostet es"],
//                    ["that is too expensive", "Das ist zu teuer"],
//                    ["I'll take it", "Ich nehme es"],
//                    ["goodbye", "Auf Wiedersehen"]
//                ]
//            }
//        ]
//    """
//    val jsonArray = JSONArray(json.trimIndent())
//    val scenes = mutableListOf<Scene>()
//    for (i in 0 until jsonArray.length()) {
//        val sceneJson = jsonArray.getJSONObject(i)
//        val pairs = mutableListOf<Pair<String, String>>()
//        val pairsArray = sceneJson.getJSONArray("pairs")
//        for (j in 0 until pairsArray.length()) {
//            val pair = pairsArray.getJSONArray(j)
//            pairs.add(Pair(pair.getString(0), pair.getString(1)))
//        }
//        scenes.add(Scene(sceneJson.getInt("id"), sceneJson.getString("name"), pairs))
//    }
//    return scenes
//}