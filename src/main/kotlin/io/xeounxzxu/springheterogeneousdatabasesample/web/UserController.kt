package io.xeounxzxu.springheterogeneousdatabasesample.web

import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory.UserEntity
import io.xeounxzxu.springheterogeneousdatabasesample.infra.mysql.repsitory.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController
class UserController(private val userRepository: UserRepository) {

    @GetMapping("/users")
    fun getUsers(): List<UserEntity> {
        return userRepository.findAll()
    }
}
