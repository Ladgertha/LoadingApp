package ru.ladgertha.loadingapplication

import android.os.FileObserver
import java.io.File

class DownloadsObserver(
    path: String?,
    private val loadingButton: LoadingButton,
    private val totalFileLength: Int
) : FileObserver(path, flags) {

    private var progress = 0

    override fun onEvent(event: Int, path: String?) {
        if (path == null) {
            return
        }
        when (event) {
            CLOSE_WRITE -> {
            }
            OPEN -> {
            }
            DELETE, MOVED_FROM -> {
            }
            CREATE,
            MODIFY -> {
                progress = (File(path).length() * 100 / totalFileLength).toInt()
                loadingButton.setProgress(progress)
            }
        }
    }

    companion object {
        private const val flags = (CLOSE_WRITE
                or OPEN
                or MODIFY
                or DELETE
                or MOVED_FROM)
    }
}