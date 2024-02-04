import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqLite implements AutoCloseable {
    private final Connection _dbConnection;

    public SqLite() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        _dbConnection = DriverManager.getConnection("jdbc:sqlite:db.sqlite");
        try {
            ExecuteWithoutResult("select ID from Department");
        } catch (Exception ex) {
            CreatingTables();
        }
    }

    private void CreatingTables(){
        try {
            ExecuteWithoutResult("CREATE TABLE IF NOT EXISTS Department ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'Title_department' text);");
            ExecuteWithoutResult("CREATE TABLE IF NOT EXISTS  Manufacturer ('ID' INTEGER PRIMARY KEY AUTOINCREMENT, 'Title_manufacturer' text);");
            ExecuteWithoutResult("CREATE TABLE IF NOT EXISTS  Nomenclature ('ID' INTEGER  PRIMARY KEY AUTOINCREMENT , 'Title' text, 'Price' INTEGER, 'department_Id' INTEGER, 'manufacturer_Id' INTEGER , FOREIGN KEY (department_Id)  REFERENCES department(ID),FOREIGN KEY (manufacturer_Id)  REFERENCES Manufacturer(ID));");

        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void ExecuteWithoutResult(String sqlQuery) throws SQLException {
        var statement = _dbConnection.createStatement();
        statement.execute(sqlQuery);
    }

    public void WriteDBCalculations(int first, int sing, int second, String result) throws SQLException {

        String sqlQuery = "INSERT INTO 'Calculations' ('first', 'second' , 'sign', 'Result') VALUES (" + first + "," +
                second + ", " + sing + ", '" + result + "'); ";
        ExecuteWithoutResult(sqlQuery);
    }

    public void WriteDBComments(int ID_Calculations, String textComment) throws SQLException {

        String sqlQuery = "INSERT INTO 'Comments' ('ID_Calculations', 'comment') VALUES (" + ID_Calculations +
                ", '" + textComment + "'); ";
        ExecuteWithoutResult(sqlQuery);
    }

    public void WriteDBNomenclature(String Title, int Price, int department_Id, int Manufacturer_Id) throws SQLException {
        String sqlQuery = "INSERT INTO 'Nomenclature' ('Title', 'Price','department_Id', 'Manufacturer_Id') "
                +"VALUES ('" + Title + "', " + Price + ", " + department_Id + ", " + Manufacturer_Id + "); ";
        ExecuteWithoutResult(sqlQuery);
    }

    public void WriteDB(int kodQuery, String Title) throws SQLException {
        String sqlQuery = switch (kodQuery){
            case 0 -> "INSERT INTO 'Department' ('Title_department') VALUES ('" + Title + "'); ";
            case 1 -> "INSERT INTO 'Manufacturer' ('Title_manufacturer') VALUES ('" + Title + "'); ";
            default -> "";
        };
        ExecuteWithoutResult(sqlQuery);
    }

    private String getSqlQuery(int kodQuery) {
        return switch (kodQuery) {
            case 0 -> "select ID, Title_department  as Title from Department";
            case 1 -> "select ID, Title_manufacturer  as Title from Manufacturer";
            default -> "";
        };
    }

    public ResultSet ReadDB(int ID, int KodQuery) throws SQLException {
        var statement = _dbConnection.createStatement();
        return statement.executeQuery(getSqlQuery(KodQuery));
    }

    public ResultSet ReadDBNomenclature() throws SQLException {
        var statement = _dbConnection.createStatement();
        return statement.executeQuery("select Title, Price, Title_department, Title_manufacturer "+
                "from Nomenclature JOIN Manufacturer ON Nomenclature.manufacturer_Id = Manufacturer.ID " +
                "JOIN  Department ON Nomenclature.department_Id = Department.ID;");
    }

    public ResultSet ReadDB(int KodQuery) throws SQLException {
        var statement = _dbConnection.createStatement();
        return statement.executeQuery(getSqlQuery(KodQuery));
    }

    @Override
    public void close() throws Exception {
        _dbConnection.close();
    }
}