package com.hotaro.mizan.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Parses inline markdown: **bold**, *italic*, `code`, and [links](url).
 */
fun parseInlineMarkdown(text: String, primaryColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            when {
                // Bold: **text**
                text.startsWith("**", i) -> {
                    val end = text.indexOf("**", i + 2)
                    if (end != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(text.substring(i + 2, end))
                        }
                        i = end + 2
                    } else {
                        append(text[i])
                        i++
                    }
                }
                // Inline code: `code`
                text.startsWith("`", i) -> {
                    val end = text.indexOf("`", i + 1)
                    if (end != -1) {
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = primaryColor.copy(alpha = 0.12f),
                                fontSize = 13.sp
                            )
                        ) {
                            append(text.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        append(text[i])
                        i++
                    }
                }
                // Italic: *text* (single asterisk)
                text.startsWith("*", i) && (i + 1 < text.length && text[i + 1] != '*') -> {
                    val end = text.indexOf("*", i + 1)
                    if (end != -1) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(text.substring(i + 1, end))
                        }
                        i = end + 1
                    } else {
                        append(text[i])
                        i++
                    }
                }
                // Link: [text](url)
                text.startsWith("[", i) -> {
                    val closeBracket = text.indexOf("]", i + 1)
                    val openParen = if (closeBracket != -1) text.indexOf("(", closeBracket) else -1
                    val closeParen = if (openParen == closeBracket + 1) text.indexOf(")", openParen) else -1
                    if (closeBracket != -1 && openParen == closeBracket + 1 && closeParen != -1) {
                        val linkText = text.substring(i + 1, closeBracket)
                        withStyle(SpanStyle(color = primaryColor, fontWeight = FontWeight.SemiBold)) {
                            append(linkText)
                        }
                        i = closeParen + 1
                    } else {
                        append(text[i])
                        i++
                    }
                }
                else -> {
                    append(text[i])
                    i++
                }
            }
        }
    }
}

/**
 * Renders full markdown text supporting headings (# to ####), dividers (---),
 * bullet lists, inline code blocks, bold, and italics.
 */
@Composable
fun MarkdownContent(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    val lines = remember(markdown) { markdown.lines() }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        var inCodeBlock = false
        val codeBlockLines = mutableListOf<String>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("```")) {
                if (inCodeBlock) {
                    val codeContent = codeBlockLines.joinToString("\n")
                    codeBlockLines.clear()
                    inCodeBlock = false
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = codeContent,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    inCodeBlock = true
                }
                continue
            }

            if (inCodeBlock) {
                codeBlockLines.add(line)
                continue
            }

            when {
                trimmed.isEmpty() -> {
                    Spacer(Modifier.height(2.dp))
                }
                trimmed == "---" || trimmed == "***" || trimmed == "___" -> {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 6.dp),
                        color = outlineVariantColor.copy(alpha = 0.5f)
                    )
                }
                trimmed.startsWith("#### ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("#### ").trim(), primaryColor),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("### ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("### ").trim(), primaryColor),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("## ").trim(), primaryColor),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = onSurfaceColor,
                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                    )
                }
                trimmed.startsWith("# ") -> {
                    Text(
                        text = parseInlineMarkdown(trimmed.removePrefix("# ").trim(), primaryColor),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = primaryColor,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("+ ") || trimmed.startsWith("• ") -> {
                    val bulletContent = when {
                        trimmed.startsWith("- ") -> trimmed.removePrefix("- ")
                        trimmed.startsWith("* ") -> trimmed.removePrefix("* ")
                        trimmed.startsWith("+ ") -> trimmed.removePrefix("+ ")
                        else -> trimmed.removePrefix("• ")
                    }.trim()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            fontWeight = FontWeight.Black,
                            color = primaryColor,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = parseInlineMarkdown(bulletContent, primaryColor),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = onSurfaceColor,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
                else -> {
                    Text(
                        text = parseInlineMarkdown(trimmed, primaryColor),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = onSurfaceColor,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }
    }
}
