package phoned.clock;

import java.time.LocalDateTime;

public class ClockController {
    private ClockWidget clockWidget;
    private ClockService clockService;

    public ClockController(ClockWidget clockWidget, ClockService clockService) {
        this.clockWidget = clockWidget;
        this.clockService = clockService;
    }

    public void init() {
        this.clockService.getTime()
                .subscribe(this::onTimeUpdate);
    }

    private void onTimeUpdate(LocalDateTime localDateTime) {
        this.clockWidget.updateTime(localDateTime);
    }
}
