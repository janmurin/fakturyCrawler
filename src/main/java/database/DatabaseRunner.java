package database;

import org.hsqldb.Server;

public class DatabaseRunner {

    public static void execute() {
        Server server = new Server();
        server.setDatabaseName(0, Database.DB_NAME);
        server.setDatabasePath(0, "db/" + Database.DB_NAME);
        server.setPort(Database.PORT);
        
        server.start();

    }

    public static void main(String[] args) {
        DatabaseRunner.execute();
    }
}
