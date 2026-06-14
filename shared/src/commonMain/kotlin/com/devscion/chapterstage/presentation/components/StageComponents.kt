package com.devscion.chapterstage.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.devscion.chapterstage.design.stageColors
import com.devscion.chapterstage.design.spacing
import com.devscion.chapterstage.presentation.model.AgentStatus
import com.devscion.chapterstage.presentation.model.AgentUiModel
import com.devscion.chapterstage.presentation.model.TraceEventUiModel

@Immutable
data class StageLayoutInfo(
    val isWide: Boolean,
    val contentPadding: Dp,
)

enum class StageButtonVariant {
    Primary,
    Ghost,
    Soft,
    Danger,
}

@Composable
fun StageScreen(
    modifier: Modifier = Modifier,
    scroll: Boolean = true,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.(StageLayoutInfo) -> Unit,
) {
    val colors = MaterialTheme.stageColors
    val spacing = MaterialTheme.spacing
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.backgroundHigh, colors.background),
                ),
            )
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        val isWide = maxWidth >= spacing.wideBreakpoint
        val padding = if (isWide) spacing.wideScreenPadding else spacing.screenPadding
        val contentModifier = Modifier
            .fillMaxWidth()
            .widthIn(max = spacing.maxContentWidth)
            .padding(horizontal = padding, vertical = spacing.screenPadding)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            if (scroll) {
                Column(
                    modifier = contentModifier.verticalScroll(scrollState),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                ) {
                    content(StageLayoutInfo(isWide = isWide, contentPadding = padding))
                }
            } else {
                Column(
                    modifier = contentModifier.fillMaxSize(),
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                ) {
                    content(StageLayoutInfo(isWide = isWide, contentPadding = padding))
                }
            }
        }
    }
}

@Composable
fun StageCard(
    modifier: Modifier = Modifier,
    accent: Color? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(MaterialTheme.spacing.medium),
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.stageColors
    val borderColor = accent?.copy(alpha = 0.42f) ?: colors.line
    val shape = MaterialTheme.shapes.extraLarge
    val cardModifier = modifier
        .clip(shape)
        .animateContentSize(animationSpec = spring())
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)

    Card(
        modifier = cardModifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(
                    if (accent != null) {
                        Brush.verticalGradient(
                            listOf(accent.copy(alpha = 0.12f), Color.Transparent),
                        )
                    } else {
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                    },
                )
                .padding(contentPadding),
        ) {
            Column(content = content)
        }
    }
}

@Composable
fun StageLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.stageColors.textTertiary,
    dotColor: Color? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dotColor != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun StageButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: StageButtonVariant = StageButtonVariant.Primary,
    enabled: Boolean = true,
    large: Boolean = false,
    leadingText: String? = null,
    trailingText: String? = null,
) {
    val colors = MaterialTheme.stageColors
    val buttonColors = when (variant) {
        StageButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = Color.White,
            disabledContainerColor = colors.primary.copy(alpha = 0.42f),
            disabledContentColor = Color.White.copy(alpha = 0.54f),
        )
        StageButtonVariant.Ghost -> ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.04f),
            contentColor = colors.textPrimary,
            disabledContainerColor = Color.White.copy(alpha = 0.03f),
            disabledContentColor = colors.textTertiary,
        )
        StageButtonVariant.Soft -> ButtonDefaults.buttonColors(
            containerColor = colors.primarySoft,
            contentColor = colors.primaryText,
            disabledContainerColor = colors.primarySoft.copy(alpha = 0.42f),
            disabledContentColor = colors.primaryText.copy(alpha = 0.54f),
        )
        StageButtonVariant.Danger -> ButtonDefaults.buttonColors(
            containerColor = colors.error.copy(alpha = 0.14f),
            contentColor = colors.error,
            disabledContainerColor = colors.error.copy(alpha = 0.06f),
            disabledContentColor = colors.error.copy(alpha = 0.48f),
        )
    }

    val shape = if (large) MaterialTheme.shapes.extraLarge else MaterialTheme.shapes.large

    Button(
        onClick = onClick,
        modifier = modifier
            .height(if (large) 56.dp else 50.dp)
            .clip(shape)
            .animateContentSize(animationSpec = spring()),
        enabled = enabled,
        shape = shape,
        colors = buttonColors,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp,
        ),
        contentPadding = PaddingValues(horizontal = if (large) MaterialTheme.spacing.large else MaterialTheme.spacing.medium),
    ) {
        if (leadingText != null) {
            Text(
                text = leadingText,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small),
            )
        }
        Text(
            text = text,
            style = if (large) MaterialTheme.typography.titleMedium else MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (trailingText != null) {
            Text(
                text = trailingText,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = MaterialTheme.spacing.small),
            )
        }
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.stageColors
    val shape = MaterialTheme.shapes.large

    Surface(
        modifier = modifier
            .size(38.dp)
            .clip(shape)
            .clickable(onClick = onClick),
        shape = shape,
        color = Color.White.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, colors.line),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "<-",
                color = colors.textPrimary,
                style = MaterialTheme.typography.labelLarge,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
