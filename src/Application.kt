package com.nickagas

import com.nickagas.auth.JwtService
import com.nickagas.auth.MySession
import com.nickagas.auth.hash
import com.nickagas.repository.DatabaseFactory
import com.nickagas.repository.TodoRepository
import com.nickagas.routes.todos
import com.nickagas.routes.users
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    // 1
    DatabaseFactory.init()
    val db = TodoRepository()
// 2
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }


    install(Authentication) {
        jwt("jwt") { //1
            verifier(jwtService.verifier) // 2
            realm = "Todo Server"
            validate { // 3
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = db.findUser(claimString) // 4
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        users(db, jwtService, hashFunction)
        todos(db)

//        get("/") {
//            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
//        }
//
//        get<MyLocation> {
//            call.respondText("Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}")
//        }
//        // Register nested routes
//        get<Type.Edit> {
//            call.respondText("Inside $it")
//        }
//        get<Type.List> {
//            call.respondText("Inside $it")
//        }
//
//        get("/session/increment") {
//            val session = call.sessions.get<MySession>() ?: MySession()
//            call.sessions.set(session.copy(count = session.count + 1))
//            call.respondText("Counter is ${session.count}. Refresh to increment.")
//        }
//
//        get("/json/gson") {
//            call.respond(mapOf("hello" to "world"))
//        }
    }
}
const val API_VERSION = "/v1"

//@Location("/location/{name}")
//class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

//@Location("/type/{name}") data class Type(val name: String) {
//    @Location("/edit")
//    data class Edit(val type: Type)
//
//    @Location("/list/{page}")
//    data class List(val type: Type, val page: Int)
//}

//data class MySession(val count: Int = 0)

