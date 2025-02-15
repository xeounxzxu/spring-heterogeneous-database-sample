package io.xeounxzxu.springheterogeneousdatabasesample.web

import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory.UserEntity
import io.xeounxzxu.springheterogeneousdatabasesample.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController
class UserController(
    private val userService: UserService
) {

    @GetMapping("/users")
    fun getUsers(): List<UserEntity> {
        return userService.getAll()
    }
}
