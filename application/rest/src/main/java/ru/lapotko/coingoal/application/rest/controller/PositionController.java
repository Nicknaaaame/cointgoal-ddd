package ru.lapotko.coingoal.application.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.lapotko.coingoal.application.rest.dto.CalculatedGoalDto;
import ru.lapotko.coingoal.application.rest.dto.CoinDto;
import ru.lapotko.coingoal.application.rest.dto.response.PositionResponse;
import ru.lapotko.coingoal.application.rest.dto.value.FiatCoinPercentValue;
import ru.lapotko.coingoal.application.rest.dto.value.FiatCoinValue;
import ru.lapotko.coingoal.application.rest.dto.value.FiatPercentValue;
import ru.lapotko.coingoal.application.rest.dto.value.PnlValue;
import ru.lapotko.coingoal.application.rest.mapper.RestMapper;
import ru.lapotko.coingoal.core.pagination.PageInfo;
import ru.lapotko.coingoal.core.position.PositionAggregate;
import ru.lapotko.coingoal.core.position.request.PositionCreate;
import ru.lapotko.coingoal.core.position.request.PositionUpdate;
import ru.lapotko.coingoal.core.position.service.PositionDomainService;
import ru.lapotko.coingoal.core.valueobjects.Pnl;
import ru.lapotko.coingoal.infrastructure.jpa.filter.CoinFilter;
import ru.lapotko.coingoal.infrastructure.jpa.filter.PositionFilter;
import ru.lapotko.coingoal.infrastructure.jpa.util.ConvertUtil;

import java.util.Optional;
import java.util.stream.Collectors;

import static ru.lapotko.coingoal.application.rest.mapper.RestMapper.toPositionResponse;

@RestController
@RequestMapping("/api/v1/position")
@RequiredArgsConstructor
public class PositionController {
    private final PositionDomainService positionDomainService;

    @GetMapping
    public ResponseEntity<Page<PositionResponse>> getPositionPage(
            @RequestParam(name = "coin_name", required = false)
            String coinName,
            @RequestParam(name = "coin_symbol", required = false)
            String coinSymbol,
            Pageable pageable) {
        PositionFilter filter = PositionFilter.builder()
                .coinFilter(CoinFilter.builder()
                        .name(coinName)
                        .symbol(coinSymbol)
                        .build())
                .build();
        PageInfo<PositionAggregate> positionPageInfo = positionDomainService.getPositionPage(
                ConvertUtil.convertToPositionFilter(filter),
                ConvertUtil.convertToPageableInfo(pageable));
        Page<PositionAggregate> positionPage = ConvertUtil.convertToPage(positionPageInfo, pageable);
        return ResponseEntity.ok(positionPage.map(RestMapper::toPositionResponse));
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<PositionResponse> getPositionById(@PathVariable Long positionId) {
        return ResponseEntity.ok(toPositionResponse(positionDomainService.getPosition(positionId)));
    }

    @PostMapping
    public ResponseEntity<Long> createPosition(
            @RequestBody
            PositionCreate request) {
        PositionAggregate position = positionDomainService.createPosition(request);
        return ResponseEntity.ok(position.getId());
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long positionId) {
        positionDomainService.deletePosition(positionId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping
    public ResponseEntity<PositionResponse> patchPosition(
            @RequestBody
            PositionUpdate request) {
        return ResponseEntity.ok(toPositionResponse(positionDomainService.updatePosition(request)));
    }



    /*@PostMapping("/{positionId}/goal")
    public ResponseEntity<Long> createGoal(
            @Valid @RequestBody GoalRequest request,
            @PathVariable Long positionId) {
//        return ResponseEntity.ok(goalService.createGoal(request).getId());
        return null;
    }

    @DeleteMapping("/{positionId}/goal/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long positionId,
            @PathVariable Long goalId) {
//        goalService.deleteGoal(goalId);
        return ResponseEntity.ok().build();
    }*/
}
