package competitions.domain.com.sportcompetitions.Connection;

public enum Commands {
    GET_SEATS,GET_ONE_SEAT,ADD_SEAT,UPDATE_SEAT,DELETE_SEAT,
    GET_COMPETITIONS,GET_ONE_COMPETITION,ADD_COMPETITION,UPDATE_COMPETITION,DELETE_COMPETITION,
    GET_ATHLETES,GET_ONE_ATHLET,ADD_ATHLET,UPDATE_ATHLET,DELETE_ATHLET,
    GET_TEAMS,GET_ONE_TEAM,ADD_TEAM,UPDATE_TEAM,DELETE_TEAM;

    @Override
    public String toString() {
        return this.name().toUpperCase();
    }
}
