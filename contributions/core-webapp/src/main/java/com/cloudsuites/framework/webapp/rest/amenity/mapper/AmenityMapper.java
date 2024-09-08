package com.cloudsuites.framework.webapp.rest.amenity.mapper;

import com.cloudsuites.framework.services.amenity.entities.Amenity;
import com.cloudsuites.framework.services.amenity.entities.AmenityType;
import com.cloudsuites.framework.services.amenity.entities.features.*;
import com.cloudsuites.framework.webapp.rest.amenity.dto.AmenityDto;
import com.cloudsuites.framework.webapp.rest.amenity.dto.features.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AmenityMapper {

    private final ModelMapper modelMapper;

    // Maps to convert between DTOs and Entities
    private final Map<Class<? extends AmenityDto>, Class<? extends Amenity>> dtoToEntityMap;
    private final Map<Class<? extends Amenity>, Class<? extends AmenityDto>> entityToDtoMap;

    @Autowired
    public AmenityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        this.dtoToEntityMap = Map.ofEntries(
                Map.entry(AmenityDto.class, Amenity.class),
                Map.entry(TennisCourtDto.class, TennisCourt.class),
                Map.entry(AerobicsRoomDto.class, AerobicsRoom.class),
                Map.entry(PartyRoomDto.class, PartyRoom.class),
                Map.entry(BarbequeAreaDto.class, BarbequeArea.class),
                Map.entry(GymDto.class, Gym.class),
                Map.entry(SwimmingPoolDto.class, SwimmingPool.class),
                Map.entry(TheaterDto.class, Theater.class),
                Map.entry(MassageRoomDto.class, MassageRoom.class),
                Map.entry(WineTastingRoomDto.class, WineTastingRoom.class),
                Map.entry(GuestSuiteDto.class, GuestSuite.class),
                Map.entry(BilliardRoomDto.class, BilliardRoom.class),
                Map.entry(GamesRoomDto.class, GamesRoom.class),
                Map.entry(GolfSimulatorDto.class, GolfSimulator.class),
                Map.entry(BowlingAlleyDto.class, BowlingAlley.class),
                Map.entry(LibraryDto.class, Library.class),
                Map.entry(YogaStudioDto.class, YogaStudio.class),
                Map.entry(ElevatorDto.class, Elevator.class),
                Map.entry(OtherDto.class, Other.class)
        );

        this.entityToDtoMap = Map.ofEntries(
                Map.entry(Amenity.class, AmenityDto.class),
                Map.entry(TennisCourt.class, TennisCourtDto.class),
                Map.entry(AerobicsRoom.class, AerobicsRoomDto.class),
                Map.entry(PartyRoom.class, PartyRoomDto.class),
                Map.entry(BarbequeArea.class, BarbequeAreaDto.class),
                Map.entry(Gym.class, GymDto.class),
                Map.entry(SwimmingPool.class, SwimmingPoolDto.class),
                Map.entry(Theater.class, TheaterDto.class),
                Map.entry(MassageRoom.class, MassageRoomDto.class),
                Map.entry(WineTastingRoom.class, WineTastingRoomDto.class),
                Map.entry(GuestSuite.class, GuestSuiteDto.class),
                Map.entry(BilliardRoom.class, BilliardRoomDto.class),
                Map.entry(GamesRoom.class, GamesRoomDto.class),
                Map.entry(GolfSimulator.class, GolfSimulatorDto.class),
                Map.entry(BowlingAlley.class, BowlingAlleyDto.class),
                Map.entry(Library.class, LibraryDto.class),
                Map.entry(YogaStudio.class, YogaStudioDto.class),
                Map.entry(Elevator.class, ElevatorDto.class),
                Map.entry(Other.class, OtherDto.class)
        );

        // Configure automatic mappings
        configureMappings();
    }


    public Amenity convertToEntity(AmenityDto amenityDto) {
        Class<? extends Amenity> entityClass = dtoToEntityMap.get(amenityDto.getClass());
        if (entityClass == null) {
            throw new IllegalArgumentException("Unknown DTO type: " + amenityDto.getClass());
        }
        Amenity amenity = modelMapper.map(amenityDto, entityClass);

        if (amenityDto instanceof SwimmingPoolDto) {
            amenity.setType(AmenityType.SWIMMING_POOL);
        } else if (amenityDto instanceof TennisCourtDto) {
            amenity.setType(AmenityType.TENNIS_COURT);
        } else if (amenityDto instanceof GymDto) {
            amenity.setType(AmenityType.GYM);
        } else if (amenityDto instanceof TheaterDto) {
            amenity.setType(AmenityType.THEATER);
        } else if (amenityDto instanceof AerobicsRoomDto) {
            amenity.setType(AmenityType.AEROBICS_ROOM);
        } else if (amenityDto instanceof PartyRoomDto) {
            amenity.setType(AmenityType.PARTY_ROOM);
        } else if (amenityDto instanceof BarbequeAreaDto) {
            amenity.setType(AmenityType.BARBECUE_AREA);
        } else if (amenityDto instanceof MassageRoomDto) {
            amenity.setType(AmenityType.MASSAGE_ROOM);
        } else if (amenityDto instanceof WineTastingRoomDto) {
            amenity.setType(AmenityType.WINE_TASTING_ROOM);
        } else if (amenityDto instanceof GuestSuiteDto) {
            amenity.setType(AmenityType.GUEST_SUITE);
        } else if (amenityDto instanceof BilliardRoomDto) {
            amenity.setType(AmenityType.BILLIARD_ROOM);
        } else if (amenityDto instanceof GamesRoomDto) {
            amenity.setType(AmenityType.GAMES_ROOM);
        } else if (amenityDto instanceof GolfSimulatorDto) {
            amenity.setType(AmenityType.GOLF_SIMULATOR);
        } else if (amenityDto instanceof BowlingAlleyDto) {
            amenity.setType(AmenityType.BOWLING_ALLEY);
        } else if (amenityDto instanceof LibraryDto) {
            amenity.setType(AmenityType.LIBRARY);
        } else if (amenityDto instanceof YogaStudioDto) {
            amenity.setType(AmenityType.YOGA_STUDIO);
        } else if (amenityDto instanceof ElevatorDto) {
            amenity.setType(AmenityType.ELEVATOR);
        } else if (amenityDto instanceof OtherDto) {
            amenity.setType(AmenityType.OTHER);
        }
        return amenity;
    }

    public AmenityDto convertToDTO(Amenity amenity) {
        Class<? extends AmenityDto> dtoClass = entityToDtoMap.get(amenity.getClass());
        if (dtoClass == null) {
            throw new IllegalArgumentException("Unknown entity type: " + amenity.getClass());
        }
        return modelMapper.map(amenity, dtoClass);
    }
    public List<AmenityDto> convertToDTOList(List<Amenity> amenities) {
        return amenities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public <E extends Amenity> List<E> convertToEntityList(List<? extends AmenityDto> amenityDtos, Class<E> entityClass) {
        return amenityDtos.stream()
                .map(dto -> modelMapper.map(dto, entityClass))
                .collect(Collectors.toList());
    }

    private void configureMappings() {
        // Automatic mappings
    }
}
