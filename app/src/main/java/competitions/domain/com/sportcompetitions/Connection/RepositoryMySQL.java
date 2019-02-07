package competitions.domain.com.sportcompetitions.Connection;

import competitions.domain.com.sportcompetitions.model.Athlet;
import competitions.domain.com.sportcompetitions.model.Competition;
import competitions.domain.com.sportcompetitions.model.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryMySQL implements Repository {
    private static final int PORT_DEFAULT = 3307;
    private final String ip;
    private final int port;
    private final String databaseName;
    private final Connection connection;

    public RepositoryMySQL(String ip, int port, String databaseName, String databaseUser, String databasePassword) throws SQLException{
        this.ip = ip;
        this.port = port;
        this.databaseName = databaseName;
        this.connection = DriverManager.getConnection(getDatabaseUrl(), databaseUser, databasePassword);
    }

    public RepositoryMySQL(String ip, String databaseName, String databaseUser, String databasePassword) throws SQLException{
        this(ip, PORT_DEFAULT, databaseName, databaseUser, databasePassword);
    }

    private String getDatabaseUrl(){
        return String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                this.ip, this.port, this.databaseName);
    }

    @Override
    public List<Team> getTeams(){
        List<Team> teams = new ArrayList<>();
        try {
            ResultSet resultSet = executeQuery("Select teams.teams_id,teams.name_of_team,teams.coach,athletes.athlet_last_name From teams,athletes \n" +
                    "Where teams.captain = athletes.athletes_id;");
            while (resultSet.next()) {
                teams.add(readTeam(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }

    private Team readTeam(ResultSet resultSet) throws SQLException{
        return new Team(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4));
    }

    @Override
    public Team getTeamById(int id){
        Team team = null;
        try {
            String query = String.format(
                    "Select teams.teams_id,teams.name_of_team,teams.coach,athletes.athlet_last_name " +
                            "From teams,athletes " +
                            "Where teams.captain = athletes.athletes_id AND teams.teams_id = '%d';", id);
            ResultSet resultSet = executeQuery(query);
            while (resultSet.next()) {
                team = readTeam(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return team;
    }

    @Override
    public void addTeam(Team team){
        try {
            String query = String.format(
                    "INSERT INTO `teams` " +
                            "(teams_id, `name_of_team`, `coach`) " +
                            "VALUES ('%d', '%s', '%s');",
                    team.getTeams_id(), team.getName_of_team(), team.getCoach());
            executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Boolean removeTeam(String nameOfTeam){
        return null;
    }

    @Override
    public Boolean updateTeam(String nameOfTeamOld, Team team){
        return null;
    }

    @Override
    public List<Athlet> getAthlet(){
        return null;
    }

    @Override
    public Athlet getAthletById(int id){
        return null;
    }

    @Override
    public Boolean addAthlet(Athlet athlet){
        return null;
    }

    @Override
    public Boolean removeAthlet(String athletLastName){
        return null;
    }

    @Override
    public Boolean updateAthlet(int id, Athlet athlet){
        return null;
    }

    @Override
    public List<Competition> getCompetition(){
        return null;
    }

    @Override
    public Competition getCompetitionById(int id){
        return null;
    }

    @Override
    public Boolean addCompetition(Competition competition){
        return null;
    }

    @Override
    public Boolean removeCompetition(String tournamentName){
        return null;
    }

    @Override
    public Boolean updateCompetition(int id, Competition competition){
        return null;
    }

    private ResultSet executeQuery(String query) throws SQLException{
        //Statement statement = connection.createStatement();
        return connection.createStatement().executeQuery(query);
    }

    private void executeUpdate(String update) throws SQLException{
        Statement statement = connection.createStatement();
        statement.executeUpdate(update);
    }
}
