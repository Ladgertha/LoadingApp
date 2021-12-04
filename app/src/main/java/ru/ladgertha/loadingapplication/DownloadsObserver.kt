package ru.ladgertha.loadingapplication

import android.os.FileObserver

// TODO Подумать, как иначе и как связать с кнопкой. Убрать FileObserver
class DownloadsObserver(path: String?) : FileObserver(path, flags) {
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
            MODIFY -> {
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