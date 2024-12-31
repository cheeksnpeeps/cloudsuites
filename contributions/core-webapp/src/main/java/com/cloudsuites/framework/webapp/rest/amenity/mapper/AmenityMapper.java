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

    // Map to hold mapping of AmenityType to Entity and DTO classes
    private final Map<AmenityType, MappingEntry> amenityTypeToMappingEntry;

    @Autowired
    public AmenityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;

        // Populate the mapping for AmenityType to Entity and DTO classes
        this.amenityTypeToMappingEntry = Map.ofEntries(
                Map.entry(AmenityType.SWIMMING_POOL, new MappingEntry(SwimmingPool.class, SwimmingPoolDto.class)),
                Map.entry(AmenityType.TENNIS_COURT, new MappingEntry(TennisCourt.class, TennisCourtDto.class)),
                Map.entry(AmenityType.GYM, new MappingEntry(Gym.class, GymDto.class)),
                Map.entry(AmenityType.THEATER, new MappingEntry(Theater.class, TheaterDto.class)),
                Map.entry(AmenityType.AEROBICS_ROOM, new MappingEntry(AerobicsRoom.class, AerobicsRoomDto.class)),
                Map.entry(AmenityType.PARTY_ROOM, new MappingEntry(PartyRoom.class, PartyRoomDto.class)),
                Map.entry(AmenityType.BARBECUE_AREA, new MappingEntry(BarbequeArea.class, BarbequeAreaDto.class)),
                Map.entry(AmenityType.MASSAGE_ROOM, new MappingEntry(MassageRoom.class, MassageRoomDto.class)),
                Map.entry(AmenityType.WINE_TASTING_ROOM, new MappingEntry(WineTastingRoom.class, WineTastingRoomDto.class)),
                Map.entry(AmenityType.GUEST_SUITE, new MappingEntry(GuestSuite.class, GuestSuiteDto.class)),
                Map.entry(AmenityType.BILLIARD_ROOM, new MappingEntry(BilliardRoom.class, BilliardRoomDto.class)),
                Map.entry(AmenityType.GAMES_ROOM, new MappingEntry(GamesRoom.class, GamesRoomDto.class)),
                Map.entry(AmenityType.GOLF_SIMULATOR, new MappingEntry(GolfSimulator.class, GolfSimulatorDto.class)),
                Map.entry(AmenityType.BOWLING_ALLEY, new MappingEntry(BowlingAlley.class, BowlingAlleyDto.class)),
                Map.entry(AmenityType.LIBRARY, new MappingEntry(Library.class, LibraryDto.class)),
                Map.entry(AmenityType.YOGA_STUDIO, new MappingEntry(YogaStudio.class, YogaStudioDto.class)),
                Map.entry(AmenityType.ELEVATOR, new MappingEntry(Elevator.class, ElevatorDto.class)),
                Map.entry(AmenityType.OTHER, new MappingEntry(Other.class, OtherDto.class))
        );
    }

    public Amenity convertToEntity(AmenityDto amenityDto) {
        if (amenityDto.getType() == null) {
            throw new IllegalArgumentException("The 'type' field is missing or null in the provided DTO.");
        }

        MappingEntry mappingEntry = amenityTypeToMappingEntry.get(amenityDto.getType());
        if (mappingEntry == null) {
            throw new IllegalArgumentException("Unknown AmenityType: " + amenityDto.getType());
        }

        // Map DTO to the corresponding Entity
        Amenity amenity = modelMapper.map(amenityDto, mappingEntry.getEntityClass());

        // Set the AmenityType
        amenity.setType(amenityDto.getType());

        return amenity;
    }

    public AmenityDto convertToDTO(Amenity amenity) {
        if (amenity.getType() == null) {
            throw new IllegalArgumentException("The 'type' field is missing or null in the provided Entity.");
        }

        MappingEntry mappingEntry = amenityTypeToMappingEntry.get(amenity.getType());
        if (mappingEntry == null) {
            throw new IllegalArgumentException("Unknown AmenityType: " + amenity.getType());
        }

        return modelMapper.map(amenity, mappingEntry.getDtoClass());
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

    // Inner class to store mapping details
    private static class MappingEntry {
        private final Class<? extends Amenity> entityClass;
        private final Class<? extends AmenityDto> dtoClass;

        public MappingEntry(Class<? extends Amenity> entityClass, Class<? extends AmenityDto> dtoClass) {
            this.entityClass = entityClass;
            this.dtoClass = dtoClass;
        }

        public Class<? extends Amenity> getEntityClass() {
            return entityClass;
        }

        public Class<? extends AmenityDto> getDtoClass() {
            return dtoClass;
        }
    }
}
