package competitions.domain.com.sportcompetitions.Connection;

import competitions.domain.com.sportcompetitions.model.Athlet;
import competitions.domain.com.sportcompetitions.model.Competition;
import competitions.domain.com.sportcompetitions.model.Seat;
import competitions.domain.com.sportcompetitions.model.Team;

import java.util.List;

/**
 * Represents all operations that can be executed to get or set data from storage (lke MySQL database)
 */
public interface Repository {
    List<Team> getTeams();
    Team getTeamById(int id);
    void addTeam(Team team);
    Boolean removeTeam(String nameOfTeam);
    Boolean updateTeam(String nameOfTeamOld, Team team);

    List<Athlet> getAthlet();
    Athlet getAthletById(int id);
    Boolean addAthlet(Athlet athlet);
    Boolean removeAthlet(String athletLastName);
    Boolean updateAthlet(int id, Athlet athlet);

    List<Competition> getCompetition();
    Competition getCompetitionById(int id);
    Boolean addCompetition(Competition competition);
    Boolean removeCompetition(String tournamentName);
    Boolean updateCompetition(int id, Competition competition);

}
