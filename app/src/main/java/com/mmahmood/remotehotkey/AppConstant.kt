package com.mmahmood.remotehotkey

class AppConstant {
    companion object {
        val allData = arrayOf(
            HotkeyData("TODO", "todo", byteArrayOf(0xE2.toByte(), 0x26)), //1-INDEXED (lookup id-1)
            HotkeyData("Excel Daily", "excel", byteArrayOf(0xE2.toByte(), 0x27)),
            HotkeyData("SyncTrayzor", "synctrayzor", byteArrayOf(0xA2.toByte(), 0x16)),
            HotkeyData("Dropbox", "dbox", byteArrayOf(0xA2.toByte(), 0x07)), //4
            HotkeyData("Powerpoint", "ppoint", byteArrayOf(0xC2.toByte(), 0x13)),
            HotkeyData("Calculator", "calc", byteArrayOf(0xC2.toByte(), 0x06)),
            HotkeyData("SourceTree", "stree", byteArrayOf(0xC2.toByte(), 0x16)),
            HotkeyData("GitHub", "ghubdesk", byteArrayOf(0xC2.toByte(), 0x0A)), //8
            HotkeyData("Altium", "altium", byteArrayOf(0xC2.toByte(), 0x04)),
            HotkeyData("Android", "android", byteArrayOf(0xA2.toByte(), 0x04)),
            HotkeyData("Embedded", "embedded", byteArrayOf(0xA2.toByte(), 0x08)),
            HotkeyData("IntelliJ IDEA", "iidea", byteArrayOf(0xC2.toByte(), 0x09)), //12
            HotkeyData("Clion", "clion", byteArrayOf(0xA2.toByte(), 0x06)),
            HotkeyData("Pycharm", "pycharm", byteArrayOf(0xA2.toByte(), 0x13)),
            HotkeyData("MATLAB", "matlab", byteArrayOf(0xA2.toByte(), 0x10)),
            HotkeyData("Visual Studio", "vstudio", byteArrayOf(0xA2.toByte(), 0x19)), //16
            HotkeyData("", "", byteArrayOf(0xA2.toByte(), 0x57.toByte())),
            HotkeyData("", "", byteArrayOf(0xE2.toByte(), 0x11.toByte())),
            HotkeyData("", "", byteArrayOf(0xC2.toByte(), 0x19.toByte())),
            HotkeyData("", "", byteArrayOf(0xA2.toByte(), 0x09.toByte())), //20
            HotkeyData("", "", byteArrayOf(0xE2.toByte(), 0x2D.toByte())),
            HotkeyData("", "", byteArrayOf(0xE2.toByte(), 0x1E.toByte())),
            HotkeyData("", "", byteArrayOf(0xE2.toByte(), 0x2E.toByte())),
            HotkeyData("", "", byteArrayOf(0xA2.toByte(), 0x17.toByte())), //24
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)), //28
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)), //32
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)), //36
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)), //40
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)), //44
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)), //48
        )
    }
}