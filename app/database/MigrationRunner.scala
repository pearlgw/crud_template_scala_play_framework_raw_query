package database

import utils.DBConnection

import java.io.File
import scala.io.Source

object MigrationRunner {

  val migrationPath = "app/database/migrations"

  def fresh(): Unit = {
    val conn = DBConnection.getConnection()
    val rs = conn.createStatement().executeQuery("SHOW TABLES")
    val tables = scala.collection.mutable.ListBuffer[String]()

    while (rs.next()) {
      tables += rs.getString(1)
    }

    conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0")

    tables.foreach { table =>
      conn.createStatement().execute(s"DROP TABLE IF EXISTS $table")
    }

    conn.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1")

    conn.close()
    migrate()
  }

  def migrate(): Unit = {
    val conn = DBConnection.getConnection()

    // pastikan tabel migrations ada
    conn.createStatement().execute(
      """CREATE TABLE IF NOT EXISTS migrations (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(255),
        executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      )"""
    )

    val executed = collection.mutable.Set[String]()

    val rs = conn.createStatement().executeQuery("SELECT name FROM migrations")
    while (rs.next()) {
      executed.add(rs.getString("name"))
    }

    val files = new File(migrationPath)
      .listFiles()
      .filter(_.getName.endsWith(".sql"))
      .sortBy(_.getName)

    files.foreach { file =>
      if (!executed.contains(file.getName)) {
        println(s"Running migration: ${file.getName}")

        val sql = Source.fromFile(file).mkString
        conn.createStatement().execute(sql)

        val stmt = conn.prepareStatement("INSERT INTO migrations(name) VALUES (?)")
        stmt.setString(1, file.getName)
        stmt.execute()
      }
    }

    conn.close()
  }

  def seed(): Unit = {
    val conn = DBConnection.getConnection()

    val files = new File("app/database/seeds")
      .listFiles()
      .filter(_.getName.endsWith(".sql"))

    files.foreach { file =>
      println(s"Seeding: ${file.getName}")

      val sql = scala.io.Source.fromFile(file).mkString

      val queries = sql
        .split(";") // 🔥 pecah berdasarkan ;
        .map(_.trim)
        .filter(_.nonEmpty) // buang kosong

      queries.foreach { query =>
        println(s"Executing: $query") // optional debug
        conn.createStatement().execute(query)
      }
    }

    conn.close()
  }
}