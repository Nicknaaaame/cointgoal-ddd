package ru.lapotko.coingoal.core.position;

import ru.lapotko.coingoal.core.valueobjects.*;

public record CalculatedGoal(Long id, Weight weight, FiatPercent sellPrice, FiatCoinPercent sellAmount,
                             FiatCoin holdingsRemain,
                             Pnl pnl) {
}
