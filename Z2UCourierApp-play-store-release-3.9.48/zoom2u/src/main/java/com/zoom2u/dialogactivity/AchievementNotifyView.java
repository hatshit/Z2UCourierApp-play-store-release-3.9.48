package com.zoom2u.dialogactivity;

import com.github.jinatonic.confetti.CommonConfetti;
import com.github.jinatonic.confetti.ConfettiManager;

public class AchievementNotifyView extends AbstractActivity{

    @Override
    protected ConfettiManager generateOnce() {
        return CommonConfetti.rainingConfetti(container, colors).oneShot();
    }

    @Override
    protected ConfettiManager generateStream() {
        return CommonConfetti.rainingConfetti(container, colors).stream(3000);
    }

    @Override
    protected ConfettiManager generateInfinite() {
        return CommonConfetti.rainingConfetti(container, colors).infinite();
    }
}
