package com.example.evprogect

data class User(var id : String = "", var name : String = "", var login : String = "", var password : String = "", val role : String = "user", val darkModeEnabled: Boolean = false) {
}