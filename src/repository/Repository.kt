package com.nickagas.repository

import com.nickagas.models.Todo
import com.nickagas.models.User


interface Repository {
    suspend fun addUser(email: String,
                        displayName: String,
                        passwordHash: String): User?
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?

    suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo?
    suspend fun getTodos(userId: Int): List<Todo>

}