fun StageTopBar(
    title: String? = null,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    showLogo: Boolean = false,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        when {
            showLogo -> ChapterStageLogo()
            onBack != null -> BackButton(onClick = onBack)
        }

        Column(modifier = Modifier.weight(1f)) {
            if (title != null) {
                Text(
                    text = title,
                    color = MaterialTheme.stageColors.textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = MaterialTheme.stageColors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        trailing?.invoke()
    }
}

@Composable
fun ChapterStageLogo(
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = MaterialTheme.stageColors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        Canvas(modifier = Modifier.size(if (compact) 18.dp else 24.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val points = listOf(
                center to colors.primary,
                Offset(size.width * 0.2f, size.height * 0.25f) to colors.cyan,
                Offset(size.width * 0.8f, size.height * 0.28f) to colors.success,
                Offset(size.width * 0.25f, size.height * 0.78f) to colors.warning,
                Offset(size.width * 0.75f, size.height * 0.78f) to colors.pink,
            )
            points.drop(1).forEach { (point, _) ->
                drawLine(
                    color = Color.White.copy(alpha = 0.22f),
                    start = center,
                    end = point,
                    strokeWidth = 1.dp.toPx(),
                )
            }
            points.forEach { (point, color) ->
                drawCircle(color = color, radius = if (point == center) 3.2.dp.toPx() else 1.8.dp.toPx(), center = point)
            }
        }
        Text(
            text = "Chapter",
            color = colors.textPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "Stage",
            color = colors.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun StageIconBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(color.copy(alpha = 0.12f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.32f)), MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun StageTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else 8,
) {
    val colors = MaterialTheme.stageColors

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                color = colors.textTertiary,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = MaterialTheme.shapes.medium,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.textPrimary),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            focusedBorderColor = colors.primary.copy(alpha = 0.68f),
            unfocusedBorderColor = colors.line,
            focusedContainerColor = Color.White.copy(alpha = 0.03f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
            cursorColor = colors.primary,
        ),
    )
}

@Composable
fun FieldLabel(
    label: String,
    modifier: Modifier = Modifier,
    optional: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.stageColors.textSecondary,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
        if (optional) {
            Text(
                text = "OPTIONAL",
                color = MaterialTheme.stageColors.textTertiary,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
fun SegmentedControl(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.stageColors
    val containerShape = MaterialTheme.shapes.medium
    val itemShape = MaterialTheme.shapes.small

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(Color.White.copy(alpha = 0.04f))
            .border(BorderStroke(1.dp, colors.line), containerShape)
            .padding(MaterialTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) colors.primary else Color.Transparent,
                animationSpec = tween(durationMillis = 180),
                label = "SegmentBackground",
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else colors.textSecondary,
                animationSpec = tween(durationMillis = 180),
                label = "SegmentText",
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(itemShape)
                    .background(backgroundColor)
                    .clickable { onSelected(option) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PillGroup(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        options.forEach { option ->
            val colors = MaterialTheme.stageColors
            val isSelected = option == selected
            val shape = MaterialTheme.shapes.medium
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) colors.primary.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.03f),
                animationSpec = tween(durationMillis = 180),
                label = "PillBackground",
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) colors.primary.copy(alpha = 0.68f) else colors.line,
                animationSpec = tween(durationMillis = 180),
                label = "PillBorder",
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) colors.textPrimary else colors.textSecondary,
                animationSpec = tween(durationMillis = 180),
                label = "PillText",
            )
            Surface(
                modifier = Modifier
                    .clip(shape)
                    .clickable { onSelected(option) },
                shape = shape,
                color = containerColor,
                border = BorderStroke(
                    width = 1.dp,
                    color = borderColor,
                ),
            ) {
                Text(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.medium,
                        vertical = MaterialTheme.spacing.small,
                    ),
                    text = option,
                    color = textColor,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun StageSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.stageColors

    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = colors.primary,
            uncheckedThumbColor = colors.textSecondary,
            uncheckedTrackColor = Color.White.copy(alpha = 0.12f),
            uncheckedBorderColor = colors.lineHigh,
        ),
    )
}

@Composable
fun StatusDot(
    status: AgentStatus,
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    val resolved = when (status) {
        AgentStatus.Waiting -> MaterialTheme.stageColors.textTertiary
        AgentStatus.Active -> color
        AgentStatus.Completed -> MaterialTheme.stageColors.success
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(resolved),
    )
}

@Composable
fun AgentAvatar(
    agent: AgentUiModel,
    status: AgentStatus,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    avatarSize: Dp = 42.dp,
) {
    val colors = MaterialTheme.stageColors
    val ringColor = when {
        active -> agent.color
        status == AgentStatus.Completed -> colors.success
        status == AgentStatus.Active -> agent.color
        else -> colors.lineHigh
    }
    val animatedRingColor by animateColorAsState(
        targetValue = ringColor,
        animationSpec = tween(durationMillis = 220),
        label = "AgentRingColor",
    )
    val ringWidth by animateDpAsState(
        targetValue = if (active) 2.dp else 1.dp,
        animationSpec = spring(),
        label = "AgentRingWidth",
    )
    val avatarAlpha by animateFloatAsState(
        targetValue = if (active) 0.16f else 0.06f,
        animationSpec = tween(durationMillis = 220),
        label = "AgentAvatarAlpha",
    )
    val pulseTransition = rememberInfiniteTransition(label = "AgentPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 920),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "AgentPulseAlpha",
    )

    Box(
        modifier = modifier.size(avatarSize),
        contentAlignment = Alignment.Center,
    ) {
        if (active) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(agent.color.copy(alpha = pulseAlpha)),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(agent.color.copy(alpha = avatarAlpha))
                .border(BorderStroke(ringWidth, animatedRingColor), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = agent.initials,
                color = if (status == AgentStatus.Waiting) colors.textSecondary else agent.color,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
        }

        AnimatedVisibility(
            visible = status == AgentStatus.Completed,
            enter = scaleIn(animationSpec = spring()) + fadeIn(animationSpec = tween(durationMillis = 140)),
            exit = scaleOut(animationSpec = tween(durationMillis = 120)) + fadeOut(animationSpec = tween(durationMillis = 120)),
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            Box(
                modifier = Modifier
                    .size(avatarSize * 0.36f)
                    .clip(CircleShape)
                    .background(colors.success)
                    .border(BorderStroke(2.dp, colors.background), CircleShape),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = colors.background,
                        start = Offset(size.width * 0.28f, size.height * 0.54f),
                        end = Offset(size.width * 0.45f, size.height * 0.7f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                    drawLine(
                        color = colors.background,
                        start = Offset(size.width * 0.45f, size.height * 0.7f),
                        end = Offset(size.width * 0.74f, size.height * 0.34f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
fun BandRoomBadge(
    live: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.stageColors
    val badgeColor by animateColorAsState(
        targetValue = if (live) colors.primary else colors.success,
        animationSpec = tween(durationMillis = 220),
        label = "BandRoomBadgeColor",
    )

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(badgeColor.copy(alpha = 0.1f))
            .border(BorderStroke(1.dp, badgeColor.copy(alpha = 0.28f)), CircleShape)
            .padding(horizontal = MaterialTheme.spacing.small, vertical = MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        StatusDot(
            status = if (live) AgentStatus.Active else AgentStatus.Completed,
            color = badgeColor,
            size = 7.dp,
        )
        Text(
            text = "BAND ROOM",
            color = if (live) colors.primaryText else colors.success,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
        )
        if (live) {
            Text(
                text = "LIVE",
                color = colors.success,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@Composable
fun StageProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.stageColors.primary,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress / 100f,
        animationSpec = tween(durationMillis = 420),
        label = "StageProgress",
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(7.dp)
            .clip(CircleShape),
        color = color,
        trackColor = Color.White.copy(alpha = 0.08f),
    )
}

@Composable
fun AgentStrip(
    agents: List<AgentUiModel>,
    statuses: Map<String, AgentStatus>,
    activeAgentId: String?,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.stageColors

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.Top,
    ) {
        agents.forEachIndexed { index, agent ->
            val status = statuses[agent.id] ?: AgentStatus.Waiting
            val active = agent.id == activeAgentId
            Column(
                modifier = Modifier.width(64.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                AgentAvatar(agent = agent, status = status, active = active, avatarSize = 38.dp)
                Text(
                    text = agent.shortName,
                    color = if (status == AgentStatus.Waiting) colors.textTertiary else colors.textPrimary,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = when {
                        active -> "working"
                        status == AgentStatus.Completed -> "done"
                        else -> "-"
                    },
                    color = when {
                        active -> agent.color
                        status == AgentStatus.Completed -> colors.success
                        else -> colors.textTertiary
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                )
            }

            if (index < agents.lastIndex) {
                val nextStatus = statuses[agents[index + 1].id] ?: AgentStatus.Waiting
                Box(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.medium)
                        .width(18.dp)
                        .height(2.dp)
                        .clip(CircleShape)
                        .background(if (nextStatus == AgentStatus.Waiting) colors.lineHigh else agent.color),
                )
            }
        }
    }
}

@Composable
fun TraceEventRow(
    event: TraceEventUiModel,
    agent: AgentUiModel,
    modifier: Modifier = Modifier,
    dense: Boolean = false,
) {
    val colors = MaterialTheme.stageColors
    val accent = eventAccentColor(event = event, agentColor = agent.color)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring()),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(agent.color),
            )
            if (!dense) {
                Box(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.extraSmall)
                        .width(2.dp)
                        .height(52.dp)
                        .clip(CircleShape)
                        .background(colors.line),
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                Text(
                    text = agent.name,
                    color = agent.color,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Surface(
                    shape = MaterialTheme.shapes.extraSmall,
                    color = accent.copy(alpha = 0.12f),
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.small,
                            vertical = MaterialTheme.spacing.xSmall,
                        ),
                        text = event.type,
                        color = accent,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = event.timestamp,
                    color = colors.textTertiary,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                )
            }

            Text(
                modifier = Modifier.padding(top = MaterialTheme.spacing.extraSmall),
                text = event.title,
                color = colors.textPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                modifier = Modifier.padding(top = MaterialTheme.spacing.xxSmall),
                text = event.message,
                color = colors.textSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (event.payload != null) {
                Surface(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.small)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = Color.White.copy(alpha = 0.03f),
                    border = BorderStroke(1.dp, colors.line),
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = MaterialTheme.spacing.small,
                            vertical = MaterialTheme.spacing.small,
                        ),
                        text = event.payload,
                        color = colors.textSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}

@Composable
fun PlanetSystemArtwork(
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.stageColors

    Canvas(
        modifier = modifier.size(width = 300.dp, height = 244.dp),
    ) {
        val center = Offset(size.width * 0.5f, size.height * 0.49f)
        val orbitColor = Color.White.copy(alpha = 0.08f)
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 7.dp.toPx()))

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(colors.primary.copy(alpha = 0.2f), Color.Transparent),
                center = center,
                radius = 110.dp.toPx(),
            ),
            radius = 110.dp.toPx(),
            center = center,
        )
        drawOval(
            color = orbitColor,
            topLeft = Offset(center.x - 118.dp.toPx(), center.y - 84.dp.toPx()),
            size = Size(236.dp.toPx(), 168.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
        )
        drawOval(
            color = Color.White.copy(alpha = 0.05f),
            topLeft = Offset(center.x - 80.dp.toPx(), center.y - 56.dp.toPx()),
            size = Size(160.dp.toPx(), 112.dp.toPx()),
            style = Stroke(width = 1.dp.toPx()),
        )

        val planets = listOf(
            Triple(Offset(size.width * 0.5f, size.height * 0.15f), 22.dp.toPx(), colors.warning),
            Triple(Offset(size.width * 0.84f, size.height * 0.38f), 21.dp.toPx(), colors.cyan),
            Triple(Offset(size.width * 0.73f, size.height * 0.77f), 19.dp.toPx(), Color(0xFF7894FF)),
            Triple(Offset(size.width * 0.27f, size.height * 0.77f), 30.dp.toPx(), Color(0xFFD19064)),
            Triple(Offset(size.width * 0.16f, size.height * 0.38f), 20.dp.toPx(), Color(0xFF8FE0E2)),
        )

        planets.forEach { (planetCenter, radius, color) ->
            val direction = planetCenter - center
            val length = direction.getDistance().coerceAtLeast(1f)
            val start = center + direction / length * 34.dp.toPx()
            val end = planetCenter - direction / length * (radius + 4.dp.toPx())
            drawLine(
                color = color.copy(alpha = 0.42f),
                start = start,
                end = end,
                strokeWidth = 1.2.dp.toPx(),
                pathEffect = dashEffect,
            )
        }

        planets.forEach { (planetCenter, radius, color) ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.55f), color, color.copy(alpha = 0.46f)),
                    center = planetCenter - Offset(radius * 0.35f, radius * 0.35f),
                    radius = radius * 1.5f,
                ),
                radius = radius,
                center = planetCenter,
            )
        }

        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF8B6BFF), Color(0xFF5B7BFF)),
                start = center - Offset(26.dp.toPx(), 26.dp.toPx()),
                end = center + Offset(26.dp.toPx(), 26.dp.toPx()),
            ),
            topLeft = center - Offset(26.dp.toPx(), 26.dp.toPx()),
            size = Size(52.dp.toPx(), 52.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx(), 16.dp.toPx()),
        )
        drawRoundRect(
            color = Color.White.copy(alpha = 0.28f),
            topLeft = center - Offset(10.dp.toPx(), 14.dp.toPx()),
            size = Size(20.dp.toPx(), 28.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx()),
            style = Stroke(width = 1.6.dp.toPx()),
        )
    }
}

private fun eventAccentColor(event: TraceEventUiModel, agentColor: Color): Color =
    when (event.type) {
        "Verified", "Selected" -> Color(0xFF2EE59D)
        "Rejected" -> Color(0xFFFF5C7A)
        "Published" -> Color(0xFF7C5CFF)
        else -> agentColor
    }
