package repository

import play.api.db.Database
import requests.{CreateUserRequest, UpdateUserRequest}
import response.UserResponse

import java.util.UUID
import javax.inject.*
import scala.collection.mutable.ListBuffer

@Singleton
class UserRepository @Inject()(db: Database) {
  def findAll(page: Int, limit: Int, search: Option[String]): (List[UserResponse], Int) = {
    db.withConnection { conn =>

      val offset = (page - 1) * limit

      val baseQuery = new StringBuilder("SELECT * FROM users WHERE 1=1")
      val countQuery = new StringBuilder("SELECT COUNT(*) as total FROM users WHERE 1=1")

      search.foreach { s =>
        baseQuery.append(" AND nama LIKE ?")
        countQuery.append(" AND nama LIKE ?")
      }

      baseQuery.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?")

      val stmt = conn.prepareStatement(baseQuery.toString())
      val countStmt = conn.prepareStatement(countQuery.toString())

      var index = 1

      search.foreach { s =>
        stmt.setString(index, s"%$s%")
        countStmt.setString(index, s"%$s%")
        index += 1
      }

      stmt.setInt(index, limit)
      stmt.setInt(index + 1, offset)

      val rs = stmt.executeQuery()
      val countRs = countStmt.executeQuery()

      val result = ListBuffer[UserResponse]()

      while (rs.next()) {
        result += UserResponse(
          id = rs.getString("id"),
          nama = rs.getString("nama"),
          no_telepon = Option(rs.getString("no_telepon")),
          alamat = Option(rs.getString("alamat")),
          created_at = Option(rs.getString("created_at"))
        )
      }

      val total = if (countRs.next()) countRs.getInt("total") else 0

      (result.toList, total)
    }
  }

  def create(req: CreateUserRequest): Int = {
    db.withConnection { conn =>

      val sql =
        """
            INSERT INTO users (id, nama, no_telepon, alamat, created_at)
            VALUES (?, ?, ?, ?, NOW())
          """

      val stmt = conn.prepareStatement(sql)

      stmt.setString(1, UUID.randomUUID().toString)
      stmt.setString(2, req.nama)
      stmt.setString(3, req.no_telepon.orNull)
      stmt.setString(4, req.alamat.orNull)

      stmt.executeUpdate() // return jumlah row (Int)
    }
  }

  def findById(id: String): Option[UserResponse] = {
    db.withConnection { conn =>

      val sql = "SELECT * FROM users WHERE id = ?"
      val stmt = conn.prepareStatement(sql)
      stmt.setString(1, id)

      val rs = stmt.executeQuery()

      if (rs.next()) {
        Some(
          UserResponse(
            id = rs.getString("id"),
            nama = rs.getString("nama"),
            no_telepon = Option(rs.getString("no_telepon")),
            alamat = Option(rs.getString("alamat")),
            created_at = Option(rs.getString("created_at"))
          )
        )
      } else {
        None
      }
    }
  }

  def update(id: String, req: UpdateUserRequest): Int = {
    db.withConnection { conn =>

      val sql =
        """
          UPDATE users
          SET
            nama = COALESCE(?, nama),
            no_telepon = COALESCE(?, no_telepon),
            alamat = COALESCE(?, alamat)
          WHERE id = ?
        """

      val stmt = conn.prepareStatement(sql)

      stmt.setString(1, req.nama.orNull)
      stmt.setString(2, req.no_telepon.orNull)
      stmt.setString(3, req.alamat.orNull)
      stmt.setString(4, id)

      stmt.executeUpdate()
    }
  }

  def delete(id: String): Int = {
    db.withConnection { conn =>

      val sql = "DELETE FROM users WHERE id = ?"
      val stmt = conn.prepareStatement(sql)

      stmt.setString(1, id)

      stmt.executeUpdate() // return jumlah row yang terhapus
    }
  }
}