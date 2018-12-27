package competitions.domain.com.sportcompetitions.model;

public class Athletes {
    private int athlets_id;
    private String athlet_first_name;
    private String athlet_last_name;
    private String athlet_age;
    private Team history_of_teams;

    public Athletes(int athlets_id, String athlet_first_name, String athlet_last_name, String athlet_age){
        this.athlets_id = athlets_id;
        this.athlet_first_name = athlet_first_name;
        this.athlet_last_name = athlet_last_name;
        this.athlet_age = athlet_age;
        this.history_of_teams = null;
    }
    public void setHistoryOfTeam(Team team){
        this.history_of_teams = team;
    }

    public Athletes(Team history_of_teams){
        this.history_of_teams = history_of_teams;
    }

    public int getAthlets_id(){
        return athlets_id;
    }

    public void setAthlets_id(int athletes_id){
        this.athlets_id = athletes_id;
    }

    public String getAthlet_first_name(){
        return athlet_first_name;
    }

    public void setAthlet_first_name(String athlet_first_name){
        this.athlet_first_name = athlet_first_name;
    }

    public String getAthlet_last_name(){
        return athlet_last_name;
    }

    public void setAthlet_last_name(String athlet_last_name){
        this.athlet_last_name = athlet_last_name;
    }

    public String getAthlet_age(){
        return athlet_age;
    }

    public void setAthlet_age(String athlet_age){
        this.athlet_age = athlet_age;
    }

    public Team getHistory_of_teams(){
        return history_of_teams;
    }

    public void setHistory_of_teams(Team history_of_teams){
        this.history_of_teams = history_of_teams;
    }

    @Override
    public String toString(){
        if (history_of_teams != null) {
            return "ID: " + getAthlets_id() +
                    "Имя: " + getAthlet_first_name() +
                    "Фамилия: " + getAthlet_last_name() +
                    "Возраст спорстмена: " + getAthlet_age() +
                    "Играл в команде: " + getHistory_of_teams().getName_of_team();
        } else {
            return "ID: " + getAthlets_id() +
                    "Имя: " + getAthlet_first_name() +
                    "Фамилия: " + getAthlet_last_name() +
                    "Возраст спорстмена: " + getAthlet_age();
        }
    }

}
