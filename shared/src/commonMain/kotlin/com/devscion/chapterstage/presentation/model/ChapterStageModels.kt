package com.devscion.chapterstage.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import org.koin.core.annotation.Single

@Immutable
data class ChapterSourceDraft(
    val bookTitle: String = "",
    val chapterTitle: String = "",
    val text: String = "",
    val selectedFileName: String? = null,
    val selectedFileSizeLabel: String? = null,
    val selectedFileExtension: String? = null,
)

data class PickedChapterFile(
    val fileName: String,
    val bytes: ByteArray,
    val contentType: String,
) {
    val extension: String = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
    val sizeBytes: Long = bytes.size.toLong()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PickedChapterFile) return false

        if (fileName != other.fileName) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (contentType != other.contentType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileName.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }
}

enum class SourceMode(val label: String) {
    PasteText("Paste text"),
    UploadFile("Upload file"),
}

enum class AudienceLevelOption(val label: String) {
    Beginner("Beginner"),
    Intermediate("Intermediate"),
    Expert("Expert"),
}

enum class ExperienceStyleOption(
    val label: String,
    val shortLabel: String,
    val description: String,
) {
    VisualStory(
        label = "Visual Story",
        shortLabel = "Story",
        description = "Cinematic scenes that build the concept step by step.",
    ),
    LectureMode(
        label = "Lecture Mode",
        shortLabel = "Lecture",
        description = "Structured explainer with clear sections and notes.",
    ),
    ConceptMapFirst(
        label = "Concept Map First",
        shortLabel = "Map",
        description = "Lead with a map of how the ideas connect.",
    ),
    QuizFirst(
        label = "Quiz First",
        shortLabel = "Quiz",
        description = "Probe understanding, then teach to the gaps.",
    ),
    CaseStudy(
        label = "Case Study",
        shortLabel = "Case",
        description = "Anchor the chapter in one concrete real example.",
    ),
}

@Immutable
data class GenerationSettingsDraft(
    val audienceLevel: AudienceLevelOption = AudienceLevelOption.Intermediate,
    val experienceStyle: ExperienceStyleOption = ExperienceStyleOption.VisualStory,
    val targetScreenCount: Int = 8,
    val autoBrainstorm: Boolean = true,
)

enum class AgentStatus {
    Waiting,
    Active,
    Completed,
}

@Immutable
data class AgentUiModel(
    val id: String,
    val name: String,
    val shortName: String,
    val initials: String,
    val role: String,
    val color: Color,
)

@Immutable
data class TraceEventUiModel(
    val id: String,
    val agentId: String,
    val type: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val payload: String? = null,
    val elapsedSeconds: Int? = null,
)

@Immutable
data class RecentJobUiModel(
    val id: String,
    val title: String,
    val book: String,
    val status: String,
    val style: String,
    val updatedAt: String,
    val progress: Int = 0,
    val currentStep: String? = null,
    val experienceId: String? = null,
    val publicUrl: String? = null,
    val errorMessage: String? = null,
)

@Immutable
data class GenerationSnapshot(
    val jobId: String? = null,
    val experienceId: String? = null,
    val title: String? = null,
    val subtitle: String? = null,
    val statuses: Map<String, AgentStatus> = emptyMap(),
    val activeAgentId: String? = null,
    val events: List<TraceEventUiModel> = emptyList(),
    val progress: Int = 0,
    val elapsedSeconds: Int = 0,
    val isComplete: Boolean = false,
    val publicUrl: String? = null,
)

enum class ViewerLoadState {
    Loading,
    Loaded,
    Error,
}

@Single
class ChapterStageDemoContent {
    val sampleBookTitle: String = "Living Systems"
    val sampleChapterTitle: String = "Ch. 4 - Photosynthesis"
    val sampleUrl: String = "chapterstage.app/c/photosynthesis"

    val sampleText: String = """
        Photosynthesis is the process by which green plants, algae, and some bacteria convert light energy into chemical energy stored in glucose. It takes place mainly in chloroplasts, where the pigment chlorophyll absorbs light most strongly in blue and red wavelengths.

        The process has two linked stages. In the light-dependent reactions, water is split and light energy is captured as ATP and NADPH. Oxygen is released as a byproduct. In the Calvin cycle, that stored energy is used to fix carbon dioxide into sugar molecules that the plant can use for growth.

        A common misconception is that plants get most of their mass from soil. In reality, the carbon atoms in plant tissue come largely from carbon dioxide in the air. Water and mineral nutrients still matter, but the core transformation is the conversion of light energy into chemical energy.

        Photosynthesis also connects to ecosystems. Plants and algae form the base of many food webs because they turn sunlight into usable chemical energy. Animals, fungi, and many microbes depend on that stored energy either directly or indirectly. The chapter uses leaves, chloroplasts, and food web examples to show why this process is both microscopic and planetary.
    """.trimIndent()

    val agents: List<AgentUiModel> = listOf(
        AgentUiModel(
            id = "structure",
            name = "Structure",
            shortName = "Structure",
            initials = "ST",
            role = "Maps the chapter",
            color = Color(0xFF22D3EE),
        ),
        AgentUiModel(
            id = "brainstorm",
            name = "Auto-Brainstorm",
            shortName = "Brainstorm",
            initials = "BR",
            role = "Tests creative formats",
            color = Color(0xFFF6C85F),
        ),
        AgentUiModel(
            id = "visual",
            name = "Visual Builder",
            shortName = "Visual",
            initials = "VB",
            role = "Builds the website",
            color = Color(0xFFFF8BD1),
        ),
        AgentUiModel(
            id = "verifier",
            name = "Verifier",
            shortName = "Verifier",
            initials = "VF",
            role = "Checks faithfulness and safety",
            color = Color(0xFFFF5C7A),
        ),
    )

