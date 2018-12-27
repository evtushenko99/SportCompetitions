package competitions.domain.com.sportcompetitions.model;

public enum  Message {
    GET_ATHLETES("getAthletes:"),GET_SEATS("getSeats:"),GET_TEAMS("getTeams:"),GET_COMPETITION("getCompetitions:"),
    GET_ONE_COMPETITION("getOneCompetition:"),UPDATE_COMPETITION("updateCompetition:"),
    SET_COMPETITION("setCompetition:"),
    DELETE_COMPETITION("removeCompetition:"),UPDATE_ATHLET("updateAthletes:"),
    DELETE_ATHLET("removeAthletes:"),SET_ATHLET("setAthletes:"),
    DELETE_TEAM("removeTeams:"),SET_TEAM("setTeams:"),UPDATE_TEAM("updateTeams:"),
    GET_ONE_ATHLET("getOneAthlet:"),
    GET_ONE_TEAM("getOneTeam:");
    private String mCommand;


    public String getCommand() {
        return mCommand;
    }

    Message(String command){
        mCommand = command;

    }

    @Override
    public String toString() {
        return getCommand();
    }
}
