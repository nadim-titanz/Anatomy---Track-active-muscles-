package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MuscleGroup
import com.example.data.MuscleRecoveryInfo
import com.example.data.RecencyState
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 🏋️ Anatomical Muscle Map Visualizer
 * Beautiful, athletic human body map with smooth anatomical contours.
 * Front and Back views with 16 distinct muscle groups dynamically colored by recency.
 */
@Composable
fun AnatomicalHeatmap(
  recoveryMap: Map<MuscleGroup, MuscleRecoveryInfo>,
  selectedMuscle: MuscleGroup?,
  onMuscleSelected: (MuscleGroup?) -> Unit,
  onLogMuscleWorkout: ((MuscleGroup) -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  var activeViewMode by remember { mutableStateOf(0) } // 0: Both, 1: Front, 2: Back

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(DarkBg),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header controls: View Switcher (Both / Front / Back) in Nothing OS style
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = NothingSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
      ) {
        Row(modifier = Modifier.padding(3.dp)) {
          val modes = listOf("ALL", "FRONT", "BACK")
          modes.forEachIndexed { index, label ->
            val isSelected = activeViewMode == index
            Box(
              modifier = Modifier
                .testTag("view_mode_$index")
                .clip(RoundedCornerShape(8.dp))
                .background(
                  if (isSelected) NothingTextDisplay else Color.Transparent
                )
                .clickable { activeViewMode = index }
                .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                letterSpacing = 1.sp,
                color = if (isSelected) NothingOledBlack else NothingTextSecondary
              )
            }
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(5.dp)
            .background(NothingRed, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "TAP TO INSPECT",
          fontSize = 10.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          letterSpacing = 1.sp,
          color = NothingTextDisabled
        )
      }
    }

    // Main Canvas containing anatomical body figures
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(395.dp)
        .padding(horizontal = 4.dp),
      contentAlignment = Alignment.Center
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .testTag("anatomical_canvas")
          .pointerInput(activeViewMode) {
            detectTapGestures { tapOffset ->
              val tappedMuscle = detectMuscleHit(
                tapOffset = tapOffset,
                canvasWidth = size.width.toFloat(),
                canvasHeight = size.height.toFloat(),
                viewMode = activeViewMode
              )
              onMuscleSelected(tappedMuscle)
            }
          }
      ) {
        val w = size.width
        val h = size.height

        // Nothing OS subtle LED dot grid background
        val dotSpacing = 18.dp.toPx()
        val dotRadius = 0.9.dp.toPx()
        var gy = 12.dp.toPx()
        while (gy < h - 12.dp.toPx()) {
          var gx = 16.dp.toPx()
          while (gx < w - 16.dp.toPx()) {
            drawCircle(
              color = NothingDotInactive,
              radius = dotRadius,
              center = Offset(gx, gy)
            )
            gx += dotSpacing
          }
          gy += dotSpacing
        }

        when (activeViewMode) {
          0 -> {
            val figureW = w * 0.49f
            val frontCenterX = w * 0.26f
            val backCenterX = w * 0.74f
            val topY = h * 0.02f
            val figH = h * 0.96f

            drawAthleticFigure(
              centerX = frontCenterX,
              topY = topY,
              figWidth = figureW,
              figHeight = figH,
              isFront = true,
              recoveryMap = recoveryMap,
              selectedMuscle = selectedMuscle
            )

            drawAthleticFigure(
              centerX = backCenterX,
              topY = topY,
              figWidth = figureW,
              figHeight = figH,
              isFront = false,
              recoveryMap = recoveryMap,
              selectedMuscle = selectedMuscle
            )
          }
          1 -> {
            val figureW = w * 0.82f
            val centerX = w * 0.5f
            val topY = h * 0.02f
            val figH = h * 0.96f

            drawAthleticFigure(
              centerX = centerX,
              topY = topY,
              figWidth = figureW,
              figHeight = figH,
              isFront = true,
              recoveryMap = recoveryMap,
              selectedMuscle = selectedMuscle
            )
          }
          2 -> {
            val figureW = w * 0.82f
            val centerX = w * 0.5f
            val topY = h * 0.02f
            val figH = h * 0.96f

            drawAthleticFigure(
              centerX = centerX,
              topY = topY,
              figWidth = figureW,
              figHeight = figH,
              isFront = false,
              recoveryMap = recoveryMap,
              selectedMuscle = selectedMuscle
            )
          }
        }
      }
    }

    // Visual Legend (Red: 1-3d, Yellow: >3d, Grey: 5-7d) - Black (8+d) omitted from tags as requested
    HeatmapLegendRow(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )

    // Selected Muscle Detail Card with Training History
    AnimatedVisibility(
      visible = selectedMuscle != null,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      if (selectedMuscle != null) {
        val info = recoveryMap[selectedMuscle]
        MuscleDetailCard(
          muscle = selectedMuscle,
          info = info,
          onDismiss = { onMuscleSelected(null) },
          onLogWorkout = { onLogMuscleWorkout?.invoke(selectedMuscle) },
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }
    }
  }
}

