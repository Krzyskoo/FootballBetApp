package com.example.demo.mapper;

import com.example.demo.Dtos.InternalEventDTO;
import com.example.demo.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "eventId", target = "eventId")
    @Mapping(source = "sportKey", target = "sportKey")
    @Mapping(source = "sportTitle", target = "sportTitle")
    @Mapping(source = "startTime", target = "startTime")
    @Mapping(source = "homeTeam", target = "homeTeam")
    @Mapping(source = "awayTeam", target = "awayTeam")
    @Mapping(source = "homeTeamOdds", target = "homeTeamOdds")
    @Mapping(source = "awayTeamOdds", target = "awayTeamOdds")
    @Mapping(source = "drawOdds", target = "drawOdds")
    @Mapping(source = "status", target = "status", qualifiedByName = "enumToString")
    @Mapping(source = "completed", target = "completed")
    InternalEventDTO toDto(Event event);

    List<InternalEventDTO> toDtoList(List<Event> events);
    @Named("enumToString")
    static String mapStatusEnumToString(Enum<?> status) {
        return status != null ? status.name() : null;
    }
}
