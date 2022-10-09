package auth

import com.typesafe.config.ConfigFactory
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.ZIO

import java.time.Clock
import java.util
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Try

object Auth {
    private val config = ConfigFactory.load()

    private val SECRET_KEY = Try(config.getString("secretKey")).getOrElse("secret")

    val algorithm: JwtAlgorithm = JwtAlgorithm.fromString(
        Try(config.getString("jwtEncryption.algorithm")).getOrElse("HS512")
    )

    implicit val clock: Clock = Clock.systemUTC

    def jwtEncode(username: String): String = {
        val json  = s"""{"user": "$username"}"""
        val claim = JwtClaim {
            json
        }.issuedNow.expiresIn(Int.MaxValue)
        Jwt.encode(claim, SECRET_KEY, JwtAlgorithm.HS512)
    }

    def jwtDecode(token: String): Option[JwtClaim] = {
        Jwt.decode(token, SECRET_KEY, Seq(JwtAlgorithm.HS512)).toOption
    }

}
