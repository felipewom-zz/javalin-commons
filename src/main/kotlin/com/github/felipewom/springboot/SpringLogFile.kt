package com.github.felipewom.springboot

import io.javalin.http.Context
import org.eclipse.jetty.http.HttpStatus
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets

object SpringLogFile {

    @JvmStatic
    fun getLogFile(ctx: Context) {
        val contentRangeHeader = "Content-Range"
        val file = File("/var/log/template-api/server.log")
        val totalBytes = file.length().toInt()
        val contentRange = ctx.req.getHeader("Range")
        var lastSize = contentRange?.let { contentRange.split("bytes=").let { it[1] }.replace("-", "") } ?: "0"
        lastSize = lastSize.replace("NaN", "0")
        val initalRead = if (lastSize.toInt() > totalBytes) {
            0
        } else {
            lastSize.toInt()
        }
        val fileSegment = readFileSegment(file, initalRead.toLong(), file.length().toInt())
        val result = String(fileSegment, StandardCharsets.UTF_8)
        if (contentRange == null) {
            ctx.status(HttpStatus.PARTIAL_CONTENT_206)
                .header(contentRangeHeader, "bytes $totalBytes/$totalBytes")
                .result(result)
        } else {
            ctx.status(HttpStatus.PARTIAL_CONTENT_206)
                .header(contentRangeHeader, "bytes $totalBytes/$totalBytes")
                .json(result)
        }
    }

    @Throws(IOException::class)
    fun readFileSegment(file: File, index: Long, totalSize: Int): ByteArray {
        val raf = RandomAccessFile(file, "r")
        val buffer = ByteArray(totalSize)
        raf.use {
            it.seek(index)
            it.readFully(buffer, index.toInt(), it.length().toInt() - 1)
            return buffer
        }
    }

}