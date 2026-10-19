package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BodyMetric
import com.example.data.ExerciseEntry
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExerciseProgressionSection(
  exerciseEntries: List<ExerciseEntry>,
  modifier: Modifier = Modifier
) {
  // Distinct exercise names
  val defaultExercises = listOf("Barbell Bench Press", "Barbell Squat", "Barbell Deadlift", "Incline Dumbbell Press", "Barbell Bent-over Row", "Incline Dumbbell Curl")
  val existingExercises = exerciseEntries.map { it.exerciseName }.distinct()
  val availableExercises = (existingExercises + defaultExercises).distinct()

  var selectedExercise by remember { mutableStateOf(availableExercises.firstOrNull() ?: "Barbell Bench Press") }

  // Filter entries for selected exercise
  val entriesForSelected = exerciseEntries.filter { it.exerciseName.equals(selectedExercise, ignoreCase = true) }

  // Group by session or synthesized data points
  val progressionPoints: List<Pair<String, Double>> = if (entriesForSelected.isNotEmpty()) {
    entriesForSelected.groupBy { it.sessionId }.map { (sessId, sets) ->
      val maxWeight = sets.maxOfOrNull { it.weightLbs } ?: 0.0
      Pair("Session #$sessId", maxWeight)
    }
  } else {
    // Default demonstration progression for selected exercise
    when (selectedExercise) {
      "Barbell Bench Press" -> listOf("Week 1" to 185.0, "Week 2" to 195.0, "Week 3" to 205.0, "Week 4" to 225.0)
      "Barbell Squat" -> listOf("Week 1" to 195.0, "Week 2" to 205.0, "Week 3" to 215.0, "Week 4" to 225.0)
      "Barbell Deadlift" -> listOf("Week 1" to 275.0, "Week 2" to 295.0, "Week 3" to 305.0, "Week 4" to 315.0)
      else -> listOf("Week 1" to 60.0, "Week 2" to 65.0, "Week 3" to 70.0, "Week 4" to 75.0)
    }
  }

  val highestWeight = progressionPoints.maxOfOrNull { it.second } ?: 0.0
  val lowestWeight = progressionPoints.minOfOrNull { it.second } ?: 0.0
  val gain = if (progressionPoints.size >= 2) progressionPoints.last().second - progressionPoints.first().second else 0.0

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = CardElevatedBg),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Exercise Progression",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${highestWeight.toInt()} lbs Peak",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = AccentEmerald.copy(alpha = 0.15f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = null,
              tint = AccentEmerald,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (gain >= 0) "+${gain.toInt()} lbs" else "${gain.toInt()} lbs",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = AccentEmerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Exercise selector chips
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(availableExercises) { exercise ->
          val isSelected = exercise == selectedExercise
          Box(
            modifier = Modifier
              .background(
                if (isSelected) TextPrimary else CardBg,
                RoundedCornerShape(16.dp)
              )
              .clickable { selectedExercise = exercise }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = exercise,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) DarkBg else TextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Progression Line Graph
      LineProgressionChart(
        dataPoints = progressionPoints.map { it.second },
        labels = progressionPoints.map { it.first },
        lineColor = AccentEmerald,
        modifier = Modifier
          .fillMaxWidth()
          .height(140.dp)
      )
    }
  }
}

@Composable
fun BodyMetricProgressionSection(
  metrics: List<BodyMetric>,
  onLogNewWeight: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sortedMetrics = metrics.sortedBy { it.timestamp }
  val currentWeight = sortedMetrics.lastOrNull()?.weightLbs ?: 190.0
  val startWeight = sortedMetrics.firstOrNull()?.weightLbs ?: currentWeight
  val delta = currentWeight - startWeight

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = CardElevatedBg),
    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Body Weight Metric",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = "${currentWeight.toInt()}",
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "lbs",
              fontSize = 16.sp,
              color = TextSecondary,
              modifier = Modifier.padding(bottom = 3.dp)
            )
          }
        }

        Button(
          onClick = onLogNewWeight,
          colors = ButtonDefaults.buttonColors(containerColor = CardBg),
          shape = RoundedCornerShape(14.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
          Text("Log Weight", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      val points = if (sortedMetrics.size >= 2) {
        sortedMetrics.map { it.weightLbs }
      } else {
        listOf(193.0, 192.2, 191.0, 190.0)
      }

      val labels = if (sortedMetrics.size >= 2) {
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        sortedMetrics.map { sdf.format(Date(it.timestamp)) }
      } else {
        listOf("Aug 20", "Aug 27", "Sep 3", "Today")
      }

      LineProgressionChart(
        dataPoints = points,
        labels = labels,
        lineColor = AccentBlue,
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
      )
    }
  }
}

@Composable
fun LineProgressionChart(
  dataPoints: List<Double>,
  labels: List<String>,
  lineColor: Color,
  modifier: Modifier = Modifier
) {
  if (dataPoints.isEmpty()) return

  val minVal = dataPoints.minOrNull() ?: 0.0
  val maxVal = dataPoints.maxOrNull() ?: 1.0
  val range = if (maxVal == minVal) 1.0 else maxVal - minVal

  Column(modifier = modifier) {
    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val paddingHorizontal = 24f
        val paddingVertical = 16f
        val usableW = w - paddingHorizontal * 2
        val usableH = h - paddingVertical * 2

        val points = dataPoints.mapIndexed { idx, value ->
          val normX = if (dataPoints.size > 1) idx.toFloat() / (dataPoints.size - 1) else 0.5f
          val normY = ((value - minVal) / range).toFloat()
          val px = paddingHorizontal + normX * usableW
          val py = h - paddingVertical - normY * usableH
          Offset(px, py)
        }

        // Draw horizontal subtle grid lines
        for (i in 0..2) {
          val lineY = paddingVertical + (usableH / 2f) * i
          drawLine(
            color = CardBorder,
            start = Offset(paddingHorizontal, lineY),
            end = Offset(w - paddingHorizontal, lineY),
            strokeWidth = 1f
          )
        }

        // Draw connecting line
        val path = Path().apply {
          points.forEachIndexed { i, pt ->
            if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
          }
        }

        drawPath(
          path = path,
          color = lineColor,
          style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw glowing point circles
        points.forEach { pt ->
          drawCircle(
            color = DarkBg,
            radius = 5.dp.toPx(),
            center = pt
          )
          drawCircle(
            color = lineColor,
            radius = 3.5.dp.toPx(),
            center = pt
          )
        }
      }
    }

    // Horizontal Labels
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      labels.forEach { label ->
        Text(
          text = label,
          fontSize = 10.sp,
          color = TextSecondary,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
