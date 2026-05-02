import database.MigrationRunner

object Main {
  def main(args: Array[String]): Unit = {

    if (args == null || args.isEmpty) {
      println("Command: migrate | fresh | seed")
    } else {
      args(0) match {
        case "migrate" => MigrationRunner.migrate()
        case "fresh" => MigrationRunner.fresh()
        case "seed" => MigrationRunner.seed()
        case _ => println("Unknown command")
      }
    }

  }
}