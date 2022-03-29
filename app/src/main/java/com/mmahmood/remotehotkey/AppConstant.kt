package com.mmahmood.remotehotkey

class AppConstant {
    companion object {
        // Note,    Byte 1 Format [Ctrl, Shift, Alt, Win | 0020]
        //          Byte 2 Format = Windows VirtualKey Code - See: Windows.System.VirtualKey
        val allData = arrayOf(
            HotkeyData("TODO", "todo",                  byteArrayOf(0xE2.toByte(), 0x39.toByte())), //1-INDEXED (lookup id-1)
            HotkeyData("Excel Daily", "excel",          byteArrayOf(0xE2.toByte(), 0x30.toByte())),
            HotkeyData("SyncTrayzor", "synctrayzor",    byteArrayOf(0xA2.toByte(), 0x53.toByte())),
            HotkeyData("Dropbox", "dbox",               byteArrayOf(0xA2.toByte(), 0x44.toByte())), //4
            HotkeyData("Powerpoint", "ppoint",          byteArrayOf(0xC2.toByte(), 0x50.toByte())),
            HotkeyData("Calculator", "calc",            byteArrayOf(0xC2.toByte(), 0x43.toByte())),
            HotkeyData("SourceTree", "stree",           byteArrayOf(0xC2.toByte(), 0x53.toByte())),
            HotkeyData("GitHub", "ghubdesk",            byteArrayOf(0xC2.toByte(), 0x47.toByte())), //8
            HotkeyData("Altium", "altium",              byteArrayOf(0xC2.toByte(), 0x41.toByte())),
            HotkeyData("Android", "android",            byteArrayOf(0xA2.toByte(), 0x41.toByte())),
            HotkeyData("Embedded", "embedded",          byteArrayOf(0xA2.toByte(), 0x45.toByte())),
            HotkeyData("IntelliJ IDEA", "iidea",        byteArrayOf(0xC2.toByte(), 0x46.toByte())), //12
            HotkeyData("Clion", "clion",                byteArrayOf(0xA2.toByte(), 0x43.toByte())),
            HotkeyData("Pycharm", "pycharm",            byteArrayOf(0xA2.toByte(), 0x50.toByte())),
            HotkeyData("MATLAB", "matlab",              byteArrayOf(0xA2.toByte(), 0x4D.toByte())),
            HotkeyData("Visual Studio", "vstudio",      byteArrayOf(0xA2.toByte(), 0x56.toByte())), //16
            HotkeyData("Sublime", "subl",               byteArrayOf(0xA2.toByte(), 0x6B.toByte())),
            HotkeyData("Android Projects", "android",   byteArrayOf(0xE2.toByte(), 0x4E.toByte())),
            HotkeyData("Jlink RTT", "jlink",            byteArrayOf(0xC2.toByte(), 0x56.toByte())),
            HotkeyData("Flutter Projects", "flutter",   byteArrayOf(0xA2.toByte(), 0x46.toByte())), //20
            HotkeyData("Windows Projects", "w11",       byteArrayOf(0xE2.toByte(), 0x32.toByte())),
            HotkeyData("Python Projects", "python",     byteArrayOf(0xE2.toByte(), 0x31.toByte())),
            HotkeyData("Linux Projects", "linux",       byteArrayOf(0xE2.toByte(), 0x4C.toByte())),
            HotkeyData("TeamViewer", "teamv",           byteArrayOf(0xA2.toByte(), 0x54.toByte())), //24
            HotkeyData("VMware", "vmware", byteArrayOf(0xE2.toByte(), 0x56.toByte())),
            HotkeyData("Pushbullet", "pushbullet", byteArrayOf(0xE2.toByte(), 0x50.toByte())),
            HotkeyData("Tiling Zones", "powertoys", byteArrayOf(0x52.toByte(), 0x46.toByte())),
            HotkeyData("Pin Window", "pushpin", byteArrayOf(0x92.toByte(), 0x54.toByte())), //28
            HotkeyData("cmd", "cmd", byteArrayOf(0xE2.toByte(), 0x43.toByte())),
            HotkeyData("Google", "gchrome", byteArrayOf(0xE2.toByte(), 0x4D.toByte())),
            HotkeyData("Ungoogled", "chromium", byteArrayOf(0xA2.toByte(), 0x55.toByte())),
            HotkeyData("Signal", "signal", byteArrayOf(0xA2.toByte(), 0x60.toByte())), //32
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