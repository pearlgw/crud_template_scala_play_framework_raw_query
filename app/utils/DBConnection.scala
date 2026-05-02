package utils

import com.typesafe.config.ConfigFactory
import java.sql.{Connection, DriverManager}

object DBConnection {

  private val config = ConfigFactory.load()

  private val url = config.getString("db.default.url")
  private val user = config.getString("db.default.username")
  private val password = config.getString("db.default.password")

  def getConnection(): Connection = {
    DriverManager.getConnection(url, user, password)
  }
}