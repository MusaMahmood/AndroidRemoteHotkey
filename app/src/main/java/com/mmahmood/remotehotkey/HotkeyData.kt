package com.mmahmood.remotehotkey

data class HotkeyData(val name: String, val drawablePath: String, val byteArray: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HotkeyData

        if (name != other.name) return false
        if (drawablePath != other.drawablePath) return false
        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + drawablePath.hashCode()
        result = 31 * result + byteArray.contentHashCode()
        return result
    }

}
