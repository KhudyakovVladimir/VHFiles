package com.khudyakovvladimir.vhfileexplorer.model

import java.io.File
import java.util.*

object Model {
    var ROOT_PATH = "/storage/emulated/0"
    var sdCardIsAvailable = false
    var stack: Stack<String> = Stack()
    var map = mutableMapOf<File, Boolean>()
    var listOfChecked = mutableListOf<String>()
    var listForCopyTo = mutableListOf<String>()
}