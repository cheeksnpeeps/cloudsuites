package com.cloudsuites.framework.services.amenity.entities.features;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "games_room")
public class GamesRoom extends Amenity {

    @Column(name = "number_of_game_consoles")
    private Integer numberOfGameConsoles; // Number of video game consoles available

    @Column(name = "has_board_games")
    private Boolean hasBoardGames; // If board games are available

    @Column(name = "max_capacity")
    private Integer maxCapacity; // Maximum capacity of the games room
}

