package com.mmahmood.remotehotkey

class AppConstant {
    companion object {
        // Note,    Byte 1 Format [Ctrl, Shift, Alt, Win | 0020]
        //          Byte 2 Format = Windows VirtualKey Code - See: Windows.System.VirtualKey\
        val DefaultTile = HotkeyData("", "", byteArrayOf(0x00, 0x00))

        val DefaultTileSet = arrayOf(
            HotkeyData("TODO", "todo",                  byteArrayOf(0xE2.toByte(), 0x39.toByte())),
            HotkeyData("Excel Daily", "excel",          byteArrayOf(0xE2.toByte(), 0x30.toByte())),
            HotkeyData("Powerpoint", "ppoint",          byteArrayOf(0xC2.toByte(), 0x50.toByte())),
            HotkeyData("SyncTrayzor", "synctrayzor",    byteArrayOf(0xA2.toByte(), 0x53.toByte())),
            HotkeyData("Dropbox", "dbox",               byteArrayOf(0xA2.toByte(), 0x44.toByte())),
            HotkeyData("Sublime Merge", "smerge",       byteArrayOf(0x32.toByte(), 0x53.toByte())),
            HotkeyData("GitHub", "ghubdesk",            byteArrayOf(0xE2.toByte(), 0x47.toByte())),
            HotkeyData("Altium", "altium",              byteArrayOf(0xC2.toByte(), 0x41.toByte())),
            HotkeyData("Embedded", "embedded",          byteArrayOf(0xA2.toByte(), 0x45.toByte())),
            HotkeyData("Android", "android",            byteArrayOf(0xA2.toByte(), 0x41.toByte())),
            HotkeyData("IntelliJ IDEA", "iidea",        byteArrayOf(0xE2.toByte(), 0x46.toByte())),
            HotkeyData("Clion", "clion",                byteArrayOf(0xA2.toByte(), 0x43.toByte())),
            HotkeyData("Pycharm", "pycharm",            byteArrayOf(0xA2.toByte(), 0x50.toByte())),
            HotkeyData("Visual Studio", "vstudio",      byteArrayOf(0xA2.toByte(), 0x56.toByte())),
            HotkeyData("MATLAB", "matlab",              byteArrayOf(0xA2.toByte(), 0x4D.toByte())),
            HotkeyData("Acrobat", "acrobat",            byteArrayOf(0x32.toByte(), 0x41.toByte())),
            HotkeyData("Jlink RTT", "jlink",            byteArrayOf(0xC2.toByte(), 0x56.toByte())),
            HotkeyData("OpenBCI", "openbci",            byteArrayOf(0x72.toByte(), 0x4F.toByte())),
            HotkeyData("Sublime", "subl",               byteArrayOf(0xA2.toByte(), 0x6B.toByte())),
            HotkeyData("Android Projects", "android",   byteArrayOf(0xE2.toByte(), 0x4E.toByte())),
            HotkeyData("Flutter Projects", "flutter",   byteArrayOf(0xA2.toByte(), 0x46.toByte())),
            HotkeyData("Windows Projects", "w11",       byteArrayOf(0xE2.toByte(), 0x32.toByte())),
            HotkeyData("Python Projects", "python",     byteArrayOf(0xE2.toByte(), 0x31.toByte())),
            HotkeyData("Linux Projects", "linux",       byteArrayOf(0xE2.toByte(), 0x4C.toByte())),
            HotkeyData("nRFConnect", "nrfconn",         byteArrayOf(0xE2.toByte(), 0x4A.toByte())),
            HotkeyData("VS Code", "vscode",             byteArrayOf(0xE2.toByte(), 0x4F.toByte())),
            HotkeyData("Prusa Slicer", "prusa",         byteArrayOf(0xE2.toByte(), 0x53)),
            HotkeyData("Signal", "signal",              byteArrayOf(0xA2.toByte(), 0x60.toByte())),
            HotkeyData("Pushbullet", "pushbullet",      byteArrayOf(0xE2.toByte(), 0x50.toByte())),
            HotkeyData("1password", "pass2",            byteArrayOf(0xE2.toByte(), 0x20.toByte())),
            HotkeyData("TeamViewer", "teamv",           byteArrayOf(0xA2.toByte(), 0x54.toByte())),
            HotkeyData("Google", "gchrome",             byteArrayOf(0xE2.toByte(), 0x4D.toByte())),
            HotkeyData("Ungoogled", "chromium",         byteArrayOf(0xA2.toByte(), 0x55.toByte())),
            HotkeyData("VMware", "vmware",              byteArrayOf(0xE2.toByte(), 0x56.toByte())),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00.toByte(), 0x00.toByte())),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("Task Manager", "taskman",       byteArrayOf(0xC2.toByte(), 0x1B.toByte())),
            HotkeyData("Afterburner", "msiab",          byteArrayOf(0xA2.toByte(), 0x37)),
            HotkeyData("AppWiz", "appwiz",              byteArrayOf(0xA2.toByte(), 0x36)),
            HotkeyData("New Desktop", "newdesktop",     byteArrayOf(0x92.toByte(), 0x44)),
            HotkeyData("Tiling Zones", "powertoys",     byteArrayOf(0x52.toByte(), 0x46.toByte())),
            HotkeyData("Pin Window", "pushpin",         byteArrayOf(0x92.toByte(), 0x54.toByte())),
            HotkeyData("cmd", "cmd",                    byteArrayOf(0xE2.toByte(), 0x43.toByte())),
            HotkeyData("Calculator", "calc",            byteArrayOf(0x82.toByte(), 0x60.toByte())),
        )

        val TextTileSet = arrayOf( // 48 Total
            HotkeyData("Sublime", "subl",       byteArrayOf(0xA2.toByte(), 0x6B.toByte())),
            HotkeyData("Close window", "xout",  byteArrayOf(0x82.toByte(), 0x57.toByte())),
            HotkeyData("Delete Line", "bckspc", byteArrayOf(0xC2.toByte(), 0x4B.toByte())),
//            HotkeyData("Select Line", "select_line", byteArrayOf(0x82.toByte(), 0x76.toByte())),
            HotkeyData("Indent", "right_indent", byteArrayOf(0x82.toByte(), 0xDD.toByte())),
            HotkeyData("Unindent", "left_indent", byteArrayOf(0x82.toByte(), 0xDB.toByte())),
//            HotkeyData("Find& Replace", "find_replace", byteArrayOf(0x82.toByte(), 0x72.toByte()))
        )

        val AltiumTileSet = arrayOf(
            HotkeyData("Altium", "altium", byteArrayOf(0xC2.toByte(), 0x41.toByte())),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
            HotkeyData("", "", byteArrayOf(0x00, 0x00)),
        )
    }
}