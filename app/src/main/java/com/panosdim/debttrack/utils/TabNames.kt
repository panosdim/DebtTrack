package com.panosdim.debttrack.utils

interface IGetFirebasePath {
    fun getFirebasePath(): String
}

enum class TabNames(val tabName: String) : IGetFirebasePath {
    THEYOWEME("They Owe Me") {
        override fun getFirebasePath() = "TheyOweMe"
    },
    IOWE("I Owe") {
        override fun getFirebasePath() = "IOwe"
    },
}