@Composable
fun HeatmapLegendRow(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    LegendPill(color = NothingRed, label = "RECENT [1-3D]")
    LegendPill(color = StatusWarningAmber, label = "> 3D AGO")
    LegendPill(color = StatusGreyRecovered, label = "RESTED [5-7D]")
  }
}

@Composable
private fun LegendPill(color: Color, label: String) {
  Surface(
    shape = RoundedCornerShape(999.dp),
    color = NothingSurface,
    border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .background(color, CircleShape)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        fontSize = 10.sp,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.8.sp,
        color = NothingTextPrimary
      )
    }
  }
}

@Composable
fun MuscleDetailCard(
  muscle: MuscleGroup,
  info: MuscleRecoveryInfo?,
  onDismiss: () -> Unit,
  onLogWorkout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val state = info?.state ?: RecencyState.NEEDS_ATTENTION
  val daysAgo = info?.daysAgo

  val daysText = when {
    daysAgo == null -> "Never logged"
    daysAgo == 0L -> "Trained today"
    daysAgo == 1L -> "Trained 1 day ago"
    else -> "Trained $daysAgo days ago"
  }

  val lastTrainedDateStr = info?.lastTrainedTimestamp?.let {
    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    sdf.format(Date(it))
  } ?: "No logged history"

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = NothingSurface),
    border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
  ) {
    Column(modifier = Modifier.padding(18.dp)) {
      // Header: Technical Target & Close
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(state.color, CircleShape)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "// TARGET: ${muscle.displayName.uppercase()}",
            fontSize = 15.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = NothingTextDisplay
          )
        }

        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = NothingTextSecondary,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status pill & Days since trained in Nothing telemetry style
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(4.dp),
          color = NothingSurfaceElevated,
          border = androidx.compose.foundation.BorderStroke(1.dp, NothingBorder)
        ) {
          Text(
            text = "[ ${state.label.uppercase()} ]",
            fontSize = 11.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            color = state.color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Text(
          text = daysText.uppercase(),
          fontSize = 11.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          letterSpacing = 0.5.sp,
          color = NothingTextSecondary
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Last trained date
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "LAST LOGGED: ",
          fontSize = 11.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          color = NothingTextSecondary
        )
        Text(
          text = lastTrainedDateStr.uppercase(),
          fontSize = 11.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          fontWeight = FontWeight.Bold,
          color = NothingTextDisplay
        )
      }

      // Training History for this muscle
      val history = info?.workoutHistory ?: emptyList()
      if (history.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(
          text = "HISTORY [${history.size}]",
          fontSize = 11.sp,
          fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
          letterSpacing = 1.sp,
          color = NothingTextDisabled
        )
        Spacer(modifier = Modifier.height(6.dp))

        history.take(3).forEach { session ->
          val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
          val dateFormatted = sdf.format(Date(session.timestamp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "• $dateFormatted — ${session.name}".uppercase(),
              fontSize = 12.sp,
              fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
              color = NothingTextPrimary
            )
            Text(
              text = session.dayOfWeek.uppercase(),
              fontSize = 11.sp,
              fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
              color = NothingTextSecondary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Nothing Pill Button to log workout for this muscle
      NothingPillButton(
        text = "LOG ${muscle.displayName.uppercase()} WORKOUT",
        onClick = onLogWorkout,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

// -------------------------------------------------------------
// Athletic Anatomical Drawing Engine
// -------------------------------------------------------------

private fun DrawScope.drawAthleticFigure(
  centerX: Float,
  topY: Float,
  figWidth: Float,
  figHeight: Float,
  isFront: Boolean,
  recoveryMap: Map<MuscleGroup, MuscleRecoveryInfo>,
  selectedMuscle: MuscleGroup?
) {
  // Natural muscular athletic proportions: broader shoulders, thicker torso & limbs
  val baseScaleY = figHeight / 300f
  val baseScaleX = minOf(figWidth / 96f, baseScaleY * 1.25f)
  val scaleX = baseScaleX
  val scaleY = baseScaleY

  fun x(rel: Float): Float = centerX + (rel - 85f) * scaleX
  fun y(rel: Float): Float = topY + (rel - 10f) * scaleY

  fun getColor(muscle: MuscleGroup): Color {
    if (selectedMuscle == muscle) {
      return Color.White
    }
    val info = recoveryMap[muscle]
    val state = info?.state ?: RecencyState.NEEDS_ATTENTION
    return when (state) {
      RecencyState.RECENT -> Color(0xFFEF4444)            // Red = recent 1-3 days
      RecencyState.MORE_THAN_3_DAYS -> Color(0xFFEAB308)  // Yellow = more than 3 days
      RecencyState.GREY_RECOVERED -> Color(0xFF71717A)    // Grey = 5-7 days ago
      RecencyState.NEEDS_ATTENTION -> Color(0xFF14171F)   // Black = Needs attention 8+ days
    }
  }

  // Base palette
  val baseBodyColor = Color(0xFF14171F)
  val baseBorderColor = Color(0xFF282F3E)
  val baseBorderStroke = Stroke(width = 1.2f * scaleX)
  val muscleBorderStroke = Stroke(width = 1.0f * scaleX)
  val selectedBorderStroke = Stroke(width = 2.0f * scaleX)
  val muscleBorderColor = Color(0xFF2E374A)

  // 1. Subtle Radial Ground Glow beneath feet
  val shadowW = 85f * scaleX
  val shadowH = 18f * scaleY
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(Color(0x33384256), Color.Transparent),
      center = Offset(centerX, y(306f)),
      radius = shadowW / 2
    ),
    topLeft = Offset(centerX - shadowW / 2, y(300f)),
    size = Size(shadowW, shadowH)
  )

  // 2. Base Complete Human Silhouette (Smooth Athletic Base)
  val baseBodyPath = Path().apply {
    // Cranium
    moveTo(x(85f), y(10f))
    cubicTo(x(92f), y(10f), x(95f), y(16f), x(94f), y(24f))
    cubicTo(x(93f), y(29f), x(91f), y(32f), x(89f), y(34f)) // chin/jaw
    // Right Neck & Trapezius slope
    lineTo(x(90f), y(40f))
    cubicTo(x(96f), y(42f), x(108f), y(45f), x(118f), y(48f)) // shoulder
    // Right Deltoid & Arm outer edge
    cubicTo(x(124f), y(54f), x(126f), y(66f), x(122f), y(76f))
    cubicTo(x(124f), y(88f), x(125f), y(102f), x(121f), y(114f)) // elbow
    cubicTo(x(125f), y(128f), x(126f), y(148f), x(123f), y(166f)) // forearm
    cubicTo(x(124f), y(174f), x(123f), y(182f), x(121f), y(184f)) // hand
    // Right Hand inner edge
    lineTo(x(118f), y(184f))
    cubicTo(x(117f), y(172f), x(115f), y(156f), x(115f), y(134f))
    cubicTo(x(115f), y(114f), x(114f), y(96f), x(113f), y(82f))
    // Right Torso / Axilla down to waist
    cubicTo(x(106f), y(80f), x(104f), y(90f), x(103f), y(102f))
    cubicTo(x(102f), y(114f), x(103f), y(122f), x(105f), y(136f)) // hip flare
    // Right Leg outer edge
    cubicTo(x(107f), y(152f), x(107f), y(178f), x(103f), y(210f)) // quad to knee
    cubicTo(x(102f), y(220f), x(104f), y(236f), x(104f), y(252f)) // calf bulge
    cubicTo(x(103f), y(268f), x(99f), y(284f), x(98f), y(294f)) // ankle
    // Right Foot
    cubicTo(x(99f), y(298f), x(100f), y(304f), x(97f), y(306f))
    lineTo(x(92f), y(306f))
    // Right Inseam up to crotch
    cubicTo(x(92f), y(298f), x(93f), y(278f), x(95f), y(252f))
    cubicTo(x(95f), y(234f), x(94f), y(218f), x(94f), y(210f)) // inner knee
    cubicTo(x(93f), y(188f), x(90f), y(164f), x(85f), y(144f)) // crotch center

    // Left Inseam down to ankle
    cubicTo(x(80f), y(164f), x(77f), y(188f), x(76f), y(210f)) // inner knee
    cubicTo(x(76f), y(218f), x(75f), y(234f), x(75f), y(252f))
    cubicTo(x(77f), y(278f), x(78f), y(298f), x(78f), y(306f)) // left foot
    lineTo(x(73f), y(306f))
    cubicTo(x(70f), y(304f), x(71f), y(298f), x(72f), y(294f)) // ankle
    // Left Leg outer edge
    cubicTo(x(71f), y(284f), x(67f), y(268f), x(66f), y(252f)) // calf bulge
    cubicTo(x(66f), y(236f), x(68f), y(220f), x(67f), y(210f)) // outer knee
    cubicTo(x(63f), y(178f), x(63f), y(152f), x(65f), y(136f)) // hip flare
    // Left Torso / Axilla
    cubicTo(x(67f), y(122f), x(68f), y(114f), x(67f), y(102f))
    cubicTo(x(66f), y(90f), x(64f), y(80f), x(57f), y(82f))
    // Left Hand & Arm inner edge
    cubicTo(x(56f), y(96f), x(55f), y(114f), x(55f), y(134f))
    cubicTo(x(55f), y(156f), x(53f), y(172f), x(52f), y(184f))
    lineTo(x(49f), y(184f))
    // Left Arm outer edge
    cubicTo(x(47f), y(182f), x(46f), y(174f), x(47f), y(166f))
    cubicTo(x(44f), y(148f), x(45f), y(128f), x(49f), y(114f)) // elbow
    cubicTo(x(44f), y(102f), x(46f), y(88f), x(48f), y(76f))
    cubicTo(x(44f), y(66f), x(46f), y(54f), x(52f), y(48f)) // shoulder
    // Left Trapezius slope & neck
    cubicTo(x(62f), y(45f), x(74f), y(42f), x(80f), y(40f))
    lineTo(x(81f), y(34f))
    // Cranium left
    cubicTo(x(79f), y(32f), x(77f), y(29f), x(76f), y(24f))
    cubicTo(x(75f), y(16f), x(78f), y(10f), x(85f), y(10f))
    close()
  }

  // Draw Base Silhouette
  drawPath(baseBodyPath, color = baseBodyColor, style = Fill)
  drawPath(baseBodyPath, color = baseBorderColor, style = baseBorderStroke)

  // 3. Render Individual Anatomical Muscle Groups
  if (isFront) {
    // ---------------------------------------------
    // FRONT MUSCLES
    // ---------------------------------------------

    // NECK (Front - Sternocleidomastoid & Neck Triangle)
    val neckColor = getColor(MuscleGroup.NECK)
    val isNeckSelected = selectedMuscle == MuscleGroup.NECK
    val neckFront = Path().apply {
      moveTo(x(80f), y(33f))
      cubicTo(x(78f), y(37f), x(77f), y(42f), x(76f), y(47f))
      lineTo(x(94f), y(47f))
      cubicTo(x(93f), y(42f), x(92f), y(37f), x(90f), y(33f))
      close()
    }
    drawPath(neckFront, color = neckColor, style = Fill)
    drawPath(
      neckFront,
      color = if (isNeckSelected) Color.White else muscleBorderColor,
      style = if (isNeckSelected) selectedBorderStroke else muscleBorderStroke
    )

    // CHEST (Pectoralis Major: Left & Right)
    val chestColor = getColor(MuscleGroup.CHEST)
    val isChestSelected = selectedMuscle == MuscleGroup.CHEST
    val leftChest = Path().apply {
      moveTo(x(83f), y(48f))
      lineTo(x(68f), y(51f))
      cubicTo(x(65f), y(60f), x(65f), y(70f), x(71f), y(75f))
      cubicTo(x(76f), y(77f), x(81f), y(76f), x(83f), y(73f))
      close()
    }
    val rightChest = Path().apply {
      moveTo(x(87f), y(48f))
      lineTo(x(102f), y(51f))
      cubicTo(x(105f), y(60f), x(105f), y(70f), x(99f), y(75f))
      cubicTo(x(94f), y(77f), x(89f), y(76f), x(87f), y(73f))
      close()
    }
    drawPath(leftChest, color = chestColor, style = Fill)
    drawPath(leftChest, color = if (isChestSelected) Color.White else muscleBorderColor, style = if (isChestSelected) selectedBorderStroke else muscleBorderStroke)
    drawPath(rightChest, color = chestColor, style = Fill)
    drawPath(rightChest, color = if (isChestSelected) Color.White else muscleBorderColor, style = if (isChestSelected) selectedBorderStroke else muscleBorderStroke)

    // FRONT SHOULDERS (Front Delts)
    val frontDeltColor = getColor(MuscleGroup.FRONT_DELTS)
    val leftFrontDelt = Path().apply {
      moveTo(x(67f), y(49f))
      cubicTo(x(59f), y(51f), x(56f), y(60f), x(60f), y(70f))
      lineTo(x(65f), y(69f))
      cubicTo(x(63f), y(60f), x(65f), y(53f), x(67f), y(49f))
      close()
    }
    val rightFrontDelt = Path().apply {
      moveTo(x(103f), y(49f))
      cubicTo(x(111f), y(51f), x(114f), y(60f), x(110f), y(70f))
      lineTo(x(105f), y(69f))
      cubicTo(x(107f), y(60f), x(105f), y(53f), x(103f), y(49f))
      close()
    }
    drawPath(leftFrontDelt, color = frontDeltColor, style = Fill)
    drawPath(leftFrontDelt, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightFrontDelt, color = frontDeltColor, style = Fill)
    drawPath(rightFrontDelt, color = muscleBorderColor, style = muscleBorderStroke)

    // SIDE SHOULDERS (Side / Lateral Delts)
    val sideDeltColor = getColor(MuscleGroup.SIDE_DELTS)
    val leftSideDelt = Path().apply {
      moveTo(x(56f), y(50f))
      cubicTo(x(49f), y(54f), x(48f), y(64f), x(53f), y(72f))
      lineTo(x(57f), y(69f))
      cubicTo(x(54f), y(62f), x(54f), y(55f), x(56f), y(50f))
      close()
    }
    val rightSideDelt = Path().apply {
      moveTo(x(114f), y(50f))
      cubicTo(x(121f), y(54f), x(122f), y(64f), x(117f), y(72f))
      lineTo(x(113f), y(69f))
      cubicTo(x(116f), y(62f), x(116f), y(55f), x(114f), y(50f))
      close()
    }
    drawPath(leftSideDelt, color = sideDeltColor, style = Fill)
    drawPath(leftSideDelt, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightSideDelt, color = sideDeltColor, style = Fill)
    drawPath(rightSideDelt, color = muscleBorderColor, style = muscleBorderStroke)

    // BICEPS
    val bicepColor = getColor(MuscleGroup.BICEPS)
    val leftBicep = Path().apply {
      moveTo(x(54f), y(74f))
      cubicTo(x(47f), y(82f), x(47f), y(96f), x(51f), y(106f))
      cubicTo(x(56f), y(105f), x(57f), y(94f), x(57f), y(76f))
      close()
    }
    val rightBicep = Path().apply {
      moveTo(x(116f), y(74f))
      cubicTo(x(123f), y(82f), x(123f), y(96f), x(119f), y(106f))
      cubicTo(x(114f), y(105f), x(113f), y(94f), x(113f), y(76f))
      close()
    }
    drawPath(leftBicep, color = bicepColor, style = Fill)
    drawPath(leftBicep, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightBicep, color = bicepColor, style = Fill)
    drawPath(rightBicep, color = muscleBorderColor, style = muscleBorderStroke)

    // FOREARMS
    val forearmColor = getColor(MuscleGroup.FOREARMS)
    val leftForearm = Path().apply {
      moveTo(x(51f), y(112f))
      cubicTo(x(44f), y(124f), x(43f), y(142f), x(46f), y(162f))
      lineTo(x(50f), y(162f))
      cubicTo(x(52f), y(144f), x(54f), y(126f), x(54f), y(114f))
      close()
    }
    val rightForearm = Path().apply {
      moveTo(x(119f), y(112f))
      cubicTo(x(126f), y(124f), x(127f), y(142f), x(124f), y(162f))
      lineTo(x(120f), y(162f))
      cubicTo(x(118f), y(144f), x(116f), y(126f), x(116f), y(114f))
      close()
    }
    drawPath(leftForearm, color = forearmColor, style = Fill)
    drawPath(leftForearm, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightForearm, color = forearmColor, style = Fill)
    drawPath(rightForearm, color = muscleBorderColor, style = muscleBorderStroke)

    // ABS (Rectus Abdominis - Sculpted 6-Pack)
    val absColor = getColor(MuscleGroup.ABS)
    val absRows = listOf(
      Pair(77f, 9f),
      Pair(88f, 9f),
      Pair(99f, 11f)
    )
    for ((rowY, height) in absRows) {
      // Left ab
      drawRoundRect(
        color = absColor,
        topLeft = Offset(x(78f), y(rowY)),
        size = Size(6f * scaleX, height * scaleY),
        cornerRadius = CornerRadius(2.5f * scaleX, 2.5f * scaleY)
      )
      drawRoundRect(
        color = muscleBorderColor,
        topLeft = Offset(x(78f), y(rowY)),
        size = Size(6f * scaleX, height * scaleY),
        cornerRadius = CornerRadius(2.5f * scaleX, 2.5f * scaleY),
        style = muscleBorderStroke
      )

      // Right ab
      drawRoundRect(
        color = absColor,
        topLeft = Offset(x(86f), y(rowY)),
        size = Size(6f * scaleX, height * scaleY),
        cornerRadius = CornerRadius(2.5f * scaleX, 2.5f * scaleY)
      )
      drawRoundRect(
        color = muscleBorderColor,
        topLeft = Offset(x(86f), y(rowY)),
        size = Size(6f * scaleX, height * scaleY),
        cornerRadius = CornerRadius(2.5f * scaleX, 2.5f * scaleY),
        style = muscleBorderStroke
      )
    }

    // OBLIQUES
    val obliqueColor = getColor(MuscleGroup.OBLIQUES)
    val leftOblique = Path().apply {
      moveTo(x(71f), y(78f))
      cubicTo(x(70f), y(92f), x(72f), y(108f), x(76f), y(122f))
      lineTo(x(79f), y(116f))
      cubicTo(x(76f), y(102f), x(74f), y(90f), x(75f), y(80f))
      close()
    }
    val rightOblique = Path().apply {
      moveTo(x(99f), y(78f))
      cubicTo(x(100f), y(92f), x(98f), y(108f), x(94f), y(122f))
      lineTo(x(91f), y(116f))
      cubicTo(x(94f), y(102f), x(96f), y(90f), x(95f), y(80f))
      close()
    }
    drawPath(leftOblique, color = obliqueColor, style = Fill)
    drawPath(leftOblique, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightOblique, color = obliqueColor, style = Fill)
    drawPath(rightOblique, color = muscleBorderColor, style = muscleBorderStroke)

    // QUADRICEPS (Rectus Femoris, Vastus Lateralis & Medialis)
    val quadColor = getColor(MuscleGroup.QUADS)
    val leftQuad = Path().apply {
      moveTo(x(77f), y(138f))
      cubicTo(x(69f), y(148f), x(67f), y(176f), x(72f), y(206f))
      cubicTo(x(75f), y(210f), x(79f), y(210f), x(80f), y(206f))
      cubicTo(x(83f), y(186f), x(83f), y(162f), x(84f), y(144f))
      close()
    }
    val rightQuad = Path().apply {
      moveTo(x(93f), y(138f))
      cubicTo(x(101f), y(148f), x(103f), y(176f), x(98f), y(206f))
      cubicTo(x(95f), y(210f), x(91f), y(210f), x(90f), y(206f))
      cubicTo(x(87f), y(186f), x(87f), y(162f), x(86f), y(144f))
      close()
    }
    drawPath(leftQuad, color = quadColor, style = Fill)
    drawPath(leftQuad, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightQuad, color = quadColor, style = Fill)
    drawPath(rightQuad, color = muscleBorderColor, style = muscleBorderStroke)

    // CALVES (Front - Tibialis Anterior & Gastrocnemius Lateral Sweep)
    val calfColor = getColor(MuscleGroup.CALVES)
    val leftCalf = Path().apply {
      moveTo(x(72f), y(222f))
      cubicTo(x(68f), y(234f), x(68f), y(258f), x(73f), y(282f))
      lineTo(x(78f), y(282f))
      cubicTo(x(78f), y(260f), x(79f), y(236f), x(78f), y(222f))
      close()
    }
    val rightCalf = Path().apply {
      moveTo(x(98f), y(222f))
      cubicTo(x(102f), y(234f), x(102f), y(258f), x(97f), y(282f))
      lineTo(x(92f), y(282f))
      cubicTo(x(92f), y(260f), x(91f), y(236f), x(92f), y(222f))
      close()
    }
    drawPath(leftCalf, color = calfColor, style = Fill)
    drawPath(leftCalf, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightCalf, color = calfColor, style = Fill)
    drawPath(rightCalf, color = muscleBorderColor, style = muscleBorderStroke)

  } else {
    // ---------------------------------------------
    // BACK MUSCLES
    // ---------------------------------------------

    // NECK (Back - Cervical Splenius & Upper Trapezius Neck Band)
    val neckColor = getColor(MuscleGroup.NECK)
    val isNeckSelected = selectedMuscle == MuscleGroup.NECK
    val neckBack = Path().apply {
      moveTo(x(79f), y(28f))
      cubicTo(x(77f), y(33f), x(76f), y(39f), x(75f), y(45f))
      lineTo(x(95f), y(45f))
      cubicTo(x(94f), y(39f), x(93f), y(33f), x(91f), y(28f))
      close()
    }
    drawPath(neckBack, color = neckColor, style = Fill)
    drawPath(
      neckBack,
      color = if (isNeckSelected) Color.White else muscleBorderColor,
      style = if (isNeckSelected) selectedBorderStroke else muscleBorderStroke
    )

    // UPPER BACK / TRAPEZIUS (Majestic Diamond / Kite Shape)
    val upperBackColor = getColor(MuscleGroup.UPPER_BACK)
    val isUpperBackSelected = selectedMuscle == MuscleGroup.UPPER_BACK
    val upperBackPath = Path().apply {
      moveTo(x(85f), y(45f))
      lineTo(x(102f), y(54f))
      lineTo(x(85f), y(86f))
      lineTo(x(68f), y(54f))
      close()
    }
    drawPath(upperBackPath, color = upperBackColor, style = Fill)
    drawPath(upperBackPath, color = if (isUpperBackSelected) Color.White else muscleBorderColor, style = if (isUpperBackSelected) selectedBorderStroke else muscleBorderStroke)

    // REAR DELTS
    val rearDeltColor = getColor(MuscleGroup.REAR_DELTS)
    val leftRearDelt = Path().apply {
      moveTo(x(67f), y(49f))
      cubicTo(x(57f), y(52f), x(55f), y(63f), x(60f), y(72f))
      lineTo(x(65f), y(69f))
      cubicTo(x(63f), y(62f), x(64f), y(54f), x(67f), y(49f))
      close()
    }
    val rightRearDelt = Path().apply {
      moveTo(x(103f), y(49f))
      cubicTo(x(113f), y(52f), x(115f), y(63f), x(110f), y(72f))
      lineTo(x(105f), y(69f))
      cubicTo(x(107f), y(62f), x(106f), y(54f), x(103f), y(49f))
      close()
    }
    drawPath(leftRearDelt, color = rearDeltColor, style = Fill)
    drawPath(leftRearDelt, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightRearDelt, color = rearDeltColor, style = Fill)
    drawPath(rightRearDelt, color = muscleBorderColor, style = muscleBorderStroke)

    // TRICEPS (Posterior Horseshoe)
    val tricepColor = getColor(MuscleGroup.TRICEPS)
    val leftTricep = Path().apply {
      moveTo(x(55f), y(74f))
      cubicTo(x(48f), y(82f), x(48f), y(96f), x(52f), y(108f))
      cubicTo(x(57f), y(106f), x(58f), y(94f), x(58f), y(76f))
      close()
    }
    val rightTricep = Path().apply {
      moveTo(x(115f), y(74f))
      cubicTo(x(122f), y(82f), x(122f), y(96f), x(118f), y(108f))
      cubicTo(x(113f), y(106f), x(112f), y(94f), x(112f), y(76f))
      close()
    }
    drawPath(leftTricep, color = tricepColor, style = Fill)
    drawPath(leftTricep, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightTricep, color = tricepColor, style = Fill)
    drawPath(rightTricep, color = muscleBorderColor, style = muscleBorderStroke)

    // LATS (Latissimus Dorsi - V-Taper Wings)
    val latsColor = getColor(MuscleGroup.LATS)
    val leftLat = Path().apply {
      moveTo(x(69f), y(76f))
      cubicTo(x(66f), y(88f), x(68f), y(106f), x(77f), y(122f))
      lineTo(x(83f), y(106f))
      lineTo(x(83f), y(86f))
      close()
    }
    val rightLat = Path().apply {
      moveTo(x(101f), y(76f))
      cubicTo(x(104f), y(88f), x(102f), y(106f), x(93f), y(122f))
      lineTo(x(87f), y(106f))
      lineTo(x(87f), y(86f))
      close()
    }
    drawPath(leftLat, color = latsColor, style = Fill)
    drawPath(leftLat, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightLat, color = latsColor, style = Fill)
    drawPath(rightLat, color = muscleBorderColor, style = muscleBorderStroke)

    // LOWER BACK (Erector Spinae)
    val lowerBackColor = getColor(MuscleGroup.LOWER_BACK)
    drawRoundRect(
      color = lowerBackColor,
      topLeft = Offset(x(80.5f), y(92f)),
      size = Size(3.5f * scaleX, 30f * scaleY),
      cornerRadius = CornerRadius(2f * scaleX, 2f * scaleY)
    )
    drawRoundRect(
      color = muscleBorderColor,
      topLeft = Offset(x(80.5f), y(92f)),
      size = Size(3.5f * scaleX, 30f * scaleY),
      cornerRadius = CornerRadius(2f * scaleX, 2f * scaleY),
      style = muscleBorderStroke
    )
    drawRoundRect(
      color = lowerBackColor,
      topLeft = Offset(x(86f), y(92f)),
      size = Size(3.5f * scaleX, 30f * scaleY),
      cornerRadius = CornerRadius(2f * scaleX, 2f * scaleY)
    )
    drawRoundRect(
      color = muscleBorderColor,
      topLeft = Offset(x(86f), y(92f)),
      size = Size(3.5f * scaleX, 30f * scaleY),
      cornerRadius = CornerRadius(2f * scaleX, 2f * scaleY),
      style = muscleBorderStroke
    )

    // GLUTES (Gluteus Maximus)
    val gluteColor = getColor(MuscleGroup.GLUTES)
    val leftGlute = Path().apply {
      moveTo(x(83f), y(126f))
      cubicTo(x(73f), y(130f), x(70f), y(146f), x(73f), y(158f))
      cubicTo(x(76f), y(164f), x(82f), y(165f), x(83f), y(158f))
      close()
    }
    val rightGlute = Path().apply {
      moveTo(x(87f), y(126f))
      cubicTo(x(97f), y(130f), x(100f), y(146f), x(97f), y(158f))
      cubicTo(x(94f), y(164f), x(88f), y(165f), x(87f), y(158f))
      close()
    }
    drawPath(leftGlute, color = gluteColor, style = Fill)
    drawPath(leftGlute, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightGlute, color = gluteColor, style = Fill)
    drawPath(rightGlute, color = muscleBorderColor, style = muscleBorderStroke)

    // HAMSTRINGS (Biceps Femoris & Semitendinosus)
    val hamColor = getColor(MuscleGroup.HAMSTRINGS)
    val leftHam = Path().apply {
      moveTo(x(74f), y(164f))
      cubicTo(x(69f), y(176f), x(69f), y(194f), x(73f), y(208f))
      lineTo(x(81f), y(208f))
      cubicTo(x(82f), y(194f), x(82f), y(178f), x(81f), y(164f))
      close()
    }
    val rightHam = Path().apply {
      moveTo(x(96f), y(164f))
      cubicTo(x(101f), y(176f), x(101f), y(194f), x(97f), y(208f))
      lineTo(x(89f), y(208f))
      cubicTo(x(88f), y(194f), x(88f), y(178f), x(89f), y(164f))
      close()
    }
    drawPath(leftHam, color = hamColor, style = Fill)
    drawPath(leftHam, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightHam, color = hamColor, style = Fill)
    drawPath(rightHam, color = muscleBorderColor, style = muscleBorderStroke)

    // CALVES (Back - Diamond Gastrocnemius Heads)
    val calfColor = getColor(MuscleGroup.CALVES)
    val leftCalf = Path().apply {
      moveTo(x(74f), y(222f))
      cubicTo(x(66f), y(234f), x(67f), y(258f), x(72f), y(282f))
      lineTo(x(78f), y(282f))
      cubicTo(x(80f), y(260f), x(81f), y(236f), x(77f), y(222f))
      close()
    }
    val rightCalf = Path().apply {
      moveTo(x(96f), y(222f))
      cubicTo(x(104f), y(234f), x(103f), y(258f), x(98f), y(282f))
      lineTo(x(92f), y(282f))
      cubicTo(x(90f), y(260f), x(89f), y(236f), x(93f), y(222f))
      close()
    }
    drawPath(leftCalf, color = calfColor, style = Fill)
    drawPath(leftCalf, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightCalf, color = calfColor, style = Fill)
    drawPath(rightCalf, color = muscleBorderColor, style = muscleBorderStroke)

    // FOREARMS (Posterior view)
    val forearmColor = getColor(MuscleGroup.FOREARMS)
    val leftForearm = Path().apply {
      moveTo(x(51f), y(112f))
      cubicTo(x(44f), y(124f), x(43f), y(142f), x(46f), y(162f))
      lineTo(x(50f), y(162f))
      cubicTo(x(52f), y(144f), x(54f), y(126f), x(54f), y(114f))
      close()
    }
    val rightForearm = Path().apply {
      moveTo(x(119f), y(112f))
      cubicTo(x(126f), y(124f), x(127f), y(142f), x(124f), y(162f))
      lineTo(x(120f), y(162f))
      cubicTo(x(118f), y(144f), x(116f), y(126f), x(116f), y(114f))
      close()
    }
    drawPath(leftForearm, color = forearmColor, style = Fill)
    drawPath(leftForearm, color = muscleBorderColor, style = muscleBorderStroke)
    drawPath(rightForearm, color = forearmColor, style = Fill)
    drawPath(rightForearm, color = muscleBorderColor, style = muscleBorderStroke)
  }
}

// -------------------------------------------------------------
// Interactive Tap Detection
// -------------------------------------------------------------

private fun detectMuscleHit(
  tapOffset: Offset,
  canvasWidth: Float,
  canvasHeight: Float,
  viewMode: Int
): MuscleGroup? {
  val x = tapOffset.x
  val y = tapOffset.y

  val isTappingFront = when (viewMode) {
    0 -> x < canvasWidth * 0.5f
    1 -> true
    else -> false
  }

  val topY = canvasHeight * 0.02f
  val figH = canvasHeight * 0.96f
  val relY = ((y - topY) / figH).coerceIn(0f, 1f)

  val centerX = when (viewMode) {
    0 -> if (isTappingFront) canvasWidth * 0.26f else canvasWidth * 0.74f
    else -> canvasWidth * 0.5f
  }
  val halfFigW = when (viewMode) {
    0 -> canvasWidth * 0.245f
    else -> canvasWidth * 0.41f
  }
  val normX = (0.5f + (x - centerX) / (halfFigW * 2f)).coerceIn(0f, 1f)

  return if (isTappingFront) {
    when {
      relY in 0.05f..0.15f && normX in 0.38f..0.62f -> MuscleGroup.NECK
      relY in 0.15f..0.26f -> {
        if (normX < 0.26f || normX > 0.74f) MuscleGroup.SIDE_DELTS
        else if (normX in 0.26f..0.38f || normX in 0.62f..0.74f) MuscleGroup.FRONT_DELTS
        else MuscleGroup.CHEST
      }
      relY in 0.26f..0.36f -> {
        if (normX < 0.28f || normX > 0.72f) MuscleGroup.BICEPS
        else MuscleGroup.ABS
      }
      relY in 0.36f..0.48f -> {
        if (normX < 0.26f || normX > 0.74f) MuscleGroup.FOREARMS
        else if (normX in 0.26f..0.38f || normX in 0.62f..0.74f) MuscleGroup.OBLIQUES
        else MuscleGroup.ABS
      }
      relY in 0.48f..0.68f -> MuscleGroup.QUADS
      relY in 0.68f..0.94f -> MuscleGroup.CALVES
      else -> null
    }
  } else {
    when {
      relY in 0.05f..0.15f && normX in 0.38f..0.62f -> MuscleGroup.NECK
      relY in 0.15f..0.26f -> {
        if (normX in 0.36f..0.64f) MuscleGroup.UPPER_BACK
        else MuscleGroup.REAR_DELTS
      }
      relY in 0.26f..0.37f -> {
        if (normX < 0.28f || normX > 0.72f) MuscleGroup.TRICEPS
        else MuscleGroup.LATS
      }
      relY in 0.37f..0.48f -> {
        if (normX in 0.38f..0.62f) MuscleGroup.LOWER_BACK
        else MuscleGroup.GLUTES
      }
      relY in 0.48f..0.68f -> MuscleGroup.HAMSTRINGS
      relY in 0.68f..0.94f -> MuscleGroup.CALVES
      else -> null
    }
  }
}
