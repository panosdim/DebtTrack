package com.panosdim.debttrack.utils

interface IGetFirebasePath {
    fun getFirebasePath(): String
}

enum class TabNames(val tabName: String) : IGetFirebasePath {
    THEY_OWE_ME("They Owe Me") {
        override fun getFirebasePath() = "TheyOweMe"
    },
    I_OWE("I Owe") {
        override fun getFirebasePath() = "IOwe"
    },
}