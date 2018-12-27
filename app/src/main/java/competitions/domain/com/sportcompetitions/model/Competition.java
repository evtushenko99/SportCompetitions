package competitions.domain.com.sportcompetitions.model;

import java.util.Comparator;

public class Competition {

    private int competitions_id;
    private String tournament_name;
    private String location;
    private String kind_of_sport;
    private String time_of_comp;
    private Team participating_teams;
    private Athletes participating_athletes;
    private Seats seats;
    private String results;//счет или очки, распределение местсчет или очки, распределение мест

    public Competition(int competitions_id, String tournament_name, String location, String kind_of_sport, String time_of_comp, String results) {
        this.competitions_id = competitions_id;
        this.tournament_name = tournament_name;
        this.location = location;
        this.kind_of_sport = kind_of_sport;
        this.time_of_comp = time_of_comp;
        this.participating_teams = null;
        this.participating_athletes = null;
        this.seats = null;
        this.results = results;
    }

    public void setTAS(Team participating_teams, Athletes participating_athletes, Seats seats) {
        this.participating_teams = participating_teams;
        this.participating_athletes = participating_athletes;
        this.seats = seats;
    }
    public void setSeat(Seats seats) {
        this.seats = seats;
    }

    public int getCompetitions_id() {
        return competitions_id;
    }

    public void setCompetitions_id(int competitions_id) {
        this.competitions_id = competitions_id;
    }

    public String getTournament_name() {
        return tournament_name;
    }

    public void setTournament_name(String tournament_name) {
        this.tournament_name = tournament_name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getKind_of_sport() {
        return kind_of_sport;
    }

    public void setKind_of_sport(String kind_of_sport) {
        this.kind_of_sport = kind_of_sport;
    }

    public String getTime_of_comp() {
        return time_of_comp;
    }

    public void setTime_of_comp(String time_of_comp) {
        this.time_of_comp = time_of_comp;
    }

    public Team getParticipating_teams() {
        return participating_teams;
    }

    public void setParticipating_teams(Team participating_teams) {
        this.participating_teams = participating_teams;
    }

    public Athletes getParticipating_athletes() {
        return participating_athletes;
    }

    public void setParticipating_athletes(Athletes participating_athletes) {
        this.participating_athletes = participating_athletes;
    }

    public Seats getSeats() {
        return seats;
    }

    public void setSeats(Seats seats) {
        this.seats = seats;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }
    public static class Comparators{
        public static Comparator<Competition> DATE = new Comparator<Competition>() {
            @Override
            public int compare(Competition o1, Competition o2) {
                return o1.time_of_comp.compareTo(o2.time_of_comp);
            }
        };
    }


    @Override
    public String toString() {
        if (participating_athletes != null && participating_teams == null && seats == null) {
            return "ID: " + getCompetitions_id() + "\n" +
                    "Tournament name: " + getTournament_name() + "\n" +
                    "Location: " + getLocation() + "\n" +
                    "Kind of sport: " + getKind_of_sport() + "\n" +
                    "Time of competition: " + getTime_of_comp() + "\n" +
                    "Athlet: " + getParticipating_athletes().getAthlet_last_name() + "\n" +
                    "Results:" + getResults();
        } else if (participating_teams != null && participating_athletes != null && seats == null) {
            return "ID: " + getCompetitions_id() + "\n" +
                    "Tournament name: " + getTournament_name() + "\n" +
                    "Location: " + getLocation() + "\n" +
                    "Kind of sport: " + getKind_of_sport() + "\n" +
                    "Time of competition: " + getTime_of_comp() + "\n" +
                    "Team: " + getParticipating_teams().getName_of_team() + "\n" +
                    "Athlet: " + getParticipating_athletes().getAthlet_last_name() + "\n" +
                    "Results:" + getResults();
        } else if (seats != null && participating_teams != null && participating_athletes != null) {
            return "ID: " + getCompetitions_id() + "\n" +
                    "Tournament name: " + getTournament_name() + "\n" +
                    "Location: " + getLocation() + "\n" +
                    "Kind of sport: " + getKind_of_sport() + "\n" +
                    "Time of competition: " + getTime_of_comp() + "\n" +
                    "Team: " + getParticipating_teams().getName_of_team() + "\n" +
                    "Athlet: " + getParticipating_athletes().getAthlet_last_name() + "\n" +
                    "Free seats" + +getSeats().getFree_seats() + "\n" +
                    "Results:" + getResults();
        } else {
            return "ID: " + getCompetitions_id() + "\n" +
                    "Tournament name: " + getTournament_name() + "\n" +
                    "Location: " + getLocation() + "\n" +
                    "Kind of sport: " + getKind_of_sport() + "\n" +
                    "Time of competition: " + getTime_of_comp() + "\n" +
                    "Results:" + getResults();
        }
    }

}

