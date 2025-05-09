package com.example.demo.mapper;

import com.example.demo.dtos.BetDTO;
import com.example.demo.dtos.BetSelectionDTO;
import com.example.demo.model.Bet;
import com.example.demo.model.BetSelection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BetMapper {
    private final EventMapper eventMapper;
    public BetDTO toBetDTO(Bet bet){
        List<BetSelectionDTO> selections = bet.getSelections()
                .stream()
                .map(this::mapSelection)
                .toList();
        return new BetDTO(
                bet.getBetId(),
                bet.getTotalOdds(),
                bet.getStake(),
                bet.getStatus(),
                bet.getWinAmount(),
                selections,
                bet.getCreatedDt()
        );
    }

    private BetSelectionDTO mapSelection(BetSelection selection){
        return new BetSelectionDTO(
                selection.getId(),
                eventMapper.toDto(selection.getEvent()),
                selection.getLockedOdds(),
                selection.getPredictedResult().name(),
                selection.isWon(),
                selection.isCompleted()
        );
    }
    public List<BetDTO> toDtoList(List<Bet> bets){
        return bets.stream().map(this::toBetDTO).toList();
    }
}