    val recentJobs: List<RecentJobUiModel> = listOf(
        RecentJobUiModel(
            id = "recent-photosynthesis",
            title = "Photosynthesis",
            book = "Living Systems - Ch.4",
            status = "ready",
            style = "Visual Story",
            updatedAt = "2m ago",
        ),
        RecentJobUiModel(
            id = "recent-supply-demand",
            title = "Supply and Demand",
            book = "Foundations of Economics - Ch.2",
            status = "generating",
            style = "Case Study",
            updatedAt = "now",
        ),
        RecentJobUiModel(
            id = "recent-french-revolution",
            title = "The French Revolution",
            book = "A Short History - Ch.9",
            status = "ready",
            style = "Concept Map",
            updatedAt = "1h ago",
        ),
    )

    val traceEvents: List<TraceEventUiModel> = listOf(
        TraceEventUiModel(
            id = "event-1",
            agentId = "structure",
            type = "Analyzed",
            title = "Reading source",
            message = "Extracting sections, figures, and key terms from the chapter.",
            timestamp = "00:01",
            payload = """{"source_ref":"Living Systems - Ch.4","style":"Visual Story","level":"Intermediate"}""",
        ),
        TraceEventUiModel(
            id = "event-2",
            agentId = "structure",
            type = "Generated",
            title = "Chapter map ready",
            message = "6 core concepts, 3 sub-topics, 11 key terms.",
            timestamp = "00:03",
            payload = """{"sections":["light reactions","Calvin cycle"],"ideas":["chlorophyll","ATP","NADPH"]}""",
        ),
        TraceEventUiModel(
            id = "event-3",
            agentId = "brainstorm",
            type = "Brainstormed",
            title = "Format candidates",
            message = "Visual Story, Lecture, Map-first, Quiz-first, Case.",
            timestamp = "00:05",
            payload = """{"candidates":[{"variant_id":"visual","score":0.91},{"variant_id":"case","score":0.78}]}""",
        ),
        TraceEventUiModel(
            id = "event-4",
            agentId = "brainstorm",
            type = "Selected",
            title = "Selected Visual Story",
            message = "Visual Story leads on clarity and engagement. Handing blueprint to Visual Builder.",
            timestamp = "00:07",
        ),
        TraceEventUiModel(
            id = "event-5",
            agentId = "visual",
            type = "Generated",
            title = "Building scenes",
            message = "Composing interactive scenes with diagrams.",
            timestamp = "00:09",
        ),
        TraceEventUiModel(
            id = "event-6",
            agentId = "visual",
            type = "Generated",
            title = "8 scenes built",
            message = "Hook, chapter map, four concepts, quiz, recap.",
            timestamp = "00:12",
            payload = """{"site_size_kb":86,"external_scripts":false,"mobile_ok":true}""",
        ),
        TraceEventUiModel(
            id = "event-7",
            agentId = "verifier",
            type = "Verified",
            title = "Faithfulness and safety",
            message = "Cross-checked every claim against the source chapter.",
            timestamp = "00:15",
            payload = """{"faithfulness":0.96,"safety":"PASS"}""",
        ),
        TraceEventUiModel(
            id = "event-8",
            agentId = "visual",
            type = "Published",
            title = "Experience published",
            message = "Interactive chapter is live and shareable.",
            timestamp = "00:17",
        ),
    )

    fun snapshotFor(
        revealedEventCount: Int,
        elapsedSeconds: Int,
        settings: GenerationSettingsDraft,
    ): GenerationSnapshot {
        val visibleEvents = traceEvents.take(revealedEventCount)
        val isComplete = revealedEventCount >= traceEvents.size
        val activeAgentId = when {
            isComplete -> null
            revealedEventCount <= 2 -> "structure"
            revealedEventCount <= 4 -> "brainstorm"
            revealedEventCount <= 6 -> "visual"
            else -> "verifier"
        }

        val completedAgents = when {
            isComplete -> agents.map { it.id }.toSet()
            revealedEventCount <= 2 -> emptySet()
            revealedEventCount <= 4 -> setOf("structure")
            revealedEventCount <= 6 -> setOf("structure", "brainstorm")
            else -> setOf("structure", "brainstorm", "visual")
        }

        val statuses = agents.associate { agent ->
            val status = when {
                agent.id in completedAgents -> AgentStatus.Completed
                agent.id == activeAgentId -> AgentStatus.Active
                else -> AgentStatus.Waiting
            }
            agent.id to status
        }

        return GenerationSnapshot(
            jobId = "job-px-4471",
            experienceId = if (isComplete) "experience-photosynthesis" else null,
            statuses = statuses,
            activeAgentId = activeAgentId,
            events = visibleEvents,
            progress = if (isComplete) 100 else (revealedEventCount * 100 / traceEvents.size).coerceAtLeast(6),
            elapsedSeconds = elapsedSeconds,
            isComplete = isComplete,
            publicUrl = if (isComplete) sampleUrl else null,
        )
    }

    fun completedSnapshot(settings: GenerationSettingsDraft): GenerationSnapshot =
        snapshotFor(
            revealedEventCount = traceEvents.size,
            elapsedSeconds = 22,
            settings = settings,
        )
